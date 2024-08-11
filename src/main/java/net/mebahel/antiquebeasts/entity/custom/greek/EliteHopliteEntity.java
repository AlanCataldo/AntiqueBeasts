package net.mebahel.antiquebeasts.entity.custom.greek;

import net.mebahel.antiquebeasts.entity.ai.CustomRevengeGoal;
import net.mebahel.antiquebeasts.entity.ai.ModPatrolGoal;
import net.mebahel.antiquebeasts.entity.ai.greek.GreekMeleeAttackGoal;
import net.mebahel.antiquebeasts.entity.custom.egyptian.EgyptianEntity;
import net.mebahel.antiquebeasts.entity.custom.norse.NorseEntity;
import net.mebahel.antiquebeasts.entity.variant.EliteHopliteVariant;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.mebahel.antiquebeasts.util.ModSoundUtil;
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
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Util;
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


public class EliteHopliteEntity extends GreekEntity implements GeoEntity {

    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);

    public static final TrackedData<Float> COOLDOWN = DataTracker.registerData(EliteHopliteEntity.class,
            TrackedDataHandlerRegistry.FLOAT);

    public float getCooldown() { return this.dataTracker.get(COOLDOWN);}
    public void setCooldown(float cooldown) {
        this.dataTracker.set(COOLDOWN, cooldown);
    }
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
        this.dataTracker.startTracking(PATROL_UUID, "");
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
        this.goalSelector.add(2, new ModPatrolGoal(this, 0.37f, 0.4f));
        this.goalSelector.add(3, new GreekMeleeAttackGoal(this, 0.45f, 21, 10));
        this.goalSelector.add(4, new WanderAroundFarGoal(this, 0.35f, 1f));
        this.goalSelector.add(5, new LookAroundGoal(this));

        this.targetSelector.add(1, new CustomRevengeGoal(this, GreekEntity.class));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, ZombieEntity.class, true));
        this.targetSelector.add(4, new ActiveTargetGoal<>(this, EgyptianEntity.class, true));
        this.targetSelector.add(4, new ActiveTargetGoal<>(this, NorseEntity.class, true));

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

    @Override
    public boolean damage(DamageSource source, float amount) {
        return super.damage(source, amount);
    }

    /* VARIANTS */

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty,
                                 SpawnReason spawnReason, @Nullable EntityData entityData,
                                 @Nullable NbtCompound entityNbt) {
        EliteHopliteVariant variant = Util.getRandom(EliteHopliteVariant.values(), this.random);
        setVariant(variant);
        ModSoundUtil.InfantryPlaySound(spawnReason, this);
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    public EliteHopliteVariant getVariant() {
        return EliteHopliteVariant.byId(this.getTypeVariant() & 255);
    }

    public int getTypeVariant() {
        return this.dataTracker.get(DATA_ID_TYPE_VARIANT);
    }

    private void setVariant(EliteHopliteVariant variant) {
        this.dataTracker.set(DATA_ID_TYPE_VARIANT, variant.getId() & 255);
    }
}
