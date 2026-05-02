package net.mebahel.antiquebeasts.item.staff;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.mebahel.antiquebeasts.entity.ModEntities;
import net.mebahel.antiquebeasts.entity.custom.other.FlameAtronachEntity;
import net.mebahel.antiquebeasts.item.custom.ModItems;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class FlameAtronachSummoningStaff extends TridentItem {
    private static final Logger LOGGER = LogManager.getLogger("mebahelcreaturesdraugr/FlameAtronachStaff");

    // Active/désactive les logs de debug
    private static final boolean DEBUG_SPAWN_LOG = false;

    private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;

    private static final int CHARGE_TICKS = 20;
    private static final double MAX_RANGE = 8.0D;

    private static final int SUMMON_LIFE_TICKS = 20 * 60;
    private static final int COOLDOWN_TICKS = 60;

    public FlameAtronachSummoningStaff(Settings settings) {
        super(settings);

        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE,
                new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Tool modifier", 4.0, EntityAttributeModifier.Operation.ADDITION));
        builder.put(EntityAttributes.GENERIC_ATTACK_SPEED,
                new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Tool modifier", -2.9000000953674316, EntityAttributeModifier.Operation.ADDITION));
        this.attributeModifiers = builder.build();
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return !miner.isCreative();
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.NONE;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (user.getItemCooldownManager().isCoolingDown(this)) {
            return TypedActionResult.fail(stack);
        }

        user.setCurrentHand(hand);

        if (world.isClient) {
            user.playSound(ModSounds.FLAME_ATRONACH_SUMMON_CHARGE, SoundCategory.PLAYERS, 1.0F, 1.0F);
        }

        return TypedActionResult.consume(stack);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity player)) return;

        int chargeTicks = this.getMaxUseTime(stack) - remainingUseTicks;
        if (chargeTicks < CHARGE_TICKS) return;

        if (world.isClient) return;
        if (!(world instanceof ServerWorld serverWorld)) return;

        // Position visée
        Vec3d spawnPos = getTargetedSpawnPos(serverWorld, player, MAX_RANGE);

        if (DEBUG_SPAWN_LOG) {
            LOGGER.info("[Summon] player={} dim={} ceiling={} candidateSpawn={}",
                    player.getName().getString(),
                    serverWorld.getRegistryKey().getValue(),
                    serverWorld.getDimension().hasCeiling(),
                    vec(spawnPos));
        }

        discardExistingSummon(serverWorld, player);

        FlameAtronachEntity atronach = ModEntities.FLAME_ATRONACH.create(serverWorld);
        if (atronach == null) {
            LOGGER.warn("[Summon] could not create FlameAtronachEntity (registry?)");
            return;
        }

        atronach.refreshPositionAndAngles(spawnPos.x, spawnPos.y, spawnPos.z, player.getYaw(), 0.0F);
        atronach.initialize((ServerWorldAccess) world, world.getLocalDifficulty(BlockPos.ofFloored(spawnPos)),
                SpawnReason.EVENT, null, null);

        atronach.setSummonData(player.getUuid(), SUMMON_LIFE_TICKS);
        atronach.setHasSpawned(false);
        serverWorld.spawnEntity(atronach);

        player.swingHand(player.getActiveHand(), true);
        player.incrementStat(Stats.USED.getOrCreateStat(this));

        stack.damage(1, player, (p) -> p.sendToolBreakStatus(player.getActiveHand()));
        player.getItemCooldownManager().set(this, COOLDOWN_TICKS);
    }

    private Vec3d getTargetedSpawnPos(ServerWorld world, PlayerEntity player, double maxRange) {
        HitResult hit = player.raycast(maxRange, 1.0F, false);

        Vec3d candidate;
        if (hit.getType() == HitResult.Type.BLOCK && hit instanceof BlockHitResult bhr) {
            BlockPos bp = bhr.getBlockPos();
            Direction side = bhr.getSide();
            BlockPos spawnBlock = bp.offset(side);
            candidate = Vec3d.ofCenter(spawnBlock);

            if (DEBUG_SPAWN_LOG) {
                LOGGER.info("[Summon] raycast=BLOCK hitPos={} side={} spawnBlock={} candidate={}",
                        bp, side, spawnBlock, vec(candidate));
            }
        } else {
            Vec3d look = player.getRotationVec(1.0F);
            candidate = player.getPos().add(look.multiply(maxRange));

            if (DEBUG_SPAWN_LOG) {
                LOGGER.info("[Summon] raycast=MISS playerPos={} look={} candidate={}",
                        vec(player.getPos()), vec(look), vec(candidate));
            }
        }

        Vec3d safe = findSafeSpawnPos(world, candidate, player);
        return safe != null ? safe : player.getPos().add(0, 3, 0); // fallback propre
    }

    /**
     * Nouvelle logique :
     * - Nether/ceiling: scan vers le bas depuis la hauteur du candidat/joueur pour éviter le roof.
     * - Overworld/no ceiling: on tente d'abord proche de la hauteur du candidat, sinon petit scan.
     */
    private @Nullable Vec3d findSafeSpawnPos(ServerWorld world, Vec3d candidate, PlayerEntity player) {
        FlameAtronachEntity probe = ModEntities.FLAME_ATRONACH.create(world);
        if (probe == null) return null;

        // On prend XZ du candidat, et comme Y de départ : celui du candidat (clamp)
        BlockPos base = BlockPos.ofFloored(candidate);
        int bottom = world.getBottomY();
        int top = world.getTopY();

        int startY = clamp(base.getY(), bottom + 2, top - 2);

        // Si tu veux absolument éviter les gros écarts, tu peux forcer startY proche du joueur :
        // startY = clamp(player.getBlockY(), bottom + 2, top - 2);

        boolean ceiling = world.getDimension().hasCeiling();

        if (DEBUG_SPAWN_LOG) {
            LOGGER.info("[Summon] findSafeSpawnPos dim={} ceiling={} base={} startY={} bottom={} top={}",
                    world.getRegistryKey().getValue(), ceiling, base, startY, bottom, top);
        }

        // Offsets XZ (autour du point)
        int[][] offsets = new int[][]{
                {0, 0},
                {1, 0}, {-1, 0},
                {0, 1}, {0, -1},
                {1, 1}, {-1, -1},
                {1, -1}, {-1, 1},
                {2, 0}, {-2, 0}, {0, 2}, {0, -2}
        };

        // Stratégie de scan Y
        // - ceiling: scan down large
        // - no ceiling: scan down/up court pour rester proche
        int downLimit = ceiling ? 128 : 24; // en overworld inutile de descendre de 200 blocs
        int upLimit = ceiling ? 4 : 8;

        for (int i = 0; i < offsets.length; i++) {
            int ox = offsets[i][0];
            int oz = offsets[i][1];

            int x = base.getX() + ox;
            int z = base.getZ() + oz;

            if (DEBUG_SPAWN_LOG) {
                LOGGER.info("[Summon] tryXZ #{} xz=({}, {})", i, x, z);
            }

            // 1) Scan DOWN
            Vec3d down = scanForStand(world, probe, x, z, startY, startY - downLimit, player.getYaw(), "DOWN");
            if (down != null) return down;

            // 2) Petit scan UP (utile si candidat est dans un trou / demi-bloc)
            Vec3d up = scanForStand(world, probe, x, z, startY + 1, startY + upLimit, player.getYaw(), "UP");
            if (up != null) return up;
        }

        LOGGER.warn("[Summon] No safe spot found near candidate={}, fallback will be used.", vec(candidate));
        return null;
    }

    private @Nullable Vec3d scanForStand(ServerWorld world,
                                         FlameAtronachEntity probe,
                                         int x, int z,
                                         int yFrom, int yTo,
                                         float yaw,
                                         String mode) {

        int step = (yTo <= yFrom) ? -1 : 1;
        int y = yFrom;

        // clamp yFrom/yTo dans les limites monde
        int bottom = world.getBottomY() + 1;
        int top = world.getTopY() - 2;

        int from = clamp(yFrom, bottom, top);
        int to = clamp(yTo, bottom, top);

        // recompute step après clamp
        step = (to <= from) ? -1 : 1;
        y = from;

        while (true) {
            BlockPos feet = new BlockPos(x, y, z);

            if (canStandAt(world, probe, feet, yaw)) {
                Vec3d spawn = new Vec3d(x + 0.5D, y + 0.05D, z + 0.5D);

                if (DEBUG_SPAWN_LOG) {
                    LOGGER.info("[Summon] FOUND mode={} feet={} spawn={}", mode, feet, vec(spawn));
                }
                return spawn;
            } else if (DEBUG_SPAWN_LOG && (Math.abs(y - from) % 8 == 0)) {
                // log léger toutes les 8 itérations (évite spam)
                LOGGER.info("[Summon] mode={} scanning... y={}", mode, y);
            }

            if (y == to) break;
            y += step;
        }

        return null;
    }

    /**
     * Règles:
     * - Il faut un "sol" sous les pieds (collision non vide)
     * - L'espace pour la hitbox doit être vide (isSpaceEmpty)
     * - Et on évite de spawn dans un bloc solide au niveau des pieds
     */
    private boolean canStandAt(ServerWorld world, FlameAtronachEntity probe, BlockPos feet, float yaw) {
        try {
            BlockPos below = feet.down();

            BlockState stateFeet = world.getBlockState(feet);
            BlockState stateBelow = world.getBlockState(below);

            boolean feetClear = stateFeet.getCollisionShape(world, feet).isEmpty();
            boolean hasFloor = !stateBelow.getCollisionShape(world, below).isEmpty();

            if (!feetClear || !hasFloor) {
                if (DEBUG_SPAWN_LOG) {
                    LOGGER.debug("[Summon] reject feet={} feetClear={} hasFloor={} stateFeet={} stateBelow={}",
                            feet, feetClear, hasFloor, stateFeet.getBlock(), stateBelow.getBlock());
                }
                return false;
            }

            // place probe
            probe.refreshPositionAndAngles(feet.getX() + 0.5D, feet.getY() + 0.05D, feet.getZ() + 0.5D, yaw, 0.0F);

            // vérif espace (hitbox vs blocs)
            boolean space = world.isSpaceEmpty(probe, probe.getBoundingBox());
            if (!space && DEBUG_SPAWN_LOG) {
                LOGGER.debug("[Summon] reject collision feet={} bbox={}", feet, probe.getBoundingBox());
            }
            return space;
        } catch (Throwable t) {
            // Anti-crash absolu (ne jamais casser le serveur sur un summon)
            LOGGER.warn("[Summon] canStandAt exception at feet={} err={}", feet, t.toString());
            return false;
        }
    }

    private void discardExistingSummon(ServerWorld world, PlayerEntity owner) {
        double r = 64.0D;

        var list = world.getEntitiesByClass(
                FlameAtronachEntity.class,
                owner.getBoundingBox().expand(r),
                e -> e.isSummoned() && owner.getUuid().equals(e.getOwnerUuid())
        );

        if (DEBUG_SPAWN_LOG) {
            LOGGER.info("[Summon] discardExistingSummon found={} aroundOwner={}", list.size(), owner.getName().getString());
        }

        for (FlameAtronachEntity e : list) {
            e.discard();
        }
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.damage(1, attacker, (e) -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        return true;
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.attributeModifiers : super.getAttributeModifiers(slot);
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return ingredient.isOf(ModItems.FLAME_ATRONACH_HEART);
    }

    // ===== helpers =====

    private static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }

    private static String vec(Vec3d v) {
        return String.format("(%.3f, %.3f, %.3f)", v.x, v.y, v.z);
    }
}
