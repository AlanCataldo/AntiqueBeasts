package net.mebahel.antiquebeasts.entity.ai.dwarven.dwarven_centurion;

import net.mebahel.antiquebeasts.entity.custom.dwemer.DwemerCenturionEntity;
import net.mebahel.antiquebeasts.entity.custom.dwemer.DwemerEntity;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import software.bernie.geckolib.util.ClientUtils;

import java.util.EnumSet;
import java.util.Objects;

import static java.lang.Math.random;

public class DwarvenCenturionMeleeAttackGoal extends Goal {

    protected final DwemerCenturionEntity mob;
    private final double speed;
    private String attackName;
    private int max_cooldown;
    private final int attackDistance;

    public int cooldown;
    double rand;

    public DwarvenCenturionMeleeAttackGoal(DwemerCenturionEntity mob, double speed, int max_cooldown, int attackDistance) {
        this.mob = mob;
        this.speed = speed;
        this.max_cooldown = max_cooldown;
        this.attackDistance = attackDistance;
        this.cooldown = this.max_cooldown + 1;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        this.attackName = this.mob.getAttackName();
    }

    public boolean canStart() {
        LivingEntity livingEntity = this.mob.getTarget();

        if (livingEntity instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity) livingEntity;
            if (playerEntity.isCreative() || playerEntity.isSpectator()) {
                return false;
            }
        }
        return livingEntity != null && livingEntity.isAlive() && !this.mob.isShooting();
    }

    public boolean shouldContinue() {
        LivingEntity livingEntity = this.mob.getTarget();

        if (livingEntity instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity) livingEntity;
            if (playerEntity.isCreative() || playerEntity.isSpectator()) {
                return false;
            }
        }
        return livingEntity != null && livingEntity.isAlive() && !this.mob.isShooting();
    }

    public void start() {
        rand = random();
        if (rand <= 0.6 && rand > 0.3) {
            this.attackName = "attack";
            this.max_cooldown = 24;
        } else if (rand <= 0.3){
            this.attackName = "attack2";
            this.max_cooldown = 39;
        } else {
            this.attackName = "power_attack";
            this.max_cooldown = 30;
        }
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
            if (Objects.equals(this.attackName, "attack"))
                this.attack(livingEntity);
            else if (Objects.equals(this.attackName, "attack2"))
                this.attack2(livingEntity);
            else
                this.powerAttack(livingEntity);
            this.mob.setAttackName(this.attackName);
        }
    }

    protected void attack(LivingEntity target) {
        double squaredDistance = this.mob.squaredDistanceTo(target.getX(), target.getY(), target.getZ());
        double d = this.getSquaredMaxAttackDistance();
        this.cooldown = Math.max(this.cooldown - 1, 0);
        Path path = this.mob.getNavigation().findPathTo(target, attackDistance);
        this.mob.getNavigation().startMovingAlong(path, this.speed);

        if (this.cooldown == 0) {
            rand = random();
            if (rand <= 0.6 && rand > 0.3) {
                this.attackName = "attack";
                this.max_cooldown = 24;
            } else if (rand <= 0.3){
                this.attackName = "attack2";
                this.max_cooldown = 39;
            } else {
                    this.attackName = "power_attack";
                this.max_cooldown = 30;
            }

            this.cooldown = this.max_cooldown + 8;
            this.mob.setSwinging(false);
        }

        if (squaredDistance <= d && this.cooldown == this.max_cooldown) {
            this.mob.setSwinging(true);
        } else if (squaredDistance <= d + 1 && this.cooldown == 10 && this.mob.isSwinging()) {
            this.mob.tryAttack(target);
            dealAreaDamage(target, 4.0f);
        } else if (squaredDistance <= d && this.cooldown == this.max_cooldown - 4) {
            if (this.mob.getWorld().isClient()) {
                PlayerEntity player = ClientUtils.getClientPlayer();
                if (player != null) {
                    if (Objects.equals(this.attackName, "attack"))
                        this.mob.getWorld().playSound(player, this.mob.getX(), this.mob.getY(),
                                this.mob.getZ(), ModSounds.DWARVEN_CENTURION_ATTACK_1, this.mob.getSoundCategory(), 0.5f, 1f);
                }
            }
        } else if (squaredDistance > d + 1) {
            this.cooldown = this.max_cooldown + 6;
            this.mob.setSwinging(false);
        }
    }
    protected void attack2(LivingEntity target) {
        double squaredDistance = this.mob.squaredDistanceTo(target.getX(), target.getY(), target.getZ());
        double d = this.getSquaredMaxAttackDistance();
        this.cooldown = Math.max(this.cooldown - 1, 0);
        this.mob.getNavigation().startMovingTo(target, this.speed);

        if (this.cooldown == 0) {
            rand = random();
            if (rand <= 0.6 && rand > 0.3) {
                this.attackName = "attack";
                this.max_cooldown = 24;
            } else if (rand <= 0.3){
                this.attackName = "attack2";
                this.max_cooldown = 39;
            } else {
                this.attackName = "power_attack";
                this.max_cooldown = 30;
            }

            this.cooldown = this.max_cooldown + 8;
            this.mob.setSwinging(false);
        }

        if (squaredDistance <= d && this.cooldown == this.max_cooldown) {
            this.mob.setSwinging(true);
        } else if (squaredDistance <= d + 1 && (this.cooldown == 22 || this.cooldown == 9) && this.mob.isSwinging()) {
            this.mob.tryAttack(target);
            dealAreaDamage(target, 4.0f);
        } else if (squaredDistance <= d && this.cooldown == this.max_cooldown - 4) {
            if (this.mob.getWorld().isClient()) {
                PlayerEntity player = ClientUtils.getClientPlayer();
                if (player != null && Objects.equals(this.attackName, "attack2")) {
                    this.mob.getWorld().playSound(player, this.mob.getX(), this.mob.getY(),
                            this.mob.getZ(), ModSounds.DWARVEN_CENTURION_ATTACK_2, this.mob.getSoundCategory(), 0.5f, 1f);
                }
            }
        } else if (squaredDistance > d + 1) {
            this.cooldown = this.max_cooldown + 6;
            this.mob.setSwinging(false);
        }
    }

    protected void powerAttack(LivingEntity target) {
        double squaredDistance = this.mob.squaredDistanceTo(target.getX(), target.getY(), target.getZ());
        double d = this.getSquaredMaxAttackDistance();
        this.cooldown = Math.max(this.cooldown - 1, 0);
        Path path = this.mob.getNavigation().findPathTo(target, attackDistance);
        this.mob.getNavigation().startMovingAlong(path, this.speed);

        if (this.cooldown == 0) {
            rand = random();
            if (rand <= 0.6 && rand > 0.3) {
                this.attackName = "attack";
                this.max_cooldown = 24;
            } else if (rand <= 0.3){
                this.attackName = "attack2";
                this.max_cooldown = 39;
            } else {
                this.attackName = "power_attack";
                this.max_cooldown = 30;
            }

            this.cooldown = this.max_cooldown + 8;
            this.mob.setSwinging(false);
        }

        if (squaredDistance <= d && this.cooldown == this.max_cooldown) {
            this.mob.setSwinging(true);
        } else if (squaredDistance <= d + 1 && this.cooldown == 10 && this.mob.isSwinging()) {
            this.mob.tryAttack(target);
            dealAreaDamage(target, 8.0f);
            breakShield(target);
        } else if (squaredDistance <= d && this.cooldown == this.max_cooldown - 8) {
            if (this.mob.getWorld().isClient()) {
                PlayerEntity player = ClientUtils.getClientPlayer();
                if (player != null) {
                    if (Objects.equals(this.attackName, "power_attack"))
                        this.mob.getWorld().playSound(player, this.mob.getX(), this.mob.getY(),
                                this.mob.getZ(), ModSounds.DWARVEN_CENTURION_POWER_ATTACK, this.mob.getSoundCategory(), 0.5f, 1f);
                }
            }

        } else if (squaredDistance > d + 1) {
            this.cooldown = this.max_cooldown + 6;
            this.mob.setSwinging(false);
        }
    }

    private void breakShield(LivingEntity target) {
        if (target instanceof PlayerEntity player) {
            if (player.isBlocking()) {
                ItemStack activeItem = player.getActiveItem();
                if (activeItem.getItem() instanceof ShieldItem) {
                    player.getItemCooldownManager().set(activeItem.getItem(), 100);
                    player.getWorld().sendEntityStatus(player, EntityStatuses.BREAK_SHIELD);
                    player.clearActiveItem();
                }
            }
        }
    }
    protected double getSquaredMaxAttackDistance() {
        return 11;
    }
    private void dealAreaDamage(LivingEntity primaryTarget, float areaDamage) {
        this.mob.getWorld().getOtherEntities(this.mob, primaryTarget.getBoundingBox().expand(1.0), e ->
                e instanceof LivingEntity entity &&
                        !(entity instanceof DwemerEntity) &&
                        entity != primaryTarget &&
                        entity.isAlive() &&
                        this.mob.canSee(entity)
        ).forEach(entity -> {
            entity.timeUntilRegen = 0;
            entity.damage(this.mob.getDamageSources().mobAttack(this.mob), areaDamage);
        });
    }
}
