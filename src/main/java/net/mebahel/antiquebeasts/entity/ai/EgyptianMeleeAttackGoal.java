package net.mebahel.antiquebeasts.entity.ai;

import net.mebahel.antiquebeasts.entity.custom.EgyptianEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;

import java.util.EnumSet;

import static java.lang.Math.random;

public class EgyptianMeleeAttackGoal extends Goal {
    protected final EgyptianEntity mob;
    private final double speed;
    private final double attackRange;
    private final int attackDistance;
    private final int attackMoment;
    private static final int MAX_COOLDOWN = 21;

    public int cooldown;
    private long lastUpdateTime;
    double rand;
    public EgyptianMeleeAttackGoal(EgyptianEntity mob, double speed, double attackRange, int attackDistance, int attackMoment) {
        this.mob = mob;
        this.speed = speed;
        this.attackRange = attackRange;
        this.attackDistance = attackDistance;
        this.attackMoment = attackMoment;
        this.cooldown = MAX_COOLDOWN + 8;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }
    public boolean canStart() {
        return this.mob.getTarget() != null;
    }
    public boolean shouldContinue() {
        LivingEntity livingEntity = this.mob.getTarget();
        return livingEntity != null;
    }
    public void start() {
        this.mob.setAttacking(true);
    }
    public void stop() {
        this.mob.setAttacking(false);
        this.mob.setSwinging(false);
    }
    public boolean shouldRunEveryTick() {
        return true;
    }
    public void tick() {
        LivingEntity livingEntity = this.mob.getTarget();
        if (livingEntity != null) {
            this.attack(livingEntity);
        } else {
            this.stop();
        }
    }
    protected void attack(LivingEntity target) {
        double squaredDistance = this.mob.squaredDistanceTo(target.getX(), target.getY(), target.getZ());
        double d = this.getSquaredMaxAttackDistance(target);
        this.cooldown = Math.max(this.cooldown - 1, 0);
        this.mob.getNavigation().startMovingTo(target, this.speed);

        if (this.cooldown == 0) {
            this.cooldown = MAX_COOLDOWN + 2;
            this.mob.setSwinging(false);
        } else if (squaredDistance <= d && this.cooldown == 20) {
            this.mob.setSwinging(true);
        } else if (squaredDistance <= d + 1 && this.cooldown == 10 && this.mob.isSwinging()) {
            this.mob.tryAttack(target);
        }
    }
    protected double getSquaredMaxAttackDistance(LivingEntity entity) {
        return attackRange + entity.getWidth();
    }
}