package net.mebahel.antiquebeasts.entity.ai;

import net.mebahel.antiquebeasts.entity.custom.greek.CentaurEntity;
import net.mebahel.antiquebeasts.entity.custom.patrol.ModPatrolEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class CentaurShootingGoal extends Goal {
    private final CentaurEntity actor;
    private final float squaredRange;
    private int targetSeeingTicker;
    private boolean movingToLeft;
    private boolean backward;
    private int combatTicks = -1;

    public CentaurShootingGoal(CentaurEntity actor, float squaredRange) {
        this.actor = actor;
        this.squaredRange = squaredRange;
    }

    public boolean canStart() {
        return this.actor.getTarget() != null && this.actor.isArcher();
    }

    public void start() {
        ModPatrolEntity patrolEntity = this.actor;
        LivingEntity target = this.actor.getTarget();

        List<ModPatrolEntity> patrolMembers = patrolEntity.getWorld().getEntitiesByClass(ModPatrolEntity.class, patrolEntity.getBoundingBox().expand(32.0), e -> e.isPartOfSamePatrol(patrolEntity));

        for (ModPatrolEntity member : patrolMembers) {
            member.setPatrolling(false);
            member.setTarget(target);
        }
        this.actor.setCooldown(81);
    }

    public void stop() {
        this.actor.setCooldown(81);
        this.actor.setShooting(false);
        this.actor.getMoveControl().strafeTo(0, 0);

        ModPatrolEntity patrolEntity = this.actor;
        if (patrolEntity.wasInitiallyInPatrol()) {
            patrolEntity.checkAndResumePatrolling();
        }
    }

    public boolean shouldRunEveryTick() {
        return true;
    }

    public boolean shouldContinue() {
        LivingEntity target = this.actor.getTarget();
        return target != null && target.isAlive() && this.actor.isArcher();
    }

    public void tick() {
        LivingEntity livingEntity = this.actor.getTarget();
        if (livingEntity == null || !livingEntity.isAlive()) {
            this.stop();
            return;
        }

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
        if (!(d > (double) this.squaredRange) && this.targetSeeingTicker >= 20) {
            this.actor.getNavigation().stop();
            ++this.combatTicks;
        } else {
            this.actor.getNavigation().startMovingTo(livingEntity, 0.4f);
            this.combatTicks = -1;
        }
        if (this.combatTicks >= 20) {
            if ((double) this.actor.getRandom().nextFloat() < 0.3) {
                this.movingToLeft = !this.movingToLeft;
            }
            if ((double) this.actor.getRandom().nextFloat() < 0.3) {
                this.backward = !this.backward;
            }
            this.combatTicks = 0;
        }
        if (this.combatTicks > -1) {
            if (this.actor.horizontalCollision && this.actor.isOnGround()) {
                this.backward = false;
            } else if (d > (double) (this.squaredRange * 0.75F)) {
                this.backward = false;
            } else if (d < (double) (this.squaredRange * 0.25F)) {
                this.backward = true;
            }

            // Check the block behind the actor when moving backward
            if (this.backward) {
                Vec3d backwardsVec = this.actor.getRotationVec(1.0F).multiply(-1.0);
                BlockPos blockBehindPos = new BlockPos(MathHelper.floor(this.actor.getX() + backwardsVec.x), MathHelper.floor(this.actor.getY()), MathHelper.floor(this.actor.getZ() + backwardsVec.z));
                BlockState blockBehindState = this.actor.getWorld().getBlockState(blockBehindPos);

                // Check the block below the block behind
                BlockPos blockBelowBehindPos = blockBehindPos.down();
                BlockState blockBelowBehindState = this.actor.getWorld().getBlockState(blockBelowBehindPos);

                if (blockBehindState.isFullCube(this.actor.getWorld(), blockBehindPos)) {
                    // Calculate the jump direction (backward and upward)
                    Vec3d jumpDirection = new Vec3d(backwardsVec.x, 0.5, backwardsVec.z).normalize().multiply(0.35);
                    this.actor.performJump(jumpDirection);
                } else if (!blockBelowBehindState.isAir()) {
                    // Move backward even if the block behind is lower
                    this.actor.getMoveControl().strafeTo(-0.4F, this.movingToLeft ? 0.4F : -0.4F);
                }
            } else {
                this.actor.getMoveControl().strafeTo(this.backward ? -0.4F : 0.4F, this.movingToLeft ? 0.4F : -0.4F);
            }
        }

        if (this.actor.getVisibilityCache().canSee(livingEntity)) {
            World world = this.actor.getWorld();
            this.actor.setCooldown(Math.max(this.actor.getCooldown() - 1, 0));
            if (this.actor.getCooldown() == 4) {
                ProjectileEntity throwingAxeEntity;
                throwingAxeEntity = new ArrowEntity(world, this.actor);

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
                    speed = 1.8f;
                } else if (this.actor.distanceTo(livingEntity) >= 12 && this.actor.distanceTo(livingEntity) <= 17) {
                    distance = 0.6f;
                    speed = 1.8f;
                } else {
                    distance = 0.6f;
                    speed = 1.8f;
                }
                throwingAxeEntity.setVelocity(e, f + h * distance, g, speed, 1F);
                throwingAxeEntity.setPosition(xProjectile, this.actor.getBodyY(1), zProjectile);
                world.spawnEntity(throwingAxeEntity);
            } else if (this.actor.getCooldown() == 17) {
                this.actor.setShooting(true);
            } else if (this.actor.getCooldown() == 0) {
                this.actor.setCooldown(81);
                this.actor.setShooting(false);
            } else if (this.actor.getCooldown() > 15 && this.actor.getCooldown() < 81) {
                this.actor.setShooting(false);
            }
        } else {
            this.actor.setShooting(false);
            this.actor.setCooldown(81);
        }
    }
}
