package net.mebahel.antiquebeasts.entity.ai.other;

import net.mebahel.antiquebeasts.entity.custom.other.DraugrEntity;
import net.mebahel.antiquebeasts.entity.custom.other.DraugrOverlordEntity;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;
import java.util.Objects;

import static net.mebahel.antiquebeasts.entity.custom.other.DraugrOverlordEntity.AreaCrackedGround;

public class DraugrOverlordSpecialAttackGoal extends Goal {
    private final DraugrOverlordEntity overlord;

    public DraugrOverlordSpecialAttackGoal(DraugrOverlordEntity overlord) {
        this.overlord = overlord;
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
        this.overlord.setSpecialCooldown(100 + this.overlord.getRandom().nextInt(40));
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
            case 0 ->
                this.stop();
            case 4 -> {
                if (this.overlord.getRandom().nextInt(2) == 0) { // 1 chance sur 5
                    if (this.overlord.getRandom().nextBoolean()) {
                        this.overlord.playSound(ModSounds.DRAUGR_TAUNT_1, 1.0F, 1.0F);
                    } else {
                        this.overlord.playSound(ModSounds.DRAUGR_TAUNT_2, 1.0F, 1.0F);
                    }
                }
            }

            case 10-> {
                this.executeSpecialAttack(12, 1f);
                AreaCrackedGround(this.overlord, this.overlord.getWorld(), this.overlord.getBlockPos(), 12);
                this.overlord.playSound(ModSounds.WEAPON_GROUND_IMPACT, 1.0f, 0.8f);
            }
            case 18 -> {
                this.executeSpecialAttack(6,0.3f);
                AreaCrackedGround(this.overlord, this.overlord.getWorld(), this.overlord.getBlockPos(), 6);
                this.overlord.playSound(ModSounds.WEAPON_CRACKED_GROUND, 1.0f, 0.8f);
            }
            case 25 -> {
                Objects.requireNonNull(this.overlord.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(0f);
                this.overlord.triggerAnim("specialController", "sl_attack_quake");
                this.overlord.setSpecial(true);
            }
        }
    }

    private void executeSpecialAttack(int radius, float velocity) {
        List<LivingEntity> entities = this.overlord.getWorld().getEntitiesByClass(
                LivingEntity.class,
                this.overlord.getBoundingBox().expand(radius),
                entity -> !(entity instanceof DraugrEntity) && entity.isAlive()
        );

        for (LivingEntity entity : entities) {
            entity.damage(this.overlord.getWorld().getDamageSources().mobAttack(this.overlord), 20.0f);
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 6 * 20, 2));

            // ✅ Calculer la direction de repoussement
            double deltaX = entity.getX() - this.overlord.getX();
            double deltaZ = entity.getZ() - this.overlord.getZ();
            double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

            if (distance > 0) {
                entity.setVelocity(0, velocity, 0);
            }
        }
    }
}

