package net.mebahel.antiquebeasts.entity.custom.other;

import net.mebahel.antiquebeasts.entity.ai.FlameAtronachShootingGoal;
import net.mebahel.antiquebeasts.entity.ai.FollowOwnerGoal;
import net.mebahel.antiquebeasts.entity.ai.OwnerHurtByTargetGoal;
import net.mebahel.antiquebeasts.entity.ai.OwnerHurtTargetGoal;
import net.mebahel.antiquebeasts.entity.ai.util.CappedMoveControl;
import net.mebahel.antiquebeasts.entity.ai.util.ConditionalWanderAroundFarGoal;
import net.mebahel.antiquebeasts.entity.ai.util.CustomRevengeGoal;
import net.mebahel.antiquebeasts.entity.variant.DraugrVariant;
import net.mebahel.antiquebeasts.particle.ModParticles;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.mebahel.antiquebeasts.util.DamageElementUtil;
import net.mebahel.antiquebeasts.util.config.ModSpawnRateConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeKeys;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;

import java.util.Optional;
import java.util.UUID;

public class FlameAtronachEntity extends HostileEntity implements GeoEntity {

    // =========================
    // DataTrackers
    // =========================

    public static final TrackedData<Optional<UUID>> OWNER_UUID =
            DataTracker.registerData(FlameAtronachEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);

    public static final TrackedData<Integer> SUMMON_TICKS =
            DataTracker.registerData(FlameAtronachEntity.class, TrackedDataHandlerRegistry.INTEGER);

    // spawn sequence
    public static final TrackedData<Boolean> HAS_SPAWNED =
            DataTracker.registerData(FlameAtronachEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public static final TrackedData<Integer> SPAWN_TIMER =
            DataTracker.registerData(FlameAtronachEntity.class, TrackedDataHandlerRegistry.INTEGER);

    // existing trackers
    public static final TrackedData<Integer> DATA_ID_TYPE_VARIANT =
            DataTracker.registerData(FlameAtronachEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public static final TrackedData<String> ATTACK_NAME =
            DataTracker.registerData(FlameAtronachEntity.class, TrackedDataHandlerRegistry.STRING);

    public static final TrackedData<Float> SCALE =
            DataTracker.registerData(FlameAtronachEntity.class, TrackedDataHandlerRegistry.FLOAT);

    public static final TrackedData<Boolean> SHOOTING =
            DataTracker.registerData(FlameAtronachEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public static final TrackedData<Integer> COOLDOWN =
            DataTracker.registerData(FlameAtronachEntity.class, TrackedDataHandlerRegistry.INTEGER);

    // =========================
    // Constants
    // =========================

    /** Durée totale de la phase spawn (la tienne est pilotée via age==1 / age==32) */
    private static final int SPAWN_FREEZE_UNTIL_AGE = 32;

    // =========================
    // Fields
    // =========================

    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);

    /** Capture vitesse de base pour freeze/unfreeze sans casser les attributs */
    private double baseMoveSpeed = 0.0D;

    public FlameAtronachEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new CappedMoveControl(this, 0.5);
        this.ambientSoundChance = -this.getMinAmbientSoundDelay();
        this.setPathfindingPenalty(PathNodeType.LAVA, 0.0F);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    // =========================
    // Summon/Owner API
    // =========================

    /** API staff */
    public void setSummonData(@Nullable UUID owner, int lifeTicks) {
        setOwnerUuid(owner);
        setSummonTicks(Math.max(0, lifeTicks));
        this.setPersistent();
    }

    public void setOwnerUuid(@Nullable UUID uuid) {
        this.dataTracker.set(OWNER_UUID, Optional.ofNullable(uuid));
    }

    @Nullable
    public UUID getOwnerUuid() {
        return this.dataTracker.get(OWNER_UUID).orElse(null);
    }

    public void setSummonTicks(int ticks) {
        this.dataTracker.set(SUMMON_TICKS, ticks);
    }

    public int getSummonTicks() {
        return this.dataTracker.get(SUMMON_TICKS);
    }

    public boolean isSummoned() {
        return this.dataTracker.get(SUMMON_TICKS) > 0;
    }


    public boolean isOwner(@Nullable Entity e) {
        UUID owner = getOwnerUuid();
        return owner != null && e != null && owner.equals(e.getUuid());
    }

    @Nullable
    public LivingEntity getOwnerEntity() {
        UUID id = getOwnerUuid();
        if (id == null) return null;
        if (!(this.getWorld() instanceof ServerWorld sw)) return null;

        Entity e = sw.getEntity(id);
        return (e instanceof LivingEntity le) ? le : null;
    }

    // =========================
    // Spawn sequence helpers
    // =========================

    public boolean getHasSpawned() {
        return this.dataTracker.get(HAS_SPAWNED);
    }

    public void setHasSpawned(boolean value) {
        this.dataTracker.set(HAS_SPAWNED, value);
    }

    private int getSpawnTimer() {
        return this.dataTracker.get(SPAWN_TIMER);
    }

    private void ensureBaseSpeedCaptured() {
        if (this.baseMoveSpeed > 0.0D) return;

        var inst = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (inst != null) {
            this.baseMoveSpeed = inst.getBaseValue();
            if (this.baseMoveSpeed <= 0.0D) this.baseMoveSpeed = 0.45D;
        } else {
            this.baseMoveSpeed = 0.45D;
        }
    }

    private void freezeForSpawn() {
        this.setVelocity(0, 0, 0);
        this.velocityDirty = true;
        this.getNavigation().stop();

        var inst = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (inst != null) inst.setBaseValue(0.0D);
    }

    private void unfreezeAfterSpawn() {
        ensureBaseSpeedCaptured();
        var inst = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (inst != null) inst.setBaseValue(this.baseMoveSpeed);
    }

    // =========================
    // Vanilla overrides
    // =========================

    public boolean shouldDespawnInPeaceful() {
        return this.getWorld().getDifficulty() == Difficulty.PEACEFUL;
    }

    @Override
    public boolean canImmediatelyDespawn(double distanceSquared) {
        if (isSummoned()) return false;
        return super.canImmediatelyDespawn(distanceSquared);
    }

    @Override
    public boolean cannotDespawn() {
        if (isSummoned()) return true;
        return super.cannotDespawn();
    }

    @Override
    public boolean tryAttack(Entity target) {
        if (isOwner(target)) return false;
        return super.tryAttack(target);
    }

    @Override
    public boolean canTarget(LivingEntity target) {
        if (isOwner(target)) return false;
        return super.canTarget(target);
    }

    @Override
    public boolean isTeammate(Entity other) {
        if (isOwner(other)) return true;
        return super.isTeammate(other);
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        if (!this.getWorld().isClient) {
            LivingEntity owner = getOwnerEntity();

            if (target == this) {
                super.setTarget(null);
                return;
            }

            // Refuse owner en target (quel que soit son type)
            if (owner != null && target == owner) {
                super.setTarget(null);
                return;
            }
        }

        super.setTarget(target);
    }

    // =========================
    // Trackers API
    // =========================

    public boolean isShooting() { return this.dataTracker.get(SHOOTING); }
    public void setShooting(boolean shooting) { this.dataTracker.set(SHOOTING, shooting); }

    public void setCooldown(int cooldown) { this.dataTracker.set(COOLDOWN, cooldown); }
    public int getCooldown() { return this.dataTracker.get(COOLDOWN); }

    public void setAttackName(String attackName) { this.dataTracker.set(ATTACK_NAME, attackName); }
    public String getAttackName() { return this.dataTracker.get(ATTACK_NAME); }

    public float getScale() { return this.dataTracker.get(SCALE); }
    public void setScale(float scale) { this.dataTracker.set(SCALE, scale); }

    public boolean hasLivingOwner() {
        LivingEntity owner = getOwnerEntity();
        return owner != null && owner.isAlive();
    }

    public boolean isOwnedByPlayer() {
        return getOwnerEntity() instanceof PlayerEntity;
    }

    public boolean isOwnedByNonPlayer() {
        LivingEntity owner = getOwnerEntity();
        return owner != null && !(owner instanceof PlayerEntity);
    }

    public boolean isWild() {
        return !this.isSummoned();
    }

    /** Cible joueurs si sauvage OU owner non-player (Infernal) */
    public boolean shouldTargetPlayers() {
        return isWild() || isOwnedByNonPlayer();
    }

    /** Cible villagers/golems/raiders si sauvage OU owner non-player */
    public boolean shouldTargetOverworldTargets() {
        return isWild() || isOwnedByNonPlayer();
    }


    @Override
    protected void initDataTracker() {
        super.initDataTracker();

        this.dataTracker.startTracking(DATA_ID_TYPE_VARIANT, 0);
        this.dataTracker.startTracking(ATTACK_NAME, "attack1");
        this.dataTracker.startTracking(SCALE, 1.0F);
        this.dataTracker.startTracking(COOLDOWN, 0);
        this.dataTracker.startTracking(SHOOTING, false);

        // summon/pet
        this.dataTracker.startTracking(OWNER_UUID, Optional.empty());
        this.dataTracker.startTracking(SUMMON_TICKS, 0);

        // spawn sequence
        this.dataTracker.startTracking(HAS_SPAWNED, true);
        this.dataTracker.startTracking(SPAWN_TIMER, 0);
    }

    @Override
    protected void initGoals() {
        // Combat
        this.goalSelector.add(2, new FlameAtronachShootingGoal(this,
                8f));

        // Follow owner si invoqué (player OU infernal)
        this.goalSelector.add(3, new ConditionalGoal(
                new FollowOwnerGoal(this, 0.85, 7.0f, 5.0f, 20.0f),
                this::isSummoned
        ));

        // Idle uniquement si sauvage
        this.goalSelector.add(9, new ConditionalGoal(
                new ConditionalWanderAroundFarGoal(this, 0.85f, 1f),
                this::isWild
        ));
        this.goalSelector.add(10, new LookAroundGoal(this));

        // =========================
        // PET REACTIONS (owner = LivingEntity, donc player OU infernal)
        // =========================

        this.targetSelector.add(1, new ConditionalTargetGoal<>(
                new OwnerHurtByTargetGoal(this),
                this::hasLivingOwner
        ));

        this.targetSelector.add(2, new ConditionalTargetGoal<>(
                new OwnerHurtTargetGoal(this),
                this::hasLivingOwner
        ));

        // Revenge (si lui se fait attaquer)
        this.targetSelector.add(3, new CustomRevengeGoal(this, FlameAtronachEntity.class));

        // =========================
        // CIBLES PRINCIPALES
        // =========================

        // Joueurs : sauvage OU owner non-player (infernal)
        this.targetSelector.add(4, new ActiveTargetGoal<>(
                this,
                PlayerEntity.class,
                true,
                player -> this.shouldTargetPlayers() && !this.isOwner(player)
        ));

        // Raiders / Pillagers : sauvage OU owner non-player
        this.targetSelector.add(5, new ActiveTargetGoal<>(
                this,
                RaiderEntity.class,
                true,
                raider -> this.shouldTargetOverworldTargets() && !this.isOwner(raider) && !this.isTeammate(raider)
        ));

        // Villagers : sauvage OU owner non-player
        this.targetSelector.add(6, new ActiveTargetGoal<>(
                this,
                VillagerEntity.class,
                true,
                villager -> this.shouldTargetOverworldTargets() && !this.isOwner(villager) && !this.isTeammate(villager)
        ));

        // Iron golems : sauvage OU owner non-player
        this.targetSelector.add(7, new ActiveTargetGoal<>(
                this,
                IronGolemEntity.class,
                true,
                golem -> this.shouldTargetOverworldTargets() && !this.isOwner(golem) && !this.isTeammate(golem)
        ));

        this.targetSelector.add(10, new ConditionalTargetGoal<>(
                new ActiveTargetGoal<>(
                        this,
                        HostileEntity.class,
                        true,
                        target -> {
                            // Exclusions demandées
                            if (target instanceof CreeperEntity) return false;
                            if (target instanceof ZombifiedPiglinEntity) return false;
                            if (target instanceof EndermanEntity) return false;
                            if (target instanceof EndermiteEntity) return false;

                            // (optionnel) évite de target l'owner au cas où
                            if (this.isOwner(target)) return false;

                            return true;
                        }
                ),
                this::isOwnedByPlayer
        ));
    }

    /** Wrapper simple et propre pour activer/désactiver un Goal selon une condition */
    private static class ConditionalGoal extends Goal {
        private final Goal wrapped;
        private final java.util.function.BooleanSupplier enabled;

        private ConditionalGoal(Goal wrapped, java.util.function.BooleanSupplier enabled) {
            this.wrapped = wrapped;
            this.enabled = enabled;
        }

        @Override public boolean canStart() { return enabled.getAsBoolean() && wrapped.canStart(); }
        @Override public boolean shouldContinue() { return enabled.getAsBoolean() && wrapped.shouldContinue(); }
        @Override public void start() { wrapped.start(); }
        @Override public void stop() { wrapped.stop(); }
        @Override public void tick() { wrapped.tick(); }
        @Override public boolean shouldRunEveryTick() { return wrapped.shouldRunEveryTick(); }
    }

    /** Même principe mais pour les TargetGoals (ça reste un Goal) */
    private static class ConditionalTargetGoal<T extends Goal> extends Goal {
        private final T wrapped;
        private final java.util.function.BooleanSupplier enabled;

        private ConditionalTargetGoal(T wrapped, java.util.function.BooleanSupplier enabled) {
            this.wrapped = wrapped;
            this.enabled = enabled;
        }

        @Override public boolean canStart() { return enabled.getAsBoolean() && wrapped.canStart(); }
        @Override public boolean shouldContinue() { return enabled.getAsBoolean() && wrapped.shouldContinue(); }
        @Override public void start() { wrapped.start(); }
        @Override public void stop() { wrapped.stop(); }
        @Override public void tick() { wrapped.tick(); }
        @Override public boolean shouldRunEveryTick() { return wrapped.shouldRunEveryTick(); }
    }

    // =========================
    // Movement / fluids
    // =========================

    @Override
    public boolean canWalkOnFluid(FluidState state) {
        return state.isIn(FluidTags.WATER) || state.isIn(FluidTags.LAVA);
    }

    @Override
    public boolean isFireImmune() {
        return true;
    }

    private @Nullable BlockPos getWaterBlockUnderFeet() {
        double y = this.getBoundingBox().minY - 0.05D;
        BlockPos under = BlockPos.ofFloored(this.getX(), y, this.getZ());
        FluidState fs = this.getWorld().getFluidState(under);
        return fs.isIn(FluidTags.WATER) ? under : null;
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return HostileEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 35)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.45D)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 35f)
                .add(EntityAttributes.GENERIC_ARMOR, 5)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 0f)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.2f)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.5f);
    }

    // =========================
    // Animations
    // =========================

    private PlayState mainPredicate(AnimationState state) {
        if (!this.getHasSpawned()) {
            return PlayState.STOP;
        } else if (state.isMoving()) {
            state.getController().setAnimation(RawAnimation.begin().then("walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        } else if (!state.isMoving() && !this.isShooting()) {
            state.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        return PlayState.CONTINUE;
    }

    private PlayState spawnPredicate(AnimationState state) {
        if (!this.getHasSpawned()) {
            state.getController().setAnimation(RawAnimation.begin().then("spawn", Animation.LoopType.PLAY_ONCE));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "spawning", 0, this::spawnPredicate));

        controllers.add(new AnimationController<>(this, "main", 6, this::mainPredicate)
                .triggerableAnim("attack1", RawAnimation.begin().then("attack1", Animation.LoopType.PLAY_ONCE))
                .triggerableAnim("attack2", RawAnimation.begin().then("attack2", Animation.LoopType.PLAY_ONCE))
                .triggerableAnim("attackrotate", RawAnimation.begin().then("attackrotate", Animation.LoopType.PLAY_ONCE))
                .triggerableAnim("attackdual", RawAnimation.begin().then("attackdual", Animation.LoopType.PLAY_ONCE))
        );
    }

    // =========================
    // Variant / scale
    // =========================

    public DraugrVariant getVariant() {
        return DraugrVariant.byId(this.getTypeVariant() & 255);
    }

    public int getTypeVariant() {
        return this.dataTracker.get(DATA_ID_TYPE_VARIANT);
    }

    @SuppressWarnings("unused")
    private void setVariant(DraugrVariant variant) {
        this.dataTracker.set(DATA_ID_TYPE_VARIANT, variant.getId() & 255);
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty,
                                 SpawnReason spawnReason, @Nullable EntityData entityData,
                                 @Nullable NbtCompound entityNbt) {

        float randomScale = 1.0F + this.random.nextFloat() * 0.15F;
        this.setScale(randomScale);
        this.calculateDimensions();

        // capture base speed une fois attributs OK
        if (!this.getWorld().isClient) {
            ensureBaseSpeedCaptured();
        }

        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        float scale = this.getScale();
        return super.getDimensions(pose).scaled(scale);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        if (SCALE.equals(data)) {
            this.calculateDimensions();
        }
    }

    // =========================
    // Sounds
    // =========================

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        double rand = Math.random();
        return (rand < 0.5) ? ModSounds.FLAME_ATRONACH_INJURED_1 : ModSounds.FLAME_ATRONACH_INJURED_2;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.FLAME_ATRONACH_DEATH_1;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.FLAME_ATRONACH_IDLE_1;
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

    private static final float FIRE_MULT = 0.25f;
    private static final float FROST_MULT = 1.25f;
    private static final float WATER_MULT = 2f;

    private static final DamageElementUtil.ElementConfig ELEMENT_CFG =
            DamageElementUtil.ElementConfig.defaultPriority(FIRE_MULT, FROST_MULT, WATER_MULT);

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source.isOf(DamageTypes.IN_FIRE)
                || source.isOf(DamageTypes.ON_FIRE)
                || source.isOf(DamageTypes.LAVA)
                || source.isOf(DamageTypes.HOT_FLOOR)) {
            return false;
        }

        Entity direct = source.getSource();

        DamageElementUtil.ElementResult r = DamageElementUtil.compute(source, direct, ELEMENT_CFG);
        float finalAmount = DamageElementUtil.apply(amount, r);

        return super.damage(source, finalAmount);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.shouldDespawnInPeaceful()) {
            this.remove(RemovalReason.DISCARDED);
            return;
        }

        // Summon: si owner mort/absent => despawn
        if (!this.getWorld().isClient && this.isSummoned()) {
            LivingEntity owner = getOwnerEntity();
            if (owner == null || !owner.isAlive()) {
                this.damage(this.getDamageSources().generic(), Float.MAX_VALUE);
                return;
            }
        }

        // convertit eau en cobble + smoke
        if (this.age % 2 == 0) {
            BlockPos waterPos = getWaterBlockUnderFeet();
            if (waterPos != null && !this.getWorld().isClient) {
                this.getWorld().setBlockState(waterPos, Blocks.COBBLESTONE.getDefaultState(), 3);
                ServerWorld sw = (ServerWorld) this.getWorld();

                sw.spawnParticles(
                        ParticleTypes.SMOKE,
                        waterPos.getX() + 0.5,
                        waterPos.getY() + 1.02,
                        waterPos.getZ() + 0.5,
                        6,
                        0.15, 0.05, 0.15,
                        0.0
                );

                this.getWorld().playSound(null, waterPos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS,
                        0.2F, 0.8F + this.getRandom().nextFloat() * 0.4F);
            }
        }

        // FX spawn
        if (!this.dataTracker.get(HAS_SPAWNED) && this.age <= 28) {
            spawnGroundFlameBurst();
        }

        // Spawn sequence (ta logique existante, rendue sûre)
        if (this.age == 1 && !this.dataTracker.get(HAS_SPAWNED)) {
            ensureBaseSpeedCaptured();
            freezeForSpawn();

            this.playSound(ModSounds.FLAME_ATRONACH_SUMMON,
                    0.75F + this.getRandom().nextFloat() * 0.2F,
                    0.8F + this.getRandom().nextFloat() * 0.4F);

        } else if (this.age == SPAWN_FREEZE_UNTIL_AGE && !this.dataTracker.get(HAS_SPAWNED)) {
            this.dataTracker.set(HAS_SPAWNED, true);
            unfreezeAfterSpawn();
        }

        // Lifetime (server)
        if (!this.getWorld().isClient && this.isSummoned()) {
            int t = getSummonTicks();
            if (t <= 0) {
                this.damage(this.getDamageSources().generic(), Float.MAX_VALUE);
                return;
            }
            this.setSummonTicks(t - 1);
        }
    }

    // =========================
    // NBT
    // =========================

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);

        nbt.putInt("Variant", this.getTypeVariant());

        UUID owner = getOwnerUuid();
        if (owner != null) nbt.putUuid("Owner", owner);

        nbt.putInt("SummonTicks", getSummonTicks());

        nbt.putBoolean("HasSpawned", getHasSpawned());
        nbt.putInt("SpawnTimer", getSpawnTimer());

        nbt.putDouble("BaseMoveSpeed", this.baseMoveSpeed);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);

        this.dataTracker.set(DATA_ID_TYPE_VARIANT, nbt.getInt("Variant"));

        if (nbt.containsUuid("Owner")) setOwnerUuid(nbt.getUuid("Owner"));
        else setOwnerUuid(null);

        if (nbt.contains("SummonTicks")) setSummonTicks(nbt.getInt("SummonTicks"));
        else setSummonTicks(0);

        if (nbt.contains("HasSpawned")) this.dataTracker.set(HAS_SPAWNED, nbt.getBoolean("HasSpawned"));
        else this.dataTracker.set(HAS_SPAWNED, true);

        if (nbt.contains("SpawnTimer")) this.dataTracker.set(SPAWN_TIMER, nbt.getInt("SpawnTimer"));
        else this.dataTracker.set(SPAWN_TIMER, 0);

        if (nbt.contains("BaseMoveSpeed")) this.baseMoveSpeed = nbt.getDouble("BaseMoveSpeed");

        if (isSummoned()) {
            this.setPersistent();
        }
    }

    // =========================
    // FX / misc
    // =========================

    private void spawnGroundFlameBurst() {
        if (!(this.getWorld() instanceof ServerWorld sw)) return;

        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();

        int count = 2;
        double radius = 1;

        for (int i = 0; i < count; i++) {
            double ox = (this.random.nextDouble() - 0.5) * radius;
            double oz = (this.random.nextDouble() - 0.5) * radius;

            double px = x + ox;
            double py = y + -0.075;
            double pz = z + oz;

            double dx = ox * 0.06;
            double dz = oz * 0.06;
            double dy = 0.07 + this.random.nextDouble() * 0.04;

            sw.spawnParticles(
                    ModParticles.GROUND_FLAME_PARTICLE,
                    px, py, pz,
                    1,
                    dx, dy, dz,
                    0.0
            );
        }
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(ModSounds.FLAME_ATRONACH_WALK_1,
                0.15F,
                0.9F + this.getRandom().nextFloat() * 0.4F);
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource source) {
        return false;
    }

    @Override
    public boolean hurtByWater() {
        return true;
    }

    public static boolean canMobSpawnWithRate(EntityType<? extends HostileEntity> type, ServerWorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
        if (spawnReason == SpawnReason.SPAWNER || spawnReason == SpawnReason.SPAWN_EGG
                || spawnReason == SpawnReason.COMMAND || spawnReason == SpawnReason.EVENT) {
            return true;
        }

        if (world.getBiome(pos).matchesKey(BiomeKeys.MUSHROOM_FIELDS) ||
                world.getBiome(pos).isIn(BiomeTags.ANCIENT_CITY_HAS_STRUCTURE)) {
            return false;
        }

        int randomValue = random.nextInt(10);

        return world.getDifficulty() != Difficulty.PEACEFUL && isSpawnDark(world, pos, random) &&
                canMobSpawn(type, world, spawnReason, pos, random) && randomValue < ModSpawnRateConfig.flameAtronachSpawnRate;
    }

    @Override
    protected void dropLoot(DamageSource source, boolean causedByPlayer) {
        if (this.isSummoned()) return; // pas de loot
        super.dropLoot(source, causedByPlayer);
    }

    @Override
    protected void dropEquipment(DamageSource source, int lootingMultiplier, boolean allowDrops) {
        if (this.isSummoned()) return; // pas d’équipement non plus
        super.dropEquipment(source, lootingMultiplier, allowDrops);
    }

    @Override
    protected void dropXp() {
        if (this.isSummoned()) return; // pas d'xp
        super.dropXp();
    }
}
