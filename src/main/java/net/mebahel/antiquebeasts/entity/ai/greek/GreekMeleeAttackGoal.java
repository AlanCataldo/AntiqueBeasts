package net.mebahel.antiquebeasts.entity.ai.greek;

import net.mebahel.antiquebeasts.entity.custom.greek.GreekEntity;
import net.mebahel.antiquebeasts.entity.custom.patrol.ModPatrolEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;
import java.util.List;

import static java.lang.Math.random;

public class GreekMeleeAttackGoal extends Goal {
    protected final GreekEntity mob;
    private final double speed;
    private final int max_cooldown;
    private final int damage_time;

    public int cooldown;
    double rand;
    public GreekMeleeAttackGoal(GreekEntity mob, double speed, int max_cooldown, int damage_time) {
        this.mob = mob;
        this.speed = speed;
        this.max_cooldown = max_cooldown;
        this.damage_time = damage_time;
        this.cooldown = this.max_cooldown + 1;
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

        ModPatrolEntity patrolEntity = this.mob;
        LivingEntity target = this.mob.getTarget();

        List<ModPatrolEntity> patrolMembers = patrolEntity.getWorld().getEntitiesByClass(ModPatrolEntity.class, patrolEntity.getBoundingBox().expand(32.0), e -> e.isPartOfSamePatrol(patrolEntity));
        for (ModPatrolEntity member : patrolMembers) {
            member.setPatrolling(false);
            member.setTarget(target);
        }
    }
    @Override
    public void stop() {
        this.mob.setAttacking(false);
        this.mob.setSwinging(false);

        ModPatrolEntity patrolEntity = this.mob;
        if (patrolEntity.wasInitiallyInPatrol()) {
            patrolEntity.checkAndResumePatrolling();
        }
    }
    public boolean shouldRunEveryTick() {
        return true;
    }
    public void tick() {
        LivingEntity livingEntity = this.mob.getTarget();
        if (livingEntity != null && livingEntity.isAlive()) {
            this.mob.getLookControl().lookAt(livingEntity, 15.0F, 15.0F);
            this.attack(livingEntity);
        } else {
            this.stop();
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
            this.cooldown = this.max_cooldown + 2;
            this.mob.setSwinging(false);
        } else if (squaredDistance <= d && this.cooldown == this.max_cooldown) {
            this.mob.setSwinging(true);
        } else if (squaredDistance <= d && this.cooldown == this.damage_time && this.mob.isSwinging()) {
            this.mob.tryAttack(target);
        }
    }
}