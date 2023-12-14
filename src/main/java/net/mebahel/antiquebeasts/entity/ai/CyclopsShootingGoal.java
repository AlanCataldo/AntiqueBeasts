package net.mebahel.antiquebeasts.entity.ai;

import net.mebahel.antiquebeasts.entity.custom.CyclopsEntity;
import net.mebahel.antiquebeasts.entity.projectiles.ThrowingRockEntity;
import net.mebahel.antiquebeasts.entity.projectiles.ThrowingSnowRockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.world.World;

import java.util.Objects;

public class CyclopsShootingGoal extends Goal {
    private final CyclopsEntity cyclops;
    private final String cyclopsProjectile;

    public CyclopsShootingGoal(CyclopsEntity cyclops, String cyclopsProjectile) {
        this.cyclops = cyclops;
        this.cyclopsProjectile = cyclopsProjectile;
    }

    public boolean canStart() {
        return this.cyclops.getTarget() != null;
    }

    public void start() {
        this.cyclops.setCooldown(101);
    }

    public void stop() {
        this.cyclops.setShooting(false);
        this.cyclops.setCooldown(101);
    }

    public boolean shouldRunEveryTick() {
        return true;
    }

    public void tick() {
        LivingEntity livingEntity = this.cyclops.getTarget();
        if (this.cyclops.isShooting()) {
            Objects.requireNonNull(this.cyclops.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(0);
        } else {
            Objects.requireNonNull(this.cyclops.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(0.72f);
        }
        if (this.cyclops.distanceTo(livingEntity) > 7) {
            if (this.cyclops.canSee(livingEntity)) {
                World world = this.cyclops.getWorld();
                this.cyclops.setCooldown(Math.max(this.cyclops.getCooldown() - 1, 0));
                if (this.cyclops.getCooldown() == 10) {
                    ProjectileEntity throwingRockEntity;
                    if (Objects.equals(this.cyclopsProjectile, "frost")) {
                        throwingRockEntity = new ThrowingSnowRockEntity(world, this.cyclops);
                    } else {
                        throwingRockEntity = new ThrowingRockEntity(world, this.cyclops);
                    }

                    double offsetX = 1.45;
                    double offsetZ = 1.45;
                    double yaw = this.cyclops.getBodyYaw();
                    double radians = Math.toRadians(yaw);

                    double xProjectile = this.cyclops.getX() + Math.cos(radians) * offsetX;
                    double zProjectile = this.cyclops.getZ() + Math.sin(radians) * offsetZ;

                    double d = livingEntity.getEyeY() - 1.100000023841858;
                    double e = livingEntity.getX() - xProjectile;
                    double f = d - throwingRockEntity.getY();
                    double g = livingEntity.getZ() - zProjectile;

                    double h = Math.sqrt(e * e + g * g) * 0.20000000298023224;
                    float distance;
                    float speed;
                    if (this.cyclops.distanceTo(livingEntity) > 25) {
                        distance = 1.5f;
                        speed = 1.25f;
                    } else if (this.cyclops.distanceTo(livingEntity) >= 12 && this.cyclops.distanceTo(livingEntity) <= 17) {
                        distance = 0.85f;
                        speed = 0.85f;
                    } else {
                        distance = 0.60f;
                        speed = 0.85f;
                    }
                    throwingRockEntity.setVelocity(e, f + h * distance, g, speed, 1.5F);
                    throwingRockEntity.setPosition(xProjectile, this.cyclops.getBodyY(1.1), zProjectile);
                    world.spawnEntity(throwingRockEntity);
                } else if (this.cyclops.getCooldown() == 24) {
                    this.cyclops.setShooting(true);
                } else if (this.cyclops.getCooldown() == 0) {
                    this.cyclops.setCooldown(101);
                    this.cyclops.setShooting(false);
                } else if (this.cyclops.getCooldown() <= 100 && this.cyclops.getCooldown() > 24) {
                    this.cyclops.setShooting(false);
                }
            } else {
                this.cyclops.setShooting(false);
                this.cyclops.setCooldown(101);
            }
        }
    }
}