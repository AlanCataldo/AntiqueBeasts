package net.mebahel.antiquebeasts.entity.ai;

import net.mebahel.antiquebeasts.entity.custom.ChimeraEntity;
import net.mebahel.antiquebeasts.entity.projectiles.ChimeraProjectileEntity;
import net.mebahel.antiquebeasts.entity.projectiles.VenomEntity;
import net.mebahel.antiquebeasts.entity.projectiles.MummyProjectileEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Random;

public class ChimeraFlameThrowerGoal extends Goal {
    private final ChimeraEntity chimera;
    private static final int MAX_COOLDOWN = 161;

    public ChimeraFlameThrowerGoal(ChimeraEntity chimera) {
        this.chimera = chimera;
    }

    @Override
    public boolean canStart() {
        return this.chimera.getTarget() != null;
    }

    @Override
    public boolean shouldContinue() {
        return this.chimera.getTarget() != null;
    }

    @Override
    public void start() {
        this.chimera.setFireBreathingCooldown(MAX_COOLDOWN);
    }

    @Override
    public void stop() {
        this.chimera.setFireBreathing(false);
        this.chimera.setFireBreathingCooldown(MAX_COOLDOWN);
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    private void chooseRandomAttack() {
        Random random = new Random();
        int attackChoice = random.nextInt(3);
        if (attackChoice == 0) {
            this.chimera.setAttackName("goat_shoot");
        } else if (attackChoice == 1) {
            this.chimera.setAttackName("snake_shoot");
        } else {
            this.chimera.setAttackName("lion_shoot");
        }
    }

    private void launchProjectile(World world, LivingEntity target, ProjectileEntity projectile, double xOffset, double zOffset, double yHeight, float distanceMultiplier, float speedMultiplier) {
        double xProjectile = this.chimera.getX() + xOffset;
        double zProjectile = this.chimera.getZ() + zOffset;
        double yProjectile = this.chimera.getBodyY(0.8) + yHeight;

        double targetY = target.getEyeY() - 1.100000023841858;
        double dX = target.getX() - xProjectile;
        double dY = targetY - yProjectile;
        double dZ = target.getZ() - zProjectile;

        double distanceFactor = Math.sqrt(dX * dX + dZ * dZ) * 0.20000000298023224;

        projectile.setVelocity(dX, dY + distanceFactor * distanceMultiplier, dZ, speedMultiplier, 1F);
        projectile.setPosition(xProjectile, yProjectile, zProjectile);
        world.spawnEntity(projectile);
    }

    @Override
    public void tick() {
        LivingEntity target = this.chimera.getTarget();
        if (target != null && this.chimera.canSee(target)) {
            World world = this.chimera.getWorld();
            this.chimera.setFireBreathingCooldown(Math.max(this.chimera.getFireBreathingCooldown() - 1, 0));

            if (this.chimera.getFireBreathingCooldown() == MAX_COOLDOWN - 1) {
                chooseRandomAttack();
            }

            if (this.chimera.getFireBreathingCooldown() == 14 || this.chimera.getFireBreathingCooldown() == 10 || this.chimera.getFireBreathingCooldown() == 6) {
                Vec3d lookVec = this.chimera.getRotationVec(1.0F).normalize().multiply(2);
                String attackName = this.chimera.getAttackName();
                ProjectileEntity projectile = null;

                switch (attackName) {
                    case "lion_shoot" -> {
                        projectile = new ChimeraProjectileEntity(world, this.chimera, 7);
                        launchProjectile(world, target, projectile, lookVec.x, lookVec.z, 0, getDistanceMultiplier(), getSpeedMultiplier());
                    }
                    case "snake_shoot" -> {
                        if (this.chimera.getFireBreathingCooldown() == 10) {
                            projectile = new VenomEntity(world, this.chimera, 6f);
                            launchProjectile(world, target, projectile, 0, 0, 1, getDistanceMultiplier(), getSpeedMultiplier());
                        }
                    }
                    case "goat_shoot" -> {
                        if (this.chimera.getFireBreathingCooldown() == 10) {
                            projectile = new MummyProjectileEntity(world, this.chimera, 6f);
                            launchProjectile(world, target, projectile, 1, 0, 1, getDistanceMultiplier(), getSpeedMultiplier());
                        }
                    }
                }
            }
            manageFireBreathing();
        } else {
            resetFireBreathing();
        }
    }

    private float getDistanceMultiplier() {
        float distance = this.chimera.distanceTo(this.chimera.getTarget());
        if (distance > 25) {
            return 1.5f;
        } else if (distance >= 12 && distance <= 17) {
            return 1.3f;
        } else {
            return 1f;
        }
    }

    private float getSpeedMultiplier() {
        float distance = this.chimera.distanceTo(this.chimera.getTarget());
        if (distance > 25) {
            return 0.8f;
        } else if (distance >= 12 && distance <= 17) {
            return 0.7f;
        } else {
            return 0.6f;
        }
    }

    private void manageFireBreathing() {
        if (this.chimera.getFireBreathingCooldown() == 20) {
            this.chimera.setFireBreathing(true);
        } else if (this.chimera.getFireBreathingCooldown() == 0) {
            resetFireBreathing();
        } else if (this.chimera.getFireBreathingCooldown() < MAX_COOLDOWN && this.chimera.getFireBreathingCooldown() > 20) {
            this.chimera.setFireBreathing(false);
        }
    }

    private void resetFireBreathing() {
        this.chimera.setFireBreathingCooldown(MAX_COOLDOWN);
        this.chimera.setFireBreathing(false);
    }
}
