package net.mebahel.antiquebeasts.entity.custom.other;

import net.mebahel.antiquebeasts.entity.ai.CustomRevengeGoal;
import net.mebahel.antiquebeasts.entity.ai.InfernalDraugrDodgeFireboltGoal;
import net.mebahel.antiquebeasts.entity.ai.InfernalDraugrMeleeAttackGoal;
import net.mebahel.antiquebeasts.entity.ai.InfernalDraugrSummonAtronachGoal;
import net.mebahel.antiquebeasts.entity.ai.other.DraugrBlockGoal;
import net.mebahel.antiquebeasts.entity.ai.other.DraugrDrinkPotionGoal;
import net.mebahel.antiquebeasts.entity.ai.util.ConditionalWanderAroundFarGoal;
import net.mebahel.antiquebeasts.entity.custom.dwemer.DwemerEntity;
import net.mebahel.antiquebeasts.entity.custom.egyptian.EgyptianEntity;
import net.mebahel.antiquebeasts.entity.custom.greek.GreekEntity;
import net.mebahel.antiquebeasts.entity.custom.norse.NorseEntity;
import net.mebahel.antiquebeasts.entity.variant.InfernalDraugrVariant;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.mebahel.antiquebeasts.util.DamageElementUtil;
import net.mebahel.antiquebeasts.util.PreDamageResult;
import net.mebahel.antiquebeasts.util.config.ModSpawnRateConfig;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
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
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeKeys;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.ClientUtils;

import java.util.Optional;
import java.util.UUID;

public class InfernalDraugrEntity extends DraugrEntity implements GeoEntity {
    public InfernalDraugrEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);
    public AnimatableInstanceCache getAnimatableInstanceCache() { return factory; }

    public static final TrackedData<Integer> COOLDOWN = DataTracker.registerData(InfernalDraugrEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Boolean> SHOOTING = DataTracker.registerData(InfernalDraugrEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Integer> DATA_ID_TYPE_VARIANT = DataTracker.registerData(InfernalDraugrEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Optional<UUID>> SUMMONED_ATRONACH_UUID = DataTracker.registerData(InfernalDraugrEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    private static final TrackedData<Boolean> DODGING = DataTracker.registerData(InfernalDraugrEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public boolean isShooting() { return this.dataTracker.get(SHOOTING); }
    public void setShooting(boolean shooting) { this.dataTracker.set(SHOOTING, shooting); }

    public void setCooldown(int cooldown) { this.dataTracker.set(COOLDOWN, cooldown); }
    public int getCooldown() { return this.dataTracker.get(COOLDOWN); }

    public void setSummonedAtronachUuid(@Nullable UUID id) { this.dataTracker.set(SUMMONED_ATRONACH_UUID, Optional.ofNullable(id)); }
    @Nullable
    public UUID getSummonedAtronachUuid() { return this.dataTracker.get(SUMMONED_ATRONACH_UUID).orElse(null); }

    public boolean isDodging() { return this.dataTracker.get(DODGING); }
    public void setDodging(boolean v) { this.dataTracker.set(DODGING, v); }

    public boolean canTriggerDodge() {
        return this.isAlive()
                && this.getHasSpawned()
                && !this.isDodging()
                && !this.isShooting();
    }

    public int getTypeVariant() { return this.dataTracker.get(DATA_ID_TYPE_VARIANT); }
    private void setVariant(InfernalDraugrVariant variant) { this.dataTracker.set(DATA_ID_TYPE_VARIANT, variant.getId() & 255); }
    public InfernalDraugrVariant getWightVariant() { return InfernalDraugrVariant.byId(this.getTypeVariant() & 255); }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(DATA_ID_TYPE_VARIANT, 0);
        this.dataTracker.startTracking(COOLDOWN, 80);
        this.dataTracker.startTracking(SHOOTING, false);
        this.dataTracker.startTracking(SUMMONED_ATRONACH_UUID, Optional.empty());
        this.dataTracker.startTracking(DODGING, false);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new DraugrDrinkPotionGoal(this));
        this.goalSelector.add(3, new DraugrBlockGoal(this));
        this.goalSelector.add(4, new InfernalDraugrDodgeFireboltGoal(this, 8));
        this.goalSelector.add(5, new InfernalDraugrSummonAtronachGoal(this));
        this.goalSelector.add(6, new InfernalDraugrMeleeAttackGoal(this, 1f, 25, 15));

        this.goalSelector.add(9, new ConditionalWanderAroundFarGoal(this, 0.85f, 1f));
        this.goalSelector.add(10, new LookAroundGoal(this));

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
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 40)
                .add(EntityAttributes.GENERIC_ARMOR, 5)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 7)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.2f)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.5f);
    }

    private <E extends GeoAnimatable> PlayState predicate(AnimationState<E> animationState) {
        if (!this.getHasSpawned()) {
            return PlayState.STOP;
        } else if (animationState.isMoving()) {
            animationState.getController().setAnimation(RawAnimation.begin()
                    .then("transition_walk", Animation.LoopType.PLAY_ONCE)
                    .then("walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        } else if (!animationState.isMoving() && !this.isAttacking()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        return PlayState.CONTINUE;
    }

    private <E extends GeoAnimatable> PlayState spawnPredicate(AnimationState<E> state) {
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

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
        controllers.add(new AnimationController<>(this, "attacking", 0, state -> PlayState.CONTINUE)
                .triggerableAnim("attack", RawAnimation.begin().then("attack", Animation.LoopType.PLAY_ONCE))
                .triggerableAnim("attack2", RawAnimation.begin().then("attack2", Animation.LoopType.PLAY_ONCE))
                .triggerableAnim("attack_combo1", RawAnimation.begin().then("attack_combo1", Animation.LoopType.PLAY_ONCE))
                .triggerableAnim("attack_combo2", RawAnimation.begin().then("attack_combo2", Animation.LoopType.PLAY_ONCE))
                .triggerableAnim("frostbite", RawAnimation.begin().then("frostbite", Animation.LoopType.PLAY_ONCE))
                .triggerableAnim("block_attack", RawAnimation.begin().then("block_attack", Animation.LoopType.PLAY_ONCE))
                .triggerableAnim("summoning", RawAnimation.begin().then("summoning", Animation.LoopType.PLAY_ONCE))
                .triggerableAnim("jumpback_shoot", RawAnimation.begin().then("jumpback_shoot", Animation.LoopType.PLAY_ONCE))
                .triggerableAnim("drink_potion", RawAnimation.begin().then("drink_potion", Animation.LoopType.PLAY_ONCE)));
        controllers.add(new AnimationController<>(this, "spawning", 0, this::spawnPredicate).setSoundKeyframeHandler(state -> {
            PlayerEntity player = ClientUtils.getClientPlayer();
            if (player != null)
                this.getWorld().playSound(player, this.getX(), this.getY(), this.getZ(), ModSounds.MUMMY_SPAWN, this.getSoundCategory(), 0.65f, 1f);
        }));
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty,
                                 SpawnReason spawnReason, @Nullable EntityData entityData,
                                 @Nullable NbtCompound entityNbt) {
        int roll = this.random.nextInt(2);

        InfernalDraugrVariant variant = (roll == 0)
                ? InfernalDraugrVariant.VARIANT_1
                : InfernalDraugrVariant.VARIANT_2;

        setVariant(variant);

        if (this.random.nextFloat() < 5 / 100) {
            this.giveRandomMeleePotion();
        }

        float randomScale = 1.0F + this.random.nextFloat() * 0.15F;
        this.setDraugrScale(randomScale);
        this.calculateDimensions();

        return entityData;
    }

    @Override
    public void tick() {
        super.tick();

        if (shouldDespawnInPeaceful()) {
            remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    protected PreDamageResult preDamage(DamageSource source, float amount,
                                        @Nullable Entity direct, @Nullable Entity attacker) {
        if (this.getWorld().isClient) return PreDamageResult.cancel();
        if (this.isDodging()) return PreDamageResult.cancel();

        boolean isProjectile = direct instanceof ProjectileEntity;
        boolean isMelee = !isProjectile && attacker instanceof LivingEntity;
        final boolean attackerIsCreativePlayer = attacker instanceof PlayerEntity p && p.getAbilities().creativeMode;


        if (source.isOf(DamageTypes.HOT_FLOOR)) {
            return PreDamageResult.cancel();
        }

        if (this.isBlocking() && isMelee) {
            ServerWorld sw = (ServerWorld) this.getWorld();
            sw.spawnParticles(
                    ParticleTypes.CRIT,
                    this.getX(), this.getBodyY(0.5D), this.getZ(),
                    8,
                    0.5D, 0.8D, 0.5D,
                    0.2D
            );

            this.playSound(
                    ModSounds.WEAPON_SWORD_BLOCK,
                    0.75F + this.getRandom().nextFloat() * 0.2F,
                    0.8F + this.getRandom().nextFloat() * 0.4F
            );
            return PreDamageResult.cancel();
        }

        if (!attackerIsCreativePlayer
                && isMelee && this.canTriggerDodge()
                && 10 > 0
                && this.getRandom().nextInt(100) < 10) {

            this.setDodging(true);

            if (attacker instanceof LivingEntity le
                    && (this.getTarget() == null || !this.getTarget().isAlive())) {
                this.setTarget(le);
            }

            return PreDamageResult.cancel();
        }

        DamageElementUtil.ElementResult r = DamageElementUtil.compute(source, direct,
                DamageElementUtil.ElementConfig.defaultPriority(0.5f, 1.25f, 2f));
        float finalAmount = DamageElementUtil.apply(amount, r);

        return new PreDamageResult(false, finalAmount, r);
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
                canMobSpawn(type, world, spawnReason, pos, random) && randomValue < ModSpawnRateConfig.infernalDraugrSpawnRate;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("InfernalCooldown", getCooldown());
        UUID id = getSummonedAtronachUuid();
        if (id != null) nbt.putUuid("SummonedAtronach", id);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("InfernalCooldown")) setCooldown(nbt.getInt("InfernalCooldown"));
        if (nbt.containsUuid("SummonedAtronach")) setSummonedAtronachUuid(nbt.getUuid("SummonedAtronach"));
        else setSummonedAtronachUuid(null);
    }
}
