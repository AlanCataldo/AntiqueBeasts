package net.mebahel.antiquebeasts.entity.ai.dwarven.dwarven_spider;

import net.mebahel.antiquebeasts.entity.custom.dwarven.DwarvenEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.player.PlayerEntity;

import java.util.EnumSet;

public class DwarvenSpiderMeleeAttackGoal extends Goal {
    public int ATTACK_RANGE;

    protected final DwarvenEntity mob;
    private final double speed;
    private final int ATTACK_TIME;
    private int MAX_COOLDOWN = 21;

    public int cooldown;
    public int attackCooldown;
    private long lastUpdateTime;
    double rand;
    public DwarvenSpiderMeleeAttackGoal(DwarvenEntity mob, double speed, int attackCooldwon, int attackRange, int attackMoment) {
        this.mob = mob;
        this.speed = speed;
        this.ATTACK_RANGE = attackRange;
        this.ATTACK_TIME = attackMoment;
        this.MAX_COOLDOWN = attackCooldwon;
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
                    return this.ATTACK_RANGE >= this.mob.squaredDistanceTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
                }
            }
        }
    }

    public boolean shouldContinue() {
        LivingEntity livingEntity = this.mob.getTarget();

        if (livingEntity instanceof PlayerEntity playerEntity) {
            if (playerEntity.isCreative() || playerEntity.isSpectator()) {
                return false;
            }
        }
        return livingEntity != null && livingEntity.isAlive();
    }

    public void start() {
        this.mob.setAttacking(true);
    }
    @Override
    public void stop() {
        this.mob.setAttacking(false);
        this.mob.setSwinging(false);
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
        double d = this.ATTACK_RANGE;
        this.cooldown = Math.max(this.cooldown - 1, 0);
        this.mob.getNavigation().startMovingTo(target, this.speed);

        if (this.cooldown == 0) {
            this.cooldown = this.MAX_COOLDOWN + 2;
            this.mob.setSwinging(false);
        } else if (squaredDistance <= d && this.cooldown == this.MAX_COOLDOWN) {
            this.mob.setSwinging(true);
        } else if (squaredDistance <= d + 1 && this.cooldown == this.ATTACK_TIME && this.mob.isSwinging()) {
            this.mob.tryAttack(target);
        }
    }
}