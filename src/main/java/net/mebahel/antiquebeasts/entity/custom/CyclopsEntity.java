package net.mebahel.antiquebeasts.entity.custom;

import net.mebahel.antiquebeasts.entity.ai.CyclopsMeleeAttackGoal;
import net.mebahel.antiquebeasts.entity.ai.CyclopsShootingGoal;
import net.mebahel.antiquebeasts.entity.ai.CyclopsSocializeGoal;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.ClientUtils;

import static java.lang.Math.random;

public class CyclopsEntity extends AnimalEntity implements GeoEntity {
    double rand;
    double last_step = 0;
    public static final TrackedData<Boolean> SHOOTING = DataTracker.registerData(CyclopsEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> SWINGING = DataTracker.registerData(CyclopsEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Float> COOLDOWN = DataTracker.registerData(CyclopsEntity.class,
            TrackedDataHandlerRegistry.FLOAT);
    public static final TrackedData<String> ATTACK_NAME = DataTracker.registerData(CyclopsEntity.class,
            TrackedDataHandlerRegistry.STRING);

    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);

    public CyclopsEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
        this.ambientSoundChance = -this.getMinAmbientSoundDelay();
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
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

    public void setShooting(boolean shooting) {
        this.dataTracker.set(SHOOTING, shooting);
    }

    public void setAttackName(String attackName) {
        this.dataTracker.set(ATTACK_NAME, attackName);
    }

    public String getAttackName() {
        return this.dataTracker.get(ATTACK_NAME);
    }
    private boolean shouldDespawnInPeaceful() {
        return this.getWorld().getDifficulty() == Difficulty.PEACEFUL;
    }
    @Override
    public void tick() {
        super.tick();
        if (shouldDespawnInPeaceful()) {
            remove(Entity.RemovalReason.DISCARDED);
        }
    }
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SHOOTING, false);
        this.dataTracker.startTracking(SWINGING, false);
        this.dataTracker.startTracking(COOLDOWN, 0f);
        this.dataTracker.startTracking(ATTACK_NAME, "animation.cyclops.attack");
    }
    public static DefaultAttributeContainer.Builder setAttributes() {
        return HostileEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 54.0D)
                .add(EntityAttributes.GENERIC_ARMOR, 6f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6f)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.6f)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 1.5f);
    }
    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new CyclopsMeleeAttackGoal(this, 0.42f, ""));
        this.goalSelector.add(3, new CyclopsShootingGoal(this, ""));
        this.goalSelector.add(4, new CyclopsSocializeGoal(this, StatusEffects.STRENGTH));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.35f, 1f));
        this.goalSelector.add(6, new LookAroundGoal(this));

        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, ZombieEntity.class, true));
    }

    private PlayState predicate(AnimationState animationState) {
        if(animationState.isMoving()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("animation.cyclops.walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        animationState.getController().setAnimation(RawAnimation.begin().then("animation.cyclops.idle", Animation.LoopType.LOOP));
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
            state.getController().setAnimation(RawAnimation.begin().then("animation.cyclops.ranged_attack", Animation.LoopType.PLAY_ONCE));
        }

        return PlayState.CONTINUE;
    }
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController(this, "controller", 0, this::predicate));
        controllers.add(new AnimationController(this, "attacking", 0, this::attackPredicate).setSoundKeyframeHandler(state -> {
            PlayerEntity player = ClientUtils.getClientPlayer();
            if (player != null)
                player.playSound(ModSounds.CYCLOPS_HIT1, 1, 1);
        }));
        controllers.add(new AnimationController(this, "shooting", 0, this::shootingPredicate).setSoundKeyframeHandler(state -> {
            PlayerEntity player = ClientUtils.getClientPlayer();
            if (player != null)
                player.playSound(ModSounds.SWING, 1, 0.6f);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        rand = random();
        if (rand < 0.5)
            return ModSounds.CYCLOPS_HURT1;
        return ModSounds.CYCLOPS_HURT2;
    }

    @Override
    protected SoundEvent getDeathSound() {
        rand = random();
        if (rand < 0.5)
            return ModSounds.CYCLOPS_DEATH1;
        return ModSounds.CYCLOPS_DEATH2;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        if (last_step == 0) {
            this.playSound(ModSounds.CYCLOPS_STEP1, 0.75f, 1.0f);
            last_step = this.age;
        }
        if (last_step + 14 <= this.age) {
            this.playSound(ModSounds.CYCLOPS_STEP1, 0.75f, 1.0f);
            last_step = this.age;
        }
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
        if (source.getSource() instanceof ArrowEntity arrow) {
            double heightRatio = (this.getY() - arrow.getY()) / 5.0;
            double isHitInFace = getHitInFace(arrow.getPos(), this.getPos(), this.getRotationVector());

            if (isHitInFace > 0.0 && (heightRatio > 0.8 || heightRatio < -0.8)) {
                amount *= 1.75;
            } else {
                amount *= 0.5;
            }
        }
        return super.damage(source, amount);
    }

    private double getHitInFace(Vec3d arrowPos, Vec3d entityPos, Vec3d entityLook) {
        Vec3d toArrow = arrowPos.subtract(entityPos);
        return toArrow.normalize().dotProduct(entityLook);
    }

    @Override
    public int getMinAmbientSoundDelay() {
        return 180;
    }
    @Override
    protected SoundEvent getAmbientSound() {
        LivingEntity target = this.getTarget();
        rand = random();
        if (target != null) {
            if (rand < 0.5)
                return ModSounds.CYCLOPS_ATTACKING1;
            else
                return ModSounds.CYCLOPS_ATTACKING2;
        } else {
            if (rand < 0.3)
                return ModSounds.CYCLOPS_AMBIENT1;
            else if (rand > 0.3 && rand < 0.6)
                return ModSounds.CYCLOPS_AMBIENT2;
            else
                return ModSounds.CYCLOPS_AMBIENT3;
        }
    }
    @Override
    public void playAmbientSound() {
        SoundEvent soundEvent = this.getAmbientSound();
        if (soundEvent != null) {
            this.playSound(soundEvent, 0.35f, 1f);
        }
    }
}
