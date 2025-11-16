package net.mebahel.antiquebeasts.entity.custom.greek;

import net.mebahel.antiquebeasts.entity.ai.CentaurLookAtTargetGoal;
import net.mebahel.antiquebeasts.entity.ai.CentaurMeleeAttackGoal;
import net.mebahel.antiquebeasts.entity.ai.CentaurShootingGoal;
import net.mebahel.antiquebeasts.entity.ai.util.ModPatrolGoal;
import net.mebahel.antiquebeasts.entity.custom.dwemer.DwemerEntity;
import net.mebahel.antiquebeasts.entity.custom.egyptian.EgyptianEntity;
import net.mebahel.antiquebeasts.entity.custom.norse.NorseEntity;
import net.mebahel.antiquebeasts.entity.custom.other.DraugrEntity;
import net.mebahel.antiquebeasts.entity.custom.patrol.ModPatrolEntity;
import net.mebahel.antiquebeasts.entity.variant.CentaurVariant;
import net.mebahel.antiquebeasts.item.custom.ModItems;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.mebahel.antiquebeasts.util.config.ModBonusHealthConfig;
import net.mebahel.antiquebeasts.util.config.ModSpawnRateConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.ClientUtils;

import java.util.Random;
import org.jetbrains.annotations.Nullable;

import static java.lang.Math.random;

public class CentaurEntity extends GreekEntity implements GeoEntity {
    private int blinkTimer = 0;
    private int nextBlink = 0;
    double rand;
    public static final TrackedData<Boolean> IS_ARCHER = DataTracker.registerData(CentaurEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);

    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);

    public CentaurEntity(EntityType<? extends ModPatrolEntity> entityType, World world) {
        super(entityType, world);
        this.ambientSoundChance = -this.getMinAmbientSoundDelay();
        this.scheduleNextBlink();
    }

    public float getCooldown() { return this.dataTracker.get(COOLDOWN);}

    public void setCooldown(float cooldown) {
        this.dataTracker.set(COOLDOWN, cooldown);
    }

    public boolean isArcher() {
        return this.dataTracker.get(IS_ARCHER);
    }

    public void setIsArcher(boolean swinging) {
        this.dataTracker.set(IS_ARCHER, swinging);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.shouldDespawnInPeaceful()) {
            this.remove(RemovalReason.DISCARDED);
        }
        if (this.blinkTimer > 0) {
            this.blinkTimer--;
        } else if (this.nextBlink > 0) {
            this.nextBlink--;
        } else {
            this.blinkTimer = 5;
            this.scheduleNextBlink();
        }
        if ((this.getVariant() == CentaurVariant.ARCHER
                || this.getVariant() == CentaurVariant.ARCHER_2) && !this.isArcher()) {
            this.setIsArcher(true);
        }
    }

    private void scheduleNextBlink() {
        this.nextBlink = 80 + this.random.nextInt(160 - 80 + 1);
    }

    public boolean isBlinking() {
        return this.blinkTimer > 0;
    }
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SHOOTING, false);
        this.dataTracker.startTracking(SWINGING, false);
        this.dataTracker.startTracking(IS_ARCHER, false);
        this.dataTracker.startTracking(COOLDOWN, 0f);
        this.dataTracker.startTracking(DATA_ID_TYPE_VARIANT, 0);
        this.dataTracker.startTracking(ATTACK_NAME, "attack");
        this.dataTracker.startTracking(PATROL_UUID, "");
    }
    public static DefaultAttributeContainer.Builder setAttributes() {
        return HostileEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 34.0D + ModBonusHealthConfig.centaurBonusHealth)
                .add(EntityAttributes.GENERIC_ARMOR, 4f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6f)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.6f)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.5f);
    }
    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new ModPatrolGoal(this, 0.37f, 0.4f));
        this.goalSelector.add(3, new CentaurShootingGoal(this, 80F));
        this.goalSelector.add(4, new CentaurLookAtTargetGoal(this));
        this.goalSelector.add(5, new CentaurMeleeAttackGoal(this, 0.52f, 8f, 1, 7));
        this.goalSelector.add(6, new WanderAroundFarGoal(this, 0.35f, 1f));
        this.goalSelector.add(7, new LookAroundGoal(this));

        this.targetSelector.add(1, (new RevengeGoal(this, GreekEntity.class))
                .setGroupRevenge(GreekEntity.class));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, ZombieEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, DraugrEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, DwemerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, VillagerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, IronGolemEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, RaiderEntity.class, true));
        this.targetSelector.add(5, new ActiveTargetGoal<>(this, EgyptianEntity.class, true));
        this.targetSelector.add(5, new ActiveTargetGoal<>(this, NorseEntity.class, true));
    }

    private PlayState predicate(AnimationState animationState) {
        if (animationState.isMoving()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        animationState.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
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
            state.getController().setAnimation(RawAnimation.begin().then("ranged_attack", Animation.LoopType.PLAY_ONCE));
        }

        return PlayState.CONTINUE;
    }
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController(this, "controller", 0, this::predicate));
        controllers.add(new AnimationController(this, "attacking", 0, this::attackPredicate).setSoundKeyframeHandler(state -> {
            PlayerEntity player = ClientUtils.getClientPlayer();
            if (player != null)
                this.getWorld().playSound(player, this.getX(), this.getY(), this.getZ(), ModSounds.SWING, this.getSoundCategory(), 0.5f, 0.6f);
        }));
        controllers.add(new AnimationController(this, "shooting", 0, this::shootingPredicate).setSoundKeyframeHandler(state -> {
            PlayerEntity player = ClientUtils.getClientPlayer();
            if (player != null)
                this.getWorld().playSound(player, this.getX(), this.getY(), this.getZ(), ModSounds.SWING, this.getSoundCategory(), 0.5f, 0.6f);
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
            return ModSounds.HOPLITE_HURT1;
        return ModSounds.HOPLITE_HURT2;
    }

    @Override
    protected SoundEvent getDeathSound() {
        rand = random();
        if (rand < 0.5)
            return ModSounds.HOPLITE_DEATH1;
        return ModSounds.HOPLITE_DEATH2;
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
            this.playSound(soundEvent, 0.35f, 0.92f);
        }
    }
    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty,
                                 SpawnReason spawnReason, @Nullable EntityData entityData,
                                 @Nullable NbtCompound entityNbt) {
        CentaurVariant variant = Util.getRandom(CentaurVariant.values(), this.random);
        setVariant(variant);
        this.setTarget(null);
        return entityData;
    }

    public CentaurVariant getVariant() {
        return CentaurVariant.byId(this.getTypeVariant() & 255);
    }

    private void setVariant(CentaurVariant variant) {
        this.dataTracker.set(DATA_ID_TYPE_VARIANT, variant.getId() & 255);
    }
    public void performJump(Vec3d direction) {
        this.setVelocity(direction);
        this.velocityDirty = true;
    }

    protected void dropInventory() {
        super.dropInventory();
        if (!this.getWorld().isClient) {
            Random random = new Random();
            if (random.nextInt(30) == 0 && this.isArcher()) {
                this.dropItem(ModItems.GREEK_COMPOSITE_BOW);
            } else if (random.nextInt(30) == 0 && !this.isArcher()){
                this.dropItem(ModItems.IRON_CENTAUR_SWORD);
            }
        }
    }

    public static boolean canMobSpawnWithRate(EntityType<? extends AnimalEntity> type, ServerWorldAccess world, SpawnReason spawnReason, BlockPos pos, net.minecraft.util.math.random.Random random) {
        if (spawnReason == SpawnReason.SPAWNER || spawnReason == SpawnReason.SPAWN_EGG
                || spawnReason == SpawnReason.COMMAND || spawnReason == SpawnReason.EVENT) {
            return true;
        }

        long time = world.getLevelProperties().getTimeOfDay();
        if (time % 24000L >= 13000L) {
            return false;
        }

        BlockPos blockPos = pos.down();
        BlockState blockBelow = world.getBlockState(blockPos);

        boolean isGrassyGround = blockBelow.isOf(Blocks.GRASS);

        if (!isGrassyGround) {
            return false;
        }

        if (world.getLightLevel(pos) < 9) {
            return false;
        }

        if (!world.isSkyVisible(pos)) {
            return false;
        }

        int randomValue = random.nextInt(10);
        return randomValue < ModSpawnRateConfig.centaurSpawnRate;
    }
}
