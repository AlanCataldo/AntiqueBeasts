package net.mebahel.antiquebeasts.entity.custom.other;

import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags;
import net.mebahel.antiquebeasts.entity.ai.CustomRevengeGoal;
import net.mebahel.antiquebeasts.entity.ai.other.DraugrIceSpikeSpellGoal;
import net.mebahel.antiquebeasts.entity.custom.dwemer.DwemerEntity;
import net.mebahel.antiquebeasts.entity.custom.egyptian.EgyptianEntity;
import net.mebahel.antiquebeasts.entity.custom.greek.GreekEntity;
import net.mebahel.antiquebeasts.entity.custom.norse.NorseEntity;
import net.mebahel.antiquebeasts.entity.variant.DraugrScourgeVariant;
import net.mebahel.antiquebeasts.particle.ModParticles;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.mebahel.antiquebeasts.util.config.ModBonusHealthConfig;
import net.mebahel.antiquebeasts.util.config.ModSpawnRateConfig;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.ClientUtils;

import javax.annotation.Nullable;

import static java.lang.Math.random;

public class DraugrScourgeEntity extends DraugrEntity implements GeoEntity {
    public DraugrScourgeEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }
    public static final TrackedData<Float> COOLDOWN = DataTracker.registerData(DraugrScourgeEntity.class,
            TrackedDataHandlerRegistry.FLOAT);

    public static final TrackedData<Boolean> SHOOTING = DataTracker.registerData(DraugrScourgeEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> HAS_SPAWNED = DataTracker.registerData(DraugrScourgeEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);
    public boolean getHasSpawned() {return this.dataTracker.get(HAS_SPAWNED);}
    public void setHasSpawned(boolean bool) {
        this.dataTracker.set(HAS_SPAWNED, bool);
    }
    public void setShooting(boolean shooting) {
        this.dataTracker.set(SHOOTING, shooting);
    }
    public float getCooldown() { return this.dataTracker.get(COOLDOWN);}
    public void setCooldown(float cooldown) {
        this.dataTracker.set(COOLDOWN, cooldown);
    }
    public boolean isShooting() {
        return this.dataTracker.get(SHOOTING);
    }
    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }
    double rand;
    public boolean shouldDespawnInPeaceful() {
        return this.getWorld().getDifficulty() == Difficulty.PEACEFUL;
    }

    public static final TrackedData<Integer> DATA_ID_TYPE_VARIANT =
            DataTracker.registerData(DraugrScourgeEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public static final TrackedData<Boolean> SWINGING = DataTracker.registerData(DraugrScourgeEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);

    public static final TrackedData<String> ATTACK_NAME = DataTracker.registerData(DraugrScourgeEntity.class,
            TrackedDataHandlerRegistry.STRING);

    public void setSwinging(boolean swinging) { this.dataTracker.set(SWINGING, swinging); }
    public boolean isSwinging() { return this.dataTracker.get(SWINGING); }
    public void setAttackName(String attackName) { this.dataTracker.set(ATTACK_NAME, attackName); }
    public String getAttackName() { return this.dataTracker.get(ATTACK_NAME); }

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SWINGING, false);
        this.dataTracker.startTracking(DATA_ID_TYPE_VARIANT, 0);
        this.dataTracker.startTracking(ATTACK_NAME, "ice_spike");
        this.dataTracker.startTracking(COOLDOWN, 80f);
        this.dataTracker.startTracking(SHOOTING, false);
        this.dataTracker.startTracking(HAS_SPAWNED, true);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new DraugrIceSpikeSpellGoal(this, 2.5f, 9f));

        this.targetSelector.add(1, new CustomRevengeGoal(this, DraugrEntity.class));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, VillagerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, IronGolemEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, RaiderEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, DwemerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, GreekEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, EgyptianEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, NorseEntity.class, true));
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return HostileEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 35)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3D)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 38.0D + ModBonusHealthConfig.draugrBonusHealth)
                .add(EntityAttributes.GENERIC_ARMOR, 4f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0f)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.2f)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.5f);
    }
    private PlayState predicate(AnimationState animationState) {
        if (!this.getHasSpawned()) {
            return PlayState.STOP;
        } else if (animationState.isMoving()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("transition_walk", Animation.LoopType.PLAY_ONCE).then("walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        } else if (!animationState.isMoving() && !this.isAttacking()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        return PlayState.CONTINUE;
    }

    private PlayState shootingPredicate(AnimationState state) {
        if (this.isShooting() && state.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
            state.getController().forceAnimationReset();
            state.getController().setAnimation(RawAnimation.begin().then(this.getAttackName(), Animation.LoopType.PLAY_ONCE));
            return PlayState.CONTINUE;
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController(this, "controller",0, this::predicate));
        controllers.add(new AnimationController(this, "shooting", 0, this::shootingPredicate).setSoundKeyframeHandler(state -> {
            PlayerEntity player = ClientUtils.getClientPlayer();
            if (player != null) {
                if (this.getAttackName() == "ice_spike")
                    this.getWorld().playSound(player, this.getX(), this.getY(), this.getZ(), ModSounds.DRAUGR_ICE_SPIKE, this.getSoundCategory(), 1f, 1.1f);
                else
                    this.getWorld().playSound(player, this.getX(), this.getY(), this.getZ(), ModSounds.DRAUGR_FROST_SPELL, this.getSoundCategory(), 1f, 1.1f);
            }
        }));
        controllers.add(new AnimationController(this, "spawning", 0, this::spawnPredicate).setSoundKeyframeHandler(state -> {
            PlayerEntity player = ClientUtils.getClientPlayer();
            if (player != null)
                this.getWorld().playSound(player, this.getX(), this.getY(), this.getZ(), ModSounds.MUMMY_SPAWN, this.getSoundCategory(), 0.65f, 1f);
        }));
    }
    private PlayState spawnPredicate(AnimationState state) {
        if (!this.getHasSpawned()) {
            state.getController().setAnimation(RawAnimation.begin().then("spawn", Animation.LoopType.PLAY_ONCE));
            if (state.getController().getAnimationState() != AnimationController.State.STOPPED) {
                spawnHoveringParticles();
            } else {
                this.setHasSpawned(true);
            }
        }
        return PlayState.CONTINUE;
    }

    public DraugrScourgeVariant getScourgeVariant() {
        return DraugrScourgeVariant.byId(this.getTypeVariant() & 255);
    }

    public int getTypeVariant() {
        return this.dataTracker.get(DATA_ID_TYPE_VARIANT);
    }

    private void setVariant(DraugrScourgeVariant variant) {
        this.dataTracker.set(DATA_ID_TYPE_VARIANT, variant.getId() & 255);
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty,
                                 SpawnReason spawnReason, @Nullable EntityData entityData,
                                 @Nullable NbtCompound entityNbt) {

        var biome = world.getBiome(this.getBlockPos());
        DraugrScourgeVariant variant;

        if (biome.isIn(ConventionalBiomeTags.DESERT) || biome.isIn(ConventionalBiomeTags.BADLANDS)) {
            variant = DraugrScourgeVariant.HOT;
        } else if (biome.isIn(ConventionalBiomeTags.CLIMATE_COLD) || biome.isIn(ConventionalBiomeTags.SNOWY) ||
                biome.isIn(ConventionalBiomeTags.ICY) || biome.isIn(ConventionalBiomeTags.AQUATIC_ICY)
                || biome.isIn(ConventionalBiomeTags.TAIGA)) {
            variant = DraugrScourgeVariant.COLD;
        } else {
            variant = DraugrScourgeVariant.TEMPERATE;
        }

        setVariant(variant);
        this.setTarget(null);

        return entityData;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        rand = random();
        if (rand < 0.5)
            return ModSounds.DRAUGR_HURT_1;
        else
            return ModSounds.DRAUGR_HURT_2;
    }
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.DRAUGR_DEATH_1;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        rand = random();
        if (rand < 0.3)
            return ModSounds.DRAUGR_AMBIENT_1;
        else if (rand > 0.3 && rand < 0.6)
            return ModSounds.DRAUGR_AMBIENT_2;
        else
            return ModSounds.DRAUGR_AMBIENT_3;
    }
    @Override
    public void playAmbientSound() {
        SoundEvent soundEvent = this.getAmbientSound();
        if (soundEvent != null) {
            this.playSound(soundEvent, 0.8f, 1f);
        }
    }

    public int getMinAmbientSoundDelay() {
        return 240;
    }
    @Override
    public void tick() {
        super.tick();

        if (shouldDespawnInPeaceful()) {
            remove(RemovalReason.DISCARDED);
        }
        if (this.age % 7 == 0) {
            // Get the rotation angle in radians
            float yawRadians = (float) Math.toRadians(this.bodyYaw);

            // Shoulder offsets
            double shoulderOffsetX = 0.6; // Distance from the center to the shoulder on the X axis
            double shoulderOffsetY = 1.5; // Height offset for the shoulder (Y axis)
            double shoulderOffsetZ = 0; // Slight offset along the Z axis

            // Calculate particle position for the left shoulder
            double leftShoulderX = this.getX() + shoulderOffsetX * Math.cos(yawRadians) - shoulderOffsetZ * Math.sin(yawRadians);
            double leftShoulderY = this.getY() + shoulderOffsetY;
            double leftShoulderZ = this.getZ() + shoulderOffsetX * Math.sin(yawRadians) + shoulderOffsetZ * Math.cos(yawRadians);

            // Calculate particle position for the right shoulder
            double rightShoulderX = this.getX() - shoulderOffsetX * Math.cos(yawRadians) - shoulderOffsetZ * Math.sin(yawRadians);
            double rightShoulderY = this.getY() + shoulderOffsetY;
            double rightShoulderZ = this.getZ() - shoulderOffsetX * Math.sin(yawRadians) + shoulderOffsetZ * Math.cos(yawRadians);

            // Add particles at both positions
            this.getWorld().addParticle(ModParticles.SNOWFLAKE_HAND_PARTICLE, leftShoulderX, leftShoulderY, leftShoulderZ, 0, 0, 0);
            this.getWorld().addParticle(ModParticles.SNOWFLAKE_HAND_PARTICLE, rightShoulderX, rightShoulderY, rightShoulderZ, 0, 0, 0);
        }
    }
    public static boolean canMobSpawnWithRate(EntityType<? extends HostileEntity> type, ServerWorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random,
                                              Boolean draugrCanSpawnInDark) {
        if (spawnReason == SpawnReason.SPAWNER || spawnReason == SpawnReason.SPAWN_EGG
                || spawnReason == SpawnReason.COMMAND || spawnReason == SpawnReason.EVENT) {
            return true;
        }

        if (draugrCanSpawnInDark) {
            BlockPos blockPos = pos.down();
            if (!world.getBlockState(blockPos).allowsSpawning(world, blockPos, type)) {
                return false;
            }

            int randomValue = random.nextInt(10);
            return randomValue < ModSpawnRateConfig.draugrScourgeSpawnRate;
        }
        return false;
    }
}
