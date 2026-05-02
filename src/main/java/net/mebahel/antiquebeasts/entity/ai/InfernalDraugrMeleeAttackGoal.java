package net.mebahel.antiquebeasts.entity.ai;

import net.mebahel.antiquebeasts.entity.custom.other.DraugrWightEntity;
import net.mebahel.antiquebeasts.entity.custom.other.InfernalDraugrEntity;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.mebahel.antiquebeasts.util.entity.MovementUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;
import java.util.Objects;

import static java.lang.Math.random;

public class InfernalDraugrMeleeAttackGoal extends Goal {
    protected final InfernalDraugrEntity mob;
    private final double speed;
    private final int max_cooldown; // delay entre deux attaques
    private final int damage_time;  // pour les attaques simples

    private int cooldown;
    private final double originalSpeed = 0.3D;

    // Compteur local pour l’anim / la timeline de l’attaque
    private int attackTicks = 0;

    // Strafe util (si tu veux le réutiliser plus tard)
    private final MovementUtil movementUtil;
    private static final int STRAFE_DISTANCE = 2;

    // Variantes d’attaque (même logique que le Draugr)
    private enum AttackVariant {
        NONE,
        SIMPLE1,   // "attack"
        SIMPLE2,   // "attack2"
        COMBO1,    // "attack_combo1"
        COMBO2     // "attackcombo2"
    }

    private AttackVariant currentAttack = AttackVariant.NONE;

    // === TIMINGS COMBOS (en ticks) ===
    // attack_combo1 : 2.21s, impacts 0.5s et 1.54s
    private static final int COMBO1_DURATION    = 44;
    private static final int COMBO1_HIT_1_TICK  = 10;
    private static final int COMBO1_HIT_2_TICK  = 31;

    // attackcombo2 : 2.5s, impacts 0.54s et 1.67s
    private static final int COMBO2_DURATION    = 50;
    private static final int COMBO2_HIT_1_TICK  = 11;
    private static final int COMBO2_HIT_2_TICK  = 33;

    // Portée identique au Draugr (check avec ATTACK_RANGE + 1)
    private static final double ATTACK_RANGE = 5.0;

    public InfernalDraugrMeleeAttackGoal(InfernalDraugrEntity mob, double speed, int max_cooldown, int damage_time) {
        this.mob = mob;
        this.speed = speed;
        this.max_cooldown = max_cooldown;
        this.damage_time = damage_time;
        this.cooldown = this.max_cooldown + 1;

        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));

        this.movementUtil = new MovementUtil(this.mob, 3);
    }

    @Override
    public boolean canStart() {
        LivingEntity target = mob.getTarget();
        if (target instanceof PlayerEntity player &&
                (player.isCreative() || player.isSpectator()))
            return false;

        if (mob.isShooting() || mob.isDodging()) return false;

        return target != null && target.isAlive() && this.mob.getHasSpawned();
    }

    @Override
    public boolean shouldContinue() {
        LivingEntity target = mob.getTarget();
        if (target instanceof PlayerEntity player &&
                (player.isCreative() || player.isSpectator()))
            return false;

        return target != null && target.isAlive() && !mob.isShooting() && this.mob.getHasSpawned() && !mob.isDodging();
    }

    @Override
    public void start() {
        mob.setAttacking(true);
        currentAttack = AttackVariant.NONE;
        attackTicks = 0;

        mob.setBlocking(false);
        mob.setUsingPotion(false);
        mob.clearBlockRequest();
        mob.clearPotionUseRequest();

        LivingEntity target = mob.getTarget();
        if (target != null) {
            mob.getNavigation().startMovingTo(target, speed);
        }

        Objects.requireNonNull(
                mob.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)
        ).setBaseValue(originalSpeed);
    }

    @Override
    public void stop() {
        mob.setAttacking(false);
        mob.setSwinging(false);
        currentAttack = AttackVariant.NONE;
        attackTicks = 0;

        mob.setBlocking(false);
        mob.setUsingPotion(false);
        mob.clearBlockRequest();
        mob.clearPotionUseRequest();
        mob.setHealTicks(0);

        Objects.requireNonNull(
                mob.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)
        ).setBaseValue(originalSpeed);
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (mob.isUsingPotion() || mob.getHealTicks() > 0 ||
        mob.isShooting() || mob.isDodging()) {
            mob.setSwinging(false);
            mob.getNavigation().stop();
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
        if (mob.isBlocking() || mob.isUsingPotion()) {
            mob.setSwinging(false);
            return;
        }

        // Check potion
        float hpRatio = mob.getHealth() / mob.getMaxHealth();
        if (shouldRequestPotion(hpRatio)) {
            mob.requestPotionUse();

            mob.setSwinging(false);
            currentAttack = AttackVariant.NONE;
            attackTicks = 0;

            Objects.requireNonNull(
                    mob.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)
            ).setBaseValue(originalSpeed);

            return;
        }

        double squaredDistance = mob.squaredDistanceTo(target.getX(), target.getY(), target.getZ());

        // Décrément du cooldown SEULEMENT quand pas en train de frapper
        if (!mob.isSwinging() && cooldown > 0) {
            cooldown = Math.max(cooldown - 1, 0);
        }

        // Déplacement normal tant qu’il n’est pas en train de frapper
        if (!mob.isSwinging()) {
            var attr = Objects.requireNonNull(
                    mob.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)
            );
            if (attr.getBaseValue() == 0.0D) {
                attr.setBaseValue(originalSpeed);
            }
            mob.getNavigation().startMovingTo(target, speed);
        }

        // Toujours regarder la cible pendant l’attaque
        if (mob.isSwinging()) {
            Vec3d dir = target.getPos().subtract(mob.getPos()).normalize();
            float yaw = (float)(MathHelper.atan2(dir.z, dir.x) * (180.0 / Math.PI)) - 90.0F;

            mob.setYaw(yaw);
            mob.setHeadYaw(yaw);
            mob.prevYaw = yaw;
        }

        // === LANCEMENT D’UNE NOUVELLE ATTAQUE ===
        if (!mob.isSwinging()
                && squaredDistance <= ATTACK_RANGE + 1
                && cooldown == 0) {

            startNewAttackPattern();
        }

        // === EXÉCUTION DE L’ATTAQUE EN COURS ===
        if (mob.isSwinging()) {
            attackTicks++;

            switch (currentAttack) {
                case SIMPLE1, SIMPLE2 -> handleSimpleAttack(target);
                case COMBO1 -> handleCombo1(target);
                case COMBO2 -> handleCombo2(target);
                case NONE -> endAttack(target); // sécurité
            }
        }
    }

    // -------------------
    // LANCEMENT PATTERN
    // -------------------
    private void startNewAttackPattern() {
        mob.setSwinging(true);
        attackTicks = 0;

        // Freeze move
        Objects.requireNonNull(
                mob.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)
        ).setBaseValue(0.0D);

        //  - 30% SIMPLE1
        //  - 30% SIMPLE2
        //  - 20% COMBO1
        //  - 20% COMBO2
        double r = random();
        if (r < 0.30) {
            currentAttack = AttackVariant.SIMPLE1;
            mob.setAttackName("attack");
        } else if (r < 0.60) {
            currentAttack = AttackVariant.SIMPLE2;
            mob.setAttackName("attack2");
        } else if (r < 0.80) {
            currentAttack = AttackVariant.COMBO1;
            mob.setAttackName("attack_combo1");
        } else {
            currentAttack = AttackVariant.COMBO2;
            mob.setAttackName("attack_combo2");
        }

        // TRIGGER anim Geckolib
        mob.triggerAnim("attacking", mob.getAttackName());

        // Reset cooldown : sera remis à max_cooldown en fin d’attaque
        cooldown = max_cooldown;
    }

    // -------------------
    // ATTAQUES SIMPLES
    // -------------------
    private void handleSimpleAttack(LivingEntity target) {
        double distSq = mob.squaredDistanceTo(target.getX(), target.getY(), target.getZ());

        if (attackTicks == damage_time - 4) {
            dashToward(target, 0.8);
        }

        if (attackTicks == damage_time) {
            if (distSq <= ATTACK_RANGE + 1) {
                mob.tryAttack(target);
            }
            playSwingSound();
        }

        if (attackTicks >= max_cooldown) {
            endAttack(target);
        }
    }

    // -------------------
    // COMBO 1
    // -------------------
    private void handleCombo1(LivingEntity target) {
        double distSq = mob.squaredDistanceTo(target.getX(), target.getY(), target.getZ());

        if (attackTicks == COMBO1_HIT_1_TICK - 4 || attackTicks == COMBO1_HIT_2_TICK - 4) {
            dashToward(target, 0.8);
        }

        if (attackTicks == COMBO1_HIT_1_TICK) {
            if (distSq <= ATTACK_RANGE + 1) {
                mob.tryAttack(target);
            }
            playSwingSound();
        }

        if (attackTicks == COMBO1_HIT_2_TICK) {
            if (distSq <= ATTACK_RANGE + 1) {
                mob.tryAttack(target);
            }
            playSwingSound();
        }

        if (attackTicks >= COMBO1_DURATION) {
            endAttack(target);
        }
    }

    // -------------------
    // COMBO 2
    // -------------------
    private void handleCombo2(LivingEntity target) {
        double distSq = mob.squaredDistanceTo(target.getX(), target.getY(), target.getZ());

        if (attackTicks == COMBO2_HIT_1_TICK - 4 || attackTicks == COMBO2_HIT_2_TICK - 4) {
            dashToward(target, 0.8);
        }

        if (attackTicks == COMBO2_HIT_1_TICK) {
            if (distSq <= ATTACK_RANGE + 1) {
                mob.tryAttack(target);
            }
            playSwingSound();
        }

        if (attackTicks == COMBO2_HIT_2_TICK) {
            if (distSq <= ATTACK_RANGE + 1) {
                mob.tryAttack(target);
            }
            playSwingSound();
        }

        if (attackTicks >= COMBO2_DURATION) {
            endAttack(target);
        }
    }

    // -------------------
    // FIN D’ATTAQUE
    // -------------------
    private void endAttack(LivingEntity target) {
        mob.setSwinging(false);
        currentAttack = AttackVariant.NONE;
        attackTicks = 0;

        Objects.requireNonNull(
                mob.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)
        ).setBaseValue(originalSpeed);

        // Cooldown avant prochaine attaque
        cooldown = max_cooldown + 2;

        // Potentiellement block après l’attaque
        if (target != null && target.isAlive()) {
            tryStartBlockAfterAttack(target);
        }
    }

    private void tryStartBlockAfterAttack(LivingEntity target) {
        if (target.getMaxHealth() > 0) {
            float targetHpRatio = target.getHealth() / target.getMaxHealth();
            if (targetHpRatio < 0.3f) return;
        }

        float hpRatio = mob.getHealth() / mob.getMaxHealth();
        hpRatio = MathHelper.clamp(hpRatio, 0.3f, 1.0f);

        float t = (1.0f - hpRatio) / (1.0f - 0.3f);

        float minChance = 5 / 100f;
        float maxChance = 10 / 100f;

        float chance = minChance + t * (maxChance - minChance);
        float roll = mob.getRandom().nextFloat();

        if (roll < chance) {
            mob.requestBlock();

            mob.setSwinging(false);
            currentAttack = AttackVariant.NONE;
            attackTicks = 0;

            Objects.requireNonNull(
                    mob.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)
            ).setBaseValue(originalSpeed);

            cooldown = max_cooldown + 5;
        }
    }

    private boolean shouldRequestPotion(float hpRatio) {
        if (!mob.hasPotion()) return false;
        if (mob.wantsToDrinkPotion()) return false;
        if (mob.getHealTicks() > 0) return false;

        int type = mob.getPotionType();

        return switch (type) {
            case DraugrWightEntity.POTION_HEAL -> hpRatio < 0.4f;
            case DraugrWightEntity.POTION_STRENGTH -> hpRatio < 0.8f;
            case DraugrWightEntity.POTION_RESISTANCE -> mob.isOnFire() || hpRatio < 0.7f;
            default -> false;
        };
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
        if (target == null || !target.isAlive()) return;

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
