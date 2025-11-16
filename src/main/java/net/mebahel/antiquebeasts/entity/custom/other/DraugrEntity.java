package net.mebahel.antiquebeasts.entity.custom.other;

import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags;
import net.mebahel.antiquebeasts.entity.ai.CustomRevengeGoal;
import net.mebahel.antiquebeasts.entity.ai.other.DraugrMeleeAttackGoal;
import net.mebahel.antiquebeasts.entity.custom.dwemer.DwemerEntity;
import net.mebahel.antiquebeasts.entity.custom.egyptian.EgyptianEntity;
import net.mebahel.antiquebeasts.entity.custom.greek.GreekEntity;
import net.mebahel.antiquebeasts.entity.custom.norse.NorseEntity;
import net.mebahel.antiquebeasts.entity.variant.DraugrVariant;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.mebahel.antiquebeasts.util.config.ModBonusHealthConfig;
import net.mebahel.antiquebeasts.util.config.ModSpawnRateConfig;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.ClientUtils;

import java.util.Objects;

import static java.lang.Math.random;

public class DraugrEntity extends HostileEntity implements GeoEntity {
    public DraugrEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.ambientSoundChance = -this.getMinAmbientSoundDelay();
        this.speed = Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).getValue();
    }

    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }
    double rand;
    public boolean shouldDespawnInPeaceful() {
        return this.getWorld().getDifficulty() == Difficulty.PEACEFUL;
    }

    public static final TrackedData<Integer> DATA_ID_TYPE_VARIANT =
            DataTracker.registerData(DraugrEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public static final TrackedData<Boolean> SWINGING = DataTracker.registerData(DraugrEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> IS_PART_OF_RAID = DataTracker.registerData(DraugrEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);

    public static final TrackedData<String> ATTACK_NAME = DataTracker.registerData(DraugrEntity.class,
            TrackedDataHandlerRegistry.STRING);
    public static final TrackedData<Boolean> HAS_SPAWNED = DataTracker.registerData(DraugrEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);
    public boolean getHasSpawned() {return this.dataTracker.get(HAS_SPAWNED);}
    public void setHasSpawned(boolean bool) {
        this.dataTracker.set(HAS_SPAWNED, bool);
    }

    public boolean isPartOfRaid() {
        return this.dataTracker.get(IS_PART_OF_RAID);
    }
    public void setPartOfRaid(Boolean isPartOfRaid) {
        this.dataTracker.set(IS_PART_OF_RAID, isPartOfRaid);
    }
    public void setSwinging(boolean swinging) { this.dataTracker.set(SWINGING, swinging); }
    public boolean isSwinging() { return this.dataTracker.get(SWINGING); }
    public void setAttackName(String attackName) { this.dataTracker.set(ATTACK_NAME, attackName); }
    public String getAttackName() { return this.dataTracker.get(ATTACK_NAME); }

    public double speed;

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SWINGING, false);
        this.dataTracker.startTracking(DATA_ID_TYPE_VARIANT, 0);
        this.dataTracker.startTracking(IS_PART_OF_RAID, false);
        this.dataTracker.startTracking(ATTACK_NAME, "attack");
        this.dataTracker.startTracking(HAS_SPAWNED, true);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new DraugrMeleeAttackGoal(this, 1f,
                25, 15, 10));
        this.goalSelector.add(6, new WanderAroundFarGoal(this, 0.85f, 1f));
        this.goalSelector.add(7, new LookAroundGoal(this));

        this.targetSelector.add(1, new CustomRevengeGoal(this, DraugrEntity.class));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, VillagerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, IronGolemEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, RaiderEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, DwemerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, GreekEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, EgyptianEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, NorseEntity.class, true));
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return HostileEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 35)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3D)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0D + ModBonusHealthConfig.draugrBonusHealth)
                .add(EntityAttributes.GENERIC_ARMOR, 6f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0f)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.2f)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.5f);
    }
    private PlayState predicate(AnimationState animationState) {
        if (!this.getHasSpawned()) {
            return PlayState.STOP;
        } else if (animationState.isMoving()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("transition_walk", Animation.LoopType.PLAY_ONCE).then("walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        } else if (!animationState.isMoving() && !this.isAttacking()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        return PlayState.CONTINUE;
    }

    private PlayState attackPredicate(AnimationState state) {
        if (this.isSwinging() && state.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
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
                this.getWorld().playSound(player, this.getX(), this.getY(), this.getZ(), ModSounds.SWING, this.getSoundCategory(), 0.7f, 1.1f);
        }));
        controllers.add(new AnimationController(this, "spawning", 0, this::spawnPredicate).setSoundKeyframeHandler(state -> {
            PlayerEntity player = ClientUtils.getClientPlayer();
            if (player != null)
                this.getWorld().playSound(player, this.getX(), this.getY(), this.getZ(), ModSounds.MUMMY_SPAWN, this.getSoundCategory(), 0.65f, 1f);
        }));
    }

    public DraugrVariant getVariant() {
        return DraugrVariant.byId(this.getTypeVariant() & 255);
    }

    public int getTypeVariant() {
        return this.dataTracker.get(DATA_ID_TYPE_VARIANT);
    }

    private void setVariant(DraugrVariant variant) {
        this.dataTracker.set(DATA_ID_TYPE_VARIANT, variant.getId() & 255);
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty,
                                 SpawnReason spawnReason, @Nullable EntityData entityData,
                                 @Nullable NbtCompound entityNbt) {
        var biome = world.getBiome(this.getBlockPos());

        DraugrVariant variant;
        boolean useAxeVariant = random.nextBoolean();

        if (biome.isIn(ConventionalBiomeTags.DESERT) || biome.isIn(ConventionalBiomeTags.BADLANDS)) {
            variant = useAxeVariant ? DraugrVariant.HOT_AXE : DraugrVariant.HOT;
        } else if (biome.isIn(ConventionalBiomeTags.CLIMATE_COLD) || biome.isIn(ConventionalBiomeTags.SNOWY) ||
                biome.isIn(ConventionalBiomeTags.ICY) || biome.isIn(ConventionalBiomeTags.AQUATIC_ICY)
                || biome.isIn(ConventionalBiomeTags.TAIGA)) {
            variant = useAxeVariant ? DraugrVariant.COLD_AXE : DraugrVariant.COLD;
        } else {
            variant = useAxeVariant ? DraugrVariant.TEMPERATE_AXE : DraugrVariant.TEMPERATE;
        }

        setVariant(variant);
        this.setTarget(null);

        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        double rand = random();

        if (rand < 0.25)
            return ModSounds.DRAUGR_HURT_1;
        else if (rand < 0.5)
            return ModSounds.DRAUGR_HURT_2;
        else if (rand < 0.75)
            return ModSounds.DRAUGR_HURT_3;
        else
            return ModSounds.DRAUGR_HURT_4;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.DRAUGR_DEATH_1;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        double rand = random();

        if (rand < 0.2) {
            return ModSounds.DRAUGR_AMBIENT_1;
        } else if (rand < 0.4) {
            return ModSounds.DRAUGR_AMBIENT_2;
        } else if (rand < 0.6) {
            return ModSounds.DRAUGR_AMBIENT_3;
        } else if (rand < 0.8) {
            return ModSounds.DRAUGR_AMBIENT_4;
        } else {
            return ModSounds.DRAUGR_AMBIENT_5;
        }
    }

    @Override
    public void playAmbientSound() {
        SoundEvent soundEvent = this.getAmbientSound();
        if (soundEvent != null) {
            this.playSound(soundEvent, 0.8f, 1f);
        }
    }

    public int getMinAmbientSoundDelay() {
        return 160 + this.getRandom().nextInt(60);
    }

    public boolean damage(DamageSource source, float amount) {
        if (source.isOf(DamageTypes.IN_FIRE) || source.isOf(DamageTypes.ON_FIRE)) {
            return super.damage(source, amount * 2);
        } else if (source.isOf(DamageTypes.FREEZE)) {
            return false;
        }
        return super.damage(source, amount);
    }
    @Override
    public void tick() {
        super.tick();

        if (this.shouldDespawnInPeaceful()) {
            this.remove(RemovalReason.DISCARDED);
        }

        if (this.isPartOfRaid() && this.age >= 1000 && !this.isGlowing()) {
            this.setGlowing(true);
        }

        if (this instanceof DraugrOverlordEntity) {
            if (this.age < 30 && this.isPartOfRaid() && !this.getHasSpawned()) {
                Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(0);
            }
            else if (Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).getValue() == 0 &&
                    !((DraugrOverlordEntity) this).getSpecial() && this.isPartOfRaid()) {
                Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(this.speed);
            }
        } else {
            if (this.age < 30 && this.isPartOfRaid() && !this.getHasSpawned()) {
                Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(0);
            }
            else if (Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).getValue() == 0
                    && this.isPartOfRaid()) {
                Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(this.speed);
            }
        }
    }
    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("Variant", this.getTypeVariant());
        nbt.putBoolean("PartOfRaid", this.isPartOfRaid());
    }
    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.dataTracker.set(DATA_ID_TYPE_VARIANT, nbt.getInt("Variant"));
        this.dataTracker.set(IS_PART_OF_RAID, nbt.getBoolean("PartOfRaid"));
        this.dataTracker.set(HAS_SPAWNED, true);
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);
    }
    private PlayState spawnPredicate(AnimationState state) {
        if (!this.getHasSpawned()) {
            state.getController().setAnimation(RawAnimation.begin().then("spawn", Animation.LoopType.PLAY_ONCE));
            if (state.getController().getAnimationState() != AnimationController.State.STOPPED) {
                spawnHoveringParticles();
            } else {
                this.setHasSpawned(true);
            }
        }
        return PlayState.CONTINUE;
    }
    public static boolean canMobSpawnWithRate(EntityType<? extends HostileEntity> type, ServerWorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random,
                                              Boolean draugrCanSpawnInDark) {
        if (spawnReason == SpawnReason.SPAWNER || spawnReason == SpawnReason.SPAWN_EGG
                || spawnReason == SpawnReason.COMMAND || spawnReason == SpawnReason.EVENT) {
            return true;
        }
        if (draugrCanSpawnInDark) {
            BlockPos blockPos = pos.down();
            if (!world.getBlockState(blockPos).allowsSpawning(world, blockPos, type)) {
                return false;
            }


            int randomValue = random.nextInt(10);
            return randomValue < ModSpawnRateConfig.draugrSpawnRate;
        }
        return false;
    }

    void spawnHoveringParticles() {
        double posX = this.getX();
        double posY = this.getY() - 0.1;
        double posZ = this.getZ();

        BlockPos blockPos = new BlockPos((int) posX, (int) (this.getY() - 0.5), (int) posZ);
        BlockState blockState = this.getWorld().getBlockState(blockPos);

        if (!blockState.isAir()) {
            for (int i = 0; i < 3; i++) { // Nombre de particules
                double offsetX = (this.random.nextDouble() - 0.5) * 0.1;
                double offsetZ = (this.random.nextDouble() - 0.5) * 0.1;
                double velocityY = 0.1;

                this.getWorld().addParticle(
                        new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState),
                        posX + offsetX, posY, posZ + offsetZ,
                        0.0, velocityY, 0.0
                );
            }
        }
    }

    @Override
    public EntityGroup getGroup() {
        return EntityGroup.UNDEAD;
    }
}
