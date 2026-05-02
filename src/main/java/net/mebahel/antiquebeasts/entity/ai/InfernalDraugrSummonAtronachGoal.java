package net.mebahel.antiquebeasts.entity.ai;

import net.mebahel.antiquebeasts.entity.ModEntities;
import net.mebahel.antiquebeasts.entity.custom.other.FlameAtronachEntity;
import net.mebahel.antiquebeasts.entity.custom.other.InfernalDraugrEntity;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ServerWorldAccess;

import java.util.EnumSet;
import java.util.UUID;

public class InfernalDraugrSummonAtronachGoal extends Goal {
    private final InfernalDraugrEntity draugr;

    // Timeline de cast
    private static final int CAST_START  = 70; // tick où on déclenche l’anim/son
    private static final int SUMMON_TICK = 18; // tick où on spawn réellement

    // Durée de vie de l’atronach
    private static final int SUMMON_LIFE_TICKS = 20 * 60;

    // Si l’atronach meurt => au bout de 10s il peut recast
    private static final int RESUMMON_DELAY_TICKS = 20 * 10; // 10s

    // Spawn cone
    private static final double MIN_DIST = 3.0D;
    private static final double MAX_DIST = 4.0D;
    private static final double CONE_DEGREES = 35.0D;

    // Anti double-spawn
    private boolean summonedThisCycle = false;

    // État
    private boolean casting = false;

    public InfernalDraugrSummonAtronachGoal(InfernalDraugrEntity draugr) {
        this.draugr = draugr;
        this.setControls(EnumSet.noneOf(Control.class));
    }

    @Override
    public boolean canStart() {
        if (draugr.getWorld().isClient) return false;

        LivingEntity target = draugr.getTarget();
        if (target == null || !target.isAlive() || !draugr.canSee(target)
                || draugr.isBlocking() || draugr.isUsingPotion()) return false;

        // Si un atronach est vivant => jamais
        if (hasLivingAtronach()) return false;

        return true;
    }

    @Override
    public boolean shouldContinue() {
        if (draugr.getWorld().isClient) return false;

        LivingEntity target = draugr.getTarget();
        if (target == null || !target.isAlive() || !draugr.canSee(target)
            || draugr.isBlocking() || draugr.isUsingPotion()) return false;

        // Si un atronach apparaît (ou redevient valide), on stop
        if (hasLivingAtronach()) return false;

        // Tant qu'on a une target valide et pas d'atronach, on continue
        return true;
    }

    @Override
    public void start() {
        summonedThisCycle = false;
        casting = false;

        // si on est en cooldown “post-mort” (10s), on ne touche à rien,
        // on va juste décrémenter dans tick() sans bloquer le melee.
    }

    @Override
    public void stop() {
        // Stop propre
        casting = false;
        summonedThisCycle = false;

        // Fin de cast => shooting false
        draugr.setShooting(false);
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity target = draugr.getTarget();
        if (target == null || !target.isAlive() || !draugr.canSee(target)) {
            stop();
            return;
        }

        // Si un atronach est vivant, on ne cast pas
        if (hasLivingAtronach()) {
            stop();
            return;
        }

        int cd = draugr.getCooldown();

        // 1) WAIT PHASE (cooldown > 0 et pas en cast) :
        // on décrémente sans rien bloquer (melee continue).
        if (!casting && cd > 0) {
            draugr.setCooldown(cd - 1);
            return;
        }

        // 2) START CAST (cooldown == 0 et conditions OK)
        if (!casting && cd <= 0) {
            casting = true;
            summonedThisCycle = false;

            // Pendant tout le summoning => shooting true
            draugr.setShooting(true);

            // Timeline cast dans le cooldown de l’entité
            draugr.setCooldown(CAST_START);
            cd = CAST_START;
        }

        // 3) CASTING PHASE
        // Re-lire cd (après éventuel set à CAST_START)
        cd = draugr.getCooldown();
        if (!casting || cd <= 0) {
            stop();
            return;
        }

        // Pendant le cast : on force l’arrêt du path + look vers la target
        draugr.getNavigation().stop();
        draugr.lookAtEntity(target, 30.0F, 30.0F);

        // Début cast : anim + son
        if (cd == CAST_START) {
            draugr.triggerAnim("attacking", "summoning");
            draugr.getWorld().playSound(
                    null,
                    draugr.getX(), draugr.getY(), draugr.getZ(),
                    ModSounds.FLAME_ATRONACH_SUMMON_CHARGE,
                    draugr.getSoundCategory(),
                    0.8F,
                    0.9F + draugr.getRandom().nextFloat() * 0.2F
            );
        }

        // Spawn au tick voulu
        if (!summonedThisCycle && cd == SUMMON_TICK) {
            summonedThisCycle = true;

            boolean ok = summonAtronach();
            // Fin de cast immédiatement, le melee peut repartir
            casting = false;
            draugr.setShooting(false);

            if (!ok) {
                // petit retry delay si spawn impossible
                draugr.setCooldown(20);
            } else {
                // Pas de cooldown long ici : le “gating” est assuré par hasLivingAtronach().
                // Si l’atronach meurt, hasLivingAtronach() appliquera 10s de cooldown.
                draugr.setCooldown(0);
            }
            return;
        }

        // Décrément timeline cast
        draugr.setCooldown(cd - 1);

        // Fin timeline
        if (cd - 1 <= 0) {
            casting = false;
            draugr.setShooting(false);
        }
    }

    /**
     * True si un atronach vivant est présent et appartient au draugr.
     * Si l’UUID est présent mais l’entité est morte/absente => on clear + cooldown 10s.
     */
    private boolean hasLivingAtronach() {
        if (!(draugr.getWorld() instanceof ServerWorld sw)) return false;

        UUID id = draugr.getSummonedAtronachUuid();
        if (id == null) return false;

        var e = sw.getEntity(id);
        if (e instanceof FlameAtronachEntity fa && fa.isAlive() && fa.isOwner(draugr)) {
            return true;
        }

        // Il “en avait un”, mais il n’est plus là => on autorise un resummon après 10s
        draugr.setSummonedAtronachUuid(null);

        // N’écrase pas un cast en cours : ici on est appelé depuis canStart/shouldContinue/tick.
        // On force seulement un minimum de 10s (si déjà plus grand, on garde).
        int current = draugr.getCooldown();
        if (current < RESUMMON_DELAY_TICKS) {
            draugr.setCooldown(RESUMMON_DELAY_TICKS);
        }

        return false;
    }

    private boolean summonAtronach() {
        if (!(draugr.getWorld() instanceof ServerWorld sw)) return false;
        if (hasLivingAtronach()) return false;

        Vec3d spawnPos = pickConeSpawnPos(sw);

        FlameAtronachEntity atronach = ModEntities.FLAME_ATRONACH.create(sw);
        if (atronach == null) return false;

        atronach.refreshPositionAndAngles(spawnPos.x, spawnPos.y, spawnPos.z, draugr.getYaw(), 0.0F);

        atronach.initialize((ServerWorldAccess) sw,
                sw.getLocalDifficulty(BlockPos.ofFloored(spawnPos)),
                SpawnReason.EVENT,
                null,
                null
        );

        // owner = draugr uuid, durée de vie
        atronach.setSummonData(draugr.getUuid(), SUMMON_LIFE_TICKS);

        // Déclenche la séquence spawn (anim + particules côté Atronach)
        atronach.setHasSpawned(false);

        boolean spawned = sw.spawnEntity(atronach);
        if (!spawned) return false;

        draugr.setSummonedAtronachUuid(atronach.getUuid());

        // Donne la même target que le draugr
        LivingEntity t = draugr.getTarget();
        if (t != null && t.isAlive() && !t.isRemoved() && !atronach.isOwner(t)) {
            atronach.setTarget(t);
        }

        sw.playSound(null, spawnPos.x, spawnPos.y, spawnPos.z,
                ModSounds.FLAME_ATRONACH_SUMMON,
                draugr.getSoundCategory(),
                0.9F,
                0.9F + draugr.getRandom().nextFloat() * 0.2F);

        return true;
    }

    private Vec3d pickConeSpawnPos(ServerWorld world) {
        float yawRad = (float) Math.toRadians(draugr.getYaw());
        Vec3d forward = new Vec3d(-MathHelper.sin(yawRad), 0.0D, MathHelper.cos(yawRad)).normalize();

        double halfCone = Math.toRadians(CONE_DEGREES);
        double angle = (draugr.getRandom().nextDouble() * 2.0D - 1.0D) * halfCone;

        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        Vec3d dir = new Vec3d(
                forward.x * cos - forward.z * sin,
                0.0D,
                forward.x * sin + forward.z * cos
        ).normalize();

        double dist = MIN_DIST + draugr.getRandom().nextDouble() * (MAX_DIST - MIN_DIST);
        Vec3d candidate = draugr.getPos().add(dir.multiply(dist));

        return findSafeSpawnPos(world, candidate, draugr.getYaw());
    }

    private Vec3d findSafeSpawnPos(ServerWorld world, Vec3d candidate, float yaw) {
        FlameAtronachEntity probe = ModEntities.FLAME_ATRONACH.create(world);
        if (probe == null) return candidate;

        BlockPos base = BlockPos.ofFloored(candidate);
        BlockPos[] offsets = new BlockPos[] {
                base,
                base.add(1, 0, 0),
                base.add(-1, 0, 0),
                base.add(0, 0, 1),
                base.add(0, 0, -1),
                base.add(1, 0, 1),
                base.add(-1, 0, -1),
                base.add(1, 0, -1),
                base.add(-1, 0, 1)
        };

        final int DOWN = 6;
        final int UP = 6;

        for (BlockPos xz : offsets) {
            for (int dy = 0; dy <= DOWN; dy++) {
                BlockPos pos = xz.down(dy);

                if (!world.getBlockState(pos.down()).isSolidBlock(world, pos.down())) continue;

                Vec3d p = new Vec3d(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D).add(0, 0.05D, 0);
                probe.refreshPositionAndAngles(p.x, p.y, p.z, yaw, 0.0F);

                if (world.isSpaceEmpty(probe, probe.getBoundingBox())) return p;
            }

            for (int dy = 1; dy <= UP; dy++) {
                BlockPos pos = xz.up(dy);

                if (!world.getBlockState(pos.down()).isSolidBlock(world, pos.down())) continue;

                Vec3d p = new Vec3d(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D).add(0, 0.05D, 0);
                probe.refreshPositionAndAngles(p.x, p.y, p.z, yaw, 0.0F);

                if (world.isSpaceEmpty(probe, probe.getBoundingBox())) return p;
            }
        }

        Vec3d fallback = candidate.add(0, 0.05D, 0);
        probe.refreshPositionAndAngles(fallback.x, fallback.y, fallback.z, yaw, 0.0F);
        if (world.isSpaceEmpty(probe, probe.getBoundingBox())) return fallback;

        return candidate;
    }
}
