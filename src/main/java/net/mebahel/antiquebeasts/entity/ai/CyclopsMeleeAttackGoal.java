package net.mebahel.antiquebeasts.entity.ai;

import net.mebahel.antiquebeasts.entity.custom.CyclopsEntity;
import net.mebahel.antiquebeasts.item.CustomShieldItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.EnumSet;
import java.util.Objects;

import static java.lang.Math.random;

public class CyclopsMeleeAttackGoal extends Goal {
    protected final CyclopsEntity mob;
    private final double speed;
    String weapon = "";
    private Path path;
    private static final int MAX_COOLDOWN = 21;
    private int cooldown;
    private long lastUpdateTime;

    public CyclopsMeleeAttackGoal(CyclopsEntity mob, double speed, String weapon) {
        this.mob = mob;
        this.speed = speed;
        this.weapon= weapon;
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
            this.mob.getLookControl().lookAt(livingEntity, 15.0F, 7.0F);
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
        } else if (squaredDistance <= d && this.cooldown == 20) {
            if (Objects.equals(this.mob.getAttackName(), "attack")) {
                Objects.requireNonNull(this.mob.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_KNOCKBACK)).setBaseValue(1.5f);
                Objects.requireNonNull(this.mob.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)).setBaseValue(6f);
            } else if (Objects.equals(this.mob.getAttackName(), "attack2")) {
                Objects.requireNonNull(this.mob.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_KNOCKBACK)).setBaseValue(3f);
                Objects.requireNonNull(this.mob.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)).setBaseValue(8f);
            }
            this.mob.setSwinging(true);
        } else if (squaredDistance <= d + 1 && this.cooldown == 10 && this.mob.isSwinging()) {
            if (target instanceof PlayerEntity player) {
                if (player.isBlocking()) {
                    ItemStack activeItem = player.getActiveItem();
                    if (!(activeItem.getItem() instanceof CustomShieldItem)) {
                        player.disableShield(true);
                    }
                }
            }
            if (this.mob.tryAttack(target) && Objects.equals(this.weapon, "frost"))
                target.setFrozenTicks(260);
        }
    }

    protected double getSquaredMaxAttackDistance(LivingEntity entity) {
        return 12f + entity.getWidth();
    }
}