package net.mebahel.antiquebeasts.entity.custom.egyptian;

import net.mebahel.antiquebeasts.entity.ai.BigEgyptianMeleeAttackGoal;
import net.mebahel.antiquebeasts.entity.ai.DefendLeadEntityGoal;
import net.mebahel.antiquebeasts.entity.ai.FollowEntityGoal;
import net.mebahel.antiquebeasts.entity.ai.util.GroupRevengeGoal;
import net.mebahel.antiquebeasts.entity.ai.util.ModPatrolGoal;
import net.mebahel.antiquebeasts.entity.custom.dwemer.DwemerEntity;
import net.mebahel.antiquebeasts.entity.custom.greek.GreekEntity;
import net.mebahel.antiquebeasts.entity.custom.norse.NorseEntity;
import net.mebahel.antiquebeasts.entity.custom.other.DraugrEntity;
import net.mebahel.antiquebeasts.entity.custom.patrol.ModPatrolEntity;
import net.mebahel.antiquebeasts.entity.variant.EgyptiantVariant;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.mebahel.antiquebeasts.util.config.ModBonusHealthConfig;
import net.minecraft.block.BlockState;
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
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
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

import java.util.List;

import static java.lang.Math.random;
import org.jetbrains.annotations.Nullable;

public class CamelryEntity extends EgyptianEntity implements GeoEntity {
    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    public CamelryEntity(EntityType<? extends ModPatrolEntity> entityType, World world) {
        super(entityType, world);
        this.ambientSoundChance = -this.getMinAmbientSoundDelay();
    }

    public CamelryEntity(EntityType<? extends ModPatrolEntity> entityType, World world, boolean isInCaravan, LivingEntity leadEntity) {
        super(entityType, world);
        this.ambientSoundChance = -this.getMinAmbientSoundDelay();
        this.setInCaravan(isInCaravan);
        List<EgyptianCaravanEntity> caravanEntities = this.getWorld().getEntitiesByClass(EgyptianCaravanEntity.class,
                this.getBoundingBox().expand(48.0D), e -> true);
        LivingEntity closest = null;
        double closestDistance = Double.MAX_VALUE;

        for (EgyptianCaravanEntity camelry : caravanEntities) {
            double distance = this.squaredDistanceTo(camelry);
            if (distance < closestDistance) {
                closestDistance = distance;
                closest = camelry;
            }
        }
        this.setLeadEntity(closest);
    }

    public static CamelryEntity create(EntityType<? extends ModPatrolEntity> entityType, World world) {
        return new CamelryEntity(entityType, world);
    }
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SWINGING, false);
        this.dataTracker.startTracking(DATA_ID_TYPE_VARIANT, 0);
        this.dataTracker.startTracking(IS_IN_CARAVAN, false);
        this.dataTracker.startTracking(ATTACK_NAME, "attack");
        this.dataTracker.startTracking(PATROL_UUID, "");
    }
    public static DefaultAttributeContainer.Builder setAttributes() {
        return HostileEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 35)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.78f)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 36.0D + ModBonusHealthConfig.camleryBonusHealth)
                .add(EntityAttributes.GENERIC_ARMOR, 5f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0f)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.3f)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.5f);
    }
    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new ModPatrolGoal(this, 0.37f, 0.4f));
        this.goalSelector.add(3, new DefendLeadEntityGoal(this));
        this.goalSelector.add(4, new BigEgyptianMeleeAttackGoal(this, 0.52f, 12f, 3, 10));
        this.goalSelector.add(5, new FollowEntityGoal(this, 0.35f));
        this.goalSelector.add(6, new WanderAroundFarGoal(this, 0.35f, 100f));
        this.goalSelector.add(7, new LookAroundGoal(this));

        this.targetSelector.add(1, new GroupRevengeGoal(this, EgyptianEntity.class).setGroupRevenge());
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, ZombieEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, DwemerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, DraugrEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, VillagerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, IronGolemEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, RaiderEntity.class, true));
        this.targetSelector.add(5, new ActiveTargetGoal<>(this, GreekEntity.class, true));
        this.targetSelector.add(5, new ActiveTargetGoal<>(this, NorseEntity.class, true));
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
        if(this.isSwinging() && state.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
            state.getController().forceAnimationReset();
            state.getController().setAnimation(RawAnimation.begin().then("attack", Animation.LoopType.PLAY_ONCE));
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
    protected SoundEvent getHurtSound(DamageSource source) {
        rand = random();
        if (rand < 0.5)
            return ModSounds.CAMELRY_HURT_1;
        else
            return ModSounds.CAMELRY_HURT_2;
    }
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.CAMELRY_DEATH_1;
    }
    @Override
    protected SoundEvent getAmbientSound() {
        LivingEntity target = this.getTarget();
        rand = random();
        if (target != null) {
            if (rand < 0.3)
                return ModSounds.EGYPTIAN_ATTACK_1;
            else if (rand > 0.3 && rand < 0.6)
                return ModSounds.EGYPTIAN_ATTACK_2;
            else
                return ModSounds.EGYPTIAN_ATTACK_3;
        } else {
            if (rand < 0.3)
                return ModSounds.EGYPTIAN_AMBIENT_1;
            else if (rand > 0.3 && rand < 0.6)
                return ModSounds.EGYPTIAN_AMBIENT_2;
            else
                return ModSounds.EGYPTIAN_AMBIENT_3;
        }
    }

    /* VARIANTS */

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty,
                                 SpawnReason spawnReason, @Nullable EntityData entityData,
                                 @Nullable NbtCompound entityNbt) {
        EgyptiantVariant variant = Util.getRandom(EgyptiantVariant.values(), this.random);
        setVariant(variant);
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    public EgyptiantVariant getVariant() {
        return EgyptiantVariant.byId(this.getTypeVariant() & 255);
    }
    public void setVariant(EgyptiantVariant variant) {
        this.dataTracker.set(DATA_ID_TYPE_VARIANT, variant.getId() & 255);
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
        if (state.getSoundGroup() == BlockSoundGroup.SAND) {
            this.playSound(SoundEvents.ENTITY_CAMEL_STEP_SAND, 1.0F, 1.0F);
        } else {
            this.playSound(SoundEvents.ENTITY_CAMEL_STEP, 1.0F, 1.0F);
        }
    }
}
