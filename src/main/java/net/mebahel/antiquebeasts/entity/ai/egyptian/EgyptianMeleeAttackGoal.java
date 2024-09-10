package net.mebahel.antiquebeasts.entity.ai.egyptian;

import net.mebahel.antiquebeasts.entity.custom.egyptian.EgyptianCaravanEntity;
import net.mebahel.antiquebeasts.entity.custom.egyptian.EgyptianEntity;
import net.mebahel.antiquebeasts.entity.custom.patrol.ModPatrolEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.player.PlayerEntity;

import java.util.EnumSet;
import java.util.List;

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
        long l = this.mob.getWorld().getTime();
        if (l - this.lastUpdateTime < 20L) {
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

        if (livingEntity instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity) livingEntity;
            if (playerEntity.isCreative() || playerEntity.isSpectator()) {
                return false;
            }
        }
        return livingEntity != null && livingEntity.isAlive();
    }

    public void start() {
        this.mob.setAttacking(true);

        ModPatrolEntity patrolEntity = this.mob;
        LivingEntity target = this.mob.getTarget();

        List<ModPatrolEntity> patrolMembers = patrolEntity.getWorld().getEntitiesByClass(ModPatrolEntity.class, patrolEntity.getBoundingBox().expand(32.0), e -> e.isPartOfSamePatrol(patrolEntity));
        for (ModPatrolEntity member : patrolMembers) {
            if (!(member instanceof EgyptianCaravanEntity) && member.isPatrolling()) {
                member.setPatrolling(false);
                member.setTarget(target);
            }
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
        return 6;
    }
}