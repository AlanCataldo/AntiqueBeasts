package net.mebahel.antiquebeasts.entity.custom.other;

import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags;
import net.mebahel.antiquebeasts.entity.ai.other.DraugrMeleeAttackGoal;
import net.mebahel.antiquebeasts.entity.custom.egyptian.EgyptianEntity;
import net.mebahel.antiquebeasts.entity.custom.greek.GreekEntity;
import net.mebahel.antiquebeasts.entity.custom.norse.NorseEntity;
import net.mebahel.antiquebeasts.entity.variant.DraugrVariant;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.mebahel.antiquebeasts.util.ModConfig;
import net.mebahel.antiquebeasts.util.ModSoundUtil;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.ClientUtils;

import javax.annotation.Nullable;

import static java.lang.Math.random;

public class DraugrEntity extends HostileEntity implements GeoEntity {
    public DraugrEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.ambientSoundChance = -this.getMinAmbientSoundDelay();
    }
    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }
    double rand;

    public static final TrackedData<Integer> DATA_ID_TYPE_VARIANT =
            DataTracker.registerData(DraugrEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public static final TrackedData<Boolean> SWINGING = DataTracker.registerData(DraugrEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);

    public static final TrackedData<String> ATTACK_NAME = DataTracker.registerData(DraugrEntity.class,
            TrackedDataHandlerRegistry.STRING);

    public void setSwinging(boolean swinging) { this.dataTracker.set(SWINGING, swinging); }
    public boolean isSwinging() { return this.dataTracker.get(SWINGING); }
    public void setAttackName(String attackName) { this.dataTracker.set(ATTACK_NAME, attackName); }
    public String getAttackName() { return this.dataTracker.get(ATTACK_NAME); }

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SWINGING, false);
        this.dataTracker.startTracking(DATA_ID_TYPE_VARIANT, 0);
        this.dataTracker.startTracking(ATTACK_NAME, "attack");
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new DraugrMeleeAttackGoal(this, 0.4f, 21, 9));
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 0.35f, 1f));
        this.goalSelector.add(4, new LookAroundGoal(this));

        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, VillagerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, IronGolemEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, PillagerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, GreekEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, EgyptianEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, NorseEntity.class, true));
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return HostileEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 35)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.72f)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0D + ModConfig.infantryBonusHealth)
                .add(EntityAttributes.GENERIC_ARMOR, 6f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0f)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.2f)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.5f);
    }
    private PlayState predicate(AnimationState animationState) {
        if (animationState.isMoving()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("transition_walk", Animation.LoopType.PLAY_ONCE).then("walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        } else if (!animationState.isMoving() && !this.isAttacking()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
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
        controllers.add(new AnimationController(this, "controller",0, this::predicate));
        controllers.add(new AnimationController(this, "attacking", 0, this::attackPredicate).setSoundKeyframeHandler(state -> {
            PlayerEntity player = ClientUtils.getClientPlayer();
            if (player != null)
                this.getWorld().playSound(player, this.getX(), this.getY(), this.getZ(), ModSounds.SWING, this.getSoundCategory(), 0.7f, 1.1f);
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

        ModSoundUtil.InfantryPlaySound(spawnReason, this);
        this.setTarget(null);

        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        rand = random();
        if (rand < 0.5)
            return ModSounds.DRAUGR_HURT_1;
        else
            return ModSounds.DRAUGR_HURT_2;
    }
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.DRAUGR_DEATH_1;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        rand = random();
        if (rand < 0.3)
            return ModSounds.DRAUGR_AMBIENT_1;
        else if (rand > 0.3 && rand < 0.6)
            return ModSounds.DRAUGR_AMBIENT_2;
        else
            return ModSounds.DRAUGR_AMBIENT_3;
    }
    @Override
    public void playAmbientSound() {
        SoundEvent soundEvent = this.getAmbientSound();
        if (soundEvent != null) {
            this.playSound(soundEvent, 0.4f, 1f);
        }
    }

    public int getMinAmbientSoundDelay() {
        return 240;
    }
}
