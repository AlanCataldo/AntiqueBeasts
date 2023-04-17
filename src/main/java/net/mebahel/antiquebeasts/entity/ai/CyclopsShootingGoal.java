package net.mebahel.antiquebeasts.entity.ai;

import net.mebahel.antiquebeasts.entity.custom.CyclopsEntity;
import net.mebahel.antiquebeasts.entity.projectiles.ThrowingRockEntity;
import net.mebahel.antiquebeasts.entity.projectiles.ThrowingSnowRockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Objects;

public class CyclopsShootingGoal extends Goal {

    ProjectileEntity throwingRockEntity;
    private final CyclopsEntity cyclops;
    private final String cyclopsProjectile;
    float sideX = 0;
    float sideZ= 0;

    public CyclopsShootingGoal(CyclopsEntity cyclops, String cyclopsProjectile) {
        this.cyclops = cyclops;
        this.cyclopsProjectile = cyclopsProjectile;
    }

    public boolean canStart() {
        return this.cyclops.getTarget() != null;
    }

    public void start() {
        this.cyclops.setCooldown(-100);
    }

    public void stop() {
        this.cyclops.setShooting(false);
    }

    public boolean shouldRunEveryTick() {
        return true;
    }

    public void tick() {
        LivingEntity livingEntity = this.cyclops.getTarget();
        if (this.cyclops.isSwinging()) {
            this.cyclops.setCooldown(-100);
        }
        if (this.cyclops.isShooting()) {
            Objects.requireNonNull(this.cyclops.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(0);
        } else {
            Objects.requireNonNull(this.cyclops.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(0.72f);
        }
        if (livingEntity != null && (this.cyclops.distanceTo(livingEntity) > 7 ||  this.cyclops.getCooldown() > 0)) {
            if (this.cyclops.canSee(livingEntity)) {
                World world = this.cyclops.world;
                this.cyclops.setCooldown(this.cyclops.getCooldown() + 1);
                if (this.cyclops.getCooldown() == 17) {
                    if (Objects.equals(this.cyclopsProjectile, "frost")) {
                        throwingRockEntity = new ThrowingSnowRockEntity(world, this.cyclops);
                    } else {
                        throwingRockEntity = new ThrowingRockEntity(world, this.cyclops);
                    }

                    Vec3d vec3d = this.cyclops.getRotationVec(1.0F);

                    if (vec3d.z > 0.20f || vec3d.z < -0.20f) {
                        sideX = -2f;
                    } else if (vec3d.z > 0) {
                        sideX = -sideX;
                    }
                    if (vec3d.x > 0.20f || vec3d.x < -0.20f) {
                        sideZ = -2f;
                    } else if (vec3d.x > 0) {
                        sideZ = -sideZ;
                    }

                    double d = Objects.requireNonNull(this.cyclops.getTarget()).getEyeY() - 1.100000023841858;
                    double e = this.cyclops.getTarget().getX() - this.cyclops.getX();

                    double f = d - throwingRockEntity.getY();
                    double g = this.cyclops.getTarget().getZ() - this.cyclops.getZ();
                    double h = Math.sqrt(e * e + g * g) * 0.20000000298023224;
                    float distance;
                    float speed;

                    if (this.cyclops.distanceTo(livingEntity) > 25) {
                        distance = 2.5f;
                        speed = 0.85f;
                    } else  if (this.cyclops.distanceTo(livingEntity) >= 12 && this.cyclops.distanceTo(livingEntity) <= 17){
                        distance = 0.50f;
                        speed = 0.85f;
                    } else {
                        distance = 0.40f;
                        speed = 0.80f;
                    }

                    throwingRockEntity.setVelocity(e - sideX, f + h * distance, g + sideZ, speed, 1.5F);
                    throwingRockEntity.setPosition(this.cyclops.getX() + vec3d.x + sideX, this.cyclops.getBodyY(1.1), throwingRockEntity.getZ() + vec3d.z - sideZ);
                    world.spawnEntity(throwingRockEntity);
                } else if (this.cyclops.getCooldown() == 25) {
                    this.cyclops.setCooldown(-100);
                }
            } else if (this.cyclops.getCooldown() > 0) {
                this.cyclops.setCooldown(this.cyclops.getCooldown() - 1);
            }
            if (this.cyclops.getCooldown() < -30) {
                this.cyclops.setShooting(false);
            } else if (this.cyclops.getCooldown() > 0) {
                this.cyclops.setShooting(true);
            }
        }
    }
}

