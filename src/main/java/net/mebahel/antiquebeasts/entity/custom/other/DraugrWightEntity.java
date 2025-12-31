package net.mebahel.antiquebeasts.entity.custom.other;

import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags;
import net.mebahel.antiquebeasts.config.draugr.DraugrBonusHealthConfig;
import net.mebahel.antiquebeasts.config.draugr.DraugrCombatBalancingConfig;
import net.mebahel.antiquebeasts.config.draugr.DraugrSpawnRateConfig;
import net.mebahel.antiquebeasts.entity.ai.CustomRevengeGoal;
import net.mebahel.antiquebeasts.entity.ai.other.DraugrBlockGoal;
import net.mebahel.antiquebeasts.entity.ai.other.DraugrDrinkPotionGoal;
import net.mebahel.antiquebeasts.entity.ai.other.DraugrSpellGoal;
import net.mebahel.antiquebeasts.entity.ai.other.DraugrWightMeleeAttackGoal;
import net.mebahel.antiquebeasts.entity.custom.dwemer.DwemerEntity;
import net.mebahel.antiquebeasts.entity.custom.egyptian.EgyptianEntity;
import net.mebahel.antiquebeasts.entity.custom.greek.GreekEntity;
import net.mebahel.antiquebeasts.entity.custom.norse.NorseEntity;
import net.mebahel.antiquebeasts.entity.variant.DraugrWightVariant;
import net.mebahel.antiquebeasts.particle.ModParticles;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.ClientUtils;

public class DraugrWightEntity extends DraugrEntity implements GeoEntity {
    public DraugrWightEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);
    public AnimatableInstanceCache getAnimatableInstanceCache() {return factory;}

    public static final TrackedData<Integer> COOLDOWN = DataTracker.registerData(DraugrWightEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Boolean> SHOOTING = DataTracker.registerData(DraugrWightEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Integer> DATA_ID_TYPE_VARIANT = DataTracker.registerData(DraugrWightEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public boolean isShooting() {return this.dataTracker.get(SHOOTING);}
    public void setShooting(boolean shooting) {this.dataTracker.set(SHOOTING, shooting);}

    public void setCooldown(int cooldown) {this.dataTracker.set(COOLDOWN, cooldown);}
    public int getCooldown() { return this.dataTracker.get(COOLDOWN);}

    public int getTypeVariant() {return this.dataTracker.get(DATA_ID_TYPE_VARIANT);}
    private void setVariant(DraugrWightVariant variant) {this.dataTracker.set(DATA_ID_TYPE_VARIANT, variant.getId() & 255);}
    public DraugrWightVariant getWightVariant() {return DraugrWightVariant.byId(this.getTypeVariant() & 255);}

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(DATA_ID_TYPE_VARIANT, 0);
        this.dataTracker.startTracking(COOLDOWN, 80);
        this.dataTracker.startTracking(SHOOTING, false);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new DraugrDrinkPotionGoal(this));
        this.goalSelector.add(3, new DraugrBlockGoal(this));
        this.goalSelector.add(4, new DraugrSpellGoal(this, 3f));
        this.goalSelector.add(5, new DraugrWightMeleeAttackGoal(this, 1f, 25, 15));
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
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 34.0D + DraugrBonusHealthConfig.draugrBonusHealth)
                .add(EntityAttributes.GENERIC_ARMOR, 4f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0f)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.2f)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.5f);
    }

    private <E extends GeoAnimatable> PlayState predicate(AnimationState<E> animationState) {
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
        controllers.add(new AnimationController<>(this, "controller",0, this::predicate));
        controllers.add(new AnimationController<>(this, "attacking", 0, state -> PlayState.CONTINUE)
                .triggerableAnim("attack", RawAnimation.begin().then("attack", Animation.LoopType.PLAY_ONCE))
                .triggerableAnim("attack2", RawAnimation.begin().then("attack2", Animation.LoopType.PLAY_ONCE))
                .triggerableAnim("attack_combo1", RawAnimation.begin().then("attack_combo1", Animation.LoopType.PLAY_ONCE))
                .triggerableAnim("attack_combo2", RawAnimation.begin().then("attack_combo2", Animation.LoopType.PLAY_ONCE))
                .triggerableAnim("frostbite", RawAnimation.begin().then("frostbite", Animation.LoopType.PLAY_ONCE))
                .triggerableAnim("block_attack", RawAnimation.begin().then("block_attack", Animation.LoopType.PLAY_ONCE))
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

        var biome = world.getBiome(this.getBlockPos());
        DraugrWightVariant variant;
        boolean useAxeVariant = random.nextBoolean();

        if (biome.isIn(ConventionalBiomeTags.DESERT) || biome.isIn(ConventionalBiomeTags.BADLANDS)) {
            variant = useAxeVariant ? DraugrWightVariant.HOT_AXE : DraugrWightVariant.HOT;
        } else if (biome.isIn(ConventionalBiomeTags.CLIMATE_COLD) || biome.isIn(ConventionalBiomeTags.SNOWY) ||
                biome.isIn(ConventionalBiomeTags.ICY) || biome.isIn(ConventionalBiomeTags.AQUATIC_ICY)
                || biome.isIn(ConventionalBiomeTags.TAIGA)) {
            variant = useAxeVariant ? DraugrWightVariant.COLD_AXE : DraugrWightVariant.COLD;
        } else {
            variant = useAxeVariant ? DraugrWightVariant.TEMPERATE_AXE : DraugrWightVariant.TEMPERATE;
        }

        setVariant(variant);

        if (this.random.nextFloat() < DraugrCombatBalancingConfig.draugrSpawnWithPotionProbability / 100) {
            this.giveRandomMeleePotion();
        }

        float randomScale = 1.0F + this.random.nextFloat() * 0.15F; // [1.0 ; 1.1]
        this.setDraugrScale(randomScale);
        this.calculateDimensions();

        return entityData;
    }

    @Override
    public void tick() {
        super.tick();

        if (shouldDespawnInPeaceful()) {
            remove(Entity.RemovalReason.DISCARDED);
        }
        if (this.age % 7 == 0 && !this.isShooting() && !this.isSwinging()) {
            generateLeftHandParticles();
        }
    }

    private void generateLeftHandParticles() {
        Vec3d leftHandOffset = new Vec3d(-0.42, 0.85, -0.075);
        Vec3d lookDirection = this.getRotationVec(1.0F);
        Vec3d leftHandPosition = this.getPos().add(
                lookDirection.crossProduct(new Vec3d(0, 1, 0))
                        .normalize().multiply(leftHandOffset.x)
        ).add(0, leftHandOffset.y, 0);

        if (this.getWorld().isClient) {
            this.getWorld().addParticle(ModParticles.SNOWFLAKE_HAND_PARTICLE,
                    leftHandPosition.x, leftHandPosition.y, leftHandPosition.z,
                    0, -0.05, 0);
        }
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
            return randomValue < DraugrSpawnRateConfig.draugrWightSpawnRate;
        }
        return false;
    }
}