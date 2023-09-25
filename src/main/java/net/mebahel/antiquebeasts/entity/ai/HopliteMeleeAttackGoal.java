package net.mebahel.antiquebeasts.entity.ai;

import net.mebahel.antiquebeasts.entity.custom.HopliteEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.Hand;

import java.util.EnumSet;
import java.util.Objects;

import static java.lang.Math.random;

public class HopliteMeleeAttackGoal extends Goal {

    protected final HopliteEntity mob;
    private final double speed;
    private final boolean pauseWhenMobIdle;
    private Path path;
    private int updateCountdownTicks;
    private static final int MAX_COOLDOWN = 23;
    private int cooldown;
    private long lastUpdateTime;

    double rand;

    public HopliteMeleeAttackGoal(HopliteEntity mob, double speed, boolean pauseWhenMobIdle) {
        this.mob = mob;
        this.speed = speed;
        this.pauseWhenMobIdle = pauseWhenMobIdle;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        this.cooldown = MAX_COOLDOWN + 3;
    }

    public boolean canStart() {
        long l = this.mob.world.getTime();
        if (l - this.lastUpdateTime < 23) {
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
        if (livingEntity == null) {
            return false;
        } else if (!livingEntity.isAlive()) {
            return false;
        } else if (!this.pauseWhenMobIdle) {
            return !this.mob.getNavigation().isIdle();
        } else if (!this.mob.isInWalkTargetRange(livingEntity.getBlockPos())) {
            return false;
        } else {
            return !(livingEntity instanceof PlayerEntity) || !livingEntity.isSpectator() && !((PlayerEntity) livingEntity).isCreative();
        }
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
        }
    }

    protected void attack(LivingEntity target, double squaredDistance) {
        double d = this.getSquaredMaxAttackDistance(target);
        if (squaredDistance <= d) {
            rand = random();
            if (rand < 0.5)
                this.mob.setAttackName("attack");
            else
                this.mob.setAttackName("attack2");
        }

        if (squaredDistance <= d && this.cooldown <= 0) {
            this.cooldown = MAX_COOLDOWN;
        } else if (squaredDistance <= d && this.cooldown == 20) {
            if (Objects.equals(this.mob.getAttackName(), "attack")
                    && Objects.requireNonNull(this.mob.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_KNOCKBACK)).getValue() == 1f)
                Objects.requireNonNull(this.mob.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_KNOCKBACK)).setBaseValue(4f);
            else if (Objects.equals(this.mob.getAttackName(), "attack2")
                    && Objects.requireNonNull(this.mob.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_KNOCKBACK)).getValue() == 4f)
                Objects.requireNonNull(this.mob.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_KNOCKBACK)).setBaseValue(1f);
            this.mob.swingHand(Hand.MAIN_HAND);
        } else if (squaredDistance <= d && this.cooldown <= 15 && this.cooldown >= 13) {
            this.mob.tryAttack(target);
        }
    }

    protected double getSquaredMaxAttackDistance(LivingEntity entity) {
        return 12f + entity.getWidth();
    }
}