package net.mebahel.antiquebeasts.entity.custom.egyptian;

import net.mebahel.antiquebeasts.entity.ai.CustomRevengeGoal;
import net.mebahel.antiquebeasts.entity.ai.egyptian.EgyptianMeleeAttackGoal;
import net.mebahel.antiquebeasts.entity.custom.greek.GreekEntity;
import net.mebahel.antiquebeasts.entity.custom.norse.NorseEntity;
import net.mebahel.antiquebeasts.entity.variant.EgyptiantVariant;
import net.mebahel.antiquebeasts.sound.ModSounds;
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
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Util;
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


public class ServantEntity extends EgyptianEntity implements IAnimatable, IAnimationTickable {
    public ServantEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
        this.ambientSoundChance = -this.getMinAmbientSoundDelay();
    }

    public String animationProcedure = "empty";
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    @Override
    public int tickTimer() {
        return age;
    }
    public static final TrackedData<Boolean> HAS_SPAWNED = DataTracker.registerData(ServantEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);
    public boolean getHasSpawned() {return this.dataTracker.get(HAS_SPAWNED);}
    public void setHasSpawned(boolean bool) {
        this.dataTracker.set(HAS_SPAWNED, bool);
    }

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SWINGING, false);
        this.dataTracker.startTracking(DATA_ID_TYPE_VARIANT, 0);
        this.dataTracker.startTracking(IS_IN_CARAVAN, false);
        this.dataTracker.startTracking(ATTACK_NAME, "attack");
        this.dataTracker.startTracking(HAS_SPAWNED, false);
        this.dataTracker.startTracking(PATROL_UUID, "");
    }
    public static DefaultAttributeContainer.Builder setAttributes() {
        return HostileEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.57f)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 18.0D)
                .add(EntityAttributes.GENERIC_ARMOR, 1f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0f)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.2f)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.5f);
    }
    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new EgyptianMeleeAttackGoal(this, 0.57f, 6f, 1, 10));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.45f, 1f));
        this.goalSelector.add(6, new LookAroundGoal(this));

        this.targetSelector.add(1, new CustomRevengeGoal(this, EgyptianEntity.class));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, GreekEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, NorseEntity.class, true));
    }

    private <E extends IAnimatable> PlayState spawnPredicate(AnimationEvent<E> event) {
        if (!this.getHasSpawned()) {
            event.getController().markNeedsReload();
            event.getController().setAnimation(new AnimationBuilder().addAnimation("spawn", ILoopType.EDefaultLoopTypes.PLAY_ONCE));
        }

        this.setHasSpawned(true);
        return PlayState.CONTINUE;
    }
    private <E extends IAnimatable> PlayState movementPredicate(AnimationEvent<E> event) {
        if (this.animationProcedure.equals("empty")) {
            if (event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F)) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("walk", ILoopType.EDefaultLoopTypes.LOOP));
                return PlayState.CONTINUE;
            } else if (!this.isSwinging()) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("idle", ILoopType.EDefaultLoopTypes.LOOP));
                return PlayState.CONTINUE;
            }
        }
        return PlayState.STOP;
    }

    private <E extends IAnimatable> PlayState attackPredicate(AnimationEvent<E> event) {
        if (this.animationProcedure.equals("empty") && this.isSwinging()) {
            if (event.getController().getAnimationState().equals(software.bernie.geckolib3.core.AnimationState.Stopped)) {
                event.getController().markNeedsReload();
                event.getController().setAnimation(new AnimationBuilder().addAnimation(this.getAttackName(), ILoopType.EDefaultLoopTypes.PLAY_ONCE));
                return PlayState.CONTINUE;
            }
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

    @Override
    public void registerControllers(AnimationData data) {
        AnimationController<ServantEntity> controller = new AnimationController<>(this, "controller", 0,
                this::movementPredicate);
        AnimationController<ServantEntity> controller1 = new AnimationController<>(this, "attacking", 0, this::attackPredicate);
        AnimationController<ServantEntity> controller2 = new AnimationController<>(this, "procedure", 0, this::procedurePredicate);
        AnimationController<ServantEntity> controller3 = new AnimationController<>(this, "spawning", 0, this::spawnPredicate);
        controller1.registerSoundListener(this::soundListener);
        controller3.registerSoundListener(this::soundListener);
        data.addAnimationController(controller);
        data.addAnimationController(controller1);
        data.addAnimationController(controller2);
        data.addAnimationController(controller3);
    }
    private <ENTITY extends IAnimatable> void soundListener(SoundKeyframeEvent<ENTITY> event) {
        if (event.sound.matches("swing1")) {
            if (this.world.isClient) {
                this.getEntityWorld().playSound(this.getX(), this.getY(), this.getZ(), ModSounds.SWING,
                        SoundCategory.HOSTILE, 1F, 1.5F, true);
            }
        } else if (event.sound.matches("mummy_spawn")) {
            if (this.world.isClient) {
                this.getEntityWorld().playSound(this.getX(), this.getY(), this.getZ(), ModSounds.MUMMY_SPAWN,
                        SoundCategory.HOSTILE, 1F, 1F, true);
            }

        }
    }
    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
    @Override
    public void tick() {
        super.tick();
        if (shouldDespawnInPeaceful()) {
            this.remove(RemovalReason.DISCARDED);
        }

        if (this.age < 40) {
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(0);
        } else if (Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).getValue() == 0) {
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(0.57f);
        }
    }
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.SERVANT_HURT_1;
    }
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.SERVANT_DEATH_1;
    }
    @Override
    protected SoundEvent getAmbientSound() {
        rand = random();
        if (rand < 0.3)
            return ModSounds.SERVANT_AMBIENT_1;
        else if (rand > 0.3 && rand < 0.6)
            return ModSounds.SERVANT_AMBIENT_2;
        else
            return ModSounds.SERVANT_AMBIENT_3;
    }
    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty,
                                 SpawnReason spawnReason, @javax.annotation.Nullable EntityData entityData,
                                 @javax.annotation.Nullable NbtCompound entityNbt) {
        EgyptiantVariant variant = Util.getRandom(EgyptiantVariant.values(), this.random);
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
    public EgyptiantVariant getVariant() {
        return EgyptiantVariant.byId(this.getTypeVariant() & 255);
    }
    public void setVariant(EgyptiantVariant variant) {
        this.dataTracker.set(DATA_ID_TYPE_VARIANT, variant.getId() & 255);
    }
}
