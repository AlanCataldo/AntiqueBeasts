package net.mebahel.antiquebeasts.entity.ai;

import net.mebahel.antiquebeasts.entity.custom.CyclopsEntity;
import net.mebahel.antiquebeasts.entity.projectiles.ThrowingRockEntity;
import net.mebahel.antiquebeasts.entity.projectiles.ThrowingSnowRockEntity;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Objects;

public class CyclopsShootingGoal extends Goal {
    private final CyclopsEntity cyclops;
    private final String cyclopsProjectile;

    private static final double MIN_SHOOT_DISTANCE = 8.0D;

    private static final int CYCLE_TICKS = 100;
    private static final int ANIM_TICKS = 25;

    // Animation commence quand cd arrive à 25.
    private static final int ANIM_START_CD = ANIM_TICKS;

    // Tir à 0.83s après début anim : 0.83 * 20 = ~17 ticks.
    // Anim timeline : cd 25 -> 0, donc 25 - 17 = 8.
    private static final int SHOOT_CD = 8;

    private static final double BASE_MOVEMENT_SPEED = 0.72D;

    private static final double FORWARD_OFFSET = 1.15D;
    private static final double SIDE_OFFSET = 0.85D;
    private static final double Y_OFFSET = 2.5D;

    public CyclopsShootingGoal(CyclopsEntity cyclops, String cyclopsProjectile) {
        this.cyclops = cyclops;
        this.cyclopsProjectile = cyclopsProjectile;
    }

    @Override
    public boolean canStart() {
        LivingEntity target = this.cyclops.getTarget();

        if (target instanceof PlayerEntity playerEntity) {
            if (playerEntity.isCreative() || playerEntity.isSpectator()) return false;
        }

        return target != null && target.isAlive();
    }

    @Override
    public void start() {
        this.cyclops.setCooldown(CYCLE_TICKS);
        this.cyclops.setShooting(false);
        restoreMovement();
    }

    @Override
    public boolean shouldContinue() {
        LivingEntity target = this.cyclops.getTarget();

        if (target instanceof PlayerEntity playerEntity) {
            if (playerEntity.isCreative() || playerEntity.isSpectator()) return false;
        }

        return target != null && target.isAlive();
    }

    @Override
    public void stop() {
        this.cyclops.setCooldown(CYCLE_TICKS);
        this.cyclops.setShooting(false);
        restoreMovement();
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    private void stopMovement() {
        Objects.requireNonNull(this.cyclops.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED))
                .setBaseValue(0.0D);

        this.cyclops.getNavigation().stop();

        Vec3d velocity = this.cyclops.getVelocity();
        this.cyclops.setVelocity(0.0D, velocity.y, 0.0D);
        this.cyclops.velocityDirty = true;
    }

    private void restoreMovement() {
        Objects.requireNonNull(this.cyclops.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED))
                .setBaseValue(BASE_MOVEMENT_SPEED);
    }

    @Override
    public void tick() {
        LivingEntity target = this.cyclops.getTarget();

        if (target == null || !target.isAlive()) {
            this.stop();
            return;
        }

        if (this.cyclops.distanceTo(target) <= MIN_SHOOT_DISTANCE || !this.cyclops.canSee(target)) {
            this.cyclops.setCooldown(CYCLE_TICKS);
            this.cyclops.setShooting(false);
            restoreMovement();
            return;
        }

        World world = this.cyclops.getWorld();

        int cd = (int) Math.max(this.cyclops.getCooldown() - 1, 0);
        this.cyclops.setCooldown(cd);

        boolean inAnimationWindow = cd <= ANIM_TICKS && cd > 0;

        if (cd == ANIM_START_CD) {
            this.cyclops.setShooting(true);
            this.cyclops.triggerAnim("shooting", "ranged_attack");
        }

        if (inAnimationWindow) {
            this.cyclops.setShooting(true);
            stopMovement();
            this.cyclops.getLookControl().lookAt(target, 30.0F, 30.0F);
        } else {
            this.cyclops.setShooting(false);
            restoreMovement();
        }

        if (cd == SHOOT_CD) {
            world.playSound(
                    null,
                    this.cyclops.getX(),
                    this.cyclops.getY(),
                    this.cyclops.getZ(),
                    ModSounds.SWING,
                    this.cyclops.getSoundCategory(),
                    0.5F,
                    0.6F
            );

            shootRock(world, target);
        }

        if (cd <= 0) {
            this.cyclops.setCooldown(CYCLE_TICKS);
            this.cyclops.setShooting(false);
            restoreMovement();
        }
    }

    private void shootRock(World world, LivingEntity target) {
        ProjectileEntity rock;

        if (Objects.equals(this.cyclopsProjectile, "frost")) {
            rock = new ThrowingSnowRockEntity(world, this.cyclops);
        } else {
            rock = new ThrowingRockEntity(world, this.cyclops);
        }

        float yawRad = (float) Math.toRadians(this.cyclops.getBodyYaw());

        double forwardX = -MathHelper.sin(yawRad);
        double forwardZ = MathHelper.cos(yawRad);

        double sideX = MathHelper.cos(yawRad);
        double sideZ = MathHelper.sin(yawRad);

        double xProjectile = this.cyclops.getX() + forwardX * FORWARD_OFFSET + sideX * SIDE_OFFSET;
        double yProjectile = this.cyclops.getBodyY(0.0D) + Y_OFFSET;
        double zProjectile = this.cyclops.getZ() + forwardZ * FORWARD_OFFSET + sideZ * SIDE_OFFSET;

        rock.setPosition(xProjectile, yProjectile, zProjectile);

        double targetY = target.getEyeY() - 1.100000023841858D;
        double dx = target.getX() - xProjectile;
        double dy = targetY - yProjectile;
        double dz = target.getZ() - zProjectile;

        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
        double arc = horizontalDistance * 0.20000000298023224D;

        float distanceFactor;
        float speed;

        double distanceToTarget = this.cyclops.distanceTo(target);

        if (distanceToTarget > 25.0D) {
            distanceFactor = 2.5F;
            speed = 1.2F;
        } else if (distanceToTarget >= 12.0D && distanceToTarget <= 17.0D) {
            distanceFactor = 0.65F;
            speed = 1.1F;
        } else {
            distanceFactor = 0.40F;
            speed = 1F;
        }

        rock.setVelocity(dx, dy + arc * distanceFactor, dz, speed, 1.5F);
        world.spawnEntity(rock);
    }
}