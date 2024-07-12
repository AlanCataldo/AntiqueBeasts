package net.mebahel.antiquebeasts.entity.custom;

import net.mebahel.antiquebeasts.entity.ai.EgyptianMeleeAttackGoal;
import net.mebahel.antiquebeasts.entity.ai.MummyShootingGoal;
import net.mebahel.antiquebeasts.entity.ai.MummySummonGoal;
import net.mebahel.antiquebeasts.entity.variant.EgyptiantVariant;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.ClientUtils;

import java.util.Objects;

import static java.lang.Math.random;


public class MummyEntity extends EgyptianEntity implements GeoEntity {
    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }
    private boolean shouldDespawnInPeaceful() {
        return this.getWorld().getDifficulty() == Difficulty.PEACEFUL;
    }
    public static final TrackedData<Integer> SPAWN_CD = DataTracker.registerData(MummyEntity.class,
            TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Boolean> SPAWN = DataTracker.registerData(MummyEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> HAS_SPAWNED = DataTracker.registerData(MummyEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Integer> COOLDOWN = DataTracker.registerData(MummyEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Boolean> SHOOTING = DataTracker.registerData(MummyEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public void setShooting(boolean shooting) {
        this.dataTracker.set(SHOOTING, shooting);
    }
    public int getCooldown() { return this.dataTracker.get(COOLDOWN);}
    public void setCooldown(int cooldown) {
        this.dataTracker.set(COOLDOWN, cooldown);
    }
    public boolean isShooting() {
        return this.dataTracker.get(SHOOTING);
    }
    public int getSpawnCooldown() {return this.dataTracker.get(SPAWN_CD);}
    public void setSpawnCooldown(int cooldown) { this.dataTracker.set(SPAWN_CD, cooldown);}
    public boolean getSpawn() {return this.dataTracker.get(SPAWN);}
    public void setSpawn(boolean bool) {
        this.dataTracker.set(SPAWN, bool);
    }
    public boolean getHasSpawned() {return this.dataTracker.get(HAS_SPAWNED);}
    public void setHasSpawned(boolean bool) {
        this.dataTracker.set(HAS_SPAWNED, bool);
    }

    public MummyEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
        this.ambientSoundChance = -this.getMinAmbientSoundDelay();
    }

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SWINGING, false);
        this.dataTracker.startTracking(SHOOTING, false);
        this.dataTracker.startTracking(COOLDOWN, 0);
        this.dataTracker.startTracking(DATA_ID_TYPE_VARIANT, 0);
        this.dataTracker.startTracking(ATTACK_NAME, "attack");
        this.dataTracker.startTracking(SPAWN_CD, 120);
        this.dataTracker.startTracking(SPAWN, false);
        this.dataTracker.startTracking(HAS_SPAWNED, false);
    }
    public static DefaultAttributeContainer.Builder setAttributes() {
        return HostileEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.55f)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 36.0D)
                .add(EntityAttributes.GENERIC_ARMOR, 4f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0f)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.2f)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.5f);
    }
    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new MummySummonGoal(this, 0.51f));
        this.goalSelector.add(3, new MummyShootingGoal(this, 0.51f));
        this.goalSelector.add(4, new EgyptianMeleeAttackGoal(this, 0.51f, 6f, 1, 10));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.45f, 1f));
        this.goalSelector.add(6, new LookAroundGoal(this));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }
    private PlayState predicate(AnimationState animationState) {
        if (!this.getHasSpawned()) {
            return PlayState.STOP;
        } else if (animationState.isMoving()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        } else
            animationState.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }
    private PlayState attackPredicate(AnimationState state) {
        if (this.isSwinging() && state.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
            state.getController().forceAnimationReset();
            state.getController().setAnimation(RawAnimation.begin().then(this.getAttackName(), Animation.LoopType.PLAY_ONCE));
        }

        return PlayState.CONTINUE;
    }

    private PlayState raisePredicate(AnimationState state) {
        if(this.getSpawn() && state.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
            state.getController().forceAnimationReset();
            state.getController().setAnimation(RawAnimation.begin().then("raise", Animation.LoopType.PLAY_ONCE));
        }
        return PlayState.CONTINUE;
    }
    private PlayState spawnPredicate(AnimationState state) {
        if (!this.getHasSpawned()) {
            state.getController().forceAnimationReset();
            state.getController().setAnimation(RawAnimation.begin().then("spawn", Animation.LoopType.PLAY_ONCE));
        }

        this.setHasSpawned(true);
        return PlayState.CONTINUE;
    }

    private PlayState shootingPredicate(AnimationState state) {
        if(this.isShooting() && !this.isSwinging() && state.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
            state.getController().forceAnimationReset();
            state.getController().setAnimation(RawAnimation.begin().then("shoot", Animation.LoopType.PLAY_ONCE));
        }

        return PlayState.CONTINUE;
    }
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController(this, "controller",0, this::predicate));
        controllers.add(new AnimationController(this, "attacking", 0, this::attackPredicate).setSoundKeyframeHandler(state -> {
            PlayerEntity player = ClientUtils.getClientPlayer();
            if (player != null)
                this.getWorld().playSound(player, this.getX(), this.getY(), this.getZ(), ModSounds.SWING, this.getSoundCategory(), 0.5f, 1.5f);
        }));
        controllers.add(new AnimationController(this, "spawning", 0, this::spawnPredicate).setSoundKeyframeHandler(state -> {
            PlayerEntity player = ClientUtils.getClientPlayer();
            if (player != null)
                this.getWorld().playSound(player, this.getX(), this.getY(), this.getZ(), ModSounds.MUMMY_SPAWN, this.getSoundCategory(), 0.65f, 1f);
        }));
        controllers.add(new AnimationController(this, "raise", 0, this::raisePredicate).setSoundKeyframeHandler(state -> {
            PlayerEntity player = ClientUtils.getClientPlayer();
            if (player != null)
                this.getWorld().playSound(player, this.getX(), this.getY(), this.getZ(), ModSounds.MUMMY_RAISE, this.getSoundCategory(), 0.65f, 1f);
        }));
        controllers.add(new AnimationController(this, "shooting", 0, this::shootingPredicate).setSoundKeyframeHandler(state -> {
            PlayerEntity player = ClientUtils.getClientPlayer();
            if (player != null)
                this.getWorld().playSound(player, this.getX(), this.getY(), this.getZ(), ModSounds.MUMMY_SHOOT, this.getSoundCategory(), 0.65f, 1f);
        }));
    }

    @Override
    public void tick() {
        super.tick();
        if (shouldDespawnInPeaceful()) {
            remove(Entity.RemovalReason.DISCARDED);
        }

        if (this.age < 40 || this.isShooting() || this.getSpawn()) {
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(0);
        } else if (Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).getValue() == 0) {
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(0.51f);
        }
    }
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        rand = random();
        if (rand < 0.5)
            return ModSounds.MUMMY_HURT_1;
        else
            return ModSounds.MUMMY_HURT_2;
    }
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.MUMMY_DEATH;
    }
    @Override
    protected SoundEvent getAmbientSound() {
        rand = random();
        if (rand < 0.5)
            return ModSounds.MUMMY_AMBIENT_1;
        else
            return ModSounds.MUMMY_AMBIENT_2;
    }

    /* VARIANTS */
    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("Variant", this.getTypeVariant());
        nbt.putBoolean("HasSpawned", true);
    }
    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.dataTracker.set(DATA_ID_TYPE_VARIANT, nbt.getInt("Variant"));
        this.setHasSpawned(nbt.getBoolean("HasSpawned"));
    }
    private static final TrackedData<Integer> DATA_ID_TYPE_VARIANT =
            DataTracker.registerData(MummyEntity.class, TrackedDataHandlerRegistry.INTEGER);

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty,
                                 SpawnReason spawnReason, @javax.annotation.Nullable EntityData entityData,
                                 @javax.annotation.Nullable NbtCompound entityNbt) {
        EgyptiantVariant variant = Util.getRandom(EgyptiantVariant.values(), this.random);
        setVariant(variant);

        if (spawnReason != SpawnReason.SPAWN_EGG && spawnReason != SpawnReason.COMMAND && spawnReason != SpawnReason.SPAWNER) {
            int randomValue = this.random.nextInt(11);
            if (randomValue >= 0 && randomValue <= 6) {
                this.remove(Entity.RemovalReason.DISCARDED);
            }
        }
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }
    public EgyptiantVariant getVariant() {
        return EgyptiantVariant.byId(this.getTypeVariant() & 255);
    }
    public int getTypeVariant() {
        return this.dataTracker.get(DATA_ID_TYPE_VARIANT);
    }
    public void setVariant(EgyptiantVariant variant) {
        this.dataTracker.set(DATA_ID_TYPE_VARIANT, variant.getId() & 255);
    }
}
