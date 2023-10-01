package net.mebahel.antiquebeasts.entity.ai;

import net.mebahel.antiquebeasts.entity.custom.HeroHopliteEntity;
import net.mebahel.antiquebeasts.entity.projectiles.HopliteSpearEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Objects;

public class HopliteShootingGoal extends Goal {

    ProjectileEntity hopliteSpearEntity;
    private final HeroHopliteEntity hoplite;
    float sideX = 0;
    float sideZ= 0;

    public HopliteShootingGoal(HeroHopliteEntity hoplite) {
        this.hoplite = hoplite;
    }

    public boolean canStart() {
        return this.hoplite.getTarget() != null;
    }

    public void start() {
        this.hoplite.setCooldown(-80);
    }

    public void stop() {
        this.hoplite.setShooting(false);
    }

    public boolean shouldRunEveryTick() {
        return true;
    }

    public void tick() {
        LivingEntity livingEntity = this.hoplite.getTarget();
        if (this.hoplite.isSwinging()) {
            this.hoplite.setCooldown(-100);
        }
        if (this.hoplite.isShooting()) {
            Objects.requireNonNull(this.hoplite.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(0);
        } else {
            Objects.requireNonNull(this.hoplite.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(0.72f);
        }
        if (livingEntity != null && (this.hoplite.distanceTo(livingEntity) > 6 ||  this.hoplite.getCooldown() > 0)) {
            if (this.hoplite.canSee(livingEntity)) {
                World world = this.hoplite.world;
                this.hoplite.setCooldown(this.hoplite.getCooldown() + 1);
                if (this.hoplite.getCooldown() == 17) {
                    hopliteSpearEntity = new HopliteSpearEntity(world, this.hoplite);

                    Vec3d vec3d = this.hoplite.getRotationVec(1.0F);

                    double d = Objects.requireNonNull(this.hoplite.getTarget()).getEyeY() - 1.100000023841858;
                    double e = this.hoplite.getTarget().getX() - this.hoplite.getX();

                    double f = d - hopliteSpearEntity.getY();
                    double g = this.hoplite.getTarget().getZ() - this.hoplite.getZ();
                    double h = Math.sqrt(e * e + g * g) * 0.20000000298023224;
                    float distance;
                    float speed;

                    if (this.hoplite.distanceTo(livingEntity) > 25) {
                        distance = 2.5f;
                        speed = 0.85f;
                    } else  if (this.hoplite.distanceTo(livingEntity) >= 12 && this.hoplite.distanceTo(livingEntity) <= 17){
                        distance = 0.50f;
                        speed = 0.85f;
                    } else {
                        distance = 0.40f;
                        speed = 0.80f;
                    }

                    hopliteSpearEntity.setVelocity(e - sideX, f + h * distance, g + sideZ, speed, 1.5F);
                    hopliteSpearEntity.setPosition(this.hoplite.getX() + vec3d.x + sideX, this.hoplite.getBodyY(1.1), hopliteSpearEntity.getZ() + vec3d.z - sideZ);
                    world.spawnEntity(hopliteSpearEntity);
                } else if (this.hoplite.getCooldown() == 20) {
                    this.hoplite.setCooldown(-100);
                }
            } else if (this.hoplite.getCooldown() > 0) {
                this.hoplite.setCooldown(this.hoplite.getCooldown() - 1);
            }
            if (this.hoplite.getCooldown() < 0) {
                this.hoplite.setShooting(false);
            } else if (this.hoplite.getCooldown() > 0) {
                this.hoplite.setShooting(true);
            }
        }
    }
}

