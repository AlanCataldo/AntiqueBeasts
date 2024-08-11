package net.mebahel.antiquebeasts.entity.ai;

import net.mebahel.antiquebeasts.entity.custom.greek.CentaurEntity;
import net.mebahel.antiquebeasts.entity.custom.patrol.ModPatrolEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;
import java.util.List;

import static java.lang.Math.random;

public class CentaurMeleeAttackGoal extends Goal {
    protected final CentaurEntity mob;
    private final double speed;
    private final double attackRange;
    private final int attackDistance;
    private final int attackMoment;
    private static final int MAX_COOLDOWN = 17;

    public int cooldown;
    public CentaurMeleeAttackGoal(CentaurEntity mob, double speed, double attackRange, int attackDistance, int attackMoment) {
        this.mob = mob;
        this.speed = speed;
        this.attackRange = attackRange;
        this.attackDistance = attackDistance;
        this.attackMoment = attackMoment;
        this.cooldown = MAX_COOLDOWN + 8;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }
    public boolean canStart() {
        return this.mob.getTarget() != null && !this.mob.isArcher();
    }
    public boolean shouldContinue() {
        LivingEntity livingEntity = this.mob.getTarget();
        return livingEntity != null && !this.mob.isArcher();
    }
    @Override
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
        double d = this.getSquaredMaxAttackDistance(target);
        this.cooldown = Math.max(this.cooldown - 1, 0);
        this.mob.getNavigation().startMovingTo(target, this.speed);
        double rand = random();

        if (rand < 0.5)
            this.mob.setAttackName("attack");
        else
            this.mob.setAttackName("attack2");


        if (this.cooldown == 0) {
            this.cooldown = MAX_COOLDOWN + 2;
            this.mob.setSwinging(false);
        } else if (squaredDistance <= d && this.cooldown == 16) {
            this.mob.setSwinging(true);
        } else if (squaredDistance <= d + 1 && this.cooldown == attackMoment && this.mob.isSwinging()) {
            this.mob.tryAttack(target);
        }
    }
    protected double getSquaredMaxAttackDistance(LivingEntity entity) {
        return attackRange + entity.getWidth();
    }
}