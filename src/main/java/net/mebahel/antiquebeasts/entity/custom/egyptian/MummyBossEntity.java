package net.mebahel.antiquebeasts.entity.custom.egyptian;

import net.mebahel.antiquebeasts.entity.ai.mummy_boss.MummyBossMeleeAttackGoal;
import net.mebahel.antiquebeasts.entity.ai.mummy_boss.MummyBossShootingGoal;
import net.mebahel.antiquebeasts.entity.ai.mummy_boss.MummyBossSummonGoal;
import net.mebahel.antiquebeasts.entity.ai.util.GroupRevengeGoal;
import net.mebahel.antiquebeasts.entity.custom.dwemer.DwemerEntity;
import net.mebahel.antiquebeasts.entity.custom.greek.GreekEntity;
import net.mebahel.antiquebeasts.entity.custom.norse.NorseEntity;
import net.mebahel.antiquebeasts.entity.custom.other.DraugrEntity;
import net.mebahel.antiquebeasts.entity.projectiles.MummyProjectileEntity;
import net.mebahel.antiquebeasts.entity.variant.EgyptiantVariant;
import net.mebahel.antiquebeasts.particle.ModParticles;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.mebahel.antiquebeasts.util.config.ModBonusHealthConfig;
import net.minecraft.entity.*;
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
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.ClientUtils;

import java.util.List;
import java.util.Objects;

import org.jetbrains.annotations.Nullable;

public class MummyBossEntity extends EgyptianEntity implements GeoEntity {
    public MummyBossEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
        this.bossBar = new ServerBossBar(Text.of("Mummy"), BossBar.Color.YELLOW, BossBar.Style.PROGRESS);
        this.bossBar.setPercent(this.getHealth() / this.getMaxHealth());
        this.bossBar.setPercent(this.getHealth() / this.getMaxHealth());
        this.ambientSoundChance = -this.getMinAmbientSoundDelay();
    }
    @Nullable private String bossNameKey;
    @Nullable private String bossSuffixKey;
    private final ServerBossBar bossBar;
    public boolean secondPhase = false;
    public boolean thirdPhase = false;
    private boolean playedTransitionAnimation = false;
    public boolean inTransitionPhase = false;
    private int secondPhaseTransitionDelay = -1;
    private int thirdPhaseTransitionDelay = -1;
    private String bossBarName;
    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }
    public static final TrackedData<Integer> SPAWN_CD = DataTracker.registerData(MummyBossEntity.class,
            TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Boolean> SPAWN = DataTracker.registerData(MummyBossEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> HAS_SPAWNED = DataTracker.registerData(MummyBossEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);

    public static final TrackedData<Integer> COOLDOWN = DataTracker.registerData(MummyBossEntity.class,
            TrackedDataHandlerRegistry.INTEGER);

    public static final TrackedData<Boolean> SHOOTING = DataTracker.registerData(MummyBossEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);
    public void setShooting(boolean shooting) {
        this.dataTracker.set(SHOOTING, shooting);
    }
    public int getCooldown() { return this.dataTracker.get(COOLDOWN);}
    public void setCooldown(int cooldown) {
        this.dataTracker.set(COOLDOWN, cooldown);
    }
    public boolean isShooting() {
        return this.dataTracker.get(SHOOTING);
    }
    public int getSpawnCooldown() {return this.dataTracker.get(SPAWN_CD);}
    public void setSpawnCooldown(int cooldown) { this.dataTracker.set(SPAWN_CD, cooldown);}
    public boolean getSpawn() {return this.dataTracker.get(SPAWN);}
    public void setSpawn(boolean bool) {
        this.dataTracker.set(SPAWN, bool);
    }
    public boolean getHasSpawned() {return this.dataTracker.get(HAS_SPAWNED);}
    public void setHasSpawned(boolean bool) {
        this.dataTracker.set(HAS_SPAWNED, bool);
    }
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SWINGING, false);
        this.dataTracker.startTracking(SHOOTING, false);
        this.dataTracker.startTracking(COOLDOWN, 0);
        this.dataTracker.startTracking(DATA_ID_TYPE_VARIANT, 0);
        this.dataTracker.startTracking(IS_IN_CARAVAN, false);
        this.dataTracker.startTracking(ATTACK_NAME, "attack");
        this.dataTracker.startTracking(SPAWN_CD, 120);
        this.dataTracker.startTracking(SPAWN, false);
        this.dataTracker.startTracking(HAS_SPAWNED, false);
        this.dataTracker.startTracking(PATROL_UUID, "");
    }
    public static DefaultAttributeContainer.Builder setAttributes() {
        return HostileEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 35)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.55f)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 300D + ModBonusHealthConfig.mummifiedPharaohBonusHealth)
                .add(EntityAttributes.GENERIC_ARMOR, 8f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 10.0f)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.2f)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.5f);
    }
    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new MummyBossSummonGoal(this, 0.51f));
        this.goalSelector.add(3, new MummyBossShootingGoal(this, 0.51f));
        this.goalSelector.add(4, new MummyBossMeleeAttackGoal(this, 0.51f, 6f, 1, 10));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.45f, 1f));
        this.goalSelector.add(6, new LookAroundGoal(this));

        this.targetSelector.add(1, (new GroupRevengeGoal(this, EgyptianEntity.class))
                .setGroupRevenge(EgyptianEntity.class));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, DraugrEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, DwemerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, VillagerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, IronGolemEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, RaiderEntity.class, true));
        this.targetSelector.add(5, new ActiveTargetGoal<>(this, GreekEntity.class, true));
        this.targetSelector.add(5, new ActiveTargetGoal<>(this, NorseEntity.class, true));
    }
    private PlayState predicate(AnimationState animationState) {
        String walkAnimation = "walk";
        String idleAnimation = "idle";

        // Adjust animations for second phase
        if (this.secondPhase) {
            walkAnimation = "second_phase_walk";
            idleAnimation = "second_phase_walk";
        }

        // Adjust animations for third phase
        if (this.thirdPhase) {
            walkAnimation = "second_phase_walk";
            idleAnimation = "second_phase_walk";
        }

        // Handle second-phase transition animation
        if (this.secondPhase && !playedTransitionAnimation) {
            animationState.getController().forceAnimationReset();
            animationState.getController().setAnimation(RawAnimation.begin().then("transition_second_phase", Animation.LoopType.PLAY_ONCE));
            playedTransitionAnimation = true;  // Play transition animation only once
            inTransitionPhase = true;  // Block other animations during transition
            return PlayState.CONTINUE;
        }

        // Handle third-phase transition animation
        if (this.thirdPhase && !playedTransitionAnimation) {
            animationState.getController().forceAnimationReset();
            animationState.getController().setAnimation(RawAnimation.begin().then("transition_third_phase", Animation.LoopType.PLAY_ONCE));
            playedTransitionAnimation = true;  // Only play transition animation once
            inTransitionPhase = true;  // Block other animations during transition
            return PlayState.CONTINUE;
        }

        // Wait for the transition phase to finish before resuming normal animations
        if (inTransitionPhase && animationState.getController().getAnimationState() != AnimationController.State.STOPPED) {
            return PlayState.CONTINUE;  // Wait for transition animation to complete
        } else if (inTransitionPhase && animationState.getController().getAnimationState() == AnimationController.State.STOPPED) {
            inTransitionPhase = false;  // Transition finished, resume normal animations
        }

        // Handle walking and idle animations based on the phase
        if (animationState.isMoving()) {
            animationState.getController().setAnimation(RawAnimation.begin().then(walkAnimation, Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        animationState.getController().setAnimation(RawAnimation.begin().then(idleAnimation, Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }


    private PlayState attackPredicate(AnimationState state) {
        String attackAnimation = "attack";

        if (this.secondPhase || this.thirdPhase) {
            attackAnimation = "second_phase_attack";
        }
        if (this.isSwinging() && state.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
            state.getController().forceAnimationReset();
            state.getController().setAnimation(RawAnimation.begin().then(attackAnimation, Animation.LoopType.PLAY_ONCE));
        }

        return PlayState.CONTINUE;
    }

    private PlayState raisePredicate(AnimationState state) {
        String raiseAnimation = "raise";

        if (this.secondPhase|| this.thirdPhase) {
            raiseAnimation = "second_phase_raise";
        }
        if (this.getSpawn() && state.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
            state.getController().forceAnimationReset();
            state.getController().setAnimation(RawAnimation.begin().then(raiseAnimation, Animation.LoopType.PLAY_ONCE));
        }
        return PlayState.CONTINUE;
    }
    private PlayState spawnPredicate(AnimationState state) {
        if (!this.getHasSpawned()) {
            state.getController().forceAnimationReset();
            state.getController().setAnimation(RawAnimation.begin().then("spawn", Animation.LoopType.PLAY_ONCE));
            this.setHasSpawned(true);
        }
        return PlayState.CONTINUE;
    }

    private PlayState shootingPredicate(AnimationState state) {
        String shootAnimation = "shoot";

        if (this.secondPhase) {
            shootAnimation = "second_phase_shoot";
        }
        if(this.isShooting() && !this.isSwinging() && state.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
            state.getController().forceAnimationReset();
            state.getController().setAnimation(RawAnimation.begin().then(shootAnimation, Animation.LoopType.PLAY_ONCE));
        }

        return PlayState.CONTINUE;
    }
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController(this, "controller",0, this::predicate));
        controllers.add(new AnimationController(this, "attacking", 0, this::attackPredicate).setSoundKeyframeHandler(state -> {
            PlayerEntity player = ClientUtils.getClientPlayer();
            if (player != null)
                this.getWorld().playSound(player, this.getX(), this.getY(), this.getZ(), ModSounds.SWING, this.getSoundCategory(), 0.5f, 0.85f);
        }));
        controllers.add(new AnimationController(this, "spawning", 0, this::spawnPredicate).setSoundKeyframeHandler(state -> {
            PlayerEntity player = ClientUtils.getClientPlayer();
            if (player != null)
                this.getWorld().playSound(player, this.getX(), this.getY(), this.getZ(), ModSounds.MUMMY_SPAWN, this.getSoundCategory(), 0.65f, 1f);
        }));
        controllers.add(new AnimationController(this, "raise", 0, this::raisePredicate).setSoundKeyframeHandler(state -> {
            PlayerEntity player = ClientUtils.getClientPlayer();
            if (player != null)
                this.getWorld().playSound(player, this.getX(), this.getY(), this.getZ(), ModSounds.MUMMY_RAISE, this.getSoundCategory(), 0.65f, 1f);
        }));
        controllers.add(new AnimationController(this, "shooting", 0, this::shootingPredicate).setSoundKeyframeHandler(state -> {
            PlayerEntity player = ClientUtils.getClientPlayer();
            if (player != null)
                this.getWorld().playSound(player, this.getX(), this.getY(), this.getZ(), ModSounds.MUMMY_SHOOT, this.getSoundCategory(), 0.65f, 1f);
        }));
    }

    @Override
    public void tick() {
        super.tick();
        this.bossBar.setPercent(this.getHealth() / this.getMaxHealth());

        if (this.age % 100 == 0) {
            applyMiningFatigue();
        }

        if (!this.getWorld().isClient()) {
            this.updateBossBar();
            this.checkPlayerDeaths();
        }

        if (!this.secondPhase && this.bossBar.getPercent() <= 0.66 && secondPhaseTransitionDelay == -1) {
            secondPhaseTransitionDelay = 20;
            this.secondPhase = true;
            this.playedTransitionAnimation = false;// 1.5 seconds delay (30 ticks)
        }

        // Handle second-phase transition delay countdown
        if (secondPhaseTransitionDelay > 0) {
            secondPhaseTransitionDelay--;
            if (secondPhaseTransitionDelay == 0) {
                this.enterSecondPhase();  // Enter the second phase after the delay
            }
        }

        // Trigger third phase with delay
        if (!this.thirdPhase && this.bossBar.getPercent() <= 0.31) {
            if (thirdPhaseTransitionDelay == -1) {
                thirdPhaseTransitionDelay = 20;
                this.thirdPhase = true;
                this.playedTransitionAnimation = false;
            }
        }

        // Handle third-phase transition delay countdown
        if (thirdPhaseTransitionDelay > 0) {
            thirdPhaseTransitionDelay--;
            if (thirdPhaseTransitionDelay == 0) {
                this.enterThirdPhase();  // Enter the third phase after the delay
            }
        }

        if (this.secondPhase && this.getWorld().isClient()) {
            spawnHoveringParticles(0.7, 2.2);
        }

        if (this.thirdPhase && this.getWorld().isClient()) {
            spawnHoveringParticles(0.5, 2);
            spawnHoveringParticles(1, 2.5);
            spawnHoveringParticles(1.5, 3);
            spawnHoveringParticles(2, 3.5);
            spawnHoveringParticles(2.5, 3.5);
            spawnHoveringParticles(3, 3.5);
            spawnHoveringParticles(3.5, 3);
            spawnHoveringParticles(4, 2.5);
            spawnHoveringParticles(4.5, 2);
        }
        if (this.thirdPhase) {
            reflectProjectiles();
        }
        if (this.thirdPhase && this.age % 20 == 0) {  // Check every 20 ticks (1 second)
            applySlownessEffectToNearbyEntities();
        }

        if (this.age < 40 || this.getSpawn() || this.isShooting()) {
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(0);
        } else if (Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).getValue() == 0
                && !this.getSpawn() && !this.isShooting()) {
            Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(0.57f);
        }
    }


    private void enterThirdPhase() {
        this.secondPhase = false;
        this.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 1600, 1, false, false,true));
        this.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 3200, 2, false, false, true));
        this.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 4800, 0,false, false, true));

        // Play sound and apply any effects specific to the third phase
        if (this.getWorld().isClient) {
            PlayerEntity player = ClientUtils.getClientPlayer();
            if (player != null) {
                this.getWorld().playSound(player, this.getX(), this.getY(), this.getZ(),
                        ModSounds.MUMMY_BOSS_SECOND_PHASE, this.getSoundCategory(), 0.85f, 1f);
            }
        }
        // Additional logic such as pushing entities away or spawning effects can be added here
        repelNearbyEntities(1.8); // Optional: reuse this for third phase, or customize for third phase
    }

    private void applySlownessEffectToNearbyEntities() {
        // Create a 4-block bounding box around the Mummy Boss
        Box area = new Box(this.getBlockPos()).expand(4);

        // Get all LivingEntities within the area except the Mummy Boss
        List<LivingEntity> nearbyEntities = this.getWorld().getEntitiesByClass(LivingEntity.class, area, entity -> entity != this);

        // Apply Slowness II for 40 ticks (2 seconds) to all nearby entities
        for (LivingEntity entity : nearbyEntities) {
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 40, 1, false, false, false));
        }
    }

    private void reflectProjectiles() {
        // Define a bounding box (5 blocks radius) around the mummy boss
        Box detectionBox = new Box(this.getBlockPos()).expand(4);

        // Find all entities in this area that are projectiles
        List<ProjectileEntity> projectiles = this.getWorld().getEntitiesByClass(ProjectileEntity.class, detectionBox, projectile -> projectile != null && !projectile.isRemoved());

        // Iterate over the detected projectiles and reflect them
        for (ProjectileEntity projectile : projectiles) {
            // Skip reflecting if the projectile is a MummyProjectileEntity
            if (projectile instanceof MummyProjectileEntity) {
                continue; // Do not reflect this projectile
            }
            reflectProjectile(projectile); // Reflect other projectiles
        }
    }
    private void reflectProjectile(ProjectileEntity projectile) {
        // Get the direction vector from the projectile to the Mummy Boss
        Vec3d mummyPosition = this.getPos();
        Vec3d projectilePosition = projectile.getPos();
        Vec3d direction = projectilePosition.subtract(mummyPosition).normalize();

        // Set the projectile's velocity to reflect it
        projectile.setVelocity(direction.x, direction.y / 2, direction.z);

        // Optional: Add some effects or sounds for visual feedback
    }

    private void spawnHoveringParticles(double high, double dispersion) {
        // Position de la Mummy
        double posX = this.getX();
        double posY = this.getY() + high; // Particules légèrement sous la momie
        double posZ = this.getZ();

        // Générer les particules de manière aléatoire autour de la Mummy
        for (int i = 0; i < 3; i++) { // Ajustez le nombre de particules
            double offsetX = (this.random.nextDouble() - 0.5) * dispersion; // Plus de dispersion en X
            double offsetZ = (this.random.nextDouble() - 0.5) * dispersion; // Plus de dispersion en Z

            // Ajouter les particules sous la Mummy avec une vitesse descendante lente
            this.getWorld().addParticle(ModParticles.MUMMY_HOVERING_PARTICLE,
                    posX + offsetX, posY, posZ + offsetZ, // Position (légèrement sous la momie)
                    0.0, -0.02, 0.0); // Vitesse lente, descendante (-0.02 en Y pour simuler la descente)
        }
    }


    private void applyMiningFatigue() {
        Box area = new Box(this.getBlockPos()).expand(150);

        // Appliquer l'effet à tous les joueurs dans la zone
        for (PlayerEntity player : this.getWorld().getEntitiesByClass(PlayerEntity.class, area, playerEntity -> true)) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 600, 2,
                    false, false, false));
        }
    }

    private void enterSecondPhase() {
        this.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 1200, 0));
        this.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 2400, 1));
        this.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 3600));

        // Exécuter uniquement sur le client
        if (this.getWorld().isClient) {
            PlayerEntity player = ClientUtils.getClientPlayer();
            if (player != null) {
                this.getWorld().playSound(player, this.getX(), this.getY(), this.getZ(),
                        ModSounds.MUMMY_BOSS_SECOND_PHASE, this.getSoundCategory(), 0.85f, 1f);
            }
        }
        repelNearbyEntities(1.5);
    }

    private void repelNearbyEntities(double force) {
        // Créer une boîte autour de la position actuelle de la momie avec un rayon de 10 blocs
        Box area = new Box(this.getBlockPos()).expand(10);

        // Récupérer toutes les entités dans cette zone, à l'exception de la momie elle-même
        for (LivingEntity entity : this.getWorld().getEntitiesByClass(LivingEntity.class, area, e -> e != this)) {
            // Calculer la direction du repoussement en fonction de la position de l'entité par rapport à la momie
            double dx = entity.getX() - this.getX();
            double dz = entity.getZ() - this.getZ();
            double distance = Math.sqrt(dx * dx + dz * dz);

            entity.addVelocity(dx / distance * force, 0.5, dz / distance * force); // Ajouter une poussée verticale également

            // Met à jour la vitesse de l'entité côté serveur
            entity.velocityModified = true;
        }
    }

    private void checkPlayerDeaths() {
        for (PlayerEntity player : this.getWorld().getPlayers()) {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                if (serverPlayer.isDead()) {
                    this.bossBar.removePlayer(serverPlayer);  // Retirer le joueur mort de la bossBar
                }
            }
        }
    }

    @Override
    public void onRemoved() {
        super.onRemoved();

        // Retirer tous les joueurs de la boss bar quand l'entité est déchargée
        this.bossBar.clearPlayers();
    }

    private void updateBossBar() {
        // Créer une boîte autour du boss avec un rayon de 50 blocs pour détecter les joueurs
        Box detectionBox = new Box(this.getBlockPos()).expand(50);

        // Parcours tous les joueurs du monde
        for (PlayerEntity player : this.getWorld().getPlayers()) {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                // Vérifie si le joueur est dans la zone d'affichage (50 blocs)
                if (detectionBox.contains(player.getPos())) {
                    // Si le joueur est dans la zone et n'est pas déjà dans la bossBar, l'ajoute
                    if (!this.bossBar.getPlayers().contains(serverPlayer)) {
                        this.bossBar.addPlayer(serverPlayer);
                    }
                } else {
                    // Si le joueur est en dehors de la zone, le retire de la bossBar
                    if (this.bossBar.getPlayers().contains(serverPlayer)) {
                        this.bossBar.removePlayer(serverPlayer);
                    }
                }
            }
        }
    }
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        rand = this.random.nextDouble();
        if (rand < 0.5)
            return ModSounds.MUMMY_HURT_1;
        else
            return ModSounds.MUMMY_HURT_2;
    }

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);
        this.bossBar.setVisible(false);
        this.bossBar.clearPlayers();

        // Donner l'XP équivalente à 30 zombies (entre 150 et 210 XP)
        int xpToDrop = 240 + this.random.nextInt(61);  // Aléatoire entre 150 et 210

        // S'assurer que le monde est du côté serveur
        if (!this.getWorld().isClient) {
            // Créer et faire apparaître des orbes d'expérience
            while (xpToDrop > 0) {
                int xpAmount = ExperienceOrbEntity.roundToOrbSize(xpToDrop);
                xpToDrop -= xpAmount;
                this.getWorld().spawnEntity(new ExperienceOrbEntity(this.getWorld(), this.getX(), this.getY(), this.getZ(), xpAmount));
            }
        }
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source.isOf(DamageTypes.IN_FIRE) || source.isOf(DamageTypes.ON_FIRE)
                || source.isOf(DamageTypes.LAVA)) {
            amount *= 2;
        }
        return super.damage(source, amount);
    }
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.MUMMY_DEATH;
    }
    @Override
    protected SoundEvent getAmbientSound() {
        rand = this.random.nextDouble();
        if (rand < 0.5)
            return ModSounds.MUMMY_AMBIENT_1;
        else
            return ModSounds.MUMMY_AMBIENT_2;
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty,
                                 SpawnReason spawnReason, @Nullable EntityData entityData,
                                 @Nullable NbtCompound entityNbt) {
        EgyptiantVariant variant = Util.getRandom(EgyptiantVariant.values(), this.random);
        setVariant(variant);

        if (this.bossNameKey == null || this.bossNameKey.isEmpty()) {

            this.bossNameKey = NAME_KEYS[this.random.nextInt(NAME_KEYS.length)];
            this.bossSuffixKey = SUFFIX_KEYS[this.random.nextInt(SUFFIX_KEYS.length)];
            this.bossBar.setName(buildBossNameText());

            this.bossBar.setName(buildBossNameText());
        }

        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }


    public EgyptiantVariant getVariant() {
        return EgyptiantVariant.byId(this.getTypeVariant() & 255);
    }
    public void setVariant(EgyptiantVariant variant) {
        this.dataTracker.set(DATA_ID_TYPE_VARIANT, variant.getId() & 255);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);

        nbt.putBoolean("HasSpawned", this.getHasSpawned());

        if (this.bossNameKey != null) nbt.putString("BossNameKey", this.bossNameKey);
        if (this.bossSuffixKey != null) nbt.putString("BossSuffixKey", this.bossSuffixKey);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);

        this.setHasSpawned(nbt.getBoolean("HasSpawned"));

        if (nbt.contains("BossNameKey")) this.bossNameKey = nbt.getString("BossNameKey");
        if (nbt.contains("BossSuffixKey")) this.bossSuffixKey = nbt.getString("BossSuffixKey");

        // Re-applique le texte au bossbar
        this.bossBar.setName(buildBossNameText());
    }

    private static final String[] NAME_KEYS = {
            "boss.name.khendjer",
            "boss.name.djedkare",
            "boss.name.seti",
            "boss.name.raneferef",
            "boss.name.hatshepsut",
            "boss.name.taharka",
            "boss.name.sheshonq",
            "boss.name.sobekhotep",
            "boss.name.hakor",
            "boss.name.menes",
            "boss.name.thoutmosis"
    };

    private static final String[] SUFFIX_KEYS = {
            "boss.suffix.none",
            "boss.suffix.2",
            "boss.suffix.3",
            "boss.suffix.4",
            "boss.suffix.5"
    };

    private Text buildBossNameText() {
        if (this.bossNameKey == null || this.bossNameKey.isEmpty()) {
            return Text.of("Mummy"); // fallback
        }

        Text base = Text.translatable(this.bossNameKey);

        if (this.bossSuffixKey == null
                || this.bossSuffixKey.isEmpty()
                || "boss.suffix.none".equals(this.bossSuffixKey)) {
            return base;
        }

        return base.copy().append(" ").append(Text.translatable(this.bossSuffixKey));
    }
}
