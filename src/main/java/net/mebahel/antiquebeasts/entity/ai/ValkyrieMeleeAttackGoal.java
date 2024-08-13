package net.mebahel.antiquebeasts.entity.ai;

import net.mebahel.antiquebeasts.entity.custom.norse.ValkyrieEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.predicate.entity.EntityPredicates;

import java.util.EnumSet;
import java.util.Optional;

public class ValkyrieMeleeAttackGoal extends Goal {
    protected final ValkyrieEntity mob;
    private final double speed;
    private static final int MAX_COOLDOWN = 21;
    public int cooldown;
    private long lastUpdateTime;
    double rand;
    public ValkyrieMeleeAttackGoal(ValkyrieEntity mob, double speed) {
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
            if (livingEntity == null || this.mob.isHealing()) {
                return false;
            } else if (!livingEntity.isAlive()) {
                return false;
            } else {
                double distance = this.mob.squaredDistanceTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
                Optional<AnimalEntity> entityToHeal = this.mob.getWorld().getEntitiesByClass(AnimalEntity.class, this.mob.getBoundingBox().expand(16f), EntityPredicates.VALID_LIVING_ENTITY).stream()
                        .filter(entity -> entity.getHealth() < entity.getMaxHealth())
                        .findFirst();
                if (entityToHeal.isEmpty())
                    return true;
                return distance <= 64;
            }
        }
    }
    public boolean shouldContinue() {
        LivingEntity livingEntity = this.mob.getTarget();
        return livingEntity != null && !this.mob.isHealing();
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
        return 7f + entity.getWidth();
    }
}