package net.mebahel.antiquebeasts.entity.ai.other;

import net.mebahel.antiquebeasts.entity.custom.other.DraugrEntity;
import net.mebahel.antiquebeasts.util.entity.MovementUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;

import java.util.EnumSet;
import java.util.Objects;

public class DraugrDrinkPotionGoal extends Goal {

    private final DraugrEntity mob;
    private final MovementUtil movementUtil;

    private static final int STRAFE_DISTANCE = 2;
    private static final int DRINK_DURATION_TICKS = 60; // ~3s

    // ⬇️ compteur local, comme blockTicks dans DraugrBlockGoal
    private int drinkTicks = 0;

    public DraugrDrinkPotionGoal(DraugrEntity mob) {
        this.mob = mob;
        this.movementUtil = new MovementUtil(this.mob);
        this.movementUtil.setStrafeDistance(STRAFE_DISTANCE);

        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    private void log(String msg) {
        System.out.println("[DRAUGR_POTION] " + msg);
    }

    @Override
    public boolean canStart() {
        LivingEntity target = mob.getTarget();
        if (target == null || !target.isAlive()) return false;

        // Doit avoir une potion
        if (!mob.hasPotion()) return false;

        // On évite de stacker avec blocage / potion déjà en cours
        if (mob.isBlocking()) return false;
        if (mob.isUsingPotion() || mob.getHealTicks() > 0) return false;

        int type = mob.getPotionType();
        float hpRatio = mob.getHealth() / mob.getMaxHealth();

        // --- 🎯 RÈGLE SPÉCIALE INVISIBILITÉ ---
        if (type == DraugrEntity.POTION_INVISIBILITY) {
            // On la boit UNIQUEMENT si HP < 65%
            if (hpRatio < 0.65f) {
                log("HP<65%, USING INVIS potion, type=" + type);
                return true;
            } else {
                // HP trop haut → il NE doit PAS l'utiliser
                return false;
            }
        }

        // --- 🎯 Sinon, auto-start si buff potion + cible joueur ---
        boolean isBuffPotion =
                type == DraugrEntity.POTION_STRENGTH ||
                        type == DraugrEntity.POTION_RESISTANCE ||
                        type == DraugrEntity.POTION_SPEED;

        if (isBuffPotion && target instanceof PlayerEntity) {
            log("AUTO-START potion buff vs player, type=" + type);
            return true;
        }

        // --- Sinon, potion de heal ou cas générique : il faut une demande explicite
        if (!mob.wantsToDrinkPotion()) return false;

        return true;
    }

    @Override
    public boolean shouldContinue() {
        LivingEntity target = mob.getTarget();
        if (target == null || !target.isAlive()) return false;

        // comme le block : basé sur le compteur local
        return drinkTicks > 0 && mob.isUsingPotion();
    }

    @Override
    public void start() {
        // On verrouille le mouvement "normal"
        mob.getNavigation().stop();
        mob.getMoveControl().strafeTo(0, 0);

        mob.setUsingPotion(true);
        mob.clearPotionUseRequest(); // on consomme la demande éventuelle

        // durée interne du goal
        drinkTicks = DRINK_DURATION_TICKS;

        // durée pour l’anim / layer
        mob.setHealTicks(DRINK_DURATION_TICKS);

        // On remet sa vitesse de base (au cas où elle serait à 0)
        Objects.requireNonNull(mob.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED))
                .setBaseValue(0.3D);

        // 🔥 Déclenchement de l'animation de potion (une seule fois)
        mob.setAttackName("drink_potion");              // adapte le nom à ton .animation.json
        mob.triggerAnim("attacking", mob.getAttackName());

        log("POTION START type=" + mob.getPotionType());
    }

    @Override
    public void stop() {
        mob.setHasPotion(false);

        mob.setUsingPotion(false);
        mob.setHealTicks(0);
        drinkTicks = 0;

        mob.getNavigation().stop();
        mob.getMoveControl().strafeTo(0, 0);

        // On calme la vélocité résiduelle, sans recovery artificiel
        var vel = mob.getVelocity();
        mob.setVelocity(vel.x * 0.2, vel.y, vel.z * 0.2);

        log("POTION END");
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity target = mob.getTarget();
        if (target == null || !target.isAlive()) {
            drinkTicks = 0;
            mob.setHealTicks(0);
            return;
        }

        // 🔁 EXACTEMENT comme le block : on décrémente d’abord
        drinkTicks--;

        // On tient aussi à jour healTicks côté entité (pour l’anim / layer)
        int healTicks = mob.getHealTicks();
        if (healTicks > 0) {
            mob.setHealTicks(healTicks - 1);
        }

        // Si on arrive à la fin, on coupe tout mouvement ici
        if (drinkTicks <= 0) {
            mob.getNavigation().stop();
            mob.getMoveControl().strafeTo(0, 0);
            var vel = mob.getVelocity();
            mob.setVelocity(vel.x * 0.2, vel.y, vel.z * 0.2);
            return;
        }

        // bruits de boisson à certains ticks (basés sur healTicks de l’entity)
        int t = mob.getHealTicks();
        if (t == 45 || t == 40 || t == 35 || t == 30 || t == 25 || t == 20) {
            this.mob.playSound(net.minecraft.sound.SoundEvents.ENTITY_GENERIC_DRINK,
                    1.0F, 0.9F + this.mob.getRandom().nextFloat() * 0.2F);
        } else if (t == 15)
            applyPotionEffect();

        // 🔸 Mouvements défensifs pendant qu’il boit (comme block)
        double distanceToTarget = this.mob.distanceTo(target);

        this.movementUtil.lookAtTarget(target, this.mob);
        this.movementUtil.checkIfStuck(target, this.mob);

        if (!this.movementUtil.isSkyVisibleAbove(this.mob)) {
            this.movementUtil.strafeUnderground(target, this.mob);
        } else if (distanceToTarget <= STRAFE_DISTANCE) {
            this.movementUtil.moveBackward(target, this.mob);
        } else {
            this.movementUtil.strafeAroundTarget(target, this.mob);
        }
    }

    private void applyPotionEffect() {
        int type = mob.getPotionType();
        System.out.println("[DRAUGR_POTION_EFFECT] type=" + type);

        switch (type) {
            case DraugrEntity.POTION_HEAL -> {
                System.out.println("[DRAUGR_POTION_EFFECT] HEAL");
                mob.heal(30.0F);
            }
            case DraugrEntity.POTION_STRENGTH -> {
                System.out.println("[DRAUGR_POTION_EFFECT] STRENGTH");
                mob.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 120 * 20, 0));
            }
            case DraugrEntity.POTION_RESISTANCE -> {
                System.out.println("[DRAUGR_POTION_EFFECT] FIRE_RES");
                mob.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 120 * 20, 0));
            }
            case DraugrEntity.POTION_SPEED -> {
                System.out.println("[DRAUGR_POTION_EFFECT] SPEED");
                mob.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 120 * 20, 0));
            }
            case DraugrEntity.POTION_INVISIBILITY -> {
                System.out.println("[DRAUGR_POTION_EFFECT] INVIS");
                mob.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 120 * 20, 0));
            }
            default -> {
                System.out.println("[DRAUGR_POTION_EFFECT] NONE");
            }
        }
    }
}
