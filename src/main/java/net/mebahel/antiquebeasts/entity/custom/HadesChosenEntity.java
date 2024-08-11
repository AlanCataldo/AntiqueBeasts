package net.mebahel.antiquebeasts.entity.custom;

import net.mebahel.antiquebeasts.entity.ModEntities;
import net.mebahel.antiquebeasts.entity.ai.HadesChosenMeleeAttackGoal;
import net.mebahel.antiquebeasts.entity.ai.HadesChosenShootingGoal;
import net.mebahel.antiquebeasts.entity.custom.greek.EliteHopliteEntity;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
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
import java.util.Objects;

import static java.lang.Math.random;


public class HadesChosenEntity extends HostileEntity implements GeoEntity {
    double rand;
    public static final TrackedData<Boolean> SHOOTING = DataTracker.registerData(HadesChosenEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> SWINGING = DataTracker.registerData(HadesChosenEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Float> COOLDOWN = DataTracker.registerData(HadesChosenEntity.class,
            TrackedDataHandlerRegistry.FLOAT);
    public static final TrackedData<String> ATTACK_NAME = DataTracker.registerData(HadesChosenEntity.class,
            TrackedDataHandlerRegistry.STRING);
    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }
    public void setAttackName(String attackName) {
        this.dataTracker.set(ATTACK_NAME, attackName);
    }
    public String getAttackName() {
        return this.dataTracker.get(ATTACK_NAME);
    }
    public void setShooting(boolean shooting) {
        this.dataTracker.set(SHOOTING, shooting);
    }
    public static final TrackedData<Integer> TICKCOUNTER = DataTracker.registerData(EliteHopliteEntity.class,
            TrackedDataHandlerRegistry.INTEGER);

    public void setTickCounter(Integer counter) {
        this.dataTracker.set(TICKCOUNTER, counter);
    }

    public int getTickCounter() {
        return this.dataTracker.get(TICKCOUNTER);
    }

    public boolean isTransitionning = false;

    private boolean shouldDespawnInPeaceful() {
        return this.getWorld().getDifficulty() == Difficulty.PEACEFUL;
    }

    @Override
    public void tick() {
        super.tick();
        if (shouldDespawnInPeaceful()) {
            remove(RemovalReason.DISCARDED);
        }
        if (this.getTickCounter() > 0) {
            this.setTickCounter(Math.max(this.getTickCounter() - 1, 0));
        }
        if (this.getTarget() != null) {
            this.setTickCounter(14);
        }
        if (this.getTickCounter() < 13 && this.getTickCounter() > 0) {
            this.isTransitionning = true;
        }
        if (this.getTickCounter() == 0) {
            this.isTransitionning = false;
        }
    }
    public HadesChosenEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.ambientSoundChance = -this.getMinAmbientSoundDelay();
    }
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SHOOTING, false);
        this.dataTracker.startTracking(SWINGING, false);
        this.dataTracker.startTracking(COOLDOWN, 0f);
        this.dataTracker.startTracking(TICKCOUNTER, 0);
        this.dataTracker.startTracking(ATTACK_NAME, "attack");
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

    public static DefaultAttributeContainer.Builder setAttributes() {
        return HostileEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.72f)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.5f)
                .add(EntityAttributes.GENERIC_ARMOR, 8f)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.3f)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.5f);
    }
    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new HadesChosenMeleeAttackGoal(this, 0.45f));
        this.goalSelector.add(3, new HadesChosenShootingGoal(this));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.35f, 1f));
        this.goalSelector.add(6, new LookAroundGoal(this));

        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }
    private PlayState predicate(AnimationState animationState) {
        if (this.age < 5) {
            animationState.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        if (!this.isAttacking() && this.isTransitionning && this.getTickCounter() != 0
                && !this.isSwinging()) {
            animationState.getController().forceAnimationReset();
            animationState.getController().setAnimation(RawAnimation.begin().then("no_target_transition", Animation.LoopType.PLAY_ONCE));
            return PlayState.CONTINUE;
        } else if (animationState.isMoving() && this.isAttacking()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("walk3", Animation.LoopType.PLAY_ONCE).then("walk2", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        } else if (animationState.isMoving() && !this.isAttacking() && this.getTickCounter() == 0) {
            animationState.getController().setAnimation(RawAnimation.begin().then("walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        var test = animationState.getController().getCurrentAnimation();
        if (test != null) {
            if (!Objects.equals(test.animation().name(), "no_target_transition") ||
                    (Objects.equals(test.animation().name(), "no_target_transition") && animationState.getController().getAnimationState().equals(AnimationController.State.STOPPED))) {
                animationState.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
                return PlayState.CONTINUE;
            }
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
            state.getController().setAnimation(RawAnimation.begin().then("throwing", Animation.LoopType.PLAY_ONCE));
        }
        return PlayState.CONTINUE;
    }
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController(this, "controller",0, this::predicate));
        controllers.add(new AnimationController(this, "attacking", 0, this::attackPredicate).setSoundKeyframeHandler(state -> {
            PlayerEntity player = ClientUtils.getClientPlayer();
            if (player != null)
                this.getWorld().playSound(player, this.getX(), this.getY(), this.getZ(), ModSounds.SWING, this.getSoundCategory(), 0.5f, 1.4f);
        }));
        controllers.add(new AnimationController(this, "shooting", 0, this::shootingPredicate).setSoundKeyframeHandler(state -> {
            PlayerEntity player = ClientUtils.getClientPlayer();
            if (player != null)
                this.getWorld().playSound(player, this.getX(), this.getY(), this.getZ(), ModSounds.SWING, this.getSoundCategory(), 0.5f, 1f);
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
        if (source.isOf(DamageTypes.IN_FIRE) || source.isOf(DamageTypes.ON_FIRE) || source.isOf(DamageTypes.WITHER)) {
            return false;
        }
        return super.damage(source, amount);
    }
    @Override
    public void onDeath(DamageSource cause) {
        super.onDeath(cause);

        rand = random();
        if (rand < 0.45) {
            if (cause.getAttacker() instanceof PlayerEntity) {
                World world = this.getEntityWorld();
                double x = this.getX();
                double y = this.getY();
                double z = this.getZ();

                HadesShadeEntity hadesShade = new HadesShadeEntity(ModEntities.HADES_SHADE, this.getWorld());
                hadesShade.setPosition(x, y, z);
                hadesShade.playSound(ModSounds.HADES_SHADE_SPAWN, 0.5f, 1);
                world.spawnEntity(hadesShade);
            }
        }
    }
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        rand = random();
        if (rand < 0.5)
            return ModSounds.HOPLITE_HURT1;
        else
            return ModSounds.HOPLITE_HURT2;
    }
    @Override
    protected SoundEvent getDeathSound() {
        rand = random();
        if (rand < 0.5)
            return ModSounds.HOPLITE_DEATH1;
        else
            return ModSounds.HOPLITE_DEATH2;
    }
    @Override
    public int getMinAmbientSoundDelay() {
        return 240;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        LivingEntity target = this.getTarget();
        rand = random();
        if (target != null) {
            if (rand < 0.5)
                return ModSounds.HOPLITE_ATTACKING1;
            else
                return ModSounds.HOPLITE_ATTACKING2;
        } else {
            if (rand < 0.3)
                return ModSounds.HOPLITE_AMBIENT1;
            else if (rand > 0.3 && rand < 0.6)
                return ModSounds.HOPLITE_AMBIENT2;
            else
                return ModSounds.HOPLITE_AMBIENT3;
        }
    }
    @Override
    public void playAmbientSound() {
        SoundEvent soundEvent = this.getAmbientSound();
        if (soundEvent != null) {
            this.playSound(soundEvent, 0.35f, 0.93f);
        }
    }
    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty,
                                 SpawnReason spawnReason, @Nullable EntityData entityData,
                                 @Nullable NbtCompound entityNbt) {
        if (spawnReason == SpawnReason.SPAWNER && this.getWorld().isClient) {
            PlayerEntity player = ClientUtils.getClientPlayer();
            if (player != null)
                this.getWorld().playSound(player, this.getX(), this.getY(), this.getZ(), ModSounds.MILITARY_CREATE, this.getSoundCategory(), 0.5f, 1f);
        }
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }
}
