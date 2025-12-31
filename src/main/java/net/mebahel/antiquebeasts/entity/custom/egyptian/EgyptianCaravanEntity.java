package net.mebahel.antiquebeasts.entity.custom.egyptian;

import net.mebahel.antiquebeasts.entity.ModEntities;
import net.mebahel.antiquebeasts.entity.ai.CaravanEscapeDangerGoal;
import net.mebahel.antiquebeasts.entity.ai.util.ModPatrolGoal;
import net.mebahel.antiquebeasts.entity.custom.patrol.ModPatrolEntity;
import net.mebahel.antiquebeasts.entity.variant.EgyptiantVariant;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.mebahel.antiquebeasts.util.config.ModBonusHealthConfig;
import net.mebahel.antiquebeasts.util.config.ModSpawnRateConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
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

import org.jetbrains.annotations.Nullable;

import static java.lang.Math.random;

public class EgyptianCaravanEntity extends EgyptianEntity implements GeoEntity {
    public EgyptianCaravanEntity(EntityType<? extends ModPatrolEntity> entityType, World world) {
        super(entityType, world);
        this.ambientSoundChance = -this.getMinAmbientSoundDelay();
    }
    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);

    // --- Escort spawn (thread-safe / C2ME-safe) ---
    private boolean escortsPending = false;
    private boolean escortsSpawned = false;

    private int pendingAxemen = 0;
    private boolean pendingElephant = false;
    private boolean pendingCamelry = false;


    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    protected void initDataTracker() {
        super.initDataTracker();

        this.dataTracker.startTracking(SWINGING, false);
        this.dataTracker.startTracking(DATA_ID_TYPE_VARIANT, 0);
        this.dataTracker.startTracking(ATTACK_NAME, "attack");
        this.dataTracker.startTracking(IS_IN_CARAVAN, false);
        this.dataTracker.startTracking(PATROL_UUID, "");
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return HostileEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.78f)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0D + ModBonusHealthConfig.egyptianCaravanBonusHealth)
                .add(EntityAttributes.GENERIC_ARMOR, 0f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0f)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.3f)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.5f);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new CaravanEscapeDangerGoal(this, 0.45f));
        this.goalSelector.add(3, new ModPatrolGoal(this, 0.37f, 0.4f));
        this.goalSelector.add(4, new WanderAroundFarGoal(this, 0.35f, 1f));
        this.goalSelector.add(5, new LookAroundGoal(this));
    }

    private PlayState predicate(AnimationState animationState) {
        if (animationState.isMoving()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        animationState.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController(this, "controller", 0, this::predicate));
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        rand = random();
        if (rand < 0.5)
            return ModSounds.CAMELRY_HURT_1;
        else
            return ModSounds.CAMELRY_HURT_2;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.CAMELRY_DEATH_1;
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
                return ModSounds.EGYPTIAN_AMBIENT_1;
            else if (rand > 0.3 && rand < 0.6)
                return ModSounds.EGYPTIAN_AMBIENT_2;
            else
                return ModSounds.EGYPTIAN_AMBIENT_3;
        }
    }

    @Override
    public void dropInventory() {
        super.dropInventory();
        if (!this.getWorld().isClient) {
            BlockPos pos = this.getBlockPos();
            while (this.getWorld().isAir(pos.down()) && pos.getY() > 0) {
                pos = pos.down();
            }
            this.getWorld().setBlockState(pos, Blocks.CHEST.getDefaultState());

            BlockEntity blockEntity = this.getWorld().getBlockEntity(pos);
            if (blockEntity instanceof ChestBlockEntity) {
                ((ChestBlockEntity) blockEntity).setLootTable(new Identifier("antiquebeasts", "chests/egyptian/egyptian_caravan"), this.random.nextLong());
            }
        }
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty,
                                 SpawnReason spawnReason, @Nullable EntityData entityData,
                                 @Nullable NbtCompound entityNbt) {

        // Variant
        EgyptiantVariant variant = Util.getRandom(EgyptiantVariant.values(), this.random);
        setVariant(variant);

        // Prépare escortes (mais ne spawn pas ici)
        this.pendingAxemen = 2 + this.random.nextInt(2);
        this.pendingElephant = (this.random.nextInt(3) == 0);
        this.pendingCamelry = (this.random.nextInt(2) == 0);

        this.escortsPending = true;
        this.escortsSpawned = false;

        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }


    private Vec3d getOffsetPosition(double offsetX, double offsetZ) {
        double x = this.getX() + offsetX;
        double y = this.getY();
        double z = this.getZ() + offsetZ;
        return new Vec3d(x, y, z);
    }

    public EgyptiantVariant getVariant() {
        return EgyptiantVariant.byId(this.getTypeVariant() & 255);
    }

    public void setVariant(EgyptiantVariant variant) {
        this.dataTracker.set(DATA_ID_TYPE_VARIANT, variant.getId() & 255);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.shouldDespawnInPeaceful()) {
            this.remove(RemovalReason.DISCARDED);
            return;
        }

        if (!this.getWorld().isClient) {
            trySpawnEscortsServer();
        }
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
        if (state.getSoundGroup() == BlockSoundGroup.SAND) {
            this.playSound(SoundEvents.ENTITY_CAMEL_STEP_SAND, 1.0F, 1.0F);
        } else {
            this.playSound(SoundEvents.ENTITY_CAMEL_STEP, 1.0F, 1.0F);
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

        boolean isSandyGround = blockBelow.isOf(Blocks.SAND)
                || blockBelow.isOf(Blocks.RED_SAND);

        if (!isSandyGround) {
            return false;
        }

        if (world.getLightLevel(pos) < 9) {
            return false;
        }

        if (!world.isSkyVisible(pos)) {
            return false;
        }

        int randomValue = random.nextInt(10);
        return randomValue < ModSpawnRateConfig.egyptianCaravanSpawnRate;
    }

    private void trySpawnEscortsServer() {
        if (!this.escortsPending || this.escortsSpawned) return;
        if (!this.isAlive()) return;

        // évite spawn pendant qu'on n'est pas “stable” dans le monde
        if (this.age < 1) return;

        // IMPORTANT: ne pas dépendre du thread courant; on pousse sur le thread serveur
        if (!(this.getWorld() instanceof net.minecraft.server.world.ServerWorld sw)) return;

        this.escortsSpawned = true;
        this.escortsPending = false;

        sw.getServer().execute(() -> {
            // Re-checks une fois sur le thread serveur
            if (!this.isAlive()) return;
            if (this.isRemoved()) return;
            if (this.getWorld() != sw) return;

            // Axemen
            for (int i = 0; i < this.pendingAxemen; i++) {
                AxemanEntity axeman = new AxemanEntity(ModEntities.AXEMAN, sw, true, this);
                Vec3d p = getOffsetPosition(4 * this.random.nextDouble(), 4 * this.random.nextDouble());
                axeman.refreshPositionAndAngles(p.x, p.y, p.z, this.getYaw(), this.getPitch());
                sw.spawnEntity(axeman);
            }

            // Elephant rider
            if (this.pendingElephant) {
                ElephantRiderEntity er = new ElephantRiderEntity(ModEntities.ELEPHANT_RIDER, sw, true, this);
                Vec3d p = getOffsetPosition(4 * this.random.nextDouble(), 4 * this.random.nextDouble());
                er.refreshPositionAndAngles(p.x, p.y, p.z, this.getYaw(), this.getPitch());
                sw.spawnEntity(er);
            }

            // Camelry
            if (this.pendingCamelry) {
                CamelryEntity cr = new CamelryEntity(ModEntities.CAMELRY, sw, true, this);
                Vec3d p = getOffsetPosition(4 * this.random.nextDouble(), 4 * this.random.nextDouble());
                cr.refreshPositionAndAngles(p.x, p.y, p.z, this.getYaw(), this.getPitch());
                sw.spawnEntity(cr);
            }

            // Nettoyage
            this.pendingAxemen = 0;
            this.pendingElephant = false;
            this.pendingCamelry = false;
        });
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);

        this.escortsSpawned = nbt.getBoolean("EscortsSpawned");
        this.escortsPending = nbt.getBoolean("EscortsPending");

        this.pendingAxemen = nbt.getInt("PendingAxemen");
        this.pendingElephant = nbt.getBoolean("PendingElephant");
        this.pendingCamelry = nbt.getBoolean("PendingCamelry");
    }


    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);

        nbt.putBoolean("EscortsSpawned", this.escortsSpawned);
        nbt.putBoolean("EscortsPending", this.escortsPending);

        nbt.putInt("PendingAxemen", this.pendingAxemen);
        nbt.putBoolean("PendingElephant", this.pendingElephant);
        nbt.putBoolean("PendingCamelry", this.pendingCamelry);
    }
}
