package net.mebahel.antiquebeasts.entity.ai;

import net.mebahel.antiquebeasts.entity.custom.norse.ThrowingAxeManEntity;
import net.mebahel.antiquebeasts.entity.projectiles.ThrowingAxeEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.world.World;

public class ThrowingAxeManShootingGoal extends Goal {
    private final ThrowingAxeManEntity actor;
    private final float squaredRange;
    private int targetSeeingTicker;
    private boolean movingToLeft;
    private boolean backward;
    private int combatTicks = -1;
    public ThrowingAxeManShootingGoal(ThrowingAxeManEntity actor, float squaredRange) {
        this.actor = actor;
        this.squaredRange = squaredRange;
    }
    public boolean canStart() {
        return this.actor.getTarget() != null;
    }
    public void start() {
        this.actor.setCooldown(61);
    }

    public void stop() {
        this.actor.setCooldown(61);
        this.actor.setShooting(false);
    }
    public boolean shouldRunEveryTick() {
        return true;
    }
    public void tick() {
        LivingEntity livingEntity = this.actor.getTarget();
        if (livingEntity != null) {
            double d = this.actor.squaredDistanceTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
            boolean bl = this.actor.getVisibilityCache().canSee(livingEntity);
            boolean bl2 = this.targetSeeingTicker > 0;
            if (bl != bl2) {
                this.targetSeeingTicker = 0;
            }
            if (bl) {
                ++this.targetSeeingTicker;
            } else {
                --this.targetSeeingTicker;
            }
            if (!(d > (double)this.squaredRange) && this.targetSeeingTicker >= 20) {
                this.actor.getNavigation().stop();
                ++this.combatTicks;
            } else {
                this.actor.getNavigation().startMovingTo(livingEntity, 0.4f);
                this.combatTicks = -1;
            }
            if (this.combatTicks >= 20) {
                if ((double)this.actor.getRandom().nextFloat() < 0.3) {
                    this.movingToLeft = !this.movingToLeft;
                }
                if ((double)this.actor.getRandom().nextFloat() < 0.3) {
                    this.backward = !this.backward;
                }
                this.combatTicks = 0;
            }
            if (this.combatTicks > -1) {
                if (this.actor.horizontalCollision && this.actor.isOnGround()) {
                    this.backward = false;
                } else if (d > (double)(this.squaredRange * 0.75F)) {
                    this.backward = false;
                } else if (d < (double)(this.squaredRange * 0.25F)) {
                    this.backward = true;
                }
                this.actor.getMoveControl().strafeTo(this.backward ? -0.4F : 0.4F, this.movingToLeft ? 0.4F : -0.4F);
            }

            if (this.actor.getVisibilityCache().canSee(livingEntity)) {
                World world = this.actor.getWorld();
                this.actor.setCooldown(Math.max(this.actor.getCooldown() - 1, 0));
                if (this.actor.getCooldown() == 9) {
                    ProjectileEntity throwingAxeEntity;
                    throwingAxeEntity = new ThrowingAxeEntity(world, this.actor, 6f);

                    double offsetX = -0.7;
                    double offsetZ = -0.7;
                    double yaw = this.actor.getBodyYaw();
                    double radians = Math.toRadians(yaw);

                    double xProjectile = this.actor.getX() + Math.cos(radians) * offsetX;
                    double zProjectile = this.actor.getZ() + Math.sin(radians) * offsetZ;

                    double a = livingEntity.getEyeY() - 1.100000023841858;
                    double e = livingEntity.getX() - xProjectile;
                    double f = a - throwingAxeEntity.getY();
                    double g = livingEntity.getZ() - zProjectile;

                    double h = Math.sqrt(e * e + g * g) * 0.20000000298023224;
                    float distance;
                    float speed;
                    if (this.actor.distanceTo(livingEntity) > 25) {
                        distance = 1f;
                        speed = 0.85f;
                    } else if (this.actor.distanceTo(livingEntity) >= 12 && this.actor.distanceTo(livingEntity) <= 17) {
                        distance = 0.75f;
                        speed = 0.90f;
                    } else {
                        distance = 0.55f;
                        speed = 0.80f;
                    }
                    throwingAxeEntity.setVelocity(e, f + h * distance, g, speed, 1.5F);
                    throwingAxeEntity.setPosition(xProjectile, this.actor.getBodyY(1), zProjectile);
                    world.spawnEntity(throwingAxeEntity);
                } else if (this.actor.getCooldown() == 15) {
                    this.actor.setShooting(true);
                } else if (this.actor.getCooldown() == 0) {
                    this.actor.setCooldown(61);
                    this.actor.setShooting(false);
                } else if (this.actor.getCooldown() > 15 && this.actor.getCooldown() < 61) {
                    this.actor.setShooting(false);
                }
            } else {
                this.actor.setShooting(false);
                this.actor.setCooldown(61);
            }
        }
    }
}
