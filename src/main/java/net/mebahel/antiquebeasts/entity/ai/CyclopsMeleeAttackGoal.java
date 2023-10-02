package net.mebahel.antiquebeasts.entity.ai;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.Hand;
import java.util.EnumSet;

public class CyclopsMeleeAttackGoal extends Goal {
    protected final PathAwareEntity mob;
    private final double speed;
    private final boolean pauseWhenMobIdle;
    private Path path;
    private int updateCountdownTicks;
    private static final int MAX_COOLDOWN = 21;
    private int cooldown;
    private long lastUpdateTime;

    public CyclopsMeleeAttackGoal(PathAwareEntity mob, double speed, boolean pauseWhenMobIdle) {
        this.mob = mob;
        this.speed = speed;
        this.pauseWhenMobIdle = pauseWhenMobIdle;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        this.cooldown = MAX_COOLDOWN;
    }

    public boolean canStart() {
        long l = this.mob.world.getTime();
        if (l - this.lastUpdateTime < MAX_COOLDOWN) {
            return false;
        } else {
            this.lastUpdateTime = l;
            LivingEntity livingEntity = this.mob.getTarget();
            if (livingEntity == null) {
                return false;
            } else if (!livingEntity.isAlive()) {
                return false;
            } else {
                this.path = this.mob.getNavigation().findPathTo(livingEntity, 0);
                if (this.path != null) {
                    return true;
                } else {
                    return this.getSquaredMaxAttackDistance(livingEntity) >= this.mob.squaredDistanceTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
                }
            }
        }
    }

    public boolean shouldContinue() {
        LivingEntity livingEntity = this.mob.getTarget();
        return livingEntity != null;
    }

    public void start() {
        this.mob.getNavigation().startMovingAlong(this.path, this.speed);
        this.mob.setAttacking(true);
    }

    public void stop() {
        LivingEntity livingEntity = this.mob.getTarget();
        if (!EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.test(livingEntity)) {
            this.mob.setTarget(null);
        }
        this.mob.setAttacking(false);
        this.mob.getNavigation().stop();
    }

    public boolean shouldRunEveryTick() {
        return true;
    }

    public void tick() {
        LivingEntity livingEntity = this.mob.getTarget();
        this.cooldown = this.cooldown - 1;
        if (livingEntity != null) {
            this.mob.getLookControl().lookAt(livingEntity, 15.0F, 0F);
            double d = this.mob.squaredDistanceTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
            this.updateCountdownTicks = Math.max(this.updateCountdownTicks - 1, 0);
            this.attack(livingEntity, d);
            this.mob.getNavigation().startMovingTo(livingEntity, this.speed);
        } else {
            this.cooldown = MAX_COOLDOWN;
        }
    }

    protected void attack(LivingEntity target, double squaredDistance) {
        double d = this.getSquaredMaxAttackDistance(target);
        if (squaredDistance <= d && this.cooldown <= 0) {
            this.cooldown = MAX_COOLDOWN;
        } else if (squaredDistance <= d && this.cooldown == 20) {
            this.mob.swingHand(Hand.MAIN_HAND);
        } else if (squaredDistance <= d && this.cooldown == 10) {
            this.mob.tryAttack(target);
        }
    }

    protected double getSquaredMaxAttackDistance(LivingEntity entity) {
        return 18f + entity.getWidth();
    }
}