package net.mebahel.antiquebeasts.entity.ai.mummy_boss;

import net.mebahel.antiquebeasts.entity.custom.egyptian.MummyBossEntity;
import net.mebahel.antiquebeasts.entity.custom.egyptian.MummyEntity;
import net.mebahel.antiquebeasts.entity.projectiles.MummyProjectileEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.world.World;

public class MummyBossShootingGoal extends Goal {
    private final MummyBossEntity cyclops;
    private float speed;
    private long lastUpdateTime;

    public MummyBossShootingGoal(MummyBossEntity cyclops, float speed) {
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
        return (livingEntity != null && livingEntity.isAlive() && this.cyclops.getSpawnCooldown() > 60)
                || !this.cyclops.inTransitionPhase;
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
        LivingEntity target = this.cyclops.getTarget();
        if (target == null || !target.isAlive()) {
            this.stop();
            return;
        }
        if (this.cyclops.canSee(target)) {
            this.cyclops.setCooldown(Math.max(this.cyclops.getCooldown() - 1, 0));

            if (this.cyclops.getCooldown() == 8) {
                if (this.cyclops.secondPhase || this.cyclops.thirdPhase) {
                    shootProjectilesInArc(target, 5);
                } else {
                    shootProjectilesInArc(target, 3);
                }
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
    }
    private void shootProjectilesInArc(LivingEntity target, int projectileCount) {
        World world = this.cyclops.getWorld();

        if (world == null || target == null) return; // On vérifie que world et target ne sont pas null

        double xProjectile = this.cyclops.getX();
        double zProjectile = this.cyclops.getZ();
        double mummyY = this.cyclops.getBodyY(0.5);

        double dirX = target.getX() - xProjectile;
        double dirZ = target.getZ() - zProjectile;
        double distanceToTarget = Math.sqrt(dirX * dirX + dirZ * dirZ);

        if (distanceToTarget == 0) return; // Éviter de diviser par zéro

        dirX /= distanceToTarget;
        dirZ /= distanceToTarget;

        double[] offsets = {0, 5, -5, 10, -10};

        for (int i = 0; i < projectileCount; i++) {
            ProjectileEntity projectile = new MummyProjectileEntity(world, this.cyclops, 7);
            double lateralOffsetX = -dirZ * offsets[i];
            double lateralOffsetZ = dirX * offsets[i];
            double launchX = xProjectile + lateralOffsetX;
            double launchZ = zProjectile + lateralOffsetZ;

            projectile.setPosition(launchX, mummyY, launchZ);
            world.spawnEntity(projectile);
        }
    }
    protected double getSquaredMaxAttackDistance(LivingEntity entity) {
        return 8;
    }
}