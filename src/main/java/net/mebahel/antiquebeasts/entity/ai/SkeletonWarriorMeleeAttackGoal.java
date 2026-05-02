package net.mebahel.antiquebeasts.entity.ai;

import net.mebahel.antiquebeasts.entity.custom.other.DraugrEntity;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;
import java.util.Objects;

import static java.lang.Math.random;

/**
 * SkeletonWarrior melee:
 * - only 2 animations: "attack" and "attack2"
 * - both: duration 1.5s (30 ticks), hit at 0.58s (~12 ticks)
 */
public class SkeletonWarriorMeleeAttackGoal extends Goal {
    protected final DraugrEntity mob;
    private final double speed;

    /** Delay entre deux attaques (verrou après l’anim). */
    private final int max_cooldown;

    // conservés pour compat éventuelle, mais non utilisés pour la timeline
    @SuppressWarnings("unused")
    private final int damage_time;
    @SuppressWarnings("unused")
    private final int dash_time;

    private int cooldown;

    private boolean hasDashed = false;
    private boolean hasHit = false;

    private final double originalSpeed = 0.3D;

    /** Compteur local pour l’anim / la timeline de l’attaque */
    private int attackTicks = 0;

    private enum AttackVariant {
        NONE,
        ATTACK1, // "attack"
        ATTACK2  // "attack2"
    }

    private AttackVariant currentAttack = AttackVariant.NONE;

    // =========================
    // TIMINGS (ticks) - 20 ticks = 1 seconde
    // =========================
    // 1.5s => 30 ticks
    private static final int SIMPLE_DURATION = 30;

    // 0.58s => 0.58 / 0.05 = 11.6 -> tick 12
    private static final int SIMPLE_HIT_TICK = 12;

    // Dash 0.2s avant l'impact (comme ton ancienne logique: hit - 4 ticks)
    private static final int SIMPLE_DASH_TICK = SIMPLE_HIT_TICK - 4; // 8

    private static final double ATTACK_RANGE = 5.0;

    public SkeletonWarriorMeleeAttackGoal(DraugrEntity mob, double speed, int max_cooldown, int damage_time, int dash_time) {
        this.mob = mob;
        this.speed = speed;
        this.max_cooldown = max_cooldown;
        this.damage_time = damage_time;
        this.dash_time = dash_time;

        // prêt "bientôt" : décrémente quand pas en attaque
        this.cooldown = this.max_cooldown + 1;

        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        LivingEntity target = mob.getTarget();
        if (target instanceof PlayerEntity player && (player.isCreative() || player.isSpectator())) return false;
        return target != null && target.isAlive() && this.mob.getHasSpawned();
    }

    @Override
    public boolean shouldContinue() {
        LivingEntity target = mob.getTarget();
        if (target instanceof PlayerEntity player && (player.isCreative() || player.isSpectator())) return false;
        return target != null && target.isAlive() && this.mob.getHasSpawned();
    }

    @Override
    public void start() {
        mob.setAttacking(true);

        resetAttackState();

        mob.setBlocking(false);
        mob.setUsingPotion(false);
        mob.clearBlockRequest();
        mob.clearPotionUseRequest();

        LivingEntity target = mob.getTarget();
        if (target != null) {
            mob.getNavigation().startMovingTo(target, speed);
        }
    }

    @Override
    public void stop() {
        mob.setAttacking(false);
        mob.setSwinging(false);

        resetAttackState();
        this.mob.setHealTicks(0);

        mob.setBlocking(false);
        mob.setUsingPotion(false);
        mob.clearBlockRequest();
        mob.clearPotionUseRequest();

        Objects.requireNonNull(mob.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED))
                .setBaseValue(originalSpeed);
    }

    private void resetAttackState() {
        hasDashed = false;
        hasHit = false;
        currentAttack = AttackVariant.NONE;
        attackTicks = 0;
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        // Si le mob boit / heal, melee stop
        if (mob.isUsingPotion() || mob.getHealTicks() > 0) {
            mob.setSwinging(false);
            mob.getNavigation().stop();
            resetAttackState();
            return;
        }

        LivingEntity target = mob.getTarget();
        if (target != null && target.isAlive()) {
            mob.getLookControl().lookAt(target, 15.0F, 15.0F);
            attack(target);
        } else {
            stop();
        }
    }

    protected void attack(LivingEntity target) {
        // Si en blocage ou boit -> ne pas attaquer
        if (mob.isBlocking() || mob.isUsingPotion()) {
            mob.setSwinging(false);
            resetAttackState();
            return;
        }

        double distSq = mob.squaredDistanceTo(target.getX(), target.getY(), target.getZ());

        // Décrément cooldown uniquement si pas en attaque
        if (!mob.isSwinging() && cooldown > 0) {
            cooldown = Math.max(cooldown - 1, 0);
        }

        // déplacement normal quand pas en train de frapper
        if (!mob.isSwinging()) {
            var attr = Objects.requireNonNull(mob.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED));
            if (attr.getBaseValue() == 0.0D) {
                attr.setBaseValue(originalSpeed);
            }
            mob.getNavigation().startMovingTo(target, speed);
        }

        // look / face target pendant l'attaque
        if (mob.isSwinging()) {
            Vec3d dir = target.getPos().subtract(mob.getPos()).normalize();
            float yaw = (float) (MathHelper.atan2(dir.z, dir.x) * (180.0 / Math.PI)) - 90.0F;
            mob.setYaw(yaw);
            mob.setHeadYaw(yaw);
            mob.prevYaw = yaw;
        }

        // Lancer une nouvelle attaque
        if (!mob.isSwinging()
                && distSq <= ATTACK_RANGE + 1
                && cooldown == 0) {
            startNewAttackPattern();
        }

        // Exécuter l'attaque en cours
        if (mob.isSwinging()) {
            attackTicks++;
            handleSimpleAttack(target);
        }
    }

    // -------------------
    // LANCEMENT ATTAQUE
    // -------------------
    private void startNewAttackPattern() {
        mob.setSwinging(true);

        hasDashed = false;
        hasHit = false;
        attackTicks = 0;

        // Freeze move pendant l'anim
        Objects.requireNonNull(mob.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED))
                .setBaseValue(0.0D);

        // Choix 50/50 attack vs attack2 (ajuste si tu veux)
        if (random() < 0.5) {
            currentAttack = AttackVariant.ATTACK1;
            mob.setAttackName("attack");
        } else {
            currentAttack = AttackVariant.ATTACK2;
            mob.setAttackName("attack2");
        }

        // trigger anim geckolib
        mob.triggerAnim("attacking", mob.getAttackName());

        // IMPORTANT: on ne met PAS cooldown ici.
        // cooldown est un verrou entre attaques, géré en fin d'anim.
    }

    // -------------------
    // ATTAQUES SIMPLES (attack / attack2)
    // Hit à 0.58s (tick 12), durée 1.5s (30 ticks)
    // -------------------
    private void handleSimpleAttack(LivingEntity target) {
        double distSq = mob.squaredDistanceTo(target.getX(), target.getY(), target.getZ());

        // dash avant impact
        if (!hasDashed && attackTicks == SIMPLE_DASH_TICK) {
            dashToward(target, 0.8);
            hasDashed = true;
        }

        // hit unique
        if (!hasHit && attackTicks == SIMPLE_HIT_TICK) {
            if (distSq <= ATTACK_RANGE + 1) {
                mob.tryAttack(target);
            }
            playSwingSound();
            hasHit = true;
        }

        // fin d'anim
        if (attackTicks >= SIMPLE_DURATION) {
            endAttack(target);
        }
    }

    // -------------------
    // FIN D’ATTAQUE
    // -------------------
    private void endAttack(LivingEntity target) {
        mob.setSwinging(false);

        resetAttackState();

        Objects.requireNonNull(mob.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED))
                .setBaseValue(originalSpeed);

        // Cooldown avant prochaine attaque (verrou gameplay)
        cooldown = max_cooldown + 2;

    }

    private void playSwingSound() {
        this.mob.getWorld().playSound(
                null,
                this.mob.getX(),
                this.mob.getY(),
                this.mob.getZ(),
                ModSounds.SWING,
                this.mob.getSoundCategory(),
                0.75F + this.mob.getRandom().nextFloat() * 0.2F,
                0.8F + this.mob.getRandom().nextFloat() * 0.4F
        );
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

        double minDist = 2.0;
        double maxDist = 10.0;
        double minPower = basePower * 0.7;
        double maxPower = basePower * 2.2;

        double clamped = MathHelper.clamp((dist - minDist) / (maxDist - minDist), 0.0, 1.0);
        double scaledPower = MathHelper.lerp(clamped, minPower, maxPower);

        Vec3d dash = new Vec3d(
                -MathHelper.sin(mob.getYaw() * ((float) Math.PI / 180F)) * scaledPower,
                0.05,
                MathHelper.cos(mob.getYaw() * ((float) Math.PI / 180F)) * scaledPower
        );

        mob.addVelocity(dash.x, dash.y, dash.z);
        mob.velocityDirty = true;
    }
}
