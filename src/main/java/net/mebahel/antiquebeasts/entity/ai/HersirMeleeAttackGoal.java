package net.mebahel.antiquebeasts.entity.ai;

import net.mebahel.antiquebeasts.entity.custom.HersirEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;

import java.util.EnumSet;

public class HersirMeleeAttackGoal extends Goal {
    protected final HersirEntity mob;
    private final double speed;
    private static final int MAX_COOLDOWN = 21;
    public int cooldown;
    private long lastUpdateTime;
    double rand;
    public HersirMeleeAttackGoal(HersirEntity mob, double speed, boolean pauseWhenMobIdle) {
        this.mob = mob;
        this.speed = speed;
        this.cooldown = MAX_COOLDOWN + 8;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
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
                Path path = this.mob.getNavigation().findPathTo(livingEntity, 0);
                if (path != null) {
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
        this.mob.setAttacking(true);
        this.cooldown = MAX_COOLDOWN + 8;
    }
    public void stop() {
        this.mob.setAttacking(false);
        this.mob.setSwinging(false);
        this.mob.getNavigation().stop();
    }
    public boolean shouldRunEveryTick() {
        return true;
    }
    public void tick() {
        LivingEntity livingEntity = this.mob.getTarget();
        if (livingEntity != null) {
            this.mob.getLookControl().lookAt(livingEntity, 15.0F, 15.0F);
            this.attack(livingEntity);
        } else {
            this.stop();
        }
    }
    protected void attack(LivingEntity target) {
        double squaredDistance = this.mob.squaredDistanceTo(target.getX(), target.getY(), target.getZ());
        double d = this.getSquaredMaxAttackDistance(target);

        if (!this.mob.isSwinging())
            this.mob.getNavigation().startMovingTo(target, this.speed);
        else
            this.mob.getNavigation().stop();

        if (squaredDistance > d) {
            this.cooldown = MAX_COOLDOWN + 2;
            this.mob.setSwinging(false);
        } else {
            this.cooldown = Math.max(this.cooldown - 1, 0);
        }

        if (squaredDistance <= d && this.cooldown == 0) {
            this.cooldown = MAX_COOLDOWN + 2;
            this.mob.setSwinging(false);
        } else if (squaredDistance <= d && this.cooldown == 20) {
            this.mob.setSwinging(true);
        } else if (squaredDistance <= d && this.cooldown == 10 && this.mob.isSwinging()) {
            this.mob.tryAttack(target);
        }
    }
    protected double getSquaredMaxAttackDistance(LivingEntity entity) {
        return 5f + entity.getWidth();
    }
}