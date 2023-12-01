package net.mebahel.antiquebeasts.entity.ai;

import net.mebahel.antiquebeasts.entity.custom.HeroHopliteEntity;
import net.mebahel.antiquebeasts.entity.custom.HopliteEntity;
import net.mebahel.antiquebeasts.entity.projectiles.HopliteSpearEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.world.World;

import java.util.Objects;

public class HopliteShootingGoal extends Goal {
    private final HopliteEntity hoplite;

    public HopliteShootingGoal(HopliteEntity hoplite) {
        this.hoplite = hoplite;
    }

    public boolean canStart() {
        return this.hoplite.getTarget() != null;
    }

    public void start() {
        this.hoplite.setCooldown(80);
    }

    public void stop() {
        this.hoplite.setCooldown(80);
        this.hoplite.setShooting(false);
    }

    public boolean shouldRunEveryTick() {
        return true;
    }

    public void tick() {
        LivingEntity livingEntity = this.hoplite.getTarget();
        if (this.hoplite.isShooting()) {
            Objects.requireNonNull(this.hoplite.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(0);
        } else {
            Objects.requireNonNull(this.hoplite.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(0.72f);
        }
        System.out.print(this.hoplite.getCooldown() + " COOLDOWN");
        if (this.hoplite.distanceTo(livingEntity) > 6) {
            if (this.hoplite.canSee(livingEntity)) {
                World world = this.hoplite.getWorld();
                this.hoplite.setCooldown(Math.max(this.hoplite.getCooldown() - 1, 0));
                if (this.hoplite.getCooldown() == 7) {
                    ProjectileEntity hopliteSpearEntity;
                    hopliteSpearEntity = new HopliteSpearEntity(world, this.hoplite);

                    double offsetX = -0.7;
                    double offsetZ = -0.7;
                    double yaw = this.hoplite.getBodyYaw();
                    double radians = Math.toRadians(yaw);

                    double xProjectile = this.hoplite.getX() + Math.cos(radians) * offsetX;
                    double zProjectile = this.hoplite.getZ() + Math.sin(radians) * offsetZ;

                    double d = livingEntity.getEyeY() - 1.100000023841858;
                    double e = livingEntity.getX() - xProjectile;
                    double f = d - hopliteSpearEntity.getY();
                    double g = livingEntity.getZ() - zProjectile;

                    double h = Math.sqrt(e * e + g * g) * 0.20000000298023224;
                    float distance;
                    float speed;
                    if (this.hoplite.distanceTo(livingEntity) > 25) {
                        distance = 1f;
                        speed = 2f;
                    } else if (this.hoplite.distanceTo(livingEntity) >= 12 && this.hoplite.distanceTo(livingEntity) <= 17) {
                        distance = 0.85f;
                        speed = 2f;
                    } else {
                        distance = 0.65f;
                        speed = 2f;
                    }
                    hopliteSpearEntity.setVelocity(e, f + h * distance, g, speed, 1.5F);
                    hopliteSpearEntity.setPosition(xProjectile, this.hoplite.getBodyY(1.1), zProjectile);
                    world.spawnEntity(hopliteSpearEntity);
                } else if (this.hoplite.getCooldown() == 20) {
                    this.hoplite.setShooting(true);
                } else if (this.hoplite.getCooldown() == 0) {
                    this.hoplite.setCooldown(81);
                    this.hoplite.setShooting(false);
                } else if (this.hoplite.getCooldown() <= 80 && this.hoplite.getCooldown() > 20) {
                    this.hoplite.setShooting(false);
                }
            } else {
                this.hoplite.setShooting(false);
                this.hoplite.setCooldown(81);
            }
        }
    }
}

