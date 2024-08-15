package net.mebahel.antiquebeasts.entity.custom.egyptian;

import net.mebahel.antiquebeasts.entity.ModEntities;
import net.mebahel.antiquebeasts.entity.ai.CaravanEscapeDangerGoal;
import net.mebahel.antiquebeasts.entity.ai.ModPatrolGoal;
import net.mebahel.antiquebeasts.entity.custom.patrol.ModPatrolEntity;
import net.mebahel.antiquebeasts.entity.variant.EgyptiantVariant;
import net.mebahel.antiquebeasts.sound.ModSounds;
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
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimationTickable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import javax.annotation.Nullable;

import static java.lang.Math.random;

public class EgyptianCaravanEntity extends EgyptianEntity implements IAnimatable, IAnimationTickable {

    public EgyptianCaravanEntity(EntityType<? extends ModPatrolEntity> entityType, World world) {
        super(entityType, world);
        this.ambientSoundChance = -this.getMinAmbientSoundDelay();
    }

    public String animationProcedure = "empty";
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    @Override
    public int tickTimer() {
        return age;
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
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0D)
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
                event.getController().setAnimation(new AnimationBuilder().addAnimation("attack2", ILoopType.EDefaultLoopTypes.PLAY_ONCE));
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
        AnimationController<EgyptianCaravanEntity> controller = new AnimationController<>(this, "controller", 0,
                this::movementPredicate);
        AnimationController<EgyptianCaravanEntity> controller2 = new AnimationController<>(this, "procedure", 0, this::procedurePredicate);
        data.addAnimationController(controller);
        data.addAnimationController(controller2);
    }
    @Override
    public AnimationFactory getFactory() {
        return factory;
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
        int numAxemen = 2 + this.random.nextInt(2);

        EgyptiantVariant variant = Util.getRandom(EgyptiantVariant.values(), this.random);
        setVariant(variant);
        if (spawnReason != SpawnReason.SPAWN_EGG &&
                spawnReason != SpawnReason.COMMAND &&
                spawnReason != SpawnReason.SPAWNER &&
                spawnReason != SpawnReason.EVENT) {
            int randomValue = this.random.nextInt(11);
            if (randomValue >= 0 && randomValue <= 8) {
                this.remove(RemovalReason.DISCARDED);
            } else {
                for (int i = 0; i < numAxemen; i++) {
                    AxemanEntity newAxeman = new AxemanEntity(ModEntities.AXEMAN, this.getWorld(), true, this);
                    Vec3d offsetPosition = getOffsetPosition(4 * this.random.nextDouble(), 4 * this.random.nextDouble());
                    newAxeman.refreshPositionAndAngles(offsetPosition.x, offsetPosition.y, offsetPosition.z, this.getYaw(), this.getPitch());
                    this.getWorld().spawnEntity(newAxeman);
                }
                if (this.random.nextInt(3) == 0) {
                    ElephantRiderEntity newElephantRider = new ElephantRiderEntity(ModEntities.ELEPHANT_RIDER, this.getWorld(), true, this);
                    Vec3d offsetPosition = getOffsetPosition(4 * this.random.nextDouble(), 4 * this.random.nextDouble());
                    newElephantRider.refreshPositionAndAngles(offsetPosition.x, offsetPosition.y, offsetPosition.z, this.getYaw(), this.getPitch());
                    this.getWorld().spawnEntity(newElephantRider);
                }
                if (this.random.nextInt(2) == 0) {
                    CamelryEntity newCamelryRider = new CamelryEntity(ModEntities.CAMELRY, this.getWorld(), true, this);
                    Vec3d offsetPosition = getOffsetPosition(4 * this.random.nextDouble(), 4 * this.random.nextDouble());
                    newCamelryRider.refreshPositionAndAngles(offsetPosition.x, offsetPosition.y, offsetPosition.z, this.getYaw(), this.getPitch());
                    this.getWorld().spawnEntity(newCamelryRider);
                }
            }
        }


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
        if (shouldDespawnInPeaceful()) {
            this.remove(RemovalReason.DISCARDED);
        }
    }
}
