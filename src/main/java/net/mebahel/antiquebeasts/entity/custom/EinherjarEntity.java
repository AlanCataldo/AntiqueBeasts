package net.mebahel.antiquebeasts.entity.custom;

import net.mebahel.antiquebeasts.entity.ai.EinherjarHornGoal;
import net.mebahel.antiquebeasts.entity.ai.EinherjarMeleeAttackGoal;
import net.mebahel.antiquebeasts.entity.variant.EinherjarVariant;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.pathing.*;
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
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Util;
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


public class EinherjarEntity extends NorseEntity implements GeoEntity {
    double rand;

    public static final TrackedData<Boolean> SWINGING = DataTracker.registerData(EinherjarEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> HORN = DataTracker.registerData(EinherjarEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);

    public static final TrackedData<String> ATTACK_NAME = DataTracker.registerData(EinherjarEntity.class,
            TrackedDataHandlerRegistry.STRING);
    public static final TrackedData<Integer> HORN_CD = DataTracker.registerData(EinherjarEntity.class,
            TrackedDataHandlerRegistry.INTEGER);

    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    public void setAttackName(String attackName) {
        this.dataTracker.set(ATTACK_NAME, attackName);
    }

    public boolean getHorn() {return this.dataTracker.get(HORN);}

    public void setHorn(boolean bool) {
        this.dataTracker.set(HORN, bool);
    }

    public int getHornCooldown() {return this.dataTracker.get(HORN_CD);}

    public void setHornCooldown(int cooldown) {
        this.dataTracker.set(HORN_CD, cooldown);
    }

    public String getAttackName() {
        return this.dataTracker.get(ATTACK_NAME);
    }

    public EinherjarEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
        this.ambientSoundChance = -this.getMinAmbientSoundDelay();
    }

    private boolean shouldDespawnInPeaceful() {
        return this.getWorld().getDifficulty() == Difficulty.PEACEFUL;
    }

    public void tick() {
        super.tick();
        if (shouldDespawnInPeaceful()) {
            remove(RemovalReason.DISCARDED);
        }
    }
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SWINGING, false);
        this.dataTracker.startTracking(ATTACK_NAME, "attack");
        this.dataTracker.startTracking(DATA_ID_TYPE_VARIANT, 0);
        this.dataTracker.startTracking(HORN_CD, 61);
        this.dataTracker.startTracking(HORN, false);
    }
    public void setSwinging(boolean swinging) {
        this.dataTracker.set(SWINGING, swinging);
    }

    public boolean isSwinging() {
        return this.dataTracker.get(SWINGING);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return HostileEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.65f)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0D)
                .add(EntityAttributes.GENERIC_ARMOR, 7f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.5f)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.45f)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.5f);
    }
    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new EinherjarHornGoal(this, StatusEffects.STRENGTH));
        this.goalSelector.add(3, new EinherjarMeleeAttackGoal(this, 0.45f));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.35f, 1f));
        this.goalSelector.add(6, new LookAroundGoal(this));

        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, ZombieEntity.class, true));
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
    protected EntityNavigation createNavigation(World world) {
        return new MobNavigation(this, world) {
            protected PathNodeNavigator createPathNodeNavigator(int range) {
                this.nodeMaker = new LandPathNodeMaker();
                this.nodeMaker.setCanEnterOpenDoors(true);
                return new PathNodeNavigator(this.nodeMaker, range) {
                    protected float getDistance(PathNode a, PathNode b) {
                        return a.getHorizontalDistance(b);
                    }
                };
            }
        };
    }
    @Override
    public boolean damage(DamageSource source, float amount) {
        return super.damage(source, amount);
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
        return 140;
    }
    @org.jetbrains.annotations.Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
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
            DataTracker.registerData(EinherjarEntity.class, TrackedDataHandlerRegistry.INTEGER);

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty,
                                 SpawnReason spawnReason, @Nullable EntityData entityData,
                                 @Nullable NbtCompound entityNbt) {
        EinherjarVariant variant = Util.getRandom(EinherjarVariant.values(), this.random);
        setVariant(variant);
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    public EinherjarVariant getVariant() {
        return EinherjarVariant.byId(this.getTypeVariant() & 255);
    }

    private int getTypeVariant() {
        return this.dataTracker.get(DATA_ID_TYPE_VARIANT);
    }

    private void setVariant(EinherjarVariant variant) {
        this.dataTracker.set(DATA_ID_TYPE_VARIANT, variant.getId() & 255);
    }
}
