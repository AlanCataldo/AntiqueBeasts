package net.mebahel.antiquebeasts.entity.ai;

import net.mebahel.antiquebeasts.entity.ai.util.ProjectileUtil;
import net.mebahel.antiquebeasts.entity.custom.other.InfernalDraugrEntity;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class InfernalDraugrDodgeFireboltGoal extends Goal {
    private final InfernalDraugrEntity mob;
    private final ProjectileUtil projectileUtil;

    // Animation duration: 2.25s -> 45 ticks
    private static final int DODGE_TOTAL_TICKS = 45;

    // Tu veux le saut au bout de 10 ticks
    private static final int JUMP_TICK = 10;

    // Tu veux tirer à 1.6s -> 32 ticks
    private static final int FIRE_TICK = 32;

    // Mouvement backstep
    private static final double BACKSTEP_POWER = 0.70; // horizontal
    private static final double JUMP_Y = 0.25;         // vertical

    private int ticks;
    private boolean jumped;
    private boolean fired;

    private float damage;

    public InfernalDraugrDodgeFireboltGoal(InfernalDraugrEntity mob, float damage) {
        this.mob = mob;
        this.projectileUtil = new ProjectileUtil();
        this.damage= damage;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        if (mob.getWorld().isClient) return false;
        if (!mob.isDodging()) return false;

        LivingEntity target = mob.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public boolean shouldContinue() {
        if (mob.getWorld().isClient) return false;
        if (!mob.isDodging()) return false;

        LivingEntity target = mob.getTarget();
        if (target == null || !target.isAlive()) return false;

        return ticks < DODGE_TOTAL_TICKS && mob.isAlive();
    }

    @Override
    public void start() {
        ticks = 0;
        jumped = false;
        fired = false;

        mob.getNavigation().stop();

        // IMPORTANT: uniquement Dodging, pas Shooting
        // Le flag dodging doit déjà être true avant que le goal démarre,
        // mais on le force par sécurité.
        mob.setDodging(true);

        // On coupe les états melee côté mob si tu les utilises
        mob.setSwinging(false);
        mob.setAttacking(false);

        // Trigger anim direct
        mob.setAttackName("jumpback_shoot");
        mob.triggerAnim("attacking", "jumpback_shoot");
        if (this.mob.getRandom().nextBoolean())
            playShootingSound(ModSounds.DRAUGR_TAUNT_1);
        else
            playShootingSound(ModSounds.DRAUGR_TAUNT_2);
    }

    @Override
    public void stop() {
        mob.setDodging(false);
        ticks = 0;
        jumped = false;
        fired = false;
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity target = mob.getTarget();
        if (target == null || !target.isAlive()) {
            stop();
            return;
        }

        // On ne laisse pas un autre goal reprendre le move pendant le dodge
        mob.getNavigation().stop();

        // Toujours regarder la cible pendant toute l’anim
        mob.getLookControl().lookAt(target, 30.0F, 30.0F);

        ticks++;

        // Saut/backstep au tick 10
        if (!jumped && ticks >= JUMP_TICK) {
            jumped = true;
            backstepJumpFrom(target);
        }

        // Tir à 1.6s (32 ticks)
        if (!fired && ticks >= FIRE_TICK) {
            fired = true;

            // Ton implémentation à toi
            projectileUtil.shootFirebolt(target, this.mob, this.damage);
            playShootingSound(ModSounds.FIREBOLT_FIRE);
        }

        if (ticks >= DODGE_TOTAL_TICKS) {
            stop();
        }
    }

    private void backstepJumpFrom(LivingEntity target) {
        // Direction = mob -> opposé à la target
        Vec3d away = mob.getPos().subtract(target.getPos()).normalize();

        Vec3d vel = new Vec3d(
                away.x * BACKSTEP_POWER,
                JUMP_Y,
                away.z * BACKSTEP_POWER
        );

        mob.setVelocity(vel);
        mob.velocityDirty = true;
    }

    private void playShootingSound(SoundEvent sound) {
        mob.getWorld().playSound(
                null,
                mob.getX(),
                mob.getY(),
                mob.getZ(),
                sound,
                mob.getSoundCategory(),
                0.9F,
                0.9F + mob.getRandom().nextFloat() * 0.2F
        );
    }
}
