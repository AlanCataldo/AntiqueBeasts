package net.mebahel.antiquebeasts.entity.ai.other;

import net.mebahel.antiquebeasts.entity.custom.other.DraugrOverlordEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;

import java.util.EnumSet;

import static java.lang.Math.random;

public class DraugrOverlordMeleeAttackGoal extends Goal {
    private long lastUpdateTime;

    protected final DraugrOverlordEntity mob;
    private final double speed;
    private final int max_cooldown;
    private final int damage_time;

    public int cooldown;
    double rand;

    public DraugrOverlordMeleeAttackGoal(DraugrOverlordEntity mob, double speed, int max_cooldown, int damage_time) {
        this.mob = mob;
        this.speed = speed;
        this.max_cooldown = max_cooldown;
        this.damage_time = damage_time;
        this.cooldown = this.max_cooldown + 1;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    public boolean canStart() {
        LivingEntity livingEntity = this.mob.getTarget();

        if (livingEntity instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity) livingEntity;
            if (playerEntity.isCreative() || playerEntity.isSpectator()) {
                return false;
            }
        }
        return livingEntity != null && livingEntity.isAlive();
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
        double d = 7;
        this.cooldown = Math.max(this.cooldown - 1, 0);
        if (!this.mob.getSpecial())
            this.mob.getNavigation().startMovingTo(target, this.speed);
        else {
            this.mob.getNavigation().stop();
            this.cooldown = this.max_cooldown + 2;
            this.mob.setSwinging(false);
        }

        rand = random();
        if (rand < 0.5)
            this.mob.setAttackName("attack");
        else
            this.mob.setAttackName("attack");

        if (this.cooldown == 0) {
            this.cooldown = this.max_cooldown + 2;
            this.mob.setSwinging(false);
        } else if (squaredDistance <= d && this.cooldown == this.max_cooldown) {
            this.mob.setSwinging(true);
        } else if (squaredDistance <= d + 1 && this.cooldown == this.damage_time && this.mob.isSwinging()) {
            this.mob.tryAttack(target);
        }
    }
    protected double getSquaredMaxAttackDistance(LivingEntity entity) {
        return 8;
    }
}