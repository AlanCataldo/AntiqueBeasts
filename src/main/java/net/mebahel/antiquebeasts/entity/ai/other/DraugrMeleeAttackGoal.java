package net.mebahel.antiquebeasts.entity.ai.other;

import net.mebahel.antiquebeasts.entity.custom.other.DraugrEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;
import java.util.Objects;

import static java.lang.Math.random;

public class DraugrMeleeAttackGoal extends Goal {
    protected final DraugrEntity mob;
    private final double speed;
    private final int max_cooldown;
    private final int damage_time;
    public int cooldown;
    public int dash_time;

    private boolean hasDashed = false;
    private double originalSpeed = 0.3D;

    public DraugrMeleeAttackGoal(DraugrEntity mob, double speed, int max_cooldown, int damage_time, int dash_time) {
        this.mob = mob;
        this.speed = speed;
        this.max_cooldown = max_cooldown;
        this.damage_time = damage_time;
        this.cooldown = this.max_cooldown + 1;
        this.dash_time = dash_time;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        LivingEntity target = mob.getTarget();
        if (target instanceof PlayerEntity player && (player.isCreative() || player.isSpectator())) return false;
        return target != null && target.isAlive();
    }

    @Override
    public boolean shouldContinue() {
        LivingEntity target = mob.getTarget();
        if (target instanceof PlayerEntity player && (player.isCreative() || player.isSpectator())) return false;
        return target != null && target.isAlive();
    }

    @Override
    public void start() {
        mob.setAttacking(true);
        hasDashed = false;
        mob.getNavigation().startMovingTo(this.mob.getTarget(), speed);
    }

    @Override
    public void stop() {
        mob.setAttacking(false);
        mob.setSwinging(false);
        hasDashed = false;
        Objects.requireNonNull(mob.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(originalSpeed);
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity target = mob.getTarget();
        if (target != null && target.isAlive()) {
            mob.getLookControl().lookAt(target, 15.0F, 15.0F);
            attack(target);
        } else {
            stop();
        }
    }

    protected void attack(LivingEntity target) {
        double squaredDistance = mob.squaredDistanceTo(target.getX(), target.getY(), target.getZ());
        double attackRange = 7;
        cooldown = Math.max(cooldown - 1, 0);

        // Navigation normale quand il n'attaque pas
        if (!mob.isSwinging()) {
            var attr = Objects.requireNonNull(mob.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED));
            if (attr.getBaseValue() == 0.0D) {
                attr.setBaseValue(originalSpeed);
            }
            mob.getNavigation().startMovingTo(target, speed);
        }

        if (mob.isSwinging()) {
            Vec3d dir = target.getPos().subtract(mob.getPos()).normalize();
            float yaw = (float) (MathHelper.atan2(dir.z, dir.x) * (180.0 / Math.PI)) - 90.0F;
            mob.setYaw(yaw);
            mob.setHeadYaw(yaw);
            mob.prevYaw = yaw;
        }

        // Choix d'animation aléatoire
        if (random() < 0.5)
            mob.setAttackName("attack");
        else
            mob.setAttackName("attack2");

        // Début d’attaque
        if (squaredDistance <= attackRange && cooldown == max_cooldown) {
            mob.setSwinging(true);
            hasDashed = false;

            // Gèle le mob
            Objects.requireNonNull(mob.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED))
                    .setBaseValue(0.0D);
        }

        // DASH à 0.5s après le début (max_cooldown - 10)
        if (mob.isSwinging() && !hasDashed && cooldown == max_cooldown - dash_time) {
            hasDashed = true;

// 🔹 recalcul direction + force rotation complète avant le dash
            Vec3d dir = target.getPos().subtract(mob.getPos()).normalize();
            float yaw = (float) (MathHelper.atan2(dir.z, dir.x) * (180.0 / Math.PI)) - 90.0F;

            mob.setYaw(yaw);
            mob.setHeadYaw(yaw);
            mob.setBodyYaw(yaw);
            mob.prevYaw = yaw;
            mob.prevBodyYaw = yaw;

// 🔹 dash vers la direction du corps
            Vec3d dash = new Vec3d(
                    -MathHelper.sin(mob.getYaw() * ((float)Math.PI / 180F)) * 0.9,
                    0.05,
                    MathHelper.cos(mob.getYaw() * ((float)Math.PI / 180F)) * 0.9
            );
            mob.addVelocity(dash.x, dash.y, dash.z);
            mob.velocityDirty = true;
        }

        // Moment du coup
        if (squaredDistance <= attackRange + 1 && cooldown == damage_time && mob.isSwinging()) {
            mob.tryAttack(target);
        }

        // Fin de l’attaque
        if (cooldown == 0) {
            mob.setSwinging(false);
            hasDashed = false;

            // Rétablit la vitesse
            Objects.requireNonNull(mob.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED))
                    .setBaseValue(originalSpeed);

            cooldown = max_cooldown + 2;
        }
    }

    protected double getSquaredMaxAttackDistance(LivingEntity entity) {
        return 6;
    }
}
