package net.mebahel.antiquebeasts.entity.ai.other;

import net.mebahel.antiquebeasts.entity.custom.other.DraugrArcherEntity;
import net.mebahel.antiquebeasts.util.ProjectileDataAccessor;
import net.mebahel.antiquebeasts.util.entity.MovementUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.world.World;

public class DraugrArcherShootingGoal extends Goal {
    private final DraugrArcherEntity actor;
    private final MovementUtil movementUtil;
    private final double STRAFE_DISTANCE = 8;

    public DraugrArcherShootingGoal(DraugrArcherEntity actor) {
        this.actor = actor;
        this.movementUtil = new MovementUtil(this.actor);
    }

    @Override
    public boolean canStart() {
        LivingEntity livingEntity = this.actor.getTarget();

        if (livingEntity instanceof PlayerEntity playerEntity) {
            if (playerEntity.isCreative() || playerEntity.isSpectator()) {
                return false;
            }
        }
        return livingEntity != null && livingEntity.isAlive();
    }

    @Override
    public void start() {
        this.actor.setCooldown(61);
        this.actor.setShooting(false);
    }

    @Override
    public void stop() {
        this.actor.setCooldown(61);
        this.actor.setShooting(false);
        this.actor.getMoveControl().strafeTo(0, 0);
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public boolean shouldContinue() {
        LivingEntity livingEntity = this.actor.getTarget();

        if (livingEntity instanceof PlayerEntity playerEntity) {
            if (playerEntity.isCreative() || playerEntity.isSpectator()) {
                return false;
            }
        }
        return livingEntity != null && livingEntity.isAlive();
    }

    @Override
    public void tick() {
        if (actor.isUsingPotion() || actor.getHealTicks() > 0) {
            actor.setShooting(false);
            actor.getNavigation().stop();
            return;
        }

        LivingEntity target = this.actor.getTarget();
        if (target == null || !target.isAlive()) {
            this.stop();
            return;
        }

        double distanceToTarget = this.actor.distanceTo(target);
        this.movementUtil.lookAtTarget(target, this.actor);
        this.movementUtil.checkIfStuck(target, this.actor);

        if (!this.movementUtil.isSkyVisibleAbove(this.actor)) {
            this.movementUtil.strafeUnderground(target, this.actor);
        } else if (distanceToTarget <= STRAFE_DISTANCE) {
            this.movementUtil.moveBackward(target, this.actor);
        } else {
            this.movementUtil.strafeAroundTarget(target, this.actor);
        }

        if (this.actor.getVisibilityCache().canSee(target)) {
            World world = this.actor.getWorld();
            this.actor.setCooldown(Math.max(this.actor.getCooldown() - 1, 0));

            // 🔥 Moment où on joue l’anim de tir
            if (this.actor.getCooldown() == 25) {
                this.actor.setShooting(true);

                // 👉 triggerAnim "shoot"
                this.actor.setAttackName("shoot");
                this.actor.triggerAnim("attacking", this.actor.getAttackName());
            }

            // Moment où le projectile part
            if (this.actor.getCooldown() == 9) {
                ArrowEntity throwingAxeEntity = new ArrowEntity(world, this.actor);

                double offsetX = -0.7;
                double offsetZ = -0.7;
                double yaw = this.actor.getBodyYaw();
                double radians = Math.toRadians(yaw);

                double xProjectile = this.actor.getX() + Math.cos(radians) * offsetX;
                double zProjectile = this.actor.getZ() + Math.sin(radians) * offsetZ;

                double a = target.getEyeY() - 1.100000023841858;
                double e = target.getX() - xProjectile;
                double f = a - throwingAxeEntity.getY();
                double g = target.getZ() - zProjectile;

                double h = Math.sqrt(e * e + g * g) * 0.20000000298023224;
                float distance;
                float speed;

                if (this.actor.distanceTo(target) > 25) {
                    distance = 0.85f;
                    speed = 2.1f;
                } else if (this.actor.distanceTo(target) >= 12 && this.actor.distanceTo(target) <= 17) {
                    distance = 0.8f;
                    speed = 2.05f;
                } else {
                    distance = 0.75f;
                    speed = 2f;
                }

                throwingAxeEntity.setVelocity(e, f + h * distance, g, speed, 0.0F);
                throwingAxeEntity.setPosition(xProjectile, this.actor.getBodyY(1), zProjectile);
                throwingAxeEntity.addEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 60, 0, false, false));
                world.spawnEntity(throwingAxeEntity);

            } else if (this.actor.getCooldown() == 0) {
                // reset cycle
                this.actor.setCooldown(61);
                this.actor.setShooting(false);

            } else if (this.actor.getCooldown() > 25 && this.actor.getCooldown() < 61) {
                this.actor.setShooting(false);
            }

        } else {
            this.actor.setShooting(false);
            this.actor.setCooldown(61);
        }
    }
}
