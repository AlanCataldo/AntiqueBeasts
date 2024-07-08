package net.mebahel.antiquebeasts.entity.ai;

import net.mebahel.antiquebeasts.entity.custom.MummyEntity;
import net.mebahel.antiquebeasts.entity.projectiles.MummyProjectileEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.world.World;

public class MummyShootingGoal extends Goal {
    private final MummyEntity cyclops;
    private float speed;

    public MummyShootingGoal(MummyEntity cyclops, float speed) {
        this.cyclops = cyclops;
        this.speed = speed;
    }

    public boolean canStart() {
        return this.cyclops.getTarget() != null && this.cyclops.getSpawnCooldown() > 60;
    }

    public boolean shouldContinue() {
        return this.cyclops.getTarget() != null && this.cyclops.getSpawnCooldown() > 60;
    }

    public void start() {
        this.cyclops.setCooldown(61);
    }

    public void stop() {
        this.cyclops.setShooting(false);
        this.cyclops.setCooldown(121);
    }

    public boolean shouldRunEveryTick() {
        return true;
    }

    public void tick() {
        LivingEntity livingEntity = this.cyclops.getTarget();
        if (this.cyclops.distanceTo(livingEntity) > 2) {
            if (this.cyclops.canSee(livingEntity)) {
                World world = this.cyclops.getWorld();
                this.cyclops.setCooldown(Math.max(this.cyclops.getCooldown() - 1, 0));
                if (this.cyclops.getCooldown() == 8) {
                    ProjectileEntity throwingRockEntity;
                    throwingRockEntity = new MummyProjectileEntity(world, this.cyclops,7);

                    double xProjectile = this.cyclops.getX();
                    double zProjectile = this.cyclops.getZ();

                    double d = livingEntity.getEyeY() - 1.100000023841858;
                    double e = livingEntity.getX() - xProjectile;
                    double f = d - throwingRockEntity.getY();
                    double g = livingEntity.getZ() - zProjectile;

                    double h = Math.sqrt(e * e + g * g) * 0.20000000298023224;
                    float distance;
                    float speed;
                    if (this.cyclops.distanceTo(livingEntity) > 25) {
                        distance = 1.5f;
                        speed = 0.85f;
                    } else if (this.cyclops.distanceTo(livingEntity) >= 12 && this.cyclops.distanceTo(livingEntity) <= 17) {
                        distance = 1.1f;
                        speed = 0.7f;
                    } else {
                        distance = 1.1f;
                        speed = 0.7f;
                    }
                    throwingRockEntity.setVelocity(e, f + h * distance, g, speed, 1.5F);
                    throwingRockEntity.setPosition(xProjectile, this.cyclops.getBodyY(1), zProjectile);
                    world.spawnEntity(throwingRockEntity);
                } else if (this.cyclops.getCooldown() == 19) {
                    this.cyclops.setShooting(true);
                } else if (this.cyclops.getCooldown() == 0) {
                    this.cyclops.setCooldown(121);
                    this.cyclops.setShooting(false);
                }
                if (this.cyclops.getCooldown() <= 120 && this.cyclops.getCooldown() > 24) {
                    this.cyclops.setShooting(false);
                }
            } else {
                this.cyclops.setCooldown(121);
                this.cyclops.setShooting(false);
            }
        } else
            this.cyclops.setShooting(false);
    }
}