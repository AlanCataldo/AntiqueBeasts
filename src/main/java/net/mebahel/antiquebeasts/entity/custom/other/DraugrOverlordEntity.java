package net.mebahel.antiquebeasts.entity.custom.other;

import net.mebahel.antiquebeasts.block.entity.BlockScanEntity;
import net.mebahel.antiquebeasts.entity.ModEntities;
import net.mebahel.antiquebeasts.entity.ai.CustomRevengeGoal;
import net.mebahel.antiquebeasts.entity.ai.other.DraugrOverlordMeleeAttackGoal;
import net.mebahel.antiquebeasts.entity.ai.other.DraugrOverlordSpecialAttackGoal;
import net.mebahel.antiquebeasts.entity.custom.dwemer.DwemerEntity;
import net.mebahel.antiquebeasts.entity.custom.egyptian.EgyptianEntity;
import net.mebahel.antiquebeasts.entity.custom.greek.GreekEntity;
import net.mebahel.antiquebeasts.entity.custom.norse.NorseEntity;
import net.mebahel.antiquebeasts.entity.variant.DraugrOverlordVariant;
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
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
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
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
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

public class DraugrOverlordEntity extends DraugrEntity implements GeoEntity {

    private final ServerBossBar bossBar;
    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);
    private int attackTick = 0;

    public DraugrOverlordEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.ambientSoundChance = -this.getMinAmbientSoundDelay();
        this.bossBar = new ServerBossBar(
                Text.of("Draugr Overlord"),
                BossBar.Color.RED,
                BossBar.Style.NOTCHED_10
        );
        this.bossBar.setPercent(1.0f);
        this.setPersistent();
    }

    // ---- TRACKED DATA ----
    public static final TrackedData<Integer> DATA_ID_TYPE_VARIANT =
            DataTracker.registerData(DraugrOverlordEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Boolean> SWINGING =
            DataTracker.registerData(DraugrOverlordEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<String> ATTACK_NAME =
            DataTracker.registerData(DraugrOverlordEntity.class, TrackedDataHandlerRegistry.STRING);
    public static final TrackedData<Boolean> HAS_SPAWNED =
            DataTracker.registerData(DraugrOverlordEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Integer> SPECIAL_COOLDOWN =
            DataTracker.registerData(DraugrOverlordEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Boolean> SPECIAL =
            DataTracker.registerData(DraugrOverlordEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    // ---- BASIC GETTERS / SETTERS ----
    public int incrementAttackTick() { return ++attackTick; }
    public void resetAttackTick() { this.attackTick = 0; }

    public boolean getSpecial() { return this.dataTracker.get(SPECIAL); }
    public void setSpecial(boolean bool) { this.dataTracker.set(SPECIAL, bool); }

    public int getSpecialCooldown() { return this.dataTracker.get(SPECIAL_COOLDOWN); }
    public void setSpecialCooldown(int value) { this.dataTracker.set(SPECIAL_COOLDOWN, value); }

    public boolean getHasSpawned() { return this.dataTracker.get(HAS_SPAWNED); }
    public void setHasSpawned(boolean bool) { this.dataTracker.set(HAS_SPAWNED, bool); }

    public void setSwinging(boolean swinging) { this.dataTracker.set(SWINGING, swinging); }
    public boolean isSwinging() { return this.dataTracker.get(SWINGING); }

    public void setAttackName(String attackName) { this.dataTracker.set(ATTACK_NAME, attackName); }
    public String getAttackName() { return this.dataTracker.get(ATTACK_NAME); }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SWINGING, false);
        this.dataTracker.startTracking(DATA_ID_TYPE_VARIANT, 0);
        this.dataTracker.startTracking(ATTACK_NAME, "sl_attack_rush");
        this.dataTracker.startTracking(HAS_SPAWNED, true);
        this.dataTracker.startTracking(SPECIAL, false);
        this.dataTracker.startTracking(SPECIAL_COOLDOWN, 70);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new DraugrOverlordSpecialAttackGoal(this));
        this.goalSelector.add(3, new DraugrOverlordMeleeAttackGoal(this, 0.95f));
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
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 400.0D + ModBonusHealthConfig.draugrBonusHealth)
                .add(EntityAttributes.GENERIC_ARMOR, 14f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 12.0f)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.75f)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.5f);
    }

    // ---- ANIMATION LOGIC ----
    private PlayState predicate(AnimationState state) {
        if (!this.getHasSpawned()) return PlayState.STOP;

        if (state.isMoving() && !this.getSpecial() && !this.isSwinging()) {
            spawnSwordDraggingParticles();
            state.getController().setAnimation(RawAnimation.begin().then("walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        } else if (!state.isMoving() && !this.isAttacking() && !this.getSpecial()) {
            state.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));

        controllers.add(
                new AnimationController<>(this, "attacking", 0, state -> PlayState.CONTINUE)
                        .triggerableAnim("sl_attack_rush", RawAnimation.begin().then("sl_attack_rush", Animation.LoopType.PLAY_ONCE))
                        .triggerableAnim("sl_attack_double1", RawAnimation.begin().then("sl_attack_double1", Animation.LoopType.PLAY_ONCE))
                        .triggerableAnim("sl_attack_double2", RawAnimation.begin().then("sl_attack_double2", Animation.LoopType.PLAY_ONCE))
                        .triggerableAnim("sl_attack_triple", RawAnimation.begin().then("sl_attack_triple", Animation.LoopType.PLAY_ONCE))
        );

        controllers.add(new AnimationController<>(this, "spawning", 0, this::spawnPredicate)
                .setSoundKeyframeHandler(state -> {
                    PlayerEntity player = ClientUtils.getClientPlayer();
                    if (player != null)
                        this.getWorld().playSound(player, this.getX(), this.getY(), this.getZ(),
                                ModSounds.MUMMY_SPAWN, this.getSoundCategory(), 0.65f, 1f);
                }));

        controllers.add(
                new AnimationController<>(this, "specialController", 0, state -> PlayState.CONTINUE)
                        .triggerableAnim("sl_attack_quake", RawAnimation.begin().then("sl_attack_quake", Animation.LoopType.PLAY_ONCE))
        );
    }

    // ---- VARIANT ----
    public DraugrOverlordVariant getOverlordVariant() {
        return DraugrOverlordVariant.byId(this.getTypeVariant() & 255);
    }

    public int getTypeVariant() { return this.dataTracker.get(DATA_ID_TYPE_VARIANT); }

    private void setVariant() {
        this.dataTracker.set(DATA_ID_TYPE_VARIANT, DraugrOverlordVariant.GREATSWORD.getId() & 255);
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason,
                                 @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        setVariant();
        return entityData;
    }

    // ---- DAMAGE ----
    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source.isOf(DamageTypes.IN_FIRE) || source.isOf(DamageTypes.ON_FIRE))
            return super.damage(source, amount * 2);
        else if (source.isOf(DamageTypes.FREEZE))
            return false;
        return super.damage(source, amount);
    }

    // ---- BOSS BAR ----
    @Override
    public void tick() {
        super.tick();
        this.updateBossBar();
        this.bossBar.setPercent(this.getHealth() / this.getMaxHealth());

        if (this.isDead() || this.isRemoved()) this.bossBar.clearPlayers();
    }

    private void updateBossBar() {
        Box detectionBox = new Box(this.getBlockPos()).expand(18);
        for (PlayerEntity player : this.getWorld().getPlayers()) {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                if (detectionBox.contains(player.getPos())) {
                    if (!this.bossBar.getPlayers().contains(serverPlayer)) this.bossBar.addPlayer(serverPlayer);
                } else {
                    if (this.bossBar.getPlayers().contains(serverPlayer)) this.bossBar.removePlayer(serverPlayer);
                }
            }
        }
    }

    @Override
    public void onStoppedTrackingBy(ServerPlayerEntity player) {
        super.onStoppedTrackingBy(player);
        this.bossBar.removePlayer(player);
    }

    // ---- SPAWN PARTICLES ----
    private <T extends GeoAnimatable> PlayState spawnPredicate(AnimationState<T> state) {
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

    void spawnHoveringParticles() {
        double posX = this.getX();
        double posY = this.getY() - 0.1;
        double posZ = this.getZ();

        BlockPos blockPos = new BlockPos((int) posX, (int) (this.getY() - 0.5), (int) posZ);
        BlockState blockState = this.getWorld().getBlockState(blockPos);

        if (!blockState.isAir()) {
            for (int i = 0; i < 3; i++) {
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

    public void spawnSwordDraggingParticles() {
        double yawRad = Math.toRadians(-this.bodyYaw);
        double cosYaw = Math.cos(yawRad);
        double sinYaw = Math.sin(yawRad);

        double offsetBackX = -sinYaw;
        double offsetBackZ = -cosYaw;
        double offsetRightX = -cosYaw;
        double offsetRightZ = sinYaw;

        double particleX = this.getX() + offsetBackX + offsetRightX;
        double particleZ = this.getZ() + offsetBackZ + offsetRightZ;
        double particleY = this.getY();

        BlockPos blockPos = new BlockPos((int) particleX, (int) (particleY - 1), (int) particleZ);
        BlockState blockState = this.getWorld().getBlockState(blockPos);

        if (blockState.isAir()) return;

        for (int i = 0; i < 1; i++) {
            double randomOffsetX = (this.random.nextDouble() - 0.5) * 0.1;
            double randomOffsetZ = (this.random.nextDouble() - 0.5) * 0.1;
            this.getWorld().addParticle(
                    new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState),
                    particleX + randomOffsetX, particleY, particleZ + randomOffsetZ,
                    0.0, 0.05, 0.0
            );
        }
    }

    // ---- AREA EFFECT ----
    public static void AreaCrackedGround(LivingEntity mob, World world, BlockPos centerPos, int radius) {
        BlockPos startPos = centerPos.down();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                BlockPos targetPos = startPos.add(x, -1, z);
                if (startPos.getSquaredDistance(targetPos) <= radius * radius) {
                    BlockState blockState = world.getBlockState(targetPos);
                    BlockPos spawnPos = targetPos.up();
                    spawnBlockScanEntity(world, spawnPos, blockState, mob.getRandom());
                }
            }
        }
    }

    public static void spawnBlockScanEntity(World world, BlockPos pos, BlockState blockState, Random random) {
        BlockScanEntity blockScanEntity = new BlockScanEntity(ModEntities.BLOCK_SCAN_ENTITY, world);
        float randomYOffset = random.nextFloat() * 0.2f + 0.2f;
        blockScanEntity.setPosition(pos.getX(), pos.getY() - 0.5 + randomYOffset, pos.getZ());
        blockScanEntity.setBlockState(blockState);

        float randomPitch = random.nextFloat() * 40 - 20;
        float randomYaw = random.nextFloat() * 40 - 20;
        blockScanEntity.setPitch(randomPitch);
        blockScanEntity.setYaw(randomYaw);
        world.spawnEntity(blockScanEntity);
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(ModSounds.DRAUGR_WALK_1, 0.65f, 0.7f);
    }
}
