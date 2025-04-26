package net.mebahel.antiquebeasts.entity.ai.dwarven.dwarven_spider;

import net.mebahel.antiquebeasts.entity.custom.dwarven.DwarvenSpiderGuardianEntity;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;

public class DwarvenSpiderGuardianJumpAttackGoal extends Goal {
    private final DwarvenSpiderGuardianEntity guardian;
    private boolean hasDealtDamage = false;
    private int cooldown;

    private static final int MAX_COOLDOWN = 600; // 30s
    private static final double MIN_DISTANCE = 3.0;
    private static final double MAX_DISTANCE = 12.0;

    public DwarvenSpiderGuardianJumpAttackGoal(DwarvenSpiderGuardianEntity guardian) {
        this.guardian = guardian;
    }

    @Override
    public boolean canStart() {
        return this.guardian.getTarget() != null;
    }

    @Override
    public boolean shouldContinue() {
        LivingEntity livingEntity = this.guardian.getTarget();

        if (livingEntity instanceof PlayerEntity playerEntity) {
            if (playerEntity.isCreative() || playerEntity.isSpectator()) {
                return false;
            }
        }
        return livingEntity != null && livingEntity.isAlive();
    }

    @Override
    public void start() {
        this.guardian.setCooldown(90f);
    }
    public void stop() {
        this.hasDealtDamage = false;
        this.guardian.setShooting(false);
        this.guardian.setCooldown(MAX_COOLDOWN);
    }

    @Override
    public void tick() {
        LivingEntity livingEntity = this.guardian.getTarget();
        if (livingEntity == null || !livingEntity.isAlive()) {
            this.stop();
            return;
        }
        if (this.guardian.isShooting()) {
            Objects.requireNonNull(this.guardian.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(0);
        } else {
            Objects.requireNonNull(this.guardian.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(0.3f);
        }

        if (this.guardian.distanceTo(livingEntity) < 10) {
            if (guardian.isShooting() && this.guardian.distanceTo(livingEntity) <= 2 && !hasDealtDamage) {
                livingEntity.damage(guardian.getDamageSources().mobAttack(guardian), 10.0f);
                guardian.playSound(ModSounds.SWING, 0.8f, 1.2f);
                livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 3 * 20, 2, false, false));
                hasDealtDamage = true;
            }
            this.guardian.setCooldown(Math.max(this.guardian.getCooldown() - 1, 0));
            if (this.guardian.getCooldown() == 12) {
                Vec3d direction = livingEntity.getPos().subtract(this.guardian.getPos()).normalize();
                Vec3d jumpVelocity;
                if (this.guardian.distanceTo(livingEntity) < 10 && this.guardian.distanceTo(livingEntity) > 4)
                    jumpVelocity = new Vec3d(direction.x * 1.4, 0.7, direction.z * 1.4);
                else
                    jumpVelocity = new Vec3d(direction.x * 0.7, 0.6, direction.z * 0.7);

                this.guardian.setVelocity(jumpVelocity);
                this.guardian.velocityModified = true;

            } else if (this.guardian.getCooldown() == 16) {
                this.guardian.setShooting(true);
                guardian.playSound(ModSounds.DWARVEN_SPIDER_JUMP_ATTACK, 0.6f, 0.85f); // Impact grave
            } else if (this.guardian.getCooldown() == 22) {
                double distance = this.guardian.distanceTo(livingEntity);

                if (distance <= 4) {
                    // 🏃‍♂️ Jump back
                    Vec3d direction = this.guardian.getPos().subtract(livingEntity.getPos()).normalize(); // vers l'arrière
                    Vec3d retreatVelocity = new Vec3d(direction.x * 0.9, 0.3, direction.z * 0.9); // petit saut arrière

                    this.guardian.setVelocity(retreatVelocity);
                    this.guardian.velocityModified = true;
                }
            } else if (this.guardian.getCooldown() == 0) {
                this.hasDealtDamage = false;
                this.guardian.setCooldown(101);
                this.guardian.setShooting(false);
            } else if (this.guardian.getCooldown() <= 100 && this.guardian.getCooldown() > 24) {
                this.hasDealtDamage = false;
                this.guardian.setShooting(false);
            }
        } else {
            this.hasDealtDamage = false;
            this.guardian.setShooting(false);
            this.guardian.setCooldown(101);
        }
    }
}
