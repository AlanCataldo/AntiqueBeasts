package net.mebahel.antiquebeasts.entity.custom;

import net.mebahel.antiquebeasts.sound.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;

import static java.lang.Math.random;


public class HopliteEntity extends AnimalEntity implements GeoEntity {
    double rand;
    public static final TrackedData<String> ATTACK_NAME = DataTracker.registerData(HopliteEntity.class,
            TrackedDataHandlerRegistry.STRING);
    public static final TrackedData<Boolean> SHOOTING = DataTracker.registerData(HopliteEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Float> COOLDOWN = DataTracker.registerData(HopliteEntity.class,
            TrackedDataHandlerRegistry.FLOAT);
    protected HopliteEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }
    public static final TrackedData<Boolean> SWINGING = DataTracker.registerData(HopliteEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);
    protected void initDataTracker() { super.initDataTracker(); }
    public void setShooting(boolean shooting) {
        this.dataTracker.set(SHOOTING, shooting);
    }
    public boolean isShooting() {
        return this.dataTracker.get(SHOOTING);
    }
    public void setSwinging(boolean swinging) { this.dataTracker.set(SWINGING, swinging); }
    public boolean isSwinging() { return this.dataTracker.get(SWINGING); }
    public void setAttackName(String attackName) { this.dataTracker.set(ATTACK_NAME, attackName); }
    public String getAttackName() { return this.dataTracker.get(ATTACK_NAME); }
    public float getCooldown() { return this.dataTracker.get(COOLDOWN);}
    public void setCooldown(float cooldown) {
        this.dataTracker.set(COOLDOWN, cooldown);
    }
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return null;
    }
    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }
    private boolean shouldDespawnInPeaceful() {
        return this.getWorld().getDifficulty() == Difficulty.PEACEFUL;
    }
    @Override
    public void tick() {
        super.tick();
        if (shouldDespawnInPeaceful()) {
            remove(Entity.RemovalReason.DISCARDED);
        }
    }
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        rand = random();
        if (rand < 0.5)
            return ModSounds.HOPLITE_HURT1;
        else
            return ModSounds.HOPLITE_HURT2;
    }
    @Override
    protected SoundEvent getDeathSound() {
        rand = random();
        if (rand < 0.5)
            return ModSounds.HOPLITE_DEATH1;
        else
            return ModSounds.HOPLITE_DEATH2;
    }
    @Override
    public int getMinAmbientSoundDelay() {
        return 160;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        LivingEntity target = this.getTarget();
        rand = random();
        if (target != null) {
            if (rand < 0.5)
                return ModSounds.HOPLITE_ATTACKING1;
            else
                return ModSounds.HOPLITE_ATTACKING2;
        } else {
            if (rand < 0.3)
                return ModSounds.HOPLITE_AMBIENT1;
            else if (rand > 0.3 && rand < 0.6)
                return ModSounds.HOPLITE_AMBIENT2;
            else
                return ModSounds.HOPLITE_AMBIENT3;
        }
    }
    @Override
    public void playAmbientSound() {
        SoundEvent soundEvent = this.getAmbientSound();
        if (soundEvent != null) {
            this.playSound(soundEvent, 0.35f, 0.93f);
        }
    }
}
