package net.mebahel.antiquebeasts.entity.custom;

import net.mebahel.antiquebeasts.entity.ai.HadesShadeFlyGoal;
import net.mebahel.antiquebeasts.entity.ai.HadesShadeLookAtTargetGoal;
import net.mebahel.antiquebeasts.entity.ai.HadesShadeMeleeAttackGoal;
import net.mebahel.antiquebeasts.entity.ai.HadesShadeMoveControl;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.FlyingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

import static java.lang.Math.random;

public class HadesShadeEntity extends FlyingEntity implements GeoEntity {
    double rand;

    public static final TrackedData<Boolean> SWINGING = DataTracker.registerData(HadesShadeEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<String> ATTACK_NAME = DataTracker.registerData(HadesShadeEntity.class,
            TrackedDataHandlerRegistry.STRING);

    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    public HadesShadeEntity(EntityType<? extends FlyingEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new HadesShadeMoveControl(this);
        this.ambientSoundChance = -this.getMinAmbientSoundDelay();
    }

    public void setSwinging(boolean swinging) {
        this.dataTracker.set(SWINGING, swinging);
    }

    public boolean isSwinging() {
        return this.dataTracker.get(SWINGING);
    }

    public String getAttackName() {
        return this.dataTracker.get(ATTACK_NAME);
    }
    private boolean shouldDespawnInPeaceful() {
        return this.getWorld().getDifficulty() == Difficulty.PEACEFUL;
    }
    @Override
    public void tick() {
        super.tick();
        if (shouldDespawnInPeaceful()) {
            remove(RemovalReason.DISCARDED);
        }
    }

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SWINGING, false);
        this.dataTracker.startTracking(ATTACK_NAME, "animation.hades_shade.attack");
    }
    public static DefaultAttributeContainer.Builder setAttributes() {
        return HostileEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 22.0D)
                .add(EntityAttributes.GENERIC_ARMOR, 2f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.5f)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.3f)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 0.6000000238418579)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.30000001192092896)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.3f);
    }
    @Override
    protected void initGoals() {
        this.goalSelector.add(4, new HadesShadeMeleeAttackGoal(this));
        this.goalSelector.add(5, new HadesShadeFlyGoal(this));
        this.goalSelector.add(7, new HadesShadeLookAtTargetGoal(this));

        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    private PlayState predicate(AnimationState animationState) {
        if(animationState.isMoving()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        animationState.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }
    private PlayState attackPredicate(AnimationState state) {
        if(this.isSwinging() && state.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
            state.getController().forceAnimationReset();
            state.getController().setAnimation(RawAnimation.begin().then(this.getAttackName(), Animation.LoopType.PLAY_ONCE));
        }

        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController(this, "controller",0, this::predicate));
        controllers.add(new AnimationController(this, "attacking",0, this::attackPredicate));
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        rand = random();
        if (rand < 0.5)
            return ModSounds.HADES_SHADE_HURT1;
        return ModSounds.HADES_SHADE_HURT2;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.HADES_SHADE_DEATH;
    }
    @Override
    public boolean damage(DamageSource source, float amount) {

        return super.damage(source, amount);
    }

    @Override
    public int getMinAmbientSoundDelay() {
        return 180;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        rand = random();

        if (rand < 0.5)
            return ModSounds.HADES_SHADE_AMBIENT1;
        else
            return ModSounds.HADES_SHADE_AMBIENT2;

    }
    @Override
    public void playAmbientSound() {
        SoundEvent soundEvent = this.getAmbientSound();
        if (soundEvent != null) {
            this.playSound(soundEvent, 0.35f, 1f);
        }
    }
}
