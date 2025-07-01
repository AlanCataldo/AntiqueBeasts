package net.mebahel.antiquebeasts.entity.custom.dwemer;

import net.mebahel.antiquebeasts.entity.ai.CustomRevengeGoal;
import net.mebahel.antiquebeasts.entity.ai.dwarven.dwarven_centurion.DwarvenCenturionMeleeAttackGoal;
import net.mebahel.antiquebeasts.entity.ai.dwarven.dwarven_centurion.DwarvenCenturionShootingGoal;
import net.mebahel.antiquebeasts.entity.custom.ChimeraEntity;
import net.mebahel.antiquebeasts.entity.custom.CyclopsEntity;
import net.mebahel.antiquebeasts.entity.custom.egyptian.EgyptianEntity;
import net.mebahel.antiquebeasts.entity.custom.greek.GreekEntity;
import net.mebahel.antiquebeasts.entity.custom.norse.NorseEntity;
import net.mebahel.antiquebeasts.entity.custom.other.DraugrEntity;
import net.mebahel.antiquebeasts.entity.variant.CyclopsVariant;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.mebahel.antiquebeasts.util.LargeEntityPathNodeMaker;
import net.mebahel.antiquebeasts.util.config.ModBonusHealthConfig;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
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
import net.minecraft.item.ItemStack;
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
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.ClientUtils;

import java.util.List;
import java.util.Objects;

import static java.lang.Math.random;

public class DwemerCenturionEntity extends DwemerEntity implements GeoEntity {
    double last_step = 0;
    private int idleCondition = 0;
    private final List<ItemStack> minedResources = new java.util.ArrayList<>();

    private boolean shouldRandomIdle = true;
    double rand;

    public static final TrackedData<Boolean> SHOOTING = DataTracker.registerData(DwemerCenturionEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);

    public static final TrackedData<Boolean> SWINGING = DataTracker.registerData(DwemerCenturionEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);

    public static final TrackedData<Float> COOLDOWN = DataTracker.registerData(DwemerCenturionEntity.class,
            TrackedDataHandlerRegistry.FLOAT);

    public static final TrackedData<String> ATTACK_NAME = DataTracker.registerData(DwemerCenturionEntity.class,
            TrackedDataHandlerRegistry.STRING);
    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);

    public DwemerCenturionEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
        this.ambientSoundChance = -this.getMinAmbientSoundDelay();
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }

    public float getCooldown() { return this.dataTracker.get(COOLDOWN);}

    public void setCooldown(float cooldown) {
        this.dataTracker.set(COOLDOWN, cooldown);
    }

    public boolean isShooting() {
        return this.dataTracker.get(SHOOTING);
    }

    public void setSwinging(boolean swinging) {
        this.dataTracker.set(SWINGING, swinging);
    }

    public boolean isSwinging() {
        return this.dataTracker.get(SWINGING);
    }

    public void setShooting(boolean shooting) {
        this.dataTracker.set(SHOOTING, shooting);
    }

    public void setAttackName(String attackName) {
        this.dataTracker.set(ATTACK_NAME, attackName);
    }

    public String getAttackName() {
        return this.dataTracker.get(ATTACK_NAME);
    }
    public boolean shouldDespawnInPeaceful() {
        return this.getWorld().getDifficulty() == Difficulty.PEACEFUL;
    }
    public String getCurrentAnimation() { return this.dataTracker.get(CURRENT_ANIMATION); }
    public void setCurrentAnimation(String animation) {
        this.dataTracker.set(CURRENT_ANIMATION, animation);
    }
    public static final TrackedData<String> CURRENT_ANIMATION = DataTracker.registerData(DwemerCenturionEntity.class, TrackedDataHandlerRegistry.STRING);
    @Override
    public void tick() {
        super.tick();
        if (shouldDespawnInPeaceful()) {
            remove(RemovalReason.DISCARDED);
        }
        if (this.isShooting()) {
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(0);
        } else if (Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).getValue() == 0
                && !this.isShooting()) {
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(0.3D);
        }

    }
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SHOOTING, false);
        this.dataTracker.startTracking(SWINGING, false);
        this.dataTracker.startTracking(COOLDOWN, 0f);
        this.dataTracker.startTracking(DATA_ID_TYPE_VARIANT, 0);
        this.dataTracker.startTracking(ATTACK_NAME, "attack2");
        this.dataTracker.startTracking(CURRENT_ANIMATION, "idle");
    }
    public static DefaultAttributeContainer.Builder setAttributes() {
        return HostileEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 20)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 84 + ModBonusHealthConfig.cyclopsBonusHealth)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3D)
                .add(EntityAttributes.GENERIC_ARMOR, 8f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 9f)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.8f)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.25f);
    }
    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new DwarvenCenturionShootingGoal(this, 3f));
        this.goalSelector.add(3, new DwarvenCenturionMeleeAttackGoal(this, 1f, 39, 2));
        this.goalSelector.add(6, new WanderAroundFarGoal(this, 0.85f, 1f));
        this.goalSelector.add(7, new LookAroundGoal(this));

        this.targetSelector.add(1, new CustomRevengeGoal(this, DwemerEntity.class));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, DraugrEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, ChimeraEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, CyclopsEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, VillagerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, IronGolemEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, RaiderEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, GreekEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, EgyptianEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, NorseEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, ZombieEntity.class, true));
    }
    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);

        int xpToDrop = 60 + this.random.nextInt(30);

        if (!this.getWorld().isClient) {
            while (xpToDrop > 0) {
                int xpAmount = ExperienceOrbEntity.roundToOrbSize(xpToDrop);
                xpToDrop -= xpAmount;
                this.getWorld().spawnEntity(new ExperienceOrbEntity(this.getWorld(), this.getX(), this.getY(), this.getZ(), xpAmount));
            }
        }
    }


    private <E extends GeoAnimatable> PlayState predicate(AnimationState<E> event) {
        if (event.isMoving()) {
            event.getController().setAnimation(RawAnimation.begin().then("walk", Animation.LoopType.LOOP));
            setCurrentAnimation("walk");
            if (this.shouldRandomIdle) {
                this.idleCondition = Math.random() < 0.5 ? 1 : 2;
                this.shouldRandomIdle = false;
            }
            return PlayState.CONTINUE;
        } else if (this.getDataTracker().get(DwemerCenturionEntity.IS_MINING)) {
            setCurrentAnimation("mine");
            event.getController().setAnimation(RawAnimation.begin().then("mine", Animation.LoopType.LOOP));
        } else {
            if (idleCondition == 1) {
                setCurrentAnimation("idle");
                event.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
            } else {
                setCurrentAnimation("idle2");
                event.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
            }
            this.shouldRandomIdle = true;
        }

        return PlayState.CONTINUE;
    }


    private PlayState attackPredicate(AnimationState state) {
        if(this.isSwinging() && !this.isShooting() && state.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
            state.getController().forceAnimationReset();
            state.getController().setAnimation(RawAnimation.begin().then(this.getAttackName(), Animation.LoopType.PLAY_ONCE));
        }

        return PlayState.CONTINUE;
    }

    private PlayState shootingPredicate(AnimationState state) {
        if(this.isShooting() && !this.isSwinging() && state.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
            state.getController().forceAnimationReset();
            state.getController().setAnimation(RawAnimation.begin().then("shooting", Animation.LoopType.PLAY_ONCE));
        }

        return PlayState.CONTINUE;
    }
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController(this, "controller", 0, this::predicate).setSoundKeyframeHandler(state -> {
            PlayerEntity player = ClientUtils.getClientPlayer();
            if (player != null)
                this.getWorld().playSound(player, this.getX(), this.getY(), this.getZ(), ModSounds.DWARVEN_SPIDER_MINING, this.getSoundCategory(), 0.5f, 0.6f);
        }));
        controllers.add(new AnimationController(this, "attacking", 0, this::attackPredicate));
        controllers.add(new AnimationController(this, "shooting", 0, this::shootingPredicate).setSoundKeyframeHandler(state -> {
            PlayerEntity player = ClientUtils.getClientPlayer();
            if (player != null)
                this.getWorld().playSound(player, this.getX(), this.getY(), this.getZ(), ModSounds.SWING, this.getSoundCategory(), 0.5f, 0.6f);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        return new MobNavigation(this, world) {
            @Override
            protected PathNodeNavigator createPathNodeNavigator(int range) {
                this.nodeMaker = new LargeEntityPathNodeMaker();
                this.nodeMaker.setCanEnterOpenDoors(true);
                return new PathNodeNavigator(this.nodeMaker, range);
            }
        };
    }

    @Override
    public float getStepHeight() {
        return 1.1f;
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
            DataTracker.registerData(DwemerCenturionEntity.class, TrackedDataHandlerRegistry.INTEGER);

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty,
                                 SpawnReason spawnReason, @javax.annotation.Nullable EntityData entityData,
                                 @javax.annotation.Nullable NbtCompound entityNbt) {
        CyclopsVariant variant = Util.getRandom(CyclopsVariant.values(), this.random);
        setVariant(variant);
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    public CyclopsVariant getVariant() {
        return CyclopsVariant.byId(this.getTypeVariant() & 255);
    }

    private int getTypeVariant() {
        return this.dataTracker.get(DATA_ID_TYPE_VARIANT);
    }

    public void setVariant(CyclopsVariant variant) {
        this.dataTracker.set(DATA_ID_TYPE_VARIANT, variant.getId() & 255);
    }
    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        if (last_step == 0) {
            this.playSound(ModSounds.DWARVEN_CENTURION_WALK, 0.25f, 1.0f);
            last_step = this.age;
        }
        if (last_step + 12 <= this.age) {
            this.playSound(ModSounds.DWARVEN_CENTURION_WALK, 0.25f, 1.0f);
            last_step = this.age;
        }
    }

    @Override
    public int getMinAmbientSoundDelay() {
        return 360;
    }
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.DWARVEN_CENTURION_AMBIENT_1;
    }
    @Override
    public void playAmbientSound() {
        SoundEvent soundEvent = this.getAmbientSound();
        if (soundEvent != null) {
            this.playSound(soundEvent, 0.35f, 1f);
        }
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.DWARVEN_CENTURION_DEATH_1;
    }
    @Override
    protected void playHurtSound(DamageSource source) {
        SoundEvent hurtSound = this.getHurtSound(source);
        if (hurtSound != null) {
            this.playSound(hurtSound, 0.45f, 1.0f);
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        rand = random();
        if (rand < 0.5)
            return ModSounds.DWARVEN_CENTURION_INJURED_1;
        return ModSounds.DWARVEN_CENTURION_INJURED_2;
    }
}
