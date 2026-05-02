package net.mebahel.antiquebeasts.entity.custom.other;

import net.mebahel.antiquebeasts.config.draugr.DraugrSpawnRateConfig;
import net.mebahel.antiquebeasts.entity.ModEntities;
import net.mebahel.antiquebeasts.entity.ai.SkeletonWarriorMeleeAttackGoal;
import net.mebahel.antiquebeasts.entity.ai.util.ConditionalWanderAroundFarGoal;
import net.mebahel.antiquebeasts.entity.ai.util.CustomRevengeGoal;
import net.mebahel.antiquebeasts.entity.variant.SkeletonWarriorVariant;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.mebahel.antiquebeasts.util.DamageElementUtil;
import net.mebahel.antiquebeasts.util.PreDamageResult;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
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
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeKeys;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

import static java.lang.Math.random;

public class SkeletonWarriorEntity extends DraugrEntity implements GeoEntity {
    public SkeletonWarriorEntity(EntityType<? extends DraugrEntity> entityType, World world) {
        super(entityType, world);
        this.ambientSoundChance = -this.getMinAmbientSoundDelay();
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
            DataTracker.registerData(SkeletonWarriorEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Boolean> SWINGING =
            DataTracker.registerData(SkeletonWarriorEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<String> ATTACK_NAME =
            DataTracker.registerData(SkeletonWarriorEntity.class, TrackedDataHandlerRegistry.STRING);
    public static final TrackedData<Boolean> HAS_SPAWNED =
            DataTracker.registerData(SkeletonWarriorEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public static final TrackedData<Boolean> CAN_DROP_HEAD =
            DataTracker.registerData(SkeletonWarriorEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public boolean getHasSpawned() {
        return this.dataTracker.get(HAS_SPAWNED);
    }

    public void setHasSpawned(boolean bool) {
        this.dataTracker.set(HAS_SPAWNED, bool);
    }

    public void setSwinging(boolean swinging) {
        this.dataTracker.set(SWINGING, swinging);
    }

    public boolean isSwinging() {
        return this.dataTracker.get(SWINGING);
    }

    public void setAttackName(String attackName) {
        this.dataTracker.set(ATTACK_NAME, attackName);
    }

    public String getAttackName() {
        return this.dataTracker.get(ATTACK_NAME);
    }

    public boolean canDropHead() {
        return this.dataTracker.get(CAN_DROP_HEAD);
    }

    public void setCanDropHead(boolean canDropHead) {
        this.dataTracker.set(CAN_DROP_HEAD, canDropHead);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SWINGING, false);
        this.dataTracker.startTracking(DATA_ID_TYPE_VARIANT, 0);
        this.dataTracker.startTracking(ATTACK_NAME, "attack");
        this.dataTracker.startTracking(HAS_SPAWNED, true);
        this.dataTracker.startTracking(CAN_DROP_HEAD, true);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new SkeletonWarriorMeleeAttackGoal(this, 1f, 30, 18, 6));
        this.goalSelector.add(9, new ConditionalWanderAroundFarGoal(this, 0.85f, 1f));
        this.goalSelector.add(10, new LookAroundGoal(this));

        this.targetSelector.add(1, new CustomRevengeGoal(this, DraugrEntity.class));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, VillagerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, IronGolemEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, RaiderEntity.class, true));
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return HostileEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 35)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3D)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 34)
                .add(EntityAttributes.GENERIC_ARMOR, 4)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.2f)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.5f);
    }

    private PlayState predicate(AnimationState animationState) {
        if (!this.getHasSpawned()) {
            return PlayState.STOP;
        } else if (animationState.isMoving()) {
            animationState.getController().setAnimation(
                    RawAnimation.begin()
                            .then("transition_walk", Animation.LoopType.PLAY_ONCE)
                            .then("walk", Animation.LoopType.LOOP)
            );
            return PlayState.CONTINUE;
        } else if (!animationState.isMoving() && !this.isAttacking()) {
            animationState.getController().setAnimation(
                    RawAnimation.begin().then("idle", Animation.LoopType.LOOP)
            );
            return PlayState.CONTINUE;
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
        controllers.add(new AnimationController<>(this, "attacking", 0, state -> PlayState.CONTINUE)
                .triggerableAnim("attack", RawAnimation.begin().then("attack", Animation.LoopType.PLAY_ONCE))
                .triggerableAnim("attack2", RawAnimation.begin().then("attack2", Animation.LoopType.PLAY_ONCE)));

        controllers.add(new AnimationController<>(this, "spawning", 0, state -> {
            if (!this.getHasSpawned()) {
                state.getController().setAnimation(
                        RawAnimation.begin().then("spawn", Animation.LoopType.PLAY_ONCE)
                );
                return PlayState.CONTINUE;
            }

            return PlayState.STOP;
        }));
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

        float randomScale = 1.0F + this.random.nextFloat() * 0.15F;
        this.setDraugrScale(randomScale);
        this.calculateDimensions();

        this.setHasSpawned(!this.raidSpawnIntro);

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
            return randomValue < DraugrSpawnRateConfig.draugrSpawnRate;
        }
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        double rand = random();

        if (rand < 0.5) {
            return ModSounds.SKELETON_WARRIOR_AMBIENT_1;
        } else {
            return ModSounds.SKELETON_WARRIOR_AMBIENT_2;
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        double rand = random();

        if (rand < 0.5) {
            return ModSounds.SKELETON_WARRIOR_HURT_2;
        } else {
            return ModSounds.SKELETON_WARRIOR_HURT_3;
        }
    }

    @Override
    protected SoundEvent getDeathSound() {
        double rand = random();

        if (rand < 0.5) {
            return ModSounds.SKELETON_WARRIOR_DEATH_1;
        } else {
            return ModSounds.SKELETON_WARRIOR_DEATH_2;
        }
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(ModSounds.SKELETON_WARRIOR_WALK_1, 0.75f,0.8F + this.getRandom().nextFloat() * 0.4F);
    }

    @Override
    protected PreDamageResult preDamage(DamageSource source, float amount,
                                        @Nullable Entity direct, @Nullable Entity attacker) {
        DamageElementUtil.ElementResult r = DamageElementUtil.compute(
                source, direct,
                DamageElementUtil.ElementConfig.defaultPriority(1.5f, 1f, 1f)
        );
        float finalAmount = DamageElementUtil.apply(amount, r);

        return new PreDamageResult(false, finalAmount, r);
    }

    @Override
    public void onDeath(DamageSource cause) {
        super.onDeath(cause);

        if (!this.canDropHead()) {
            return;
        }

        rand = random();
        if (rand < 0.4) {
            if (cause.getAttacker() instanceof PlayerEntity) {
                World world = this.getEntityWorld();
                double x = this.getX();
                double y = this.getY();
                double z = this.getZ();

                SkeletonWarriorHeadEntity head =
                        new SkeletonWarriorHeadEntity(ModEntities.SKELETON_WARRIOR_HEAD, this.getWorld());

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
            state.getController().setAnimation(
                    RawAnimation.begin().then("spawn", Animation.LoopType.PLAY_ONCE)
            );
            if (state.getController().getAnimationState() != AnimationController.State.STOPPED) {
                spawnHoveringParticles();
            } else {
                this.setHasSpawned(true);
            }
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("CanDropHead", this.canDropHead());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("CanDropHead")) {
            this.setCanDropHead(nbt.getBoolean("CanDropHead"));
        }
    }
}