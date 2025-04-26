package net.mebahel.antiquebeasts.entity.custom.norse;

import net.mebahel.antiquebeasts.entity.ai.EinherjarHornGoal;
import net.mebahel.antiquebeasts.entity.ai.norse.NorseMeleeAttackGoal;
import net.mebahel.antiquebeasts.entity.ai.util.GroupRevengeGoal;
import net.mebahel.antiquebeasts.entity.ai.util.ModPatrolGoal;
import net.mebahel.antiquebeasts.entity.custom.egyptian.EgyptianEntity;
import net.mebahel.antiquebeasts.entity.custom.greek.GreekEntity;
import net.mebahel.antiquebeasts.entity.custom.other.DraugrEntity;
import net.mebahel.antiquebeasts.entity.variant.EinherjarVariant;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.mebahel.antiquebeasts.util.config.ModBonusHealthConfig;
import net.mebahel.antiquebeasts.util.config.ModSpawnRateConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
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
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
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


public class EinherjarEntity extends NorseEntity implements GeoEntity {
    public EinherjarEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
        this.ambientSoundChance = -this.getMinAmbientSoundDelay();
    }
    public static final TrackedData<Boolean> HORN = DataTracker.registerData(EinherjarEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Integer> HORN_CD = DataTracker.registerData(EinherjarEntity.class,
            TrackedDataHandlerRegistry.INTEGER);

    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }


    public boolean getHorn() {return this.dataTracker.get(HORN);}

    public void setHorn(boolean bool) {
        this.dataTracker.set(HORN, bool);
    }

    public int getHornCooldown() {return this.dataTracker.get(HORN_CD);}

    public void setHornCooldown(int cooldown) {
        this.dataTracker.set(HORN_CD, cooldown);
    }

    public void tick() {
        super.tick();
        if (shouldDespawnInPeaceful()) {
            remove(RemovalReason.DISCARDED);
        }
        if (this.getTarget() != null) {
            if (!this.getTarget().isAlive()) {
                this.setTarget(null);
            }
        }
    }
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SWINGING, false);
        this.dataTracker.startTracking(ATTACK_NAME, "attack");
        this.dataTracker.startTracking(DATA_ID_TYPE_VARIANT, 0);
        this.dataTracker.startTracking(HORN_CD, 61);
        this.dataTracker.startTracking(HORN, false);
        this.dataTracker.startTracking(PATROL_UUID, "");
    }
    public void setSwinging(boolean swinging) {
        this.dataTracker.set(SWINGING, swinging);
    }

    public boolean isSwinging() {
        return this.dataTracker.get(SWINGING);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return HostileEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 35)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.65f)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0D + ModBonusHealthConfig.einherjarBonusHealth)
                .add(EntityAttributes.GENERIC_ARMOR, 7f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.5f)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.45f)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.5f);
    }
    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new EinherjarHornGoal(this, StatusEffects.STRENGTH));
        this.goalSelector.add(3, new ModPatrolGoal(this, 0.37f, 0.4f));
        this.goalSelector.add(4, new NorseMeleeAttackGoal(this, 0.48f, 21, 10));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.35f, 1f));
        this.goalSelector.add(6, new LookAroundGoal(this));

        this.targetSelector.add(1, new GroupRevengeGoal(this, NorseEntity.class).setGroupRevenge());
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, ZombieEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, DraugrEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, VillagerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, IronGolemEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, RaiderEntity.class, true));
        this.targetSelector.add(5, new ActiveTargetGoal<>(this, GreekEntity.class, true));
        this.targetSelector.add(5, new ActiveTargetGoal<>(this, EgyptianEntity.class, true));
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
        if(this.isSwinging() && !this.getHorn() && state.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
            state.getController().forceAnimationReset();
            state.getController().setAnimation(RawAnimation.begin().then("attack", Animation.LoopType.PLAY_ONCE));
        } else if (this.getHorn()) {
            return PlayState.STOP;
        }
        return PlayState.CONTINUE;
    }

    private PlayState hornPredicate(AnimationState state) {
        if(this.getHorn() && state.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
            state.getController().forceAnimationReset();
            state.getController().setAnimation(RawAnimation.begin().then("horn", Animation.LoopType.PLAY_ONCE));
        }
        return PlayState.CONTINUE;
    }
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController(this, "controller",0, this::predicate));
        controllers.add(new AnimationController(this, "horn", 0, this::hornPredicate).setSoundKeyframeHandler(state -> {
            PlayerEntity player = ClientUtils.getClientPlayer();
            if (player != null)
                this.getWorld().playSound(player, this.getX(), this.getY(), this.getZ(), ModSounds.EINHERJAR_HORN, this.getSoundCategory(), 0.65f, 1f);
        }));
        controllers.add(new AnimationController(this, "attacking", 0, this::attackPredicate).setSoundKeyframeHandler(state -> {
            PlayerEntity player = ClientUtils.getClientPlayer();
            if (player != null)
                this.getWorld().playSound(player, this.getX(), this.getY(), this.getZ(), ModSounds.SWING, this.getSoundCategory(), 0.5f, 0.8f);
        }));
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        rand = random();
        if (rand < 0.5)
            return ModSounds.EINHERJAR_HURT1;
        else
            return ModSounds.EINHERJAR_HURT2;
    }
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.EINHERJAR_DEATH1;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        LivingEntity target = this.getTarget();
        rand = random();
        if (target != null) {
            if (rand < 0.3)
                return ModSounds.EINHERJAR_AMBIENT1;
            else if (rand > 0.3 && rand < 0.6)
                return ModSounds.EINHERJAR_AMBIENT2;
            else
                return ModSounds.EINHERJAR_AMBIENT3;
        } else {
            if (rand < 0.3)
                return ModSounds.EINHERJAR_AMBIENT4;
            else if (rand > 0.3 && rand < 0.6)
                return ModSounds.EINHERJAR_AMBIENT5;
            else
                return ModSounds.EINHERJAR_AMBIENT6;
        }
    }
    @Override
    public void playAmbientSound() {
        SoundEvent soundEvent = this.getAmbientSound();
        if (soundEvent != null) {
            this.playSound(soundEvent, 0.35f, 1f);
        }
    }
    @Override
    protected void playHurtSound(DamageSource source) {
        this.playSound(this.getHurtSound(source), 0.6F, 0.85F);
    }
    @Override
    public int getMinAmbientSoundDelay() {
        return 180;
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty,
                                 SpawnReason spawnReason, @Nullable EntityData entityData,
                                 @Nullable NbtCompound entityNbt) {
        EinherjarVariant variant = Util.getRandom(EinherjarVariant.values(), this.random);
        setVariant(variant);

        return entityData;
    }

    public EinherjarVariant getVariant() {
        return EinherjarVariant.byId(this.getTypeVariant() & 255);
    }

    private void setVariant(EinherjarVariant variant) {
        this.dataTracker.set(DATA_ID_TYPE_VARIANT, variant.getId() & 255);
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
        BlockState blockBelow = world.getBlockState(blockPos);

        boolean isSnowyGround = blockBelow.isOf(Blocks.SNOW_BLOCK)
                || blockBelow.isOf(Blocks.POWDER_SNOW)
                || blockBelow.isOf(Blocks.GRASS_BLOCK)
                || blockBelow.isOf(Blocks.DIRT)
                || blockBelow.isOf(Blocks.SNOW);

        if (!isSnowyGround) {
            return false;
        }

        if (world.getLightLevel(pos) < 9) {
            return false;
        }

        if (!world.isSkyVisible(pos.up())) {
            return false;
        }

        int randomValue = random.nextInt(10);
        return randomValue < ModSpawnRateConfig.einherjarSpawnRate;
    }
}
