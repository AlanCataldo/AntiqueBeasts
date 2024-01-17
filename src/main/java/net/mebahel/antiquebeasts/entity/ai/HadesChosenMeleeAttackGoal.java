package net.mebahel.antiquebeasts.entity.ai;

import net.mebahel.antiquebeasts.entity.custom.HadesChosenEntity;
import net.mebahel.antiquebeasts.entity.custom.HopliteEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

import java.util.EnumSet;
import java.util.Objects;

import static java.lang.Math.random;

public class HadesChosenMeleeAttackGoal extends Goal {
    protected final HadesChosenEntity mob;
    private final double speed;
    private static final int MAX_COOLDOWN = 21;
    public int cooldown;
    private long lastUpdateTime;
    double rand;
    public HadesChosenMeleeAttackGoal(HadesChosenEntity mob, double speed) {
        this.mob = mob;
        this.speed = speed;
        this.cooldown = MAX_COOLDOWN + 8;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }
    public boolean canStart() {
        long l = this.mob.getWorld().getTime();
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

        rand = random();
        if (rand < 0.5)
            this.mob.setAttackName("attack");
        else
            this.mob.setAttackName("attack2");

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
            if (Objects.equals(this.mob.getAttackName(), "attack")
                    && Objects.requireNonNull(this.mob.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_KNOCKBACK)).getValue() == 0.5f)
                Objects.requireNonNull(this.mob.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_KNOCKBACK)).setBaseValue(1.5f);
            else if (Objects.equals(this.mob.getAttackName(), "attack2")
                    && Objects.requireNonNull(this.mob.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_KNOCKBACK)).getValue() == 1.5f)
                Objects.requireNonNull(this.mob.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_KNOCKBACK)).setBaseValue(0.5f);
            this.mob.setSwinging(true);
        } else if (squaredDistance <= d && this.cooldown == 12 && this.mob.isSwinging()) {
            if (this.mob.tryAttack(target))
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 7 * 20, 0));
        }
    }
    protected double getSquaredMaxAttackDistance(LivingEntity entity) {
        return 6f + entity.getWidth();
    }
}