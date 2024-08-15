package net.mebahel.antiquebeasts.entity.custom.egyptian;

import net.mebahel.antiquebeasts.entity.ai.*;
import net.mebahel.antiquebeasts.entity.custom.greek.GreekEntity;
import net.mebahel.antiquebeasts.entity.custom.norse.NorseEntity;
import net.mebahel.antiquebeasts.entity.custom.patrol.ModPatrolEntity;
import net.mebahel.antiquebeasts.entity.variant.EgyptiantVariant;
import net.mebahel.antiquebeasts.sound.ModSounds;
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
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
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

import java.util.List;

import static java.lang.Math.random;


public class ElephantRiderEntity extends EgyptianEntity implements IAnimatable, IAnimationTickable {
    public String animationProcedure = "empty";
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    @Override
    public int tickTimer() {
        return age;
    }

    public static final TrackedData<Integer> COOLDOWN = DataTracker.registerData(ElephantRiderEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public static final TrackedData<Boolean> SHOOTING = DataTracker.registerData(ElephantRiderEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
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

    public ElephantRiderEntity(EntityType<? extends ModPatrolEntity> entityType, World world) {
        super(entityType, world);
        this.ambientSoundChance = -this.getMinAmbientSoundDelay();
    }
    public ElephantRiderEntity(EntityType<? extends ModPatrolEntity> entityType, World world, boolean isInCaravan, LivingEntity leadEntity) {
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
    public static ElephantRiderEntity create(EntityType<? extends ModPatrolEntity> entityType, World world) {
        return new ElephantRiderEntity(entityType, world);
    }
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SWINGING, false);
        this.dataTracker.startTracking(DATA_ID_TYPE_VARIANT, 0);
        this.dataTracker.startTracking(ATTACK_NAME, "attack");
        this.dataTracker.startTracking(SHOOTING, false);
        this.dataTracker.startTracking(COOLDOWN, 0);
        this.dataTracker.startTracking(IS_IN_CARAVAN, false);
        this.dataTracker.startTracking(PATROL_UUID, "");
    }
    public static DefaultAttributeContainer.Builder setAttributes() {
        return HostileEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.78f)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 48.0D)
                .add(EntityAttributes.GENERIC_ARMOR, 6f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 7.0f)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.7f)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 3f);
    }
    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(3, new ElephantRiderShootingGoal(this, 0.51f));
        this.goalSelector.add(4, new BigEgyptianMeleeAttackGoal(this, 0.45f, 12f, 3, 6));
        this.goalSelector.add(5, new FollowEntityGoal(this, 0.35f));
        this.goalSelector.add(6, new WanderAroundFarGoal(this, 0.35f, 1f));
        this.goalSelector.add(7, new LookAroundGoal(this));

        this.targetSelector.add(1, new CustomRevengeGoal(this, EgyptianEntity.class));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, ZombieEntity.class, true));
        this.targetSelector.add(4, new ActiveTargetGoal<>(this, GreekEntity.class, true));
        this.targetSelector.add(4, new ActiveTargetGoal<>(this, NorseEntity.class, true));
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
        AnimationController<ElephantRiderEntity> controller = new AnimationController<>(this, "controller", 0,
                this::movementPredicate);
        AnimationController<ElephantRiderEntity> controller1 = new AnimationController<>(this, "attacking", 0, this::attackPredicate);
        AnimationController<ElephantRiderEntity> controller2 = new AnimationController<>(this, "procedure", 0, this::procedurePredicate);
        AnimationController<ElephantRiderEntity> controller3 = new AnimationController<>(this, "shooting", 0, this::shootingPredicate);
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
        }
    }
    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        rand = random();
        if (rand < 0.3)
            return ModSounds.ELEPHANT_HURT_1;
        else if (rand > 0.3 && rand < 0.6)
            return ModSounds.ELEPHANT_HURT_2;
        else
            return ModSounds.HOPLITE_HURT1;
    }
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.ELEPHANT_DEATH_1;
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
                return ModSounds.ELEPHANT_AMBIENT_1;
            else if (rand > 0.3 && rand < 0.6)
                return ModSounds.EGYPTIAN_AMBIENT_2;
            else
                return ModSounds.EGYPTIAN_AMBIENT_3;
        }
    }
    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty,
                                 SpawnReason spawnReason, @javax.annotation.Nullable EntityData entityData,
                                 @javax.annotation.Nullable NbtCompound entityNbt) {
        EgyptiantVariant variant = Util.getRandom(EgyptiantVariant.values(), this.random);
        setVariant(variant);
        if (spawnReason != SpawnReason.SPAWN_EGG && spawnReason != SpawnReason.COMMAND && spawnReason != SpawnReason.SPAWNER) {
            int randomValue = this.random.nextInt(11);
            if (randomValue >= 0 && randomValue <= 7) {
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
