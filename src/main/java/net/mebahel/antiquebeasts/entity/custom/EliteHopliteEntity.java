package net.mebahel.antiquebeasts.entity.custom;

import net.mebahel.antiquebeasts.entity.ai.HopliteMeleeAttackGoal;
import net.mebahel.antiquebeasts.entity.variant.EliteHopliteVariant;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.*;
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
import net.minecraft.util.Util;
import net.minecraft.world.*;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.ClientUtils;

import javax.annotation.Nullable;
import java.util.Objects;


public class EliteHopliteEntity extends HopliteEntity implements GeoEntity {
    public static final TrackedData<Boolean> SWINGING = DataTracker.registerData(EliteHopliteEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);

    public static final TrackedData<String> ATTACK_NAME = DataTracker.registerData(EliteHopliteEntity.class,
            TrackedDataHandlerRegistry.STRING);

    public void setAttackName(String attackName) {
        this.dataTracker.set(ATTACK_NAME, attackName);
    }

    public String getAttackName() {
        return this.dataTracker.get(ATTACK_NAME);
    }
    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
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

    private boolean shouldDespawnInPeaceful() {
        return this.getWorld().getDifficulty() == Difficulty.PEACEFUL;
    }

    public EliteHopliteEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
        this.ambientSoundChance = -this.getMinAmbientSoundDelay();
    }

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SWINGING, false);
        this.dataTracker.startTracking(DATA_ID_TYPE_VARIANT, 0);
        this.dataTracker.startTracking(ATTACK_NAME, "attack");
        this.dataTracker.startTracking(TICKCOUNTER, 0);
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
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 24.0D)
                .add(EntityAttributes.GENERIC_ARMOR, 4f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0f)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.2f)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.5f);
    }
    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new HopliteMeleeAttackGoal(this, 0.45f, false));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.35f, 1f));
        this.goalSelector.add(6, new LookAroundGoal(this));

        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, ZombieEntity.class, true));
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
                this.getWorld().playSound(player, this.getX(), this.getY(), this.getZ(), ModSounds.SWING, this.getSoundCategory(), 0.5f, 1.5f);
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
            DataTracker.registerData(EliteHopliteEntity.class, TrackedDataHandlerRegistry.INTEGER);

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty,
                                 SpawnReason spawnReason, @Nullable EntityData entityData,
                                 @Nullable NbtCompound entityNbt) {
        EliteHopliteVariant variant = Util.getRandom(EliteHopliteVariant.values(), this.random);
        setVariant(variant);
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    public EliteHopliteVariant getVariant() {
        return EliteHopliteVariant.byId(this.getTypeVariant() & 255);
    }

    private int getTypeVariant() {
        return this.dataTracker.get(DATA_ID_TYPE_VARIANT);
    }

    private void setVariant(EliteHopliteVariant variant) {
        this.dataTracker.set(DATA_ID_TYPE_VARIANT, variant.getId() & 255);
    }
}
