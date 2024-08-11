package net.mebahel.antiquebeasts.entity.ai.norse;

import net.mebahel.antiquebeasts.entity.ModEntities;
import net.mebahel.antiquebeasts.entity.custom.norse.EinherjarEntity;
import net.mebahel.antiquebeasts.entity.custom.norse.ValkyrieEntity;
import net.mebahel.antiquebeasts.entity.custom.norse.HersirEntity;
import net.mebahel.antiquebeasts.entity.custom.norse.NorseEntity;
import net.mebahel.antiquebeasts.entity.custom.patrol.ModPatrolEntity;
import net.mebahel.antiquebeasts.item.CustomShieldItem;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import software.bernie.geckolib.util.ClientUtils;

import java.util.EnumSet;
import java.util.List;

import static java.lang.Math.random;

public class NorseMeleeAttackGoal extends Goal {
    protected final NorseEntity mob;
    private final double speed;
    private final int max_cooldown;
    private final int damage_time;

    public int cooldown;
    double rand;

    public NorseMeleeAttackGoal(NorseEntity mob, double speed, int max_cooldown, int damage_time) {
        this.mob = mob;
        this.speed = speed;
        this.max_cooldown = max_cooldown;
        this.damage_time = damage_time;
        this.cooldown = this.max_cooldown + 1;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    public boolean canStart() {
        LivingEntity livingEntity = this.mob.getTarget();
        return livingEntity != null;
    }

    public boolean shouldContinue() {
        LivingEntity livingEntity = this.mob.getTarget();
        return livingEntity != null;
    }

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
        double d = 8;
        this.cooldown = Math.max(this.cooldown - 1, 0);
        this.mob.getNavigation().startMovingTo(target, this.speed);

        rand = random();
        if (rand < 0.5)
            this.mob.setAttackName("attack");
        else
            this.mob.setAttackName("attack2");

        if (this.cooldown == 0) {
            this.cooldown = this.max_cooldown + 2;
            this.mob.setSwinging(false);
        } else if (squaredDistance <= d && this.cooldown == this.max_cooldown) {
            this.mob.setSwinging(true);
        } else if (squaredDistance <= d + 1 && this.cooldown == this.damage_time && this.mob.isSwinging()) {
            if (this.mob instanceof HersirEntity || this.mob instanceof EinherjarEntity) {
                breakShield(target);
            }
            this.mob.tryAttack(target);
            if (this.mob instanceof HersirEntity) {
                summonMythicalUnit();
            }
        }
    }

    private void breakShield(LivingEntity target) {
        if (target instanceof PlayerEntity player) {
            if (player.isBlocking()) {
                ItemStack activeItem = player.getActiveItem();
                if (!(activeItem.getItem() instanceof CustomShieldItem)) {
                    player.disableShield(true);
                }
            }
        }
    }

    private void summonMythicalUnit() {
        if (Math.random() < 1.0 / 30.0) {
            double angle = Math.random() * 2 * Math.PI;
            double radius = 3.0;

            double offsetX = radius * Math.cos(angle);
            double offsetZ = radius * Math.sin(angle);
            double spawnX = this.mob.getX() + offsetX;
            double spawnZ = this.mob.getZ() + offsetZ;

            if (Math.random() < 0.5) {
                EinherjarEntity einherjar = new EinherjarEntity(ModEntities.EINHERJAR, this.mob.getWorld());
                einherjar.refreshPositionAndAngles(spawnX, this.mob.getY(), spawnZ, this.mob.getYaw(), this.mob.getPitch());
                this.mob.getWorld().spawnEntity(einherjar);
            } else {
                ValkyrieEntity valkyrie = new ValkyrieEntity(ModEntities.VALKYRIE, this.mob.getWorld());
                valkyrie.refreshPositionAndAngles(spawnX, this.mob.getY(), spawnZ, this.mob.getYaw(), this.mob.getPitch());
                this.mob.getWorld().spawnEntity(valkyrie);
            }

            if (!this.mob.getWorld().isClient) {
                this.mob.getWorld().playSound(
                        null,
                        this.mob.getX(), this.mob.getY(), this.mob.getZ(),
                        ModSounds.MYTH_CREATE,
                        this.mob.getSoundCategory(),
                        0.65f,
                        1f
                );
            }
        }
    }
}