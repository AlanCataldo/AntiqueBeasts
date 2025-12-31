package net.mebahel.antiquebeasts.entity.ai.other;

import net.mebahel.antiquebeasts.entity.custom.other.DraugrWightEntity;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.mebahel.antiquebeasts.util.entity.ProjectileUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class DraugrSpellGoal extends Goal {
    private final DraugrWightEntity draugr;
    private final float damage;

    private static final int COOLDOWN_TICKS = 220;
    private static final int SHOOT_START = 60;
    private static final int SHOOT_END = 10;
    private static final int SHOOT_INTERVAL = 5;

    private final ProjectileUtil projectileUtil;

    public DraugrSpellGoal(DraugrWightEntity draugr, float damage) {
        this.draugr = draugr;
        this.damage = damage;
        this.projectileUtil = new ProjectileUtil();
    }

    @Override
    public boolean canStart() {
        LivingEntity target = this.draugr.getTarget();

        if (target instanceof PlayerEntity playerEntity) {
            if (playerEntity.isCreative() || playerEntity.isSpectator()) {
                return false;
            }
        }

        return target != null
                && target.isAlive()
                && !this.draugr.isBlocking()
                && !this.draugr.isUsingPotion();
    }

    @Override
    public void start() {
        this.draugr.setCooldown(COOLDOWN_TICKS + this.draugr.getRandom().nextInt(60));
        this.draugr.setShooting(false);
    }

    @Override
    public boolean shouldContinue() {
        LivingEntity target = this.draugr.getTarget();

        if (target instanceof PlayerEntity playerEntity) {
            if (playerEntity.isCreative() || playerEntity.isSpectator()) {
                return false;
            }
        }

        return target != null
                && target.isAlive()
                && !this.draugr.isBlocking()
                && !this.draugr.isUsingPotion();
    }

    @Override
    public void stop() {
        this.draugr.setShooting(false);
        this.draugr.setCooldown(COOLDOWN_TICKS + this.draugr.getRandom().nextInt(60));
        draugr.getNavigation().stop();
        draugr.getMoveControl().strafeTo(0, 0);
        var vel = draugr.getVelocity();
        draugr.setVelocity(vel.x * 0.2, vel.y, vel.z * 0.2);
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        // Sécu : si blocage / potion commence pendant le cast
        if (this.draugr.isBlocking() || this.draugr.isUsingPotion()) {
            this.stop();
            return;
        }

        LivingEntity target = this.draugr.getTarget();
        if (target == null || !target.isAlive() || !this.draugr.canSee(target)) {
            this.stop();
            return;
        }

        // Cooldown
        int cd = this.draugr.getCooldown();
        if (cd > 0) {
            this.draugr.setCooldown(cd - 1);
            cd = cd - 1;
        }

        // Pendant le shooting : recule tout en restant face à la cible
        if (this.draugr.isShooting()) {
            this.draugr.getNavigation().stop();
            moveBackwardFromTarget(target);
        }

        // Fenêtre de tirs : 50 -> 10, toutes les 5 ticks
        if (cd <= SHOOT_START - 10 && cd >= SHOOT_END && cd % SHOOT_INTERVAL == 0) {
            projectileUtil.shootFrostBiteProjectile(
                    target,
                    this.draugr,
                    this.damage,
                    new Vec3d(0.3, 0.3, -0.6)
            );
        }

        // Début d'incantation : anim + flag shooting
        if (cd == SHOOT_START) {
            this.draugr.setAttackName("frostbite"); // nom d’anim dans ton .geo.json
            this.draugr.setShooting(true);
            this.draugr.triggerAnim("attacking", this.draugr.getAttackName());
        }
        if (cd == SHOOT_START - 10)
            this.draugr.getWorld().playSound(null, this.draugr.getX(), this.draugr.getY(), this.draugr.getZ(),
                    ModSounds.DRAUGR_FROST_SPELL,
                    this.draugr.getSoundCategory(),
                    0.75F + this.draugr.getRandom().nextFloat() * 0.2F,
                    0.8F + this.draugr.getRandom().nextFloat() * 0.4F
            );
        // Fin du cycle : reset shooting + cooldown
        if (cd == 0) {
            this.draugr.setShooting(false);
            this.draugr.setCooldown(COOLDOWN_TICKS);
        }
    }

    private void moveBackwardFromTarget(LivingEntity target) {
        // vecteur du wight -> opposé à la cible
        Vec3d directionToTarget = this.draugr.getPos().subtract(target.getPos()).normalize();

        Vec3d backwardMovement = directionToTarget.multiply(0.065);
        this.draugr.setVelocity(
                backwardMovement.x,
                this.draugr.getVelocity().y,
                backwardMovement.z
        );

        // toujours regarder la cible
        this.draugr.lookAtEntity(target, 30.0F, 30.0F);
    }
}
