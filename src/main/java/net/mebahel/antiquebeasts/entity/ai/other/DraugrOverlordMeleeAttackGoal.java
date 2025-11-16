package net.mebahel.antiquebeasts.entity.ai.other;

import net.mebahel.antiquebeasts.entity.custom.other.DraugrOverlordEntity;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Random;

import static java.lang.Math.random;

public class DraugrOverlordMeleeAttackGoal extends Goal {

    protected final DraugrOverlordEntity mob;
    private final double speed;
    private int cooldown;
    private boolean hasDashed = false;
    private final double originalSpeed = 0.3D;

    private AttackType currentAttack = AttackType.NONE;
    private AttackType nextAttack = AttackType.NONE;

    private static final int ATTACK_RANGE = 12;
    // --- Timings (en ticks)
    private static final int RUSH_DURATION = 50;   // 2.5s
    private static final int DOUBLE_DURATION = 70; // 3.5s
    private static final int TRIPLE_DURATION = 80; // 4.0s

    private static final int RUSH_DASH_TICK = 22;

    private static final int DOUBLE_HIT_T1 = 20;
    private static final int DOUBLE_HIT_T2 = 40;

    private static final int TRIPLE_HIT_T1 = 16;
    private static final int TRIPLE_HIT_T2 = 41;
    private static final int TRIPLE_HIT_T3 = 60;

    private int distanceTickCounter = 0;
    private static final int RUSH_TRIGGER_TICKS = 90;
    private static final double RUSH_MIN_RANGE = 5;  // un peu plus loin que la portée normale
    private static final double RUSH_MAX_RANGE = 16;  // pas trop loin non plus


    private enum AttackType {
        NONE, RUSH, DOUBLE1, DOUBLE2, TRIPLE
    }

    public DraugrOverlordMeleeAttackGoal(DraugrOverlordEntity mob, double speed) {
        this.mob = mob;
        this.speed = speed;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        if (mob.getSpecial() || mob.getSpecialCooldown() < 35) return false;

        LivingEntity target = mob.getTarget();
        if (target instanceof PlayerEntity player && (player.isCreative() || player.isSpectator()))
            return false;

        return target != null && target.isAlive();
    }

    @Override
    public boolean shouldContinue() {
        if (mob.getSpecial() || mob.getSpecialCooldown() < 35) return false;

        LivingEntity target = mob.getTarget();
        if (target instanceof PlayerEntity player && (player.isCreative() || player.isSpectator()))
            return false;

        return target != null && target.isAlive();
    }

    @Override
    public void start() {
        mob.setAttacking(true);
        hasDashed = false;
        cooldown = 0;
        mob.getNavigation().startMovingTo(this.mob.getTarget(), speed);
    }

    @Override
    public void stop() {
        mob.setAttacking(false);
        mob.setSwinging(false);
        hasDashed = false;
        currentAttack = AttackType.NONE;
        nextAttack = AttackType.NONE;

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
            mob.getLookControl().lookAt(target, 20.0F, 20.0F);
            handleAttack(target);
        } else {
            stop();
        }
    }

    private void handleAttack(LivingEntity target) {
        cooldown = Math.max(cooldown - 1, 0);
        double distSq = mob.squaredDistanceTo(target.getX(), target.getY(), target.getZ());

        // --- Gestion du compteur de distance pour le RUSH automatique ---
        double dist = Math.sqrt(distSq);
        if (dist > RUSH_MIN_RANGE && dist < RUSH_MAX_RANGE) {
            distanceTickCounter++;
            mob.getWorld().sendEntityStatus(mob, (byte) 14); // debug visuel facultatif (petit effet cœur/critique)
            if (distanceTickCounter >= RUSH_TRIGGER_TICKS && !mob.isSwinging() && cooldown == 0) {
                currentAttack = AttackType.RUSH;
                startAttackAnimation();
                distanceTickCounter = 0;
                return;
            }
        } else {
            distanceTickCounter = 0; // reset si trop proche ou trop loin
        }

        // --- Comportement standard ---
        if (!mob.isSwinging()) {
            // Si le boss est proche et prêt à attaquer
            if (distSq < 10 && cooldown == 0) {
                if (nextAttack != AttackType.NONE) {
                    currentAttack = nextAttack;
                } else {
                    currentAttack = pickRandomAttack();
                }
                startAttackAnimation();
            } else {
                mob.getNavigation().startMovingTo(target, speed);
            }
        } else {
            // Exécuter l’attaque en cours
            switch (currentAttack) {
                case RUSH -> attackRush(target);
                case DOUBLE1, DOUBLE2 -> attackDouble(target);
                case TRIPLE -> attackTriple(target);
            }
        }
    }


    // --- Attaque 1 : RUSH (1 dash + 1 coup)
    private void attackRush(LivingEntity target) {
        int tick = mob.incrementAttackTick();
        freezeMovement();

        if (tick == RUSH_DASH_TICK && !hasDashed) {
            dashToward(target, 3);
            int randomType = 1 + this.mob.getRandom().nextInt(2);
            this.playSwingSound(randomType);
            hasDashed = true;
        }

        if (tick == RUSH_DASH_TICK + 5 && mob.squaredDistanceTo(target) <= ATTACK_RANGE) {
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 4 * 20, 1));
            mob.tryAttack(target);
            performAoeAttack(target, 2.0, 0.7F); // rayon 2 blocs, 70% dégâts
        }

        if (tick >= RUSH_DURATION) {
            endAttack();
        }
    }

    private void attackDouble(LivingEntity target) {
        int tick = mob.incrementAttackTick();
        freezeMovement();

        // Dash 4 ticks avant chaque coup (même logique que la triple)
        if (tick == DOUBLE_HIT_T1 - 4 || tick == DOUBLE_HIT_T2 - 4) {
            dashToward(target, 1.4);
            int randomType = 1 + this.mob.getRandom().nextInt(2);
            this.playSwingSound(randomType);
        }

        // Frappe si proche
        if ((tick == DOUBLE_HIT_T1 || tick == DOUBLE_HIT_T2) && mob.squaredDistanceTo(target) <= ATTACK_RANGE) {
            mob.tryAttack(target);
            performAoeAttack(target, 2.0, 0.7F); // rayon 2 blocs, 70% dégâts
        }

        if (tick >= DOUBLE_DURATION) {
            endAttack();
        }
    }


    // --- Attaque 3 : Triple (3 coups + dash avant)
    private void attackTriple(LivingEntity target) {
        int tick = mob.incrementAttackTick();
        freezeMovement();

        if (tick == TRIPLE_HIT_T1 - 2 || tick == TRIPLE_HIT_T2 - 2) {
            this.playSwingSound(3);
        } else if (tick == TRIPLE_HIT_T3 - 2) {
            int randomType = 1 + this.mob.getRandom().nextInt(2);
            this.playSwingSound(randomType);
        }

        if (tick == TRIPLE_HIT_T1 - 4 || tick == TRIPLE_HIT_T2 - 4 || tick == TRIPLE_HIT_T3 - 4)
            dashToward(target, 1.4);

        if ((tick == TRIPLE_HIT_T1 || tick == TRIPLE_HIT_T2 || tick == TRIPLE_HIT_T3) && mob.squaredDistanceTo(target) <= ATTACK_RANGE) {
            mob.tryAttack(target);
            performAoeAttack(target, 2.0, 0.7F);
        }

        if (tick >= TRIPLE_DURATION) {
            endAttack();
        }
    }

    private AttackType pickRandomAttack() {
        double r = random();

        if (r < 0.3) {
            return AttackType.DOUBLE1;
        } else if (r < 0.6) {
            return AttackType.DOUBLE2;
        } else {
            return AttackType.TRIPLE;
        }
    }

    private void startAttackAnimation() {
        mob.resetAttackTick();
        mob.setSwinging(true);
        hasDashed = false;
        freezeMovement();

        nextAttack = pickRandomAttack();

        if (mob.getRandom().nextInt(4) == 0) { // 1 chance sur 5
            if (mob.getRandom().nextBoolean()) {
                mob.playSound(ModSounds.DRAUGR_TAUNT_1, 1.0F, 1.0F);
            } else {
                mob.playSound(ModSounds.DRAUGR_TAUNT_2, 1.0F, 1.0F);
            }
        }

        switch (currentAttack) {
            case RUSH -> {
                mob.setAttackName("sl_attack_rush");
                mob.triggerAnim("attacking", mob.getAttackName());
            }
            case DOUBLE1 -> {
                mob.setAttackName("sl_attack_double1");
                mob.triggerAnim("attacking", mob.getAttackName());
            }
            case DOUBLE2 -> {
                mob.setAttackName("sl_attack_double2");
                mob.triggerAnim("attacking", mob.getAttackName());
            }
            case TRIPLE -> {
                mob.setAttackName("sl_attack_triple");
                mob.triggerAnim("attacking", mob.getAttackName());
            }
        }
    }

    private void freezeMovement() {
        Objects.requireNonNull(mob.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED))
                .setBaseValue(0.0D);
        mob.getNavigation().stop();
    }

    private void endAttack() {
        mob.setSwinging(false);
        hasDashed = false;
        mob.resetAttackTick();

        Objects.requireNonNull(mob.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED))
                .setBaseValue(originalSpeed);

        if (nextAttack == AttackType.NONE) {
            nextAttack = pickRandomAttack();
        }

        cooldown = 20;
        currentAttack = AttackType.NONE;
    }

    private void dashToward(LivingEntity target, double basePower) {
        Vec3d dir = target.getPos().subtract(mob.getPos()).normalize();

        float yaw = (float) (MathHelper.atan2(dir.z, dir.x) * (180.0 / Math.PI)) - 90.0F;
        mob.setYaw(yaw);
        mob.setHeadYaw(yaw);
        mob.setBodyYaw(yaw);
        mob.prevYaw = yaw;
        mob.prevBodyYaw = yaw;

        double dist = mob.distanceTo(target);

        // --- Modulateur de puissance en fonction de la distance ---
        double minDist = 6.0;    // trop proche → dash faible
        double maxDist = 14.0;   // trop loin → dash plafonné
        double minPower = basePower * 0.7;
        double maxPower = basePower * 2.2;

        // Clamp la distance entre min et max pour calculer une interpolation lissée
        double clamped = MathHelper.clamp((dist - minDist) / (maxDist - minDist), 0.0, 1.0);
        double scaledPower = MathHelper.lerp(clamped, minPower, maxPower);

        // --- Application du dash ---
        Vec3d dash = new Vec3d(
                -MathHelper.sin(mob.getYaw() * ((float) Math.PI / 180F)) * scaledPower,
                0.05,
                MathHelper.cos(mob.getYaw() * ((float) Math.PI / 180F)) * scaledPower
        );

        mob.addVelocity(dash.x, dash.y, dash.z);
        mob.velocityDirty = true;

        // --- Optionnel : Debug visuel pour voir la force du dash ---
        if (mob.getWorld().isClient()) {
            mob.getWorld().addParticle(
                    ParticleTypes.CRIT,
                    mob.getX(),
                    mob.getY() + 1.5,
                    mob.getZ(),
                    0, 0.2, 0
            );
        }
    }
    // Inflige une AOE autour de la cible principale (sans toucher les Draugr)
    private void performAoeAttack(LivingEntity mainTarget, double radius, float damageMultiplier) {
        var world = mob.getWorld();
        if (world.isClient()) return;

        double baseDamage = mob.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        float aoeDamage = (float) (baseDamage * damageMultiplier);

        // Récupère les entités vivantes dans le rayon autour de la cible (sauf le boss, la cible principale et les Draugr)
        world.getOtherEntities(mob, mainTarget.getBoundingBox().expand(radius), e ->
                e instanceof LivingEntity le
                        && le.isAlive()
                        && le != mob
                        && le != mainTarget
                        && !(e instanceof net.mebahel.antiquebeasts.entity.custom.other.DraugrEntity) // <-- exclut les Draugr
        ).forEach(entity -> {
            LivingEntity nearby = (LivingEntity) entity;

            // Dégâts secondaires AOE (pas de tryAttack pour éviter les effets "on hit" du mob)
            nearby.damage(world.getDamageSources().mobAttack(mob), aoeDamage);

            // Optionnel : petit knockback radial pour le feedback
            Vec3d push = nearby.getPos().subtract(mob.getPos()).normalize().multiply(0.35);
            nearby.addVelocity(push.x, 0.15, push.z);

            // Optionnel : particule d’impact
            world.addParticle(ParticleTypes.SWEEP_ATTACK,
                    nearby.getX(), nearby.getBodyY(0.5), nearby.getZ(),
                    0, 0.05, 0);
        });
    }

    private void playSwingSound(int soundType) {
        SoundEvent soundEvent;

        // Sélection du son selon le type
        switch (soundType) {
            case 2 -> soundEvent = ModSounds.WEAPON_SWORD_SLASH_RING;
            case 3 -> soundEvent = ModSounds.WEAPON_SWORD_SLASH;
            default -> soundEvent = ModSounds.WEAPON_SWORD_IMPACT;
        }

        // Pitch aléatoire entre 0.8 et 1.2 par ex.
        float pitch = 0.8F + this.mob.getRandom().nextFloat() * 0.4F;

        // Volume aléatoire léger si tu veux un peu de variation
        float volume = 0.75F + this.mob.getRandom().nextFloat() * 0.2F;

        this.mob.getWorld().playSound(
                null, // null = audible pour tous les joueurs proches
                this.mob.getX(),
                this.mob.getY(),
                this.mob.getZ(),
                soundEvent,
                this.mob.getSoundCategory(),
                volume,
                pitch
        );
    }
}