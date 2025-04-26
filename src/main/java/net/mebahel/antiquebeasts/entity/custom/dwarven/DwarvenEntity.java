package net.mebahel.antiquebeasts.entity.custom.dwarven;

import net.mebahel.antiquebeasts.entity.variant.CyclopsVariant;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.mebahel.antiquebeasts.util.config.ModBonusHealthConfig;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.pathing.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static java.lang.Math.random;

public class DwarvenEntity extends AnimalEntity {
    private final List<ItemStack> minedResources = new java.util.ArrayList<>();
    double rand;

    public static final TrackedData<Boolean> SHOOTING = DataTracker.registerData(DwarvenEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public static final TrackedData<Boolean> SWINGING = DataTracker.registerData(DwarvenEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public static final TrackedData<Float> COOLDOWN = DataTracker.registerData(DwarvenEntity.class, TrackedDataHandlerRegistry.FLOAT);

    public static final TrackedData<String> ATTACK_NAME = DataTracker.registerData(DwarvenEntity.class, TrackedDataHandlerRegistry.STRING);
    public static final TrackedData<Boolean> IS_MINING = DataTracker.registerData(DwarvenEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Integer> MINING_COOLDOWN = DataTracker.registerData(DwarvenEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public DwarvenEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
        this.ambientSoundChance = -this.getMinAmbientSoundDelay();
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }

    public float getCooldown() { return this.dataTracker.get(COOLDOWN);}

    public void setCooldown(float cooldown) {
        this.dataTracker.set(COOLDOWN, cooldown);
    }

    public boolean isShooting() {
        return this.dataTracker.get(SHOOTING);
    }

    public void setSwinging(boolean swinging) {
        this.dataTracker.set(SWINGING, swinging);
    }

    public boolean isSwinging() {
        return this.dataTracker.get(SWINGING);
    }

    public void setShooting(boolean shooting) {
        this.dataTracker.set(SHOOTING, shooting);
    }

    public void setAttackName(String attackName) {
        this.dataTracker.set(ATTACK_NAME, attackName);
    }

    public String getAttackName() {
        return this.dataTracker.get(ATTACK_NAME);
    }
    public boolean shouldDespawnInPeaceful() {
        return this.getWorld().getDifficulty() == Difficulty.PEACEFUL;
    }
    public String getCurrentAnimation() { return this.dataTracker.get(CURRENT_ANIMATION); }
    public void setCurrentAnimation(String animation) {
        this.dataTracker.set(CURRENT_ANIMATION, animation);
    }
    public static final TrackedData<String> CURRENT_ANIMATION = DataTracker.registerData(DwarvenEntity.class, TrackedDataHandlerRegistry.STRING);

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SHOOTING, false);
        this.dataTracker.startTracking(SWINGING, false);
        this.dataTracker.startTracking(COOLDOWN, 0f);
        this.dataTracker.startTracking(DATA_ID_TYPE_VARIANT, 0);
        this.dataTracker.startTracking(ATTACK_NAME, "attack");
        this.dataTracker.startTracking(CURRENT_ANIMATION, "idle");
        this.dataTracker.startTracking(IS_MINING, false);
        this.dataTracker.startTracking(MINING_COOLDOWN, 200);
    }
    public static DefaultAttributeContainer.Builder setAttributes() {
        return HostileEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 20)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 35 + ModBonusHealthConfig.cyclopsBonusHealth)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3D)
                .add(EntityAttributes.GENERIC_ARMOR, 6f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6f)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.6f)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 1.5f);
    }

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);

        // Drop tout ce que l'araignée a miné
        for (ItemStack stack : minedResources) {
            this.dropStack(stack);
        }
        minedResources.clear();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        rand = random();
        if (rand < 0.5)
            return ModSounds.DWARVEN_SPIDER_INJURED_1;
        return ModSounds.DWARVEN_SPIDER_INJURED_1;
    }

    @Override
    protected SoundEvent getDeathSound() {
        rand = random();
        if (rand < 0.5)
            return ModSounds.DWARVEN_SPIDER_DEATH_1;
        return ModSounds.DWARVEN_SPIDER_DEATH_1;
    }
    @Override
    protected void playHurtSound(DamageSource source) {
        SoundEvent hurtSound = this.getHurtSound(source);
        if (hurtSound != null) {
            this.playSound(hurtSound, 0.45f, 1.0f); // 🔊 Réduction du volume à 30% (0.3f)
        }
    }

    protected EntityNavigation createNavigation(World world) {
        return new MobNavigation(this, world) {
            protected PathNodeNavigator createPathNodeNavigator(int range) {
                this.nodeMaker = new LandPathNodeMaker();
                this.nodeMaker.setCanEnterOpenDoors(true);
                return new PathNodeNavigator(this.nodeMaker, range) {
                    protected float getDistance(PathNode a, PathNode b) {
                        return a.getHorizontalDistance(b);
                    }
                };
            }
        };
    }
    @Override
    public int getMinAmbientSoundDelay() {
        return 240;
    }
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.DWARVEN_SPIDER_AMBIENT_1;
    }
    @Override
    public void playAmbientSound() {
        SoundEvent soundEvent = this.getAmbientSound();
        if (soundEvent != null) {
            this.playSound(soundEvent, 0.35f, 1f);
        }
    }

    /* VARIANTS */
    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("Variant", this.getTypeVariant());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.dataTracker.set(DATA_ID_TYPE_VARIANT, nbt.getInt("Variant"));
    }
    private static final TrackedData<Integer> DATA_ID_TYPE_VARIANT =
            DataTracker.registerData(DwarvenEntity.class, TrackedDataHandlerRegistry.INTEGER);


    public CyclopsVariant getVariant() {
        return CyclopsVariant.byId(this.getTypeVariant() & 255);
    }

    private int getTypeVariant() {
        return this.dataTracker.get(DATA_ID_TYPE_VARIANT);
    }

    public void setVariant(CyclopsVariant variant) {
        this.dataTracker.set(DATA_ID_TYPE_VARIANT, variant.getId() & 255);
    }
    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(ModSounds.DWARVEN_SPIDER_WALK, 0.15f, 1.0f);
    }
}
