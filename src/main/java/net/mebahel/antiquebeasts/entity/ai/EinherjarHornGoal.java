package net.mebahel.antiquebeasts.entity.ai;

import net.mebahel.antiquebeasts.entity.custom.EinherjarEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.AnimalEntity;

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
        if (this.einherjar.getHornCooldown() < 100)
            this.einherjar.setHornCooldown(100);
    }

    public void stop() {
        Objects.requireNonNull(this.einherjar.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(0.65f);
        this.einherjar.setHorn(false);
        this.einherjar.setHornCooldown(300);
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
                List<AnimalEntity> entityList = this.einherjar.getWorld().getEntitiesByClass(AnimalEntity.class, this.einherjar.getBoundingBox().expand(20),
                        cyclopsEntity -> cyclopsEntity != this.einherjar);
                for (AnimalEntity norseUnit : entityList) {
                    norseUnit.addStatusEffect(new StatusEffectInstance(this.potionEffect, 300, 0));
                }
                this.einherjar.addStatusEffect(new StatusEffectInstance(this.potionEffect, 300, 0));
            }
            case 26 -> {
                Objects.requireNonNull(this.einherjar.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(0f);
                this.einherjar.setHorn(true);
            }
        }
    }
}
