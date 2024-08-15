package net.mebahel.antiquebeasts.entity.custom.egyptian;

import net.mebahel.antiquebeasts.entity.ai.CustomRevengeGoal;
import net.mebahel.antiquebeasts.entity.ai.WadjetLookAtTargetGoal;
import net.mebahel.antiquebeasts.entity.ai.WadjetShootingGoal;
import net.mebahel.antiquebeasts.entity.custom.greek.GreekEntity;
import net.mebahel.antiquebeasts.entity.custom.norse.NorseEntity;
import net.mebahel.antiquebeasts.entity.variant.WadjetVariant;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimationTickable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.SoundKeyframeEvent;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.Objects;

import static java.lang.Math.random;

public class WadjetEntity extends EgyptianEntity implements IAnimatable, IAnimationTickable {
    double rand;

    public String animationProcedure = "empty";
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    @Override
    public int tickTimer() {
        return age;
    }

    public WadjetEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    public static final TrackedData<Float> COOLDOWN = DataTracker.registerData(WadjetEntity.class, TrackedDataHandlerRegistry.FLOAT);

    public static final TrackedData<Boolean> SHOOTING = DataTracker.registerData(WadjetEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
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

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SWINGING, false);
        this.dataTracker.startTracking(COOLDOWN, 0f);
        this.dataTracker.startTracking(SHOOTING, false);
        this.dataTracker.startTracking(IS_IN_CARAVAN, false);
        this.dataTracker.startTracking(DATA_ID_TYPE_VARIANT, 0);
        this.dataTracker.startTracking(PATROL_UUID, "");
    }
    public void tick() {
        super.tick();
        if (shouldDespawnInPeaceful()) {
            this.remove(RemovalReason.DISCARDED);
        }
        if (isShooting())
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(0f);
        else
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(0.63f);
    }
    private <E extends IAnimatable> PlayState movementPredicate(AnimationEvent<E> event) {
        if (event.isMoving() && !this.isShooting()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("walk2", ILoopType.EDefaultLoopTypes.LOOP));
            return PlayState.CONTINUE;
        } else if (!this.isShooting() && !event.isMoving() && !this.isAttacking()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("idle", ILoopType.EDefaultLoopTypes.LOOP));
            return PlayState.CONTINUE;
        }
        return PlayState.CONTINUE;
    }

    private <E extends IAnimatable> PlayState procedurePredicate(AnimationEvent<E> event) {
        if (!(this.animationProcedure.equals("empty"))
                && event.getController().getAnimationState().equals(software.bernie.geckolib3.core.AnimationState.Stopped)) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation(this.animationProcedure, ILoopType.EDefaultLoopTypes.PLAY_ONCE));
            if (event.getController().getAnimationState().equals(software.bernie.geckolib3.core.AnimationState.Stopped)) {
                this.animationProcedure = "empty";
                event.getController().markNeedsReload();
            }
        }
        return PlayState.CONTINUE;
    }

    private <E extends IAnimatable> PlayState shootingPredicate(AnimationEvent<E> event) {
        if (this.isShooting() && event.getController().getAnimationState().equals(software.bernie.geckolib3.core.AnimationState.Stopped) && !this.isSwinging()) {
            event.getController().markNeedsReload();
            event.getController().setAnimation(new AnimationBuilder().addAnimation("attack2", ILoopType.EDefaultLoopTypes.PLAY_ONCE));
            return PlayState.CONTINUE;
        }
        return PlayState.CONTINUE;
    }
    @Override
    public void registerControllers(AnimationData data) {
        AnimationController<WadjetEntity> controller = new AnimationController<>(this, "controller", 0,
                this::movementPredicate);
        AnimationController<WadjetEntity> controller2 = new AnimationController<>(this, "procedure", 0, this::procedurePredicate);
        AnimationController<WadjetEntity> controller3 = new AnimationController<>(this, "shooting", 0, this::shootingPredicate);
        controller3.registerSoundListener(this::soundListener);
        data.addAnimationController(controller);
        data.addAnimationController(controller2);
        data.addAnimationController(controller3);
    }
    private <ENTITY extends IAnimatable> void soundListener(SoundKeyframeEvent<ENTITY> event) {
        if (event.sound.matches("wadjet_spit_1")) {
            if (this.world.isClient) {
                this.getEntityWorld().playSound(this.getX(), this.getY(), this.getZ(), ModSounds.WADJET_SPIT_1,
                        SoundCategory.HOSTILE, 1F, 1F, true);
            }
        }
    }
    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
    public static DefaultAttributeContainer.Builder setAttributes() {
        return HostileEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.63f)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0D)
                .add(EntityAttributes.GENERIC_ARMOR, 5f)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.1f);
    }
    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new WadjetShootingGoal(this, 128F));
        this.goalSelector.add(3, new WadjetLookAtTargetGoal(this));
        this.goalSelector.add(6, new WanderAroundFarGoal(this, 0.35f, 1f));

        this.targetSelector.add(1, new CustomRevengeGoal(this, EgyptianEntity.class));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, ZombieEntity.class, true));
        this.targetSelector.add(4, new ActiveTargetGoal<>(this, GreekEntity.class, true));
        this.targetSelector.add(4, new ActiveTargetGoal<>(this, NorseEntity.class, true));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        rand = random();
        if (rand < 0.3)
            return ModSounds.WADJET_AMBIENT_1;
        else if (rand > 0.3 && rand < 0.6)
            return ModSounds.WADJET_AMBIENT_2;
        else
            return ModSounds.WADJET_AMBIENT_3;
    }
    protected SoundEvent getDeathSound() {
        return ModSounds.WADJET_DEATH_1;
    }
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        rand = random();
        if (rand < 0.5)
            return ModSounds.WADJET_HURT_1;
        else
            return ModSounds.WADJET_HURT_2;
    }
    @Override
    public void playAmbientSound() {
        SoundEvent soundEvent = this.getAmbientSound();
        if (soundEvent != null) {
            this.playSound(soundEvent, 0.35f, 1f);
        }
    }
    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty,
                                 SpawnReason spawnReason, @javax.annotation.Nullable EntityData entityData,
                                 @javax.annotation.Nullable NbtCompound entityNbt) {
        WadjetVariant variant = Util.getRandom(WadjetVariant.values(), this.random);
        setVariant(variant);
        if (spawnReason != SpawnReason.SPAWN_EGG &&
                spawnReason != SpawnReason.COMMAND &&
                spawnReason != SpawnReason.SPAWNER &&
                spawnReason != SpawnReason.EVENT) {
            int randomValue = this.random.nextInt(11);
            if (randomValue >= 0 && randomValue <= 6) {
                this.remove(RemovalReason.DISCARDED);
            }
        }
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    public WadjetVariant getVariant() {
        return WadjetVariant.byId(this.getTypeVariant() & 255);
    }
    public void setVariant(WadjetVariant variant) {
        this.dataTracker.set(DATA_ID_TYPE_VARIANT, variant.getId() & 255);
    }

    public void performJump(Vec3d direction) {
        this.setVelocity(direction);
        this.velocityDirty = true;
    }
}
