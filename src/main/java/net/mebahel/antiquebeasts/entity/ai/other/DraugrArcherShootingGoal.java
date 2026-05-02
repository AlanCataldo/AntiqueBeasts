package net.mebahel.antiquebeasts.entity.ai.other;


import net.mebahel.antiquebeasts.entity.custom.other.DraugrArcherEntity;
import net.mebahel.antiquebeasts.util.entity.MovementUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.world.World;

public class DraugrArcherShootingGoal extends Goal {
    private final DraugrArcherEntity actor;
    private final MovementUtil movementUtil;

    private static final int CYCLE_COOLDOWN = 71;
    private static final int ANIM_TICK = 25;
    private static final int SHOOT_TICK = 9;

    private static final int STRAFE_DISTANCE = 7;

    private static final double PROJECTILE_FORWARD_OFFSET = 1.05;
    private static final double PROJECTILE_LEFT_OFFSET = 0.35;
    private static final double PROJECTILE_FORWARD_BONUS = 0.15;

    public DraugrArcherShootingGoal(DraugrArcherEntity actor) {
        this.actor = actor;
        this.movementUtil = new MovementUtil(this.actor, STRAFE_DISTANCE);
        this.movementUtil.setTolerance(3);
    }

    @Override
    public boolean canStart() {
        LivingEntity t = this.actor.getTarget();
        if (t instanceof PlayerEntity p && (p.isCreative() || p.isSpectator())) return false;
        return t != null && t.isAlive();
    }

    @Override
    public boolean shouldContinue() {
        LivingEntity t = this.actor.getTarget();
        if (t instanceof PlayerEntity p && (p.isCreative() || p.isSpectator())) return false;
        return t != null && t.isAlive();
    }

    @Override
    public void start() {
        this.actor.setCooldown(CYCLE_COOLDOWN);
        this.actor.setShooting(false);

        // ✅ ACTIVE LOCK YAW
        this.actor.setLockYawToTarget(true);

        // propre
        this.actor.getNavigation().stop();
        this.actor.getMoveControl().strafeTo(0, 0);
    }

    @Override
    public void stop() {
        this.actor.setCooldown(CYCLE_COOLDOWN);
        this.actor.setShooting(false);

        // ✅ DISABLE LOCK YAW
        this.actor.setLockYawToTarget(false);

        this.actor.getMoveControl().strafeTo(0, 0);
        this.actor.getNavigation().stop();
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        // ✅ garde le lock tant que le goal tourne
        this.actor.setLockYawToTarget(true);

        if (actor.isUsingPotion() || actor.getHealTicks() > 0) {
            actor.setShooting(false);
            actor.getNavigation().stop();
            actor.getMoveControl().strafeTo(0, 0);
            return;
        }

        LivingEntity target = this.actor.getTarget();
        if (target == null || !target.isAlive()) {
            this.stop();
            return;
        }

        if (!this.actor.getVisibilityCache().canSee(target)) {
            this.actor.setShooting(false);
            this.actor.setCooldown(CYCLE_COOLDOWN);
            this.actor.getNavigation().stop();
            this.actor.getMoveControl().strafeTo(0, 0);
            return;
        }

        this.actor.setCooldown(Math.max(this.actor.getCooldown() - 1, 0));
        int cd = (int) this.actor.getCooldown();

        boolean rootNow = (cd <= ANIM_TICK && cd >= SHOOT_TICK);

        if (rootNow) {
            this.actor.setShooting(true);
            this.actor.getNavigation().stop();
            this.actor.getMoveControl().strafeTo(0, 0);
            this.actor.setVelocity(0.0D, this.actor.getVelocity().y, 0.0D);
            this.actor.velocityDirty = true;
        } else {
            this.actor.setShooting(false);
            this.movementUtil.maintainRangedPosition(target);
        }

        World world = this.actor.getWorld();

        if (cd == ANIM_TICK) {
            this.actor.setAttackName("shoot");
            this.actor.triggerAnim("attacking", this.actor.getAttackName());
        }

        if (cd == SHOOT_TICK) {
            ArrowEntity arrow = new ArrowEntity(world, this.actor);

            double yawRad = Math.toRadians(this.actor.getBodyYaw());

            double forwardX = -Math.sin(yawRad);
            double forwardZ =  Math.cos(yawRad);

            double rightX =  forwardZ;
            double rightZ = -forwardX;

            double leftX = -rightX;
            double leftZ = -rightZ;

            double forwardAmount = (PROJECTILE_FORWARD_OFFSET + PROJECTILE_FORWARD_BONUS);

            double xProjectile = this.actor.getX()
                    + forwardX * forwardAmount
                    + leftX * PROJECTILE_LEFT_OFFSET;

            double zProjectile = this.actor.getZ()
                    + forwardZ * forwardAmount
                    + leftZ * PROJECTILE_LEFT_OFFSET;

            double yProjectile = this.actor.getBodyY(0.8);

            double a = target.getEyeY() - 1.100000023841858;
            double e = target.getX() - xProjectile;
            double f = a - yProjectile;
            double g = target.getZ() - zProjectile;

            double h = Math.sqrt(e * e + g * g) * 0.20000000298023224;

            float distance;
            float speed;

            double dist = this.actor.distanceTo(target);
            if (dist > 25) {
                distance = 0.85f;
                speed = 2.1f;
            } else if (dist >= 12 && dist <= 17) {
                distance = 0.8f;
                speed = 2.05f;
            } else {
                distance = 0.75f;
                speed = 2f;
            }

            arrow.setVelocity(e, f + h * distance, g, speed, 0.0F);
            arrow.setDamage(arrow.getDamage() + 0);
            arrow.setPosition(xProjectile, yProjectile, zProjectile);
            arrow.addEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 60, 0, false, false));
            world.spawnEntity(arrow);
        }

        if (cd == 0) {
            this.actor.setCooldown(CYCLE_COOLDOWN);
            this.actor.setShooting(false);
        }
    }
}