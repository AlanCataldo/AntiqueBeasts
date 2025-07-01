package net.mebahel.antiquebeasts.entity.custom.other;

import net.mebahel.antiquebeasts.entity.ModEntities;
import net.mebahel.antiquebeasts.entity.ai.CustomRevengeGoal;
import net.mebahel.antiquebeasts.entity.ai.other.DraugrMeleeAttackGoal;
import net.mebahel.antiquebeasts.entity.custom.dwemer.DwemerEntity;
import net.mebahel.antiquebeasts.entity.custom.egyptian.EgyptianEntity;
import net.mebahel.antiquebeasts.entity.custom.greek.GreekEntity;
import net.mebahel.antiquebeasts.entity.custom.norse.NorseEntity;
import net.mebahel.antiquebeasts.entity.variant.DraugrVariant;
import net.mebahel.antiquebeasts.entity.variant.SkeletonWarriorVariant;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.mebahel.antiquebeasts.util.config.ModBonusHealthConfig;
import net.mebahel.antiquebeasts.util.config.ModSpawnRateConfig;
import net.minecraft.block.BlockState;
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
import net.minecraft.entity.damage.DamageTypes;
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
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Util;
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

public class SkeletonWarriorEntity extends DraugrEntity implements GeoEntity {
    public SkeletonWarriorEntity(EntityType<? extends DraugrEntity> entityType, World world) {
        super(entityType, world);
        this.ambientSoundChance = -this.getMinAmbientSoundDelay();
    }
    public boolean shouldDespawn;
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
            DataTracker.registerData(SkeletonWarriorEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public static final TrackedData<Boolean> SWINGING = DataTracker.registerData(SkeletonWarriorEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);

    public static final TrackedData<String> ATTACK_NAME = DataTracker.registerData(SkeletonWarriorEntity.class,
            TrackedDataHandlerRegistry.STRING);

    public static final TrackedData<Boolean> HAS_SPAWNED = DataTracker.registerData(SkeletonWarriorEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);
    public boolean getHasSpawned() {return this.dataTracker.get(HAS_SPAWNED);}
    public void setHasSpawned(boolean bool) {
        this.dataTracker.set(HAS_SPAWNED, bool);
    }

    public void setSwinging(boolean swinging) { this.dataTracker.set(SWINGING, swinging); }
    public boolean isSwinging() { return this.dataTracker.get(SWINGING); }
    public void setAttackName(String attackName) { this.dataTracker.set(ATTACK_NAME, attackName); }
    public String getAttackName() { return this.dataTracker.get(ATTACK_NAME); }

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SWINGING, false);
        this.dataTracker.startTracking(DATA_ID_TYPE_VARIANT, 0);
        this.dataTracker.startTracking(ATTACK_NAME, "attack");
        this.dataTracker.startTracking(HAS_SPAWNED, true);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new DraugrMeleeAttackGoal(this, 1f, 21, 10));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.85f, 1f));
        this.goalSelector.add(6, new LookAroundGoal(this));

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
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 14.0D + ModBonusHealthConfig.draugrBonusHealth)
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
        controllers.add(new AnimationController(this, "attacking", 0, this::attackPredicate).setSoundKeyframeHandler(state -> {
            PlayerEntity player = ClientUtils.getClientPlayer();
            if (player != null)
                this.getWorld().playSound(player, this.getX(), this.getY(), this.getZ(), ModSounds.SWING, this.getSoundCategory(), 0.7f, 1.1f);
        }));
        controllers.add(new AnimationController(this, "spawning", 0, this::spawnPredicate).setSoundKeyframeHandler(state -> {
            PlayerEntity player = ClientUtils.getClientPlayer();
            if (player != null)
                this.getWorld().playSound(player, this.getX(), this.getY(), this.getZ(), ModSounds.MUMMY_SPAWN, this.getSoundCategory(), 0.65f, 1f);
        }));
    }

    public DraugrVariant getVariant() {
        return DraugrVariant.byId(this.getTypeVariant() & 255);
    }

    public SkeletonWarriorVariant getSkeletonWarriorVariant() {
        return SkeletonWarriorVariant.byId(this.getTypeVariant() & 255);
    }

    public int getTypeVariant() {
        return this.dataTracker.get(DATA_ID_TYPE_VARIANT);
    }

    private void setVariant(SkeletonWarriorVariant variant) {
        this.dataTracker.set(DATA_ID_TYPE_VARIANT, variant.getId() & 255);
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty,
                                 SpawnReason spawnReason, @Nullable EntityData entityData,
                                 @Nullable NbtCompound entityNbt) {
        SkeletonWarriorVariant variant = Util.getRandom(SkeletonWarriorVariant.values(), this.random);
        setVariant(variant);
        this.setTarget(null);

        return entityData;
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
            return randomValue < ModSpawnRateConfig.skeletonWarriorSpawnRate;
        }
        return false;
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_SKELETON_AMBIENT;
    }
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_SKELETON_HURT;
    }
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_SKELETON_DEATH;
    }
    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_SKELETON_STEP, 0.75f, 1.0f);
    }
    public boolean damage(DamageSource source, float amount) {
        if (source.isOf(DamageTypes.IN_FIRE) || source.isOf(DamageTypes.ON_FIRE)) {
            return super.damage(source, amount * 2);
        } else if (source.isOf(DamageTypes.FREEZE)) {
            return false;
        }
        return super.damage(source, amount);
    }

    @Override
    public void onDeath(DamageSource cause) {
        super.onDeath(cause);

        rand = random();
        if (rand < 0.55) {
            if (cause.getAttacker() instanceof PlayerEntity) {
                World world = this.getEntityWorld();
                double x = this.getX();
                double y = this.getY();
                double z = this.getZ();

                SkeletonWarriorHeadEntity head = new SkeletonWarriorHeadEntity(ModEntities.SKELETON_WARRIOR_HEAD, this.getWorld());
                if (this.isPartOfRaid()) {
                    head.setPartOfRaid(true);
                }
                head.setPosition(x, y, z);
                world.spawnEntity(head);
            }
        }
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
}
