package net.mebahel.antiquebeasts.entity.ai;

import net.mebahel.antiquebeasts.entity.custom.CyclopsEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import java.util.List;
import java.util.Objects;

public class CyclopsSocializeGoal extends Goal {
    private final CyclopsEntity cyclops;
    private CyclopsEntity mate; // Current entity the cyclops.json is trying to socialize with
    private final int MAX_SOCIALIZE_DISTANCE = 20; // Maximum distance to search for potential mates
    private int socializeTimer = 0; // Timer for how long the entities have been socializing
    private int cooldownTimer = 0; // Timer for how long the entities need to wait before socializing again
    private final double SOCIALIZE_DISTANCE = 8.0; // Distance that entities must be within to start socializing

    private final StatusEffect potionEffect; // The potion effect that the entities receive after socializing

    public CyclopsSocializeGoal(CyclopsEntity cyclops, StatusEffect effect) {
        this.cyclops = cyclops;
        this.potionEffect = effect;
    }

    // Checks if the entity can start socializing with a mate.
    public boolean canStart() {
        // Don't start if the entity already has the potion effect.
        if (this.cyclops.hasStatusEffect(this.potionEffect)) {
            return false;
        }
        // Don't start if already socializing.
        if (this.socializeTimer > 0) {
            return false;
        }
        // Don't start if still on cooldown.
        if (this.cooldownTimer > 0) {
            return false;
        }
        // Search for other cyclopses within range that aren't already socializing and can be seen.
        List<CyclopsEntity> cyclopses = this.cyclops.world.getEntitiesByClass(CyclopsEntity.class, this.cyclops.getBoundingBox().expand(MAX_SOCIALIZE_DISTANCE),
                cyclopsEntity -> cyclopsEntity != this.cyclops && !cyclopsEntity.hasStatusEffect(this.potionEffect) && this.cyclops.canSee(cyclopsEntity));

        // Pick the first mate found and return true if one exists.
        for (CyclopsEntity otherCyclops : cyclopses) {
            if (otherCyclops != this.cyclops && !otherCyclops.hasStatusEffect(this.potionEffect) && this.cyclops.canSee(otherCyclops)) {
                this.mate = otherCyclops;
                return true;
            }
        }
        // No mate found.
        return false;
    }

    // Start socializing with a mate.
    public void start() {
        this.socializeTimer = 0;
        Objects.requireNonNull(this.cyclops.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(0.32f);
    }

    // Stop socializing with a mate.
    public void stop() {
        this.socializeTimer = 0;
        this.cooldownTimer = 0;
        this.mate = null;
        Objects.requireNonNull(this.cyclops.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(0.72f);
    }

    // Checks if socializing should continue.
    public boolean shouldContinue() {
        // Continue socializing if still socializing, still on cooldown, or too far from mate.
        return this.socializeTimer > 0 || this.cooldownTimer > 0 || (this.mate != null && this.cyclops.squaredDistanceTo(this.mate) > SOCIALIZE_DISTANCE);
    }

    // This method represents the behavior of a CyclopsEntity when it is "ticking," or updating its state every tick.
    public void tick() {
        // If the CyclopsEntity currently has the specified potion effect, it stops its current behavior.
        if (this.cyclops.hasStatusEffect(this.potionEffect)) {
            this.stop();
        }
        // If the CyclopsEntity is currently socializing and within the appropriate distance of its mate, it continues socializing.
        if (this.socializeTimer > 0 && this.cyclops.squaredDistanceTo(this.mate) <= SOCIALIZE_DISTANCE) {
            // Socialize timer is decremented every tick.
            this.socializeTimer--;
            // When the socialize timer reaches zero, both the CyclopsEntity and its mate receive the specified potion effect and a cooldown timer is set.
            if (this.socializeTimer == 0) {
                this.cyclops.addStatusEffect(new StatusEffectInstance(this.potionEffect, 2400, 1));
                this.mate.addStatusEffect(new StatusEffectInstance(this.potionEffect, 2400, 1));
                this.cooldownTimer = 2400;
            }
        }
        // If the cooldown timer is active, it decrements every tick.
        if (this.cooldownTimer > 0) {
            this.cooldownTimer--;
        // If the CyclopsEntity is not currently socializing and its mate is too far away, it searches for a new mate within a certain distance.
        } else if (this.mate != null && this.cyclops.squaredDistanceTo(this.mate) > SOCIALIZE_DISTANCE) {
            // A list of CyclopsEntity objects within a certain distance of the current CyclopsEntity is created.
            List<CyclopsEntity> cyclopses = this.cyclops.world.getEntitiesByClass(CyclopsEntity.class, this.cyclops.getBoundingBox().expand(MAX_SOCIALIZE_DISTANCE),
                    cyclopsEntity -> cyclopsEntity != this.cyclops && !cyclopsEntity.hasStatusEffect(this.potionEffect) && this.cyclops.canSee(cyclopsEntity));
            // The CyclopsEntity selects the first available mate within range and begins socializing.
            for (CyclopsEntity otherCyclops : cyclopses) {
                if (otherCyclops != this.cyclops && !otherCyclops.hasStatusEffect(this.potionEffect) && this.cyclops.canSee(otherCyclops)) {
                    this.mate = otherCyclops;
                    this.socializeTimer = 100;
                    this.cyclops.getLookControl().lookAt(this.mate, 10.0F, 10.0F);
                    // If the selected mate is too far away, the CyclopsEntity moves towards it.
                    if (this.cyclops.squaredDistanceTo(this.mate) > SOCIALIZE_DISTANCE) {
                        this.cyclops.getLookControl().lookAt(this.mate, 10.0F, 10.0F);
                        this.cyclops.getNavigation().startMovingTo(this.mate, 0.72f);
                        this.cyclops.getNavigation().tick();
                    }
                    break;
                }
            }
        }
    }
}





