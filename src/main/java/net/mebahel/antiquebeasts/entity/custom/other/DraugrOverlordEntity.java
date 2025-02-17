package net.mebahel.antiquebeasts.entity.custom.other;

import net.mebahel.antiquebeasts.entity.ai.CustomRevengeGoal;
import net.mebahel.antiquebeasts.entity.ai.other.DraugrOverlordMeleeAttackGoal;
import net.mebahel.antiquebeasts.entity.ai.other.DraugrOverlordSpecialAttackGoal;
import net.mebahel.antiquebeasts.entity.custom.egyptian.EgyptianEntity;
import net.mebahel.antiquebeasts.entity.custom.greek.GreekEntity;
import net.mebahel.antiquebeasts.entity.custom.norse.NorseEntity;
import net.mebahel.antiquebeasts.entity.variant.DraugrOverlordVariant;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.mebahel.antiquebeasts.util.config.ModBonusHealthConfig;
import net.mebahel.antiquebeasts.util.config.ModSpawnRateConfig;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
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
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
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

public class DraugrOverlordEntity extends DraugrEntity implements GeoEntity {
    private final ServerBossBar bossBar;
    public DraugrOverlordEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.ambientSoundChance = -this.getMinAmbientSoundDelay();
        this.bossBar = new ServerBossBar(
                Text.of("Draugr Overlord"),  // Titre affiché
                BossBar.Color.RED,          // Couleur rouge
                BossBar.Style.NOTCHED_10      // Style classique (progression)
        );
        this.bossBar.setPercent(1.0f);
    }
    public boolean shouldDespawn;
    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }
    double rand;
    public boolean shouldDespawnInPeaceful() {
        return this.getWorld().getDifficulty() == Difficulty.PEACEFUL;
    }

    public static final TrackedData<Integer> DATA_ID_TYPE_VARIANT =
            DataTracker.registerData(DraugrOverlordEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public static final TrackedData<Boolean> SWINGING = DataTracker.registerData(DraugrOverlordEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);

    public static final TrackedData<String> ATTACK_NAME = DataTracker.registerData(DraugrOverlordEntity.class,
            TrackedDataHandlerRegistry.STRING);

    public static final TrackedData<Boolean> HAS_SPAWNED = DataTracker.registerData(DraugrOverlordEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Integer> SPECIAL_COOLDOWN = DataTracker.registerData(DraugrOverlordEntity.class,
            TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Boolean> SPECIAL = DataTracker.registerData(DraugrOverlordEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);

    public boolean getSpecial() {return this.dataTracker.get(SPECIAL);}

    public void setSpecial(boolean bool) {
        this.dataTracker.set(SPECIAL, bool);
    }

    public Integer getSpecialCooldown() {return this.dataTracker.get(SPECIAL_COOLDOWN);}
    public void setSpecialCooldown(Integer integer) {
        this.dataTracker.set(SPECIAL_COOLDOWN, integer);
    }
    public boolean getHasSpawned() {return this.dataTracker.get(HAS_SPAWNED);}
    public void setHasSpawned(boolean bool) {
        this.dataTracker.set(HAS_SPAWNED, bool);
    }
    public void setSwinging(boolean swinging) { this.dataTracker.set(SWINGING, swinging); }
    public boolean isSwinging() { return this.dataTracker.get(SWINGING); }
    public void setAttackName(String attackName) { this.dataTracker.set(ATTACK_NAME, attackName); }
    public String getAttackName() { return this.dataTracker.get(ATTACK_NAME); }
    @Override
    public void playSound(SoundEvent sound, float volume, float pitch) {
        if (!this.isSilent()) {
            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), sound, this.getSoundCategory(), volume, 0.8f);
        }
    }
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SWINGING, false);
        this.dataTracker.startTracking(DATA_ID_TYPE_VARIANT, 0);
        this.dataTracker.startTracking(ATTACK_NAME, "attack");
        this.dataTracker.startTracking(HAS_SPAWNED, true);
        this.dataTracker.startTracking(SPECIAL, false);
        this.dataTracker.startTracking(SPECIAL_COOLDOWN, 120);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new DraugrOverlordSpecialAttackGoal(this, null));
        this.goalSelector.add(3, new DraugrOverlordMeleeAttackGoal(this, 1f, 25, 16));
        this.goalSelector.add(6, new WanderAroundFarGoal(this, 0.85f, 1f));
        this.goalSelector.add(7, new LookAroundGoal(this));

        this.targetSelector.add(1, new CustomRevengeGoal(this, DraugrEntity.class));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, VillagerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, IronGolemEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, RaiderEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, GreekEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, EgyptianEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, NorseEntity.class, true));
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return HostileEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 35)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3D)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 340.0D + ModBonusHealthConfig.draugrBonusHealth)
                .add(EntityAttributes.GENERIC_ARMOR, 10f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 8.0f)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.7f)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.5f);
    }
    private PlayState predicate(AnimationState animationState) {
        if (!this.getHasSpawned()) {
            return PlayState.STOP;
        } else if (animationState.isMoving() && !this.getSpecial() && !this.isSwinging()) {
            spawnSwordDraggingParticles();
            animationState.getController().setAnimation(RawAnimation.begin().then("walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        } else if (!animationState.isMoving() && !this.isAttacking() && !this.getSpecial()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        return PlayState.CONTINUE;
    }

    private PlayState attackPredicate(AnimationState state) {
        if (this.isSwinging() && !this.getSpecial() && state.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
            state.getController().forceAnimationReset();
            state.getController().setAnimation(RawAnimation.begin().then(this.getAttackName(), Animation.LoopType.PLAY_ONCE));
        } else if (this.getSpecial()) {
            return PlayState.STOP;
        }

        return PlayState.CONTINUE;
    }
    private PlayState specialPredicate(AnimationState state) {
        if (this.getSpecial() && state.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
            state.getController().forceAnimationReset();
            state.getController().setAnimation(RawAnimation.begin().then("attack3", Animation.LoopType.PLAY_ONCE));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController(this, "controller",0, this::predicate).setSoundKeyframeHandler(state -> {
            PlayerEntity player = ClientUtils.getClientPlayer();
            if (player != null && !this.getSpecial()) {
                this.getWorld().playSound(player, this.getX(), this.getY(), this.getZ(), ModSounds.DRAUGR_OVERLORD_STEP, this.getSoundCategory(),
                        0.85f, 1.2f);
            }
        }));
        controllers.add(new AnimationController(this, "attacking", 0, this::attackPredicate).setSoundKeyframeHandler(state -> {
            PlayerEntity player = ClientUtils.getClientPlayer();
            if (player != null)
                this.getWorld().playSound(player, this.getX(), this.getY(), this.getZ(), ModSounds.SWING, this.getSoundCategory(),
                        0.7f, 0.8f);
        }));
        controllers.add(new AnimationController(this, "spawning", 0, this::spawnPredicate).setSoundKeyframeHandler(state -> {
            PlayerEntity player = ClientUtils.getClientPlayer();
            if (player != null)
                this.getWorld().playSound(player, this.getX(), this.getY(), this.getZ(), ModSounds.MUMMY_SPAWN, this.getSoundCategory(), 0.65f, 1f);
        }));
        controllers.add(new AnimationController(this, "specialController",0, this::specialPredicate).setSoundKeyframeHandler(state -> {
            PlayerEntity player = ClientUtils.getClientPlayer();
            if (player != null)
                this.getWorld().playSound(player, this.getX(), this.getY(), this.getZ(), SoundEvents.BLOCK_ANVIL_LAND, this.getSoundCategory(), 0.65f, 0.7f);
        }));
    }

    public DraugrOverlordVariant getOverlordVariant() {
        return DraugrOverlordVariant.byId(this.getTypeVariant() & 255);
    }

    public int getTypeVariant() {
        return this.dataTracker.get(DATA_ID_TYPE_VARIANT);
    }

    private void setVariant(DraugrOverlordVariant variant) {
        this.dataTracker.set(DATA_ID_TYPE_VARIANT, variant.getId() & 255);
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty,
                                 SpawnReason spawnReason, @Nullable EntityData entityData,
                                 @Nullable NbtCompound entityNbt) {
        if (spawnReason != SpawnReason.SPAWN_EGG && spawnReason != SpawnReason.COMMAND && spawnReason != SpawnReason.SPAWNER
                && spawnReason != SpawnReason.EVENT ) {
            int randomValue = this.random.nextInt(10);
            if (randomValue >= ModSpawnRateConfig.draugrSpawnRate) {
                this.shouldDespawn = true;
            }
        }

        DraugrOverlordVariant variant = DraugrOverlordVariant.GREATSWORD;

        setVariant(variant);
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
            this.playSound(soundEvent, 0.8f, 1f);
        }
    }

    public int getMinAmbientSoundDelay() {
        return 240;
    }

    public boolean damage(DamageSource source, float amount) {
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

        float healthPercent = this.getHealth() / this.getMaxHealth();
        this.bossBar.setPercent(healthPercent);
        if (this.getSpecial() && this.getSpecialCooldown() < 16 && this.getSpecialCooldown() > 8) {
            spawnShockwaveParticles();
        }

        // Vérifie si le boss doit être retiré (évite un affichage persistant)
        if (this.isDead() || this.isRemoved()) {
            this.bossBar.clearPlayers();
        }
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);
    }
    private PlayState spawnPredicate(AnimationState state) {
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
        // Position de l'entité
        double posX = this.getX();
        double posY = this.getY() - 0.1; // Légèrement sous les pieds
        double posZ = this.getZ();

        // Récupérer la position du bloc sous l'entité
        BlockPos blockPos = new BlockPos((int) posX, (int) (this.getY() - 0.5), (int) posZ); // Bloc sous l'entité
        BlockState blockState = this.getWorld().getBlockState(blockPos);

        // Si le bloc n'est pas de l'air, générer les particules
        if (!blockState.isAir()) {
            // Particules basées sur le bloc sous l'entité
            for (int i = 0; i < 3; i++) { // Nombre de particules
                double offsetX = (this.random.nextDouble() - 0.5) * 0.1; // Dispersion légère en X
                double offsetZ = (this.random.nextDouble() - 0.5) * 0.1; // Dispersion légère en Z
                double velocityY = 0.1; // Légère vélocité verticale (comme de la poussière)

                // Générer des particules basées sur le bloc
                this.getWorld().addParticle(
                        new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState), // Particules basées sur le bloc
                        posX + offsetX, posY, posZ + offsetZ, // Position des particules
                        0.0, velocityY, 0.0 // Vélocité des particules
                );
            }
        }
    }
    public void spawnSwordDraggingParticles() {
        // ✅ Calcul de la rotation du mob
        double yawRad = Math.toRadians(-this.bodyYaw); // Yaw de l'entité (en radians)
        double cosYaw = Math.cos(yawRad);
        double sinYaw = Math.sin(yawRad);

        // ✅ Décalages derrière et à droite
        double offsetBackX = -sinYaw; // 1 bloc derrière sur X
        double offsetBackZ = -cosYaw; // 1 bloc derrière sur Z
        double offsetRightX = -cosYaw; // 1 bloc à droite sur X
        double offsetRightZ = sinYaw; // 1 bloc à droite sur Z

        // ✅ Position finale des particules (un bloc derrière et un bloc à droite)
        double particleX = this.getX() + offsetBackX + offsetRightX;
        double particleZ = this.getZ() + offsetBackZ + offsetRightZ;
        double particleY = this.getY(); // Hauteur inchangée (les pieds du mob)

        // ✅ Vérification du bloc sous la position calculée
        BlockPos blockPos = new BlockPos((int) particleX, (int) (particleY - 1), (int) particleZ);
        BlockState blockState = this.getWorld().getBlockState(blockPos);

        if (blockState.isAir()) {
            return;
        }

        // ✅ Générer les particules
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
    @Override
    public void onStartedTrackingBy(ServerPlayerEntity player) {
        super.onStartedTrackingBy(player);
        this.bossBar.addPlayer(player); // Ajoute le joueur à la barre de boss
    }

    @Override
    public void onStoppedTrackingBy(ServerPlayerEntity player) {
        super.onStoppedTrackingBy(player);
        this.bossBar.removePlayer(player); // Retire le joueur de la barre de boss
    }
    public void spawnShockwaveParticles() {
        if (!this.getSpecial()) return; // ❌ Ne rien faire si l'attaque spéciale n'est pas active

        World world = this.getWorld();
        BlockPos entityPos = this.getBlockPos();

        for (int radius = 1; radius <= 8; radius++) { // Augmente progressivement le rayon de l'onde
            int particleCount = (int) (radius * 8); // Plus le rayon est grand, plus il y a de particules

            for (int i = 0; i < particleCount; i++) {
                double angle = (2 * Math.PI / particleCount) * i; // Positionner les particules en cercle
                double xOffset = Math.cos(angle) * radius;
                double zOffset = Math.sin(angle) * radius;

                BlockPos particlePos = entityPos.add((int) xOffset, 0, (int) zOffset); // Générer sur le sol
                BlockState blockState = world.getBlockState(particlePos);

                if (!blockState.isAir()) {
                    world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState),
                            particlePos.getX() + 0.5, particlePos.getY() + 0.1, particlePos.getZ() + 0.5,
                            0.0, 0.1, 0.0);
                }
            }
        }
    }
}
