package net.mebahel.antiquebeasts.entity.custom.greek;

import net.mebahel.antiquebeasts.entity.ai.greek.GreekMeleeAttackGoal;
import net.mebahel.antiquebeasts.entity.ai.util.GroupRevengeGoal;
import net.mebahel.antiquebeasts.entity.ai.util.ModPatrolGoal;
import net.mebahel.antiquebeasts.entity.custom.dwemer.DwemerEntity;
import net.mebahel.antiquebeasts.entity.custom.egyptian.EgyptianEntity;
import net.mebahel.antiquebeasts.entity.custom.norse.NorseEntity;
import net.mebahel.antiquebeasts.entity.custom.other.DraugrEntity;
import net.mebahel.antiquebeasts.entity.variant.EliteHopliteVariant;
import net.mebahel.antiquebeasts.item.custom.ModItems;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.mebahel.antiquebeasts.util.ModSoundUtil;
import net.mebahel.antiquebeasts.util.config.ModBonusHealthConfig;
import net.mebahel.antiquebeasts.util.config.ModSpawnRateConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
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
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.ClientUtils;

import org.jetbrains.annotations.Nullable;

public class EliteHopliteEntity extends GreekEntity implements GeoEntity {
    private int idleCondition = 0;
    private boolean shouldRandomIdle = true;
    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);

    public static final TrackedData<Float> COOLDOWN = DataTracker.registerData(EliteHopliteEntity.class,
            TrackedDataHandlerRegistry.FLOAT);
    public static final TrackedData<String> CURRENT_ANIMATION =
            DataTracker.registerData(EliteHopliteEntity.class, TrackedDataHandlerRegistry.STRING);

    public float getCooldown() { return this.dataTracker.get(COOLDOWN);}
    public void setCooldown(float cooldown) {
        this.dataTracker.set(COOLDOWN, cooldown);
    }
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.shouldDespawnInPeaceful()) {
            this.remove(RemovalReason.DISCARDED);
        }
    }
    public String getCurrentAnimation() {
        return this.dataTracker.get(CURRENT_ANIMATION);
    }

    public void setCurrentAnimation(String animation) {
        this.dataTracker.set(CURRENT_ANIMATION, animation);
    }

    public EliteHopliteEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
        this.ambientSoundChance = -this.getMinAmbientSoundDelay();
    }

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SWINGING, false);
        this.dataTracker.startTracking(DATA_ID_TYPE_VARIANT, 0);
        this.dataTracker.startTracking(ATTACK_NAME, "attack");
        this.dataTracker.startTracking(PATROL_UUID, "");
        this.dataTracker.startTracking(CURRENT_ANIMATION, "idle");
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return HostileEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 35)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.72f)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 24.0D + ModBonusHealthConfig.eliteHopliteBonusHealth)
                .add(EntityAttributes.GENERIC_ARMOR, 4f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0f)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.2f)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.5f);
    }
    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new ModPatrolGoal(this, 0.37f, 0.4f));
        this.goalSelector.add(3, new GreekMeleeAttackGoal(this, 0.47f, 21, 10));
        this.goalSelector.add(4, new WanderAroundFarGoal(this, 0.35f, 1f));
        this.goalSelector.add(5, new LookAroundGoal(this));

        this.targetSelector.add(1, (new GroupRevengeGoal(this, GreekEntity.class))
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
        if (animationState.isMoving() && this.isAttacking()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("walk3", Animation.LoopType.PLAY_ONCE).then("walk2", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        } else if (animationState.isMoving() && !this.isAttacking()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("walk", Animation.LoopType.LOOP));
            if (shouldRandomIdle) {
                double rand = Math.random();
                if (rand < 0.33) {
                    idleCondition = 1;
                } else if (rand < 0.66) {
                    idleCondition = 2;
                } else {
                    idleCondition = 3;
                }
                this.shouldRandomIdle = false;
            }
            return PlayState.CONTINUE;
        } else if (!animationState.isMoving() && !this.isAttacking()) {
            if (idleCondition == 1) {
                setCurrentAnimation("idle");
                animationState.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
            } else if (idleCondition == 2) {
                setCurrentAnimation("idle2");
                animationState.getController().setAnimation(RawAnimation.begin().then("idle2", Animation.LoopType.LOOP));
            } else {
                setCurrentAnimation("idle3");
                animationState.getController().setAnimation(RawAnimation.begin().then("idle3", Animation.LoopType.LOOP));
            }
            this.shouldRandomIdle = true;
            return PlayState.CONTINUE;
        }
        return PlayState.CONTINUE;
    }

    private PlayState attackPredicate(AnimationState state) {
        if(this.isSwinging() && state.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
            state.getController().forceAnimationReset();
            state.getController().setAnimation(RawAnimation.begin().then(this.getAttackName(), Animation.LoopType.PLAY_ONCE));
        }

        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController(this, "controller",1, this::predicate));
        controllers.add(new AnimationController(this, "attacking", 1, this::attackPredicate).setSoundKeyframeHandler(state -> {
            PlayerEntity player = ClientUtils.getClientPlayer();
            if (player != null)
                this.getWorld().playSound(player, this.getX(), this.getY(), this.getZ(), ModSounds.SWING, this.getSoundCategory(), 0.5f, 1.5f);
        }));
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        return super.damage(source, amount);
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty,
                                 SpawnReason spawnReason, @Nullable EntityData entityData,
                                 @Nullable NbtCompound entityNbt) {
        EliteHopliteVariant variant = Util.getRandom(EliteHopliteVariant.values(), this.random);
        setVariant(variant);
        ModSoundUtil.InfantryPlaySound(spawnReason, this);
        this.setTarget(null);

        return entityData;
    }

    public EliteHopliteVariant getVariant() {
        return EliteHopliteVariant.byId(this.getTypeVariant() & 255);
    }

    public int getTypeVariant() {
        return this.dataTracker.get(DATA_ID_TYPE_VARIANT);
    }

    private void setVariant(EliteHopliteVariant variant) {
        this.dataTracker.set(DATA_ID_TYPE_VARIANT, variant.getId() & 255);
    }
    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);

        if (source.getAttacker() instanceof ZombieEntity) {
            if (this.random.nextFloat() < 0.25f) {
                ZombieEntity newZombie = EntityType.ZOMBIE.create(this.getWorld());
                if (newZombie != null) {
                    newZombie.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch());

                    newZombie.equipStack(EquipmentSlot.HEAD, new ItemStack(ModItems.IRON_PLATE_HELMET));
                    newZombie.equipStack(EquipmentSlot.CHEST, new ItemStack(ModItems.IRON_PLATE_CHESTPLATE));
                    newZombie.equipStack(EquipmentSlot.LEGS, new ItemStack(ModItems.IRON_PLATE_LEGGINGS));
                    newZombie.equipStack(EquipmentSlot.FEET, new ItemStack(ModItems.IRON_PLATE_BOOTS));
                    newZombie.equipStack(EquipmentSlot.MAINHAND, new ItemStack(ModItems.IRON_HOPLITE_SPEAR));
                    newZombie.equipStack(EquipmentSlot.OFFHAND, new ItemStack(ModItems.IRON_PLATE_SHIELD));

                    for (ItemStack stack : newZombie.getItemsEquipped()) {
                        if (!stack.isEmpty()) {
                            stack.setDamage(this.random.nextInt(stack.getMaxDamage()));
                        }
                    }
                    newZombie.setEquipmentDropChance(EquipmentSlot.HEAD, 0.0f);
                    newZombie.setEquipmentDropChance(EquipmentSlot.CHEST, 0.0f);
                    newZombie.setEquipmentDropChance(EquipmentSlot.LEGS, 0.0f);
                    newZombie.setEquipmentDropChance(EquipmentSlot.FEET, 0.0f);
                    newZombie.setEquipmentDropChance(EquipmentSlot.MAINHAND, 0.0f);
                    newZombie.setEquipmentDropChance(EquipmentSlot.OFFHAND, 0.0f);

                    this.getWorld().spawnEntity(newZombie);
                }
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
        return randomValue < ModSpawnRateConfig.eliteHopliteSpawnRate;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
    }
    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
    }
}
