package net.mebahel.antiquebeasts.entity.ai;

import net.mebahel.antiquebeasts.entity.custom.egyptian.MummyEntity;
import net.mebahel.antiquebeasts.entity.projectiles.MummyProjectileEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.world.World;

public class MummyShootingGoal extends Goal {
    private final MummyEntity cyclops;
    private float speed;
    private long lastUpdateTime;

    public MummyShootingGoal(MummyEntity cyclops, float speed) {
        this.cyclops = cyclops;
        this.speed = speed;
    }


    public boolean canStart() {
        return this.cyclops.getTarget() != null && this.cyclops.getSpawnCooldown() > 60;
    }

    public boolean shouldContinue() {
        LivingEntity livingEntity = this.cyclops.getTarget();

        if (livingEntity instanceof PlayerEntity playerEntity) {
            if (playerEntity.isCreative() || playerEntity.isSpectator()) {
                return false;
            }
        }
        return livingEntity != null && this.cyclops.getSpawnCooldown() > 60 && livingEntity.isAlive();
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
        if (livingEntity == null || !livingEntity.isAlive()) {
            this.stop();
            return;
        }
        if (this.cyclops.distanceTo(livingEntity) > 2) {
            if (this.cyclops.canSee(livingEntity)) {
                World world = this.cyclops.getWorld();
                this.cyclops.setCooldown(Math.max(this.cyclops.getCooldown() - 1, 0));
                if (this.cyclops.getCooldown() == 6) {
                    ProjectileEntity throwingRockEntity;
                    throwingRockEntity = new MummyProjectileEntity(world, this.cyclops,7);

                    double xProjectile = this.cyclops.getX();
                    double zProjectile = this.cyclops.getZ();

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

    protected double getSquaredMaxAttackDistance(LivingEntity entity) {
        return 8;
    }
}