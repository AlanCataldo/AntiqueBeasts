package net.mebahel.antiquebeasts.entity.custom.egyptian;

import net.mebahel.antiquebeasts.entity.ai.CustomRevengeGoal;
import net.mebahel.antiquebeasts.entity.ai.MummyShootingGoal;
import net.mebahel.antiquebeasts.entity.ai.MummySummonGoal;
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


public class MummyEntity extends EgyptianEntity implements IAnimatable, IAnimationTickable {
    public MummyEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
        this.ambientSoundChance = -this.getMinAmbientSoundDelay();
    }

    public String animationProcedure = "empty";
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    @Override
    public int tickTimer() {
        return age;
    }
    public static final TrackedData<Integer> SPAWN_CD = DataTracker.registerData(MummyEntity.class,
            TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Boolean> SPAWN = DataTracker.registerData(MummyEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> HAS_SPAWNED = DataTracker.registerData(MummyEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Integer> COOLDOWN = DataTracker.registerData(MummyEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Boolean> SHOOTING = DataTracker.registerData(MummyEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public void setShooting(boolean shooting) {
        this.dataTracker.set(SHOOTING, shooting);
    }
    public int getCooldown() { return this.dataTracker.get(COOLDOWN);}
    public void setCooldown(int cooldown) {
        this.dataTracker.set(COOLDOWN, cooldown);
    }
    public boolean isShooting() {
        return this.dataTracker.get(SHOOTING);
    }
    public int getSpawnCooldown() {return this.dataTracker.get(SPAWN_CD);}
    public void setSpawnCooldown(int cooldown) { this.dataTracker.set(SPAWN_CD, cooldown);}
    public boolean getSpawn() {return this.dataTracker.get(SPAWN);}
    public void setSpawn(boolean bool) {
        this.dataTracker.set(SPAWN, bool);
    }
    public boolean getHasSpawned() {return this.dataTracker.get(HAS_SPAWNED);}
    public void setHasSpawned(boolean bool) {
        this.dataTracker.set(HAS_SPAWNED, bool);
    }
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SWINGING, false);
        this.dataTracker.startTracking(SHOOTING, false);
        this.dataTracker.startTracking(COOLDOWN, 0);
        this.dataTracker.startTracking(DATA_ID_TYPE_VARIANT, 0);
        this.dataTracker.startTracking(IS_IN_CARAVAN, false);
        this.dataTracker.startTracking(ATTACK_NAME, "attack");
        this.dataTracker.startTracking(SPAWN_CD, 120);
        this.dataTracker.startTracking(SPAWN, false);
        this.dataTracker.startTracking(HAS_SPAWNED, false);
        this.dataTracker.startTracking(PATROL_UUID, "");
    }
    public static DefaultAttributeContainer.Builder setAttributes() {
        return HostileEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.55f)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 36.0D)
                .add(EntityAttributes.GENERIC_ARMOR, 4f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0f)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.2f)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.5f);
    }
    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new MummySummonGoal(this, 0.51f));
        this.goalSelector.add(3, new MummyShootingGoal(this, 0.51f));
        this.goalSelector.add(4, new EgyptianMeleeAttackGoal(this, 0.51f, 6f, 1, 10));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.45f, 1f));
        this.goalSelector.add(6, new LookAroundGoal(this));

        this.targetSelector.add(1, new CustomRevengeGoal(this, EgyptianEntity.class));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, GreekEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, NorseEntity.class, true));
    }
    private <E extends IAnimatable> PlayState raisePredicate(AnimationEvent<E> event) {
        if (this.getSpawn()) {
            System.out.println("JE LANCE L'ANIMAFION");
            //event.getController().markNeedsReload();
            event.getController().setAnimation(new AnimationBuilder().addAnimation("raise", ILoopType.EDefaultLoopTypes.PLAY_ONCE));
        }
        return PlayState.CONTINUE;
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
        if (this.animationProcedure.equals("empty") && !this.isShooting()) {
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
            if (this.isSwinging() && event.getController().getAnimationState().equals(software.bernie.geckolib3.core.AnimationState.Stopped)) {
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

    private <E extends IAnimatable> PlayState shootingPredicate(AnimationEvent<E> event) {
        if (this.isShooting() && event.getController().getAnimationState().equals(software.bernie.geckolib3.core.AnimationState.Stopped) && !this.isSwinging()) {
            event.getController().markNeedsReload();
            event.getController().setAnimation(new AnimationBuilder().addAnimation("shoot", ILoopType.EDefaultLoopTypes.PLAY_ONCE));
            return PlayState.CONTINUE;
        }
        return PlayState.CONTINUE;
    }
    @Override
    public void registerControllers(AnimationData data) {
        AnimationController<MummyEntity> controller = new AnimationController<>(this, "controller", 0, this::movementPredicate);
        AnimationController<MummyEntity> controller1 = new AnimationController<>(this, "attacking", 0, this::attackPredicate);
        AnimationController<MummyEntity> controller2 = new AnimationController<>(this, "procedure", 0, this::procedurePredicate);
        AnimationController<MummyEntity> controller3 = new AnimationController<>(this, "shooting", 0, this::shootingPredicate);
        AnimationController<MummyEntity> controller4 = new AnimationController<>(this, "raising", 0, this::raisePredicate);
        AnimationController<MummyEntity> controller5 = new AnimationController<>(this, "spawning", 0, this::spawnPredicate);
        controller3.registerSoundListener(this::soundListener);
        controller4.registerSoundListener(this::soundListener);
        controller5.registerSoundListener(this::soundListener);
        data.addAnimationController(controller);
        data.addAnimationController(controller1);
        data.addAnimationController(controller2);
        data.addAnimationController(controller3);
        data.addAnimationController(controller4);
        data.addAnimationController(controller5);
    }
    private <ENTITY extends IAnimatable> void soundListener(SoundKeyframeEvent<ENTITY> event) {
        if (event.sound.matches("mummy_shoot")) {
            if (this.world.isClient) {
                this.getEntityWorld().playSound(this.getX(), this.getY(), this.getZ(), ModSounds.MUMMY_SHOOT,
                        SoundCategory.HOSTILE, 1F, 1F, true);
            }
        } else if (event.sound.matches("mummy_raise")) {
            if (this.world.isClient) {
                this.getEntityWorld().playSound(this.getX(), this.getY(), this.getZ(), ModSounds.MUMMY_RAISE,
                        SoundCategory.HOSTILE, 1F, 1F, true);
                System.out.println("JE RAISE");
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
        if (shouldDespawnInPeaceful() || this.shouldDespawn) {
            remove(RemovalReason.DISCARDED);
        }
        System.out.println(this.getSpawn() + "ET LE COOLDOWN" + this.getSpawnCooldown());

        if (this.age < 40) {
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(0);
        } else if (Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).getValue() == 0) {
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(0.57f);
        }
    }
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        rand = random();
        if (rand < 0.5)
            return ModSounds.MUMMY_HURT_1;
        else
            return ModSounds.MUMMY_HURT_2;
    }
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.MUMMY_DEATH;
    }
    @Override
    protected SoundEvent getAmbientSound() {
        rand = random();
        if (rand < 0.5)
            return ModSounds.MUMMY_AMBIENT_1;
        else
            return ModSounds.MUMMY_AMBIENT_2;
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
            if (randomValue >= 0 && randomValue <= 5) {
                this.shouldDespawn = true;
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
