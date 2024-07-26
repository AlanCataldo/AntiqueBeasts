package net.mebahel.antiquebeasts.entity.ai;

import net.mebahel.antiquebeasts.entity.ModEntities;
import net.mebahel.antiquebeasts.entity.custom.HersirEntity;
import net.mebahel.antiquebeasts.entity.custom.EinherjarEntity;
import net.mebahel.antiquebeasts.entity.custom.ValkyrieEntity;
import net.mebahel.antiquebeasts.item.CustomShieldItem;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import software.bernie.geckolib.util.ClientUtils;

import java.util.EnumSet;

public class HersirMeleeAttackGoal extends Goal {
    protected final HersirEntity mob;
    private final double speed;
    private static final int MAX_COOLDOWN = 21;
    public int cooldown;
    private long lastUpdateTime;
    public HersirMeleeAttackGoal(HersirEntity mob, double speed) {
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
            if (target instanceof PlayerEntity player) {
                if (player.isBlocking()) {
                    ItemStack activeItem = player.getActiveItem();
                    if (!(activeItem.getItem() instanceof CustomShieldItem)) {
                        player.disableShield(true);
                    }
                }
            }
            this.mob.tryAttack(target);


            if (Math.random() < 1.0 / 30.0) {
                double angle = Math.random() * 2 * Math.PI;
                double radius = 3.0;

                double offsetX = radius * Math.cos(angle);
                double offsetZ = radius * Math.sin(angle);
                double spawnX = this.mob.getX() + offsetX;
                double spawnZ = this.mob.getZ() + offsetZ;

                if (Math.random() < 0.5) { // 50% chance
                    EinherjarEntity einherjar = new EinherjarEntity(ModEntities.EINHERJAR, this.mob.getWorld());
                    einherjar.refreshPositionAndAngles(spawnX, this.mob.getY(), spawnZ, this.mob.getYaw(), this.mob.getPitch());
                    this.mob.getWorld().spawnEntity(einherjar);
                } else {
                    ValkyrieEntity valkyrie = new ValkyrieEntity(ModEntities.VALKYRIE, this.mob.getWorld());
                    valkyrie.refreshPositionAndAngles(spawnX, this.mob.getY(), spawnZ, this.mob.getYaw(), this.mob.getPitch());
                    this.mob.getWorld().spawnEntity(valkyrie);
                }
                PlayerEntity player = ClientUtils.getClientPlayer();
                if (player != null)
                    this.mob.getWorld().playSound(player, this.mob.getX(), this.mob.getY(), this.mob.getZ(), ModSounds.MYTH_CREATE, this.mob.getSoundCategory(), 0.4f, 1f);
            }
        }
    }

    protected double getSquaredMaxAttackDistance(LivingEntity entity) {
        return 5f + entity.getWidth();
    }
}
