package net.mebahel.antiquebeasts.entity.custom.other;

import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags;
import net.mebahel.antiquebeasts.config.draugr.DraugrBonusHealthConfig;
import net.mebahel.antiquebeasts.config.draugr.DraugrCombatBalancingConfig;
import net.mebahel.antiquebeasts.config.draugr.DraugrSpawnRateConfig;
import net.mebahel.antiquebeasts.entity.ai.CustomRevengeGoal;
import net.mebahel.antiquebeasts.entity.ai.other.DraugrBlockGoal;
import net.mebahel.antiquebeasts.entity.ai.other.DraugrDrinkPotionGoal;
import net.mebahel.antiquebeasts.entity.ai.other.DraugrMeleeAttackGoal;
import net.mebahel.antiquebeasts.entity.custom.dwemer.DwemerEntity;
import net.mebahel.antiquebeasts.entity.custom.egyptian.EgyptianEntity;
import net.mebahel.antiquebeasts.entity.custom.greek.GreekEntity;
import net.mebahel.antiquebeasts.entity.custom.norse.NorseEntity;
import net.mebahel.antiquebeasts.entity.variant.DraugrVariant;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.mebahel.antiquebeasts.util.DamageElementUtil;
import net.mebahel.antiquebeasts.util.PreDamageResult;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
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
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;

import java.util.Objects;

import static java.lang.Math.random;

public class DraugrEntity extends HostileEntity implements GeoEntity {
    public DraugrEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.ambientSoundChance = -this.getMinAmbientSoundDelay();
        this.speed = Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).getValue();
    }

    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    public static final TrackedData<Integer> POTION_TYPE = DataTracker.registerData(DraugrEntity.class, TrackedDataHandlerRegistry.INTEGER);

    // Types de potion possibles
    public static final int POTION_NONE       = 0;
    public static final int POTION_HEAL       = 1;
    public static final int POTION_STRENGTH   = 2;
    public static final int POTION_RESISTANCE   = 3;
    public static final int POTION_SPEED      = 4;
    public static final int POTION_INVISIBILITY      = 5;

    // --- Potions ---
    public void setPotionType(int type) {this.dataTracker.set(POTION_TYPE, type);}
    public int getPotionType() {return this.dataTracker.get(POTION_TYPE);}

    public void setHasPotion(boolean value) {this.dataTracker.set(HAS_HEAL_POTION, value);}
    public boolean hasPotion() {return this.dataTracker.get(HAS_HEAL_POTION);}

    private boolean wantsToDrinkPotion = false;

    public boolean wantsToDrinkPotion() {return wantsToDrinkPotion;}
    public void requestPotionUse() {this.wantsToDrinkPotion = true;}
    public void clearPotionUseRequest() {this.wantsToDrinkPotion = false;}

    private boolean wantsToBlock = false;

    public boolean wantsToBlock() {return wantsToBlock;}
    public void requestBlock() {this.wantsToBlock = true;}
    public void clearBlockRequest() {this.wantsToBlock = false;}


    boolean raidSpawnIntro = false;

    public void setRaidSpawnIntro(boolean value) {
        this.raidSpawnIntro = value;
    }

    public boolean isInSpawnIntro() {
        return this.raidSpawnIntro && !this.getHasSpawned();
    }

    public boolean shouldDespawnInPeaceful() {
        return this.getWorld().getDifficulty() == Difficulty.PEACEFUL;
    }

    public static final TrackedData<Integer> DATA_ID_TYPE_VARIANT = DataTracker.registerData(DraugrEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Boolean> SWINGING = DataTracker.registerData(DraugrEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> IS_PART_OF_RAID = DataTracker.registerData(DraugrEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<String> ATTACK_NAME = DataTracker.registerData(DraugrEntity.class, TrackedDataHandlerRegistry.STRING);
    public static final TrackedData<Boolean> HAS_SPAWNED = DataTracker.registerData(DraugrEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> BLOCKING = DataTracker.registerData(DraugrEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> HAS_HEAL_POTION = DataTracker.registerData(DraugrEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> USING_POTION = DataTracker.registerData(DraugrEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Integer> HEAL_TICKS = DataTracker.registerData(DraugrEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Float> SCALE = DataTracker.registerData(DraugrEntity.class, TrackedDataHandlerRegistry.FLOAT);

    public void setHealTicks(int value) {
        this.dataTracker.set(HEAL_TICKS, value);
    }

    public int getHealTicks() {
        return this.dataTracker.get(HEAL_TICKS);
    }

    public boolean isUsingPotion() {return this.dataTracker.get(USING_POTION);}
    public void setUsingPotion(boolean value) {this.dataTracker.set(USING_POTION, value);}

    public void setBlocking(boolean blocking) {this.dataTracker.set(BLOCKING, blocking);}
    public boolean isBlocking() {return this.dataTracker.get(BLOCKING);}

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
        this.dataTracker.startTracking(BLOCKING, false);
        this.dataTracker.startTracking(HAS_HEAL_POTION, false);
        this.dataTracker.startTracking(USING_POTION, false);
        this.dataTracker.startTracking(POTION_TYPE, POTION_NONE);
        this.dataTracker.startTracking(HEAL_TICKS, 0);
        this.dataTracker.startTracking(SCALE, 1.0F);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new DraugrDrinkPotionGoal(this));
        this.goalSelector.add(3, new DraugrBlockGoal(this));
        this.goalSelector.add(4, new DraugrMeleeAttackGoal(this, 1f,
                25, 15));
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
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0D + DraugrBonusHealthConfig.draugrBonusHealth)
                .add(EntityAttributes.GENERIC_ARMOR, 6f)
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

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller",0, this::predicate));
        controllers.add(new AnimationController<>(this, "attacking", 0, state -> PlayState.CONTINUE)
                .triggerableAnim("attack", RawAnimation.begin().then("attack", Animation.LoopType.PLAY_ONCE))
                .triggerableAnim("attack2", RawAnimation.begin().then("attack2", Animation.LoopType.PLAY_ONCE))
                .triggerableAnim("attack_combo1", RawAnimation.begin().then("attack_combo1", Animation.LoopType.PLAY_ONCE))
                .triggerableAnim("attack_combo2", RawAnimation.begin().then("attack_combo2", Animation.LoopType.PLAY_ONCE))
                .triggerableAnim("block_attack", RawAnimation.begin().then("block_attack", Animation.LoopType.PLAY_ONCE))
                .triggerableAnim("drink_potion", RawAnimation.begin().then("drink_potion", Animation.LoopType.PLAY_ONCE)));

        controllers.add(new AnimationController<>(this, "spawning", 0, state -> PlayState.CONTINUE)
                .triggerableAnim("spawn", RawAnimation.begin().then("spawn", Animation.LoopType.PLAY_ONCE)));
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

        if (this.random.nextFloat() < DraugrCombatBalancingConfig.draugrSpawnWithPotionProbability / 100) {
            this.giveRandomMeleePotion();
        }
        float randomScale = 1.0F + this.random.nextFloat() * 0.15F; // [1.0 ; 1.1]
        this.setDraugrScale(randomScale);
        this.calculateDimensions(); // pour mettre à jour la hitbox dès le spawn

        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    @Override
    public net.minecraft.entity.EntityDimensions getDimensions(net.minecraft.entity.EntityPose pose) {
        float scale = this.getDraugrScale();
        return super.getDimensions(pose).scaled(scale);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        if (SCALE.equals(data)) {
            // Recalcule la hitbox quand le scale change
            this.calculateDimensions();
        }
    }

    public float getDraugrScale() {
        return this.dataTracker.get(SCALE);
    }

    public void setDraugrScale(float scale) {
        this.dataTracker.set(SCALE, scale);
    }

    public void giveRandomMeleePotion() {
        int roll = this.random.nextInt(3); // 0, 1, 2

        int type;
        switch (roll) {
            case 0 -> type = POTION_HEAL;
            case 1 -> type = POTION_STRENGTH;
            default -> type = POTION_RESISTANCE;
        }

        this.setPotionType(type);
        this.setHasPotion(true);
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

    @Override
    public boolean damage(DamageSource source, float amount) {
        // 👇 Mode blocage : il bloque tout, mais on montre des effets visuels/sonores
        if (this.isBlocking()) {

            // Particules de crit autour de lui
            if (!this.getWorld().isClient) {
                ServerWorld serverWorld = (ServerWorld) this.getWorld();
                serverWorld.spawnParticles(
                        ParticleTypes.CRIT,
                        this.getX(), this.getBodyY(0.5D), this.getZ(),
                        8,
                        0.5D, 0.8D, 0.5D,
                        0.2D);
            }

            // Son de blocage façon bouclier
            this.playSound(
                    ModSounds.WEAPON_SWORD_BLOCK,
                    0.75F + this.getRandom().nextFloat() * 0.2F,
                    0.8F + this.getRandom().nextFloat() * 0.4F
            );

            // Aucun dégât appliqué
            return false;
        }

        // 🔥 Vulnérable au feu quand il ne bloque pas
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

        if (!getHasSpawned())
            spawnHoveringParticles();
        if (this.age == 1 && !this.getHasSpawned()) {
            this.triggerAnim("spawning", "spawn");
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(0);
            this.playSound(ModSounds.MUMMY_SPAWN, 0.75F + this.getRandom().nextFloat() * 0.2F, 0.8F + this.getRandom().nextFloat() * 0.4F);
        } else if (this.age == 40 && !this.getHasSpawned()) {
            this.setHasSpawned(true);
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(this.speed);
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("Variant", this.getTypeVariant());
        nbt.putBoolean("PartOfRaid", this.isPartOfRaid());

        nbt.putInt("PotionType", this.getPotionType());
        nbt.putBoolean("HasPotion", this.hasPotion());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.dataTracker.set(DATA_ID_TYPE_VARIANT, nbt.getInt("Variant"));
        this.dataTracker.set(IS_PART_OF_RAID, nbt.getBoolean("PartOfRaid"));
        this.dataTracker.set(HAS_SPAWNED, true);

        if (nbt.contains("PotionType")) {
            this.setPotionType(nbt.getInt("PotionType"));
        }
        if (nbt.contains("HasPotion")) {
            this.setHasPotion(nbt.getBoolean("HasPotion"));
        }
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);
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
            return randomValue < DraugrSpawnRateConfig.draugrSpawnRate;
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

    public void giveRandomRangedPotion() {
        int roll = this.random.nextInt(2); // 0 ou 1

        int type = (roll == 0)
                ? POTION_SPEED
                : POTION_INVISIBILITY;

        this.setPotionType(type);
        this.setHasPotion(true);
    }

    @Override
    public EntityGroup getGroup() {
        return EntityGroup.UNDEAD;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(ModSounds.DRAUGR_WALK_1,
                0.4F + this.getRandom().nextFloat() * 0.2F,
                0.9F + this.getRandom().nextFloat() * 0.4F);
    }

    protected PreDamageResult preDamage(DamageSource source, float amount,
                                        @Nullable Entity direct, @Nullable Entity attacker) {

        boolean isProjectile = direct instanceof ProjectileEntity;
        boolean isMelee = !isProjectile && attacker instanceof LivingEntity;

        if (this.isBlocking() && isMelee) {
            if (!this.getWorld().isClient) {
                ServerWorld sw = (ServerWorld) this.getWorld();
                sw.spawnParticles(
                        ParticleTypes.CRIT,
                        this.getX(), this.getBodyY(0.5D), this.getZ(),
                        8,
                        0.5D, 0.8D, 0.5D,
                        0.2D
                );
            }

            this.playSound(
                    ModSounds.WEAPON_SWORD_BLOCK,
                    0.75F + this.getRandom().nextFloat() * 0.2F,
                    0.8F + this.getRandom().nextFloat() * 0.4F
            );
            return PreDamageResult.cancel();
        }

        DamageElementUtil.ElementResult r = DamageElementUtil.compute(source, direct,
                DamageElementUtil.ElementConfig.defaultPriority(1.5f, 0.75f, 1f));
        float finalAmount = DamageElementUtil.apply(amount, r);

        if (!this.getWorld().isClient && r != null && r.frostLike) {
            int add = 40;
            this.setFrozenTicks(Math.min(this.getFrozenTicks() + add, 200));
        }

        return new PreDamageResult(false, finalAmount, r);
    }
}
