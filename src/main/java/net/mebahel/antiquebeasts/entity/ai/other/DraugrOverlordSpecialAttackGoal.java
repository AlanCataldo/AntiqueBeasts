package net.mebahel.antiquebeasts.entity.ai.other;

import net.mebahel.antiquebeasts.entity.custom.other.DraugrEntity;
import net.mebahel.antiquebeasts.entity.custom.other.DraugrOverlordEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;
import java.util.Objects;

public class DraugrOverlordSpecialAttackGoal extends Goal {
    private final DraugrOverlordEntity overlord;
    private final StatusEffect potionEffect;
    public DraugrOverlordSpecialAttackGoal(DraugrOverlordEntity overlord, StatusEffect effect) {
        this.overlord = overlord;
        this.potionEffect = effect;
    }

    public boolean canStart() {
        LivingEntity livingEntity = this.overlord.getTarget();

        if (livingEntity instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity) livingEntity;
            if (playerEntity.isCreative() || playerEntity.isSpectator()) {
                return false;
            }
        }
        return livingEntity != null && livingEntity.isAlive();
    }

    public void start() {
        if (this.overlord.getSpecialCooldown() < 60)
            this.overlord.setSpecialCooldown(60);
    }

    public void stop() {
        Objects.requireNonNull(this.overlord.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(this.overlord.speed);
        this.overlord.setSpecial(false);
        this.overlord.setSpecialCooldown(180);
    }

    public boolean shouldContinue() {
        LivingEntity livingEntity = this.overlord.getTarget();

        if (livingEntity instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity) livingEntity;
            if (playerEntity.isCreative() || playerEntity.isSpectator()) {
                return false;
            }
        }
        return livingEntity != null && livingEntity.isAlive() && this.overlord.getSpecialCooldown() != 0;
    }

    public void tick() {
        if (!this.overlord.isSwinging())
            this.overlord.setSpecialCooldown(this.overlord.getSpecialCooldown() - 1);

        switch (this.overlord.getSpecialCooldown()) {
            case 0 -> this.stop();
            case 17 -> this.executeSpecialAttack();
            case 25 -> {
                Objects.requireNonNull(this.overlord.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(0f);
                this.overlord.setSpecial(true);
            }
        }
    }

    private void executeSpecialAttack() {
        List<LivingEntity> entities = this.overlord.getWorld().getEntitiesByClass(
                LivingEntity.class,
                this.overlord.getBoundingBox().expand(10),
                entity -> !(entity instanceof DraugrEntity) && entity.isAlive()
        );

        for (LivingEntity entity : entities) {
            // ✅ Appliquer 15 de dégâts
            entity.damage(this.overlord.getWorld().getDamageSources().mobAttack(this.overlord), 15.0f);
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 4 * 20, 1));


            // ✅ Calculer la direction de repoussement
            double deltaX = entity.getX() - this.overlord.getX();
            double deltaZ = entity.getZ() - this.overlord.getZ();
            double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

            if (distance > 0) {
                double knockbackStrength = 1.5;
                entity.setVelocity(deltaX / distance * knockbackStrength, 0.5, deltaZ / distance * knockbackStrength);
            }
        }
    }
}

