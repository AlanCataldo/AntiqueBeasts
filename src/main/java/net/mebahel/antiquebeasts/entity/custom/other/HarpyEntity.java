package net.mebahel.antiquebeasts.entity.custom.other;

import net.mebahel.antiquebeasts.entity.ai.CustomRevengeGoal;
import net.mebahel.antiquebeasts.entity.ai.other.HarpyFlyGoal;
import net.mebahel.antiquebeasts.entity.ai.other.HarpyRangedAttackGoal;
import net.mebahel.antiquebeasts.entity.custom.egyptian.EgyptianEntity;
import net.mebahel.antiquebeasts.entity.custom.greek.GreekEntity;
import net.mebahel.antiquebeasts.entity.custom.norse.NorseEntity;
import net.mebahel.antiquebeasts.entity.variant.HarpyVariant;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.mebahel.antiquebeasts.util.config.ModBonusHealthConfig;
import net.mebahel.antiquebeasts.util.config.ModSpawnRateConfig;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.ClientUtils;

import java.util.Objects;

public class HarpyEntity extends AnimalEntity implements GeoEntity {

    public static final TrackedData<Boolean> SHOOTING = DataTracker.registerData(HarpyEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);

    public static final TrackedData<Boolean> SWINGING = DataTracker.registerData(HarpyEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);

    public static final TrackedData<Float> COOLDOWN = DataTracker.registerData(HarpyEntity.class,
            TrackedDataHandlerRegistry.FLOAT);

    public static final TrackedData<String> ATTACK_NAME = DataTracker.registerData(HarpyEntity.class,
            TrackedDataHandlerRegistry.STRING);

    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);

    public HarpyEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
        this.ambientSoundChance = -this.getMinAmbientSoundDelay();
        this.setPathfindingPenalty(PathNodeType.LAVA, 0.0F);
        this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, 0.0F);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, 0.0F);
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }
    public float getFireBreathingCooldown() { return this.dataTracker.get(COOLDOWN);}

    public void setFireBreathingCooldown(float cooldown) {
        this.dataTracker.set(COOLDOWN, cooldown);
    }

    public boolean isFireBreathing() {
        return this.dataTracker.get(SHOOTING);
    }

    public void setSwinging(boolean swinging) {
        this.dataTracker.set(SWINGING, swinging);
    }

    public boolean isSwinging() {
        return this.dataTracker.get(SWINGING);
    }

    public void setFireBreathing(boolean shooting) {
        this.dataTracker.set(SHOOTING, shooting);
    }

    public void setAttackName(String attackName) {
        this.dataTracker.set(ATTACK_NAME, attackName);
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

        if (this.getFireBreathingCooldown() < 16) {
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(0);
        } else if (Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).getValue() == 0
                && this.getFireBreathingCooldown() >= 16) {
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(0.65f);
        }
    }
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SHOOTING, false);
        this.dataTracker.startTracking(SWINGING, false);
        this.dataTracker.startTracking(COOLDOWN, 80f);
        this.dataTracker.startTracking(DATA_ID_TYPE_VARIANT, 0);
        this.dataTracker.startTracking(ATTACK_NAME, "lion_shoot");
    }
    public static DefaultAttributeContainer.Builder setAttributes() {
        return HostileEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 35)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 24.0D + ModBonusHealthConfig.harpyBonusHealth)
                .add(EntityAttributes.GENERIC_ARMOR, 2f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5f)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.2)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.4);
    }
    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new HarpyRangedAttackGoal(this, 0.25, 8, 20, 4, 10));
        this.goalSelector.add(3, new HarpyFlyGoal(this, 0.25, 4, 8));
        this.goalSelector.add(4, new LookAroundGoal(this));

        this.targetSelector.add(1, new CustomRevengeGoal(this, HarpyEntity.class));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, ZombieEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, DraugrEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, VillagerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, IronGolemEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, RaiderEntity.class, true));
        this.targetSelector.add(5, new ActiveTargetGoal<>(this, EgyptianEntity.class, true));
        this.targetSelector.add(5, new ActiveTargetGoal<>(this, GreekEntity.class, true));
        this.targetSelector.add(5, new ActiveTargetGoal<>(this, NorseEntity.class, true));
    }

    private PlayState predicate(AnimationState animationState) {
        if (animationState.isMoving()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("transition_walk", Animation.LoopType.PLAY_ONCE).then("walking", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        animationState.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    private PlayState shootingPredicate(AnimationState state) {
        if (this.isFireBreathing() && state.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
            state.getController().forceAnimationReset();
            state.getController().setAnimation(RawAnimation.begin().then("attack", Animation.LoopType.PLAY_ONCE));
        }

        return PlayState.CONTINUE;
    }
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController(this, "controller", 0, this::predicate).setSoundKeyframeHandler(state -> {
            PlayerEntity player = ClientUtils.getClientPlayer();
            if (player != null)
                this.getWorld().playSound(player, this.getX(), this.getY(), this.getZ(), ModSounds.PEGASUS_FLAP_1, this.getSoundCategory(), 0.45f, 4.5f);
        }));
        controllers.add(new AnimationController(this, "shooting", 0, this::shootingPredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.HARPY_HURT_2;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source.isOf(DamageTypes.IN_FIRE) || source.isOf(DamageTypes.ON_FIRE)) {
            return false;
        }
        return super.damage(source, amount);
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.HARPY_DEATH_1;
    }
    @Override
    public int getMinAmbientSoundDelay() {
        return 240;
    }
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.HARPY_HURT_1;
    }
    @Override
    public void playAmbientSound() {
        SoundEvent soundEvent = this.getAmbientSound();
        if (soundEvent != null) {
            this.playSound(soundEvent, 0.25f, 1f);
        }
    }

    /* VARIANTS */
    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("Variant", this.getTypeVariant());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.dataTracker.set(DATA_ID_TYPE_VARIANT, nbt.getInt("Variant"));
    }
    private static final TrackedData<Integer> DATA_ID_TYPE_VARIANT =
            DataTracker.registerData(HarpyEntity.class, TrackedDataHandlerRegistry.INTEGER);

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty,
                                 SpawnReason spawnReason, @javax.annotation.Nullable EntityData entityData,
                                 @javax.annotation.Nullable NbtCompound entityNbt) {
        HarpyVariant variant = Util.getRandom(HarpyVariant.values(), this.random);
        setVariant(variant);

        return entityData;
    }

    public HarpyVariant getVariant() {
        return HarpyVariant.byId(this.getTypeVariant() & 255);
    }

    private int getTypeVariant() {
        return this.dataTracker.get(DATA_ID_TYPE_VARIANT);
    }

    private void setVariant(HarpyVariant variant) {
        this.dataTracker.set(DATA_ID_TYPE_VARIANT, variant.getId() & 255);
    }

    @Override
    public boolean hasNoGravity() {
        return true;
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    public static boolean canMobSpawnWithRate(EntityType<? extends AnimalEntity> type, ServerWorldAccess world, SpawnReason spawnReason, BlockPos pos, net.minecraft.util.math.random.Random random) {
        if (spawnReason == SpawnReason.SPAWNER || spawnReason == SpawnReason.SPAWN_EGG
                || spawnReason == SpawnReason.COMMAND || spawnReason == SpawnReason.EVENT) {
            return true;
        }
        long time = world.getLevelProperties().getTimeOfDay();
        if (time % 24000L >= 13000L) {
            return false;
        }

        BlockPos blockPos = pos.down();
        if (!world.getBlockState(blockPos).allowsSpawning(world, blockPos, type)) {
            return false;
        }

        if (!world.isSkyVisible(pos)) {
            return false;
        }

        int randomValue = random.nextInt(10);
        return randomValue < ModSpawnRateConfig.harpySpawnRate;
    }
}
