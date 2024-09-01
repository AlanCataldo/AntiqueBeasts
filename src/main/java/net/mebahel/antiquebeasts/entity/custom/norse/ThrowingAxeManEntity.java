package net.mebahel.antiquebeasts.entity.custom.norse;

import net.mebahel.antiquebeasts.entity.ai.CustomRevengeGoal;
import net.mebahel.antiquebeasts.entity.ai.ThrowingAxeManLookAtTargetGoal;
import net.mebahel.antiquebeasts.entity.ai.ThrowingAxeManShootingGoal;
import net.mebahel.antiquebeasts.entity.custom.egyptian.EgyptianEntity;
import net.mebahel.antiquebeasts.entity.custom.greek.GreekEntity;
import net.mebahel.antiquebeasts.entity.variant.ThrowingAxeManVariant;
import net.mebahel.antiquebeasts.item.custom.ModItems;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.mebahel.antiquebeasts.util.ModConfig;
import net.mebahel.antiquebeasts.util.ModSoundUtil;
import net.minecraft.entity.*;
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
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.ClientUtils;

import static java.lang.Math.random;

public class ThrowingAxeManEntity extends NorseEntity implements GeoEntity {
    double rand;
    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);

    public ThrowingAxeManEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }
    public static final TrackedData<Float> COOLDOWN = DataTracker.registerData(ThrowingAxeManEntity.class,
            TrackedDataHandlerRegistry.FLOAT);
    public static final TrackedData<Boolean> SHOOTING = DataTracker.registerData(ThrowingAxeManEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);

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
        this.dataTracker.startTracking(COOLDOWN, 0f);
        this.dataTracker.startTracking(SHOOTING, false);
        this.dataTracker.startTracking(DATA_ID_TYPE_VARIANT, 0);
        this.dataTracker.startTracking(PATROL_UUID, "");
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }
    private PlayState predicate(AnimationState animationState) {
        if (animationState.isMoving() && !this.isShooting()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        if (!this.isShooting() && !animationState.isMoving()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        return PlayState.CONTINUE;
    }

    public void tick() {
        super.tick();
        if (shouldDespawnInPeaceful()) {
            remove(Entity.RemovalReason.DISCARDED);
        }
    }

    private PlayState shootingPredicate(AnimationState state) {
        if (this.isShooting() && state.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
            state.getController().forceAnimationReset();
            state.getController().setAnimation(RawAnimation.begin().then("attack", Animation.LoopType.PLAY_ONCE));
            return PlayState.CONTINUE;
        }
        return PlayState.CONTINUE;
    }
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller",0, this::predicate));
        controllers.add(new AnimationController(this, "shooting", 0, this::shootingPredicate).setSoundKeyframeHandler(state -> {
            PlayerEntity player = ClientUtils.getClientPlayer();
            if (player != null)
                this.getWorld().playSound(player, this.getX(), this.getY(), this.getZ(), ModSounds.SWING, this.getSoundCategory(), 0.8f, 1.25f);
        }));
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }
    public static DefaultAttributeContainer.Builder setAttributes() {
        return HostileEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 35)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.63f)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0D + ModConfig.infantryBonusHealth)
                .add(EntityAttributes.GENERIC_ARMOR, 5f)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.1f);
    }
    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new ThrowingAxeManShootingGoal(this, 80F));
        this.goalSelector.add(3, new ThrowingAxeManLookAtTargetGoal(this));
        this.goalSelector.add(6, new WanderAroundFarGoal(this, 0.35f, 1f));

        this.targetSelector.add(1, new CustomRevengeGoal(this, NorseEntity.class));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, ZombieEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, VillagerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, PillagerEntity.class, true));
        this.targetSelector.add(5, new ActiveTargetGoal<>(this, GreekEntity.class, true));
        this.targetSelector.add(5, new ActiveTargetGoal<>(this, EgyptianEntity.class, true));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        LivingEntity target = this.getTarget();
        rand = random();
        if (target != null) {
            if (rand < 0.5)
                return ModSounds.THROWING_AXEMAN_ATTACKING1;
            else
                return ModSounds.THROWING_AXEMAN_ATTACKING2;
        } else {
            if (rand < 0.3)
                return ModSounds.THROWING_AXEMAN_AMBIENT1;
            else if (rand > 0.3 && rand < 0.6)
                return ModSounds.THROWING_AXEMAN_AMBIENT2;
            else
                return ModSounds.THROWING_AXEMAN_AMBIENT3;
        }
    }
    protected SoundEvent getDeathSound() {
        rand = random();
        if (rand < 0.5)
            return ModSounds.HOPLITE_DEATH1;
        else
            return ModSounds.HOPLITE_DEATH2;
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
        ThrowingAxeManVariant variant = Util.getRandom(ThrowingAxeManVariant.values(), this.random);
        setVariant(variant);
        ModSoundUtil.InfantryPlaySound(spawnReason, this);
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    public ThrowingAxeManVariant getVariant() {
        return ThrowingAxeManVariant.byId(this.getTypeVariant() & 255);
    }

    private void setVariant(ThrowingAxeManVariant variant) {
        this.dataTracker.set(DATA_ID_TYPE_VARIANT, variant.getId() & 255);
    }

    public void performJump(Vec3d direction) {
        this.setVelocity(direction);
        this.velocityDirty = true;
    }
    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);

        if (source.getAttacker() instanceof ZombieEntity) {
            if (this.random.nextFloat() < 0.5f) {
                ZombieEntity newZombie = EntityType.ZOMBIE.create(this.getWorld());
                if (newZombie != null) {
                    newZombie.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch());

                    newZombie.equipStack(EquipmentSlot.MAINHAND, new ItemStack(ModItems.THROWING_AXE_ITEM));

                    for (ItemStack stack : newZombie.getItemsEquipped()) {
                        if (!stack.isEmpty()) {
                            stack.setDamage(this.random.nextInt(stack.getMaxDamage()));
                        }
                    }

                    newZombie.setEquipmentDropChance(EquipmentSlot.MAINHAND, 0.0f);

                    this.getWorld().spawnEntity(newZombie);
                }
            }
        }
    }
}
