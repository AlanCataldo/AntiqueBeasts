package net.mebahel.antiquebeasts.entity.ai;

import net.mebahel.antiquebeasts.entity.custom.HopliteEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.attribute.EntityAttributes;

import java.util.EnumSet;
import java.util.Objects;

import static java.lang.Math.random;

public class HopliteMeleeAttackGoal extends Goal {
    protected final HopliteEntity mob;
    private final double speed;
    private static final int MAX_COOLDOWN = 21;
    public int cooldown;
    double rand;
    public HopliteMeleeAttackGoal(HopliteEntity mob, double speed, boolean pauseWhenMobIdle) {
        this.mob = mob;
        this.speed = speed;
        this.cooldown = MAX_COOLDOWN + 1;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }
    public boolean canStart() {
        LivingEntity livingEntity = this.mob.getTarget();
        return livingEntity != null;
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
        }
    }
    protected void attack(LivingEntity target) {
        double squaredDistance = this.mob.squaredDistanceTo(target.getX(), target.getY(), target.getZ());
        double d = 8;
        this.cooldown = Math.max(this.cooldown - 1, 0);
        this.mob.getNavigation().startMovingTo(target, this.speed);

        rand = random();
        if (rand < 0.5)
            this.mob.setAttackName("attack");
        else
            this.mob.setAttackName("attack2");


        if (this.cooldown == 0) {
            this.cooldown = MAX_COOLDOWN + 2;
            this.mob.setSwinging(false);
        } else if (squaredDistance <= d && this.cooldown == 20) {
            this.mob.setSwinging(true);
        } else if (squaredDistance <= d && this.cooldown == 10 && this.mob.isSwinging()) {
            this.mob.tryAttack(target);
        }
    }
}