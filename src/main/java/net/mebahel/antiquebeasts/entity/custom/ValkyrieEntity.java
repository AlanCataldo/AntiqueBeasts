package net.mebahel.antiquebeasts.entity.custom;

import net.mebahel.antiquebeasts.entity.ai.ValkyrieHealingGoal;
import net.mebahel.antiquebeasts.entity.ai.ValkyrieMeleeAttackGoal;
import net.mebahel.antiquebeasts.entity.variant.ValkyrieVariant;
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
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Util;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
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

import static java.lang.Math.random;


public class ValkyrieEntity extends AnimalEntity implements IAnimatable, IAnimationTickable {
    double rand;

    public String animationProcedure = "empty";
    @Override
    public int tickTimer() {
        return age;
    }

    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

    @Override
    public AnimationFactory getFactory() {
        return factory;
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
                event.getController().setAnimation(new AnimationBuilder().addAnimation("attack", ILoopType.EDefaultLoopTypes.PLAY_ONCE));
                return PlayState.CONTINUE;
            }
            return PlayState.CONTINUE;
        }
        return PlayState.CONTINUE;
    }

    private <E extends IAnimatable> PlayState healPredicate(AnimationEvent<E> event) {
        if (this.animationProcedure.equals("empty") && this.isHealing()) {
            if (event.getController().getAnimationState().equals(software.bernie.geckolib3.core.AnimationState.Stopped)) {
                event.getController().markNeedsReload();
                event.getController().setAnimation(new AnimationBuilder().addAnimation("horn", ILoopType.EDefaultLoopTypes.PLAY_ONCE));
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
        AnimationController<ValkyrieEntity> controller = new AnimationController<>(this, "controller", 0,
                this::movementPredicate);
        AnimationController<ValkyrieEntity> controller1 = new AnimationController<>(this, "attacking", 0, this::attackPredicate);
        AnimationController<ValkyrieEntity> controller2 = new AnimationController<>(this, "procedure", 0, this::procedurePredicate);
        AnimationController<ValkyrieEntity> controller3 = new AnimationController<>(this, "heal", 0,
                this::healPredicate);
        controller1.registerSoundListener(this::soundListener);
        controller3.registerSoundListener(this::soundListenerHeal);
        data.addAnimationController(controller);
        data.addAnimationController(controller1);
        data.addAnimationController(controller2);
        data.addAnimationController(controller3);
    }

    private <ENTITY extends IAnimatable> void soundListener(SoundKeyframeEvent<ENTITY> event) {
        if (event.sound.matches("swing1")) {
            if (this.world.isClient) {
                this.getEntityWorld().playSound(this.getX(), this.getY(), this.getZ(), ModSounds.SWING,
                        SoundCategory.HOSTILE, 1F, 1.0F, true);
            }
        }
    }
    private <ENTITY extends IAnimatable> void soundListenerHeal(SoundKeyframeEvent<ENTITY> event) {
        if (event.sound.matches("heal")) {
            if (this.world.isClient) {
                this.getEntityWorld().playSound(this.getX(), this.getY(), this.getZ(), ModSounds.HEAL,
                        SoundCategory.HOSTILE, 1F, 1.0F, true);
            }
        }
    }

    public static final TrackedData<Boolean> SWINGING = DataTracker.registerData(ValkyrieEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);

    public void setSwinging(boolean swinging) {
        this.dataTracker.set(SWINGING, swinging);
    }

    public boolean isSwinging() {
        return this.dataTracker.get(SWINGING);
    }

    public static final TrackedData<Boolean> HEALING = DataTracker.registerData(ValkyrieEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);

    public void setHealing(boolean healing) {
        this.dataTracker.set(HEALING, healing);
    }

    public boolean isHealing() {
        return this.dataTracker.get(HEALING);
    }

    public ValkyrieEntity(EntityType<? extends AnimalEntity> entityType, World world) {
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
        this.dataTracker.startTracking(HEALING, false);
        this.dataTracker.startTracking(DATA_ID_TYPE_VARIANT, 0);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return HostileEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.76f)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 34.0D)
                .add(EntityAttributes.GENERIC_ARMOR, 6f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0f)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.35f)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.5f);
    }
    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new ValkyrieHealingGoal(this, 16f));
        this.goalSelector.add(3, new ValkyrieMeleeAttackGoal(this, 0.45f));
        if (!this.isHealing()) {
            this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.35f, 1f));
            this.goalSelector.add(6, new LookAroundGoal(this));
        }
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, ZombieEntity.class, true));
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
            return ModSounds.VALKYRIE_HURT1;
        else
            return ModSounds.VALKYRIE_HURT2;
    }
    @Override
    protected SoundEvent getDeathSound() {
        rand = random();
        if (rand < 0.5)
            return ModSounds.VALKYRIE_DEATH1;
        else
            return ModSounds.VALKYRIE_DEATH2;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        LivingEntity target = this.getTarget();
        rand = random();
        if (target != null) {
            if (rand < 0.3)
                return ModSounds.VALKYRIE_ATTACK1;
            else if (rand > 0.3 && rand < 0.6)
                return ModSounds.VALKYRIE_ATTACK2;
            else
                return ModSounds.VALKYRIE_ATTACK3;
        } else {
            if (rand < 0.3)
                return ModSounds.VALKYRIE_AMBIENT1;
            else if (rand > 0.3 && rand < 0.6)
                return ModSounds.VALKYRIE_AMBIENT2;
            else
                return ModSounds.VALKYRIE_AMBIENT3;
        }
    }
    @Override
    public void playAmbientSound() {
        SoundEvent soundEvent = this.getAmbientSound();
        if (soundEvent != null) {
            this.playSound(soundEvent, 0.35f, 1f);
        }
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
            DataTracker.registerData(ValkyrieEntity.class, TrackedDataHandlerRegistry.INTEGER);

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty,
                                 SpawnReason spawnReason, @javax.annotation.Nullable EntityData entityData,
                                 @javax.annotation.Nullable NbtCompound entityNbt) {
        ValkyrieVariant variant = Util.getRandom(ValkyrieVariant.values(), this.random);
        setVariant(variant);
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }

    public ValkyrieVariant getVariant() {
        return ValkyrieVariant.byId(this.getTypeVariant() & 255);
    }

    private int getTypeVariant() {
        return this.dataTracker.get(DATA_ID_TYPE_VARIANT);
    }

    private void setVariant(ValkyrieVariant variant) {
        this.dataTracker.set(DATA_ID_TYPE_VARIANT, variant.getId() & 255);
    }
}
