package net.mebahel.antiquebeasts.entity.ai;

import net.mebahel.antiquebeasts.entity.custom.norse.EinherjarEntity;
import net.mebahel.antiquebeasts.entity.custom.norse.NorseEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;

import java.util.List;
import java.util.Objects;

public class EinherjarHornGoal extends Goal {
    private final EinherjarEntity einherjar;
    private final StatusEffect potionEffect;
    public EinherjarHornGoal(EinherjarEntity einherjar, StatusEffect effect) {
        this.einherjar = einherjar;
        this.potionEffect = effect;
    }

    public boolean canStart() {
        return this.einherjar.getTarget() != null;
    }

    public void start() {
        if (this.einherjar.getHornCooldown() < 60)
            this.einherjar.setHornCooldown(60);
    }

    public void stop() {
        Objects.requireNonNull(this.einherjar.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(0.65f);
        this.einherjar.setHorn(false);
        this.einherjar.setHornCooldown(180);
    }

    public boolean shouldContinue() {
        return this.einherjar.getHornCooldown() != 0 && this.einherjar.getTarget() != null;
    }

    public void tick() {
        if (!this.einherjar.isSwinging())
            this.einherjar.setHornCooldown(this.einherjar.getHornCooldown() - 1);
        switch (this.einherjar.getHornCooldown()) {
            case 0 -> this.stop();
            case 10 -> {
                List<NorseEntity> entityList = this.einherjar.getWorld().getEntitiesByClass(NorseEntity.class, this.einherjar.getBoundingBox().expand(20),
                        cyclopsEntity -> cyclopsEntity != this.einherjar);
                for (NorseEntity norseUnit : entityList) {
                    norseUnit.addStatusEffect(new StatusEffectInstance(this.potionEffect, 300, 0));
                }
                this.einherjar.addStatusEffect(new StatusEffectInstance(this.potionEffect, 300, 0));
            }
            case 21 -> {
                Objects.requireNonNull(this.einherjar.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(0f);
                this.einherjar.setHorn(true);
            }
        }
    }
}
