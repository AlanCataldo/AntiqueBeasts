package net.mebahel.antiquebeasts.entity.custom.other;

import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags;
import net.mebahel.antiquebeasts.config.draugr.DraugrBonusHealthConfig;
import net.mebahel.antiquebeasts.config.draugr.DraugrCombatBalancingConfig;
import net.mebahel.antiquebeasts.config.draugr.DraugrSpawnRateConfig;
import net.mebahel.antiquebeasts.entity.ai.CustomRevengeGoal;
import net.mebahel.antiquebeasts.entity.ai.other.DraugrDrinkPotionGoal;
import net.mebahel.antiquebeasts.entity.ai.other.DraugrIceSpikeSpellGoal;
import net.mebahel.antiquebeasts.entity.custom.dwemer.DwemerEntity;
import net.mebahel.antiquebeasts.entity.custom.egyptian.EgyptianEntity;
import net.mebahel.antiquebeasts.entity.custom.greek.GreekEntity;
import net.mebahel.antiquebeasts.entity.custom.norse.NorseEntity;
import net.mebahel.antiquebeasts.entity.variant.DraugrScourgeVariant;
import net.mebahel.antiquebeasts.particle.ModParticles;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.ClientUtils;

public class DraugrScourgeEntity extends DraugrEntity implements GeoEntity {
    public DraugrScourgeEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    public static final TrackedData<Integer> COOLDOWN = DataTracker.registerData(DraugrScourgeEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Boolean> SHOOTING = DataTracker.registerData(DraugrScourgeEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<String> ATTACK_NAME = DataTracker.registerData(DraugrScourgeEntity.class, TrackedDataHandlerRegistry.STRING);
    public static final TrackedData<Integer> DATA_ID_TYPE_VARIANT = DataTracker.registerData(DraugrScourgeEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public void setShooting(boolean shooting) {this.dataTracker.set(SHOOTING, shooting);}
    public boolean isShooting() {
        return this.dataTracker.get(SHOOTING);
    }

    public void setCooldown(int cooldown) {
        this.dataTracker.set(COOLDOWN, cooldown);
    }
    public int getCooldown() { return this.dataTracker.get(COOLDOWN);}

    public void setAttackName(String attackName) { this.dataTracker.set(ATTACK_NAME, attackName); }
    public String getAttackName() { return this.dataTracker.get(ATTACK_NAME); }

    public int getTypeVariant() {return this.dataTracker.get(DATA_ID_TYPE_VARIANT);}
    private void setVariant(DraugrScourgeVariant variant) {this.dataTracker.set(DATA_ID_TYPE_VARIANT, variant.getId() & 255);}
    public DraugrScourgeVariant getScourgeVariant() {return DraugrScourgeVariant.byId(this.getTypeVariant() & 255);}

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(DATA_ID_TYPE_VARIANT, 0);
        this.dataTracker.startTracking(ATTACK_NAME, "ice_spike");
        this.dataTracker.startTracking(COOLDOWN, 80);
        this.dataTracker.startTracking(SHOOTING, false);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new DraugrDrinkPotionGoal(this));
        this.goalSelector.add(3, new DraugrIceSpikeSpellGoal(this, 2.5f, 9f));

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
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 38.0D + DraugrBonusHealthConfig.draugrBonusHealth)
                .add(EntityAttributes.GENERIC_ARMOR, 4f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0f)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.2f)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.5f);
    }

    private  <E extends GeoAnimatable> PlayState predicate(AnimationState<E> animationState) {
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

    private <E extends GeoAnimatable> PlayState shootingPredicate(AnimationState<E> state) {
        if (this.isUsingPotion())
            state.getController().setAnimation(RawAnimation.begin().then("drink_potion", Animation.LoopType.PLAY_ONCE));

        if (this.isShooting() && state.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
            state.getController().forceAnimationReset();
            state.getController().setAnimation(RawAnimation.begin().then(this.getAttackName(), Animation.LoopType.PLAY_ONCE));
        }

        return PlayState.CONTINUE;
    }

    private <E extends GeoAnimatable> PlayState spawnPredicate(AnimationState<E> state) {
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

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller",0, this::predicate));
        controllers.add(new AnimationController<>(this, "attacking", 0, state -> PlayState.CONTINUE)
                .triggerableAnim("ice_spike", RawAnimation.begin().then("ice_spike", Animation.LoopType.PLAY_ONCE))
                .triggerableAnim("frostbite", RawAnimation.begin().then("frostbite", Animation.LoopType.PLAY_ONCE))
                .triggerableAnim("drink_potion", RawAnimation.begin().then("drink_potion", Animation.LoopType.PLAY_ONCE)));
        controllers.add(new AnimationController<>(this, "spawning", 0, this::spawnPredicate).setSoundKeyframeHandler(state -> {
            PlayerEntity player = ClientUtils.getClientPlayer();
            if (player != null)
                this.getWorld().playSound(player, this.getX(), this.getY(), this.getZ(), ModSounds.MUMMY_SPAWN, this.getSoundCategory(), 0.65f, 1f);
        }));
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

        if (this.random.nextFloat() < DraugrCombatBalancingConfig.draugrSpawnWithPotionProbability / 100) {
            this.giveRandomRangedPotion();
        }

        float randomScale = 1.0F + this.random.nextFloat() * 0.15F; // [1.0 ; 1.1]
        this.setDraugrScale(randomScale);
        this.calculateDimensions();

        return entityData;
    }

    @Override
    public void tick() {
        super.tick();

        if (shouldDespawnInPeaceful()) {
            remove(Entity.RemovalReason.DISCARDED);
        }

        this.generateShoulderSnowFlakeParticles();
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
            return randomValue < DraugrSpawnRateConfig.draugrScourgeSpawnRate;
        }
        return false;
    }

    public void generateShoulderSnowFlakeParticles() {
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
}
