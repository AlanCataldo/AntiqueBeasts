package net.mebahel.antiquebeasts.entity.custom;

import net.mebahel.antiquebeasts.entity.ModEntities;
import net.mebahel.antiquebeasts.entity.variant.PegasusVariant;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.mebahel.antiquebeasts.util.config.ModBonusHealthConfig;
import net.mebahel.antiquebeasts.util.config.ModConfig;
import net.mebahel.antiquebeasts.util.config.ModSpawnRateConfig;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.EntityView;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.ClientUtils;

import java.util.Objects;

public class PegasusEntity extends HorseEntity implements GeoEntity {

    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);
    private boolean isGliding = false;
    private boolean jumping = false;
    private boolean wasJumpPressed = false;
    private int ticksSinceJump = 0;
    private int transitionTicks = 0;
    private int transitionFromGroundTicks = 0;
    private boolean transitioningToGround = false;
    private boolean transitioningToFly = false;

    private int transition = 0;
    private float entityScale = 1.0F;
    public static final TrackedData<Float> SCALE_TRACKER = DataTracker.registerData(PegasusEntity.class, TrackedDataHandlerRegistry.FLOAT);
    public float getScale() {
        return entityScale;
    }

    public void setScale(float scale) {
        this.entityScale = scale;
    }

    public PegasusEntity(EntityType<? extends HorseEntity> entityType, World world) {
        super(entityType, world);
        this.setVariant(PegasusVariant.DEFAULT);
    }

    protected void initGoals() {
        this.goalSelector.add(1, new EscapeDangerGoal(this, 1.2));
        this.goalSelector.add(1, new HorseBondWithPlayerGoal(this, 1.2));
        this.goalSelector.add(2, new AnimalMateGoal(this, 1.0, AbstractHorseEntity.class));
        this.goalSelector.add(4, new FollowParentGoal(this, 1.0));
        this.goalSelector.add(6, new WanderAroundFarGoal(this, 1));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
        if (this.shouldAmbientStand()) {
            this.goalSelector.add(9, new AmbientStandGoal(this));
        }
        this.initCustomGoals();
    }

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(DATA_ID_TYPE_VARIANT, 0);
        this.dataTracker.startTracking(SCALE_TRACKER, 1f);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        Random random = Random.create();

        double baseSpeed = 1;
        double baseHealth = 40.0;
        double baseJumpStrength = 1;
        double minMultiplier = 1.25;
        double maxMultiplier = 1.5;
        double multiplier = minMultiplier + (maxMultiplier - minMultiplier) * random.nextDouble();
        double finalSpeed = baseSpeed * multiplier;
        double finalHealth = baseHealth * multiplier;
        double finalJumpStrength = baseJumpStrength * multiplier;

        return HorseEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 0.8)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, finalSpeed)
                .add(EntityAttributes.HORSE_JUMP_STRENGTH, finalJumpStrength)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, finalHealth + ModBonusHealthConfig.pegasusBonusHealth)
                .add(EntityAttributes.GENERIC_ARMOR, 6f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 7.5f)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.6f)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.65f);
    }

    private PlayState predicate(AnimationState animationState) {
        if (transitioningToGround) {
            animationState.getController().setAnimation(RawAnimation.begin().then("fly_to_ground", Animation.LoopType.PLAY_ONCE));
            if (++transitionTicks > 20) { // Limite de temps pour l'animation de transition
                transitioningToGround = false;
                transitionTicks = 0;
            }
            return PlayState.CONTINUE;
        }

        if (transitioningToFly) {
            animationState.getController().setAnimation(RawAnimation.begin().then("fly_from_ground", Animation.LoopType.PLAY_ONCE));
            if (++transitionFromGroundTicks > 10) { // Limite de temps pour l'animation de transition
                transitioningToFly = false;
                transitionFromGroundTicks = 0;// Activer le vol après la transition
            }
            return PlayState.CONTINUE;
        }

        if (animationState.isMoving() && !this.isGliding) {
            animationState.getController().setAnimation(RawAnimation.begin().then("idle_to_walk", Animation.LoopType.PLAY_ONCE).then("walk2", Animation.LoopType.LOOP));
        } else if (animationState.isMoving() && this.isGliding && !transitioningToFly) {
            animationState.getController().setAnimation(RawAnimation.begin().then("fly", Animation.LoopType.LOOP));
        } else if (this.isGliding && !transitioningToFly) {
            animationState.getController().setAnimation(RawAnimation.begin().then("fly", Animation.LoopType.LOOP));
        } else {
            animationState.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController(this, "predicate", 0, this::predicate).setSoundKeyframeHandler(state -> {
            PlayerEntity player = ClientUtils.getClientPlayer();
            if (player != null && this.isGliding)
                this.getWorld().playSound(player, this.getX(), this.getY(), this.getZ(), ModSounds.PEGASUS_FLAP_1, this.getSoundCategory(), 0.15f, 2f);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public EntityView method_48926() {
        return null;
    }

    @Override
    protected void jump(float strength, Vec3d movementInput) {
        PlayerEntity player = (PlayerEntity) this.getFirstPassenger();
        MinecraftClient client = MinecraftClient.getInstance();
        BlockPos pos = this.getBlockPos();
        BlockPos belowPos = pos.down(1);
        boolean isGroundClose = !this.getWorld().getBlockState(belowPos).isAir();

        if (this.jumping && player != null && !(client.currentScreen instanceof ChatScreen)) {
            Vec3d vec3d = this.getVelocity();
            this.setVelocity(vec3d.x, 1.0, vec3d.z);
            this.setNoGravity(true);
            this.isGliding = true;
            ticksSinceJump = 0;
            this.jumping = false;

            if (this.isOnGround() || isGroundClose) {
                transitioningToFly = true;
            }
        }
    }

    @Override
    protected void playJumpSound() {
        this.playSound(ModSounds.PEGASUS_FLAP_1, 0.15F, 2.0F);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isClient) { // ✅ S'assurer que seul le serveur modifie la taille
            if (this.isBaby()) {
                float age = this.getBreedingAge(); // De -24000 à 0
                float progress = Math.max(0.0F, Math.min(1.0F, 1.0F - (age / -24000.0F))); // De 0.0 à 1.0
                float growthFactor = 0.5F + (progress * 0.5F); // De 0.5 à 1.0

                this.setScale(growthFactor);
                this.getDataTracker().set(SCALE_TRACKER, growthFactor);
            } else {
                this.setScale(1.0F);
            }
        }

        PlayerEntity player = (PlayerEntity) this.getFirstPassenger();
        BlockPos pos = this.getBlockPos();
        BlockPos belowPos = pos.down(1);
        Vec3d vec3d = this.getVelocity();
        boolean isGroundClose = !this.getWorld().getBlockState(belowPos).isAir();

        boolean isJumpPressed = false;
        if (isClientSide()) {
            // Code spécifique au client
            if (player != null) {
                isJumpPressed = InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_SPACE);
                if (MinecraftClient.getInstance().currentScreen instanceof ChatScreen) {
                    isJumpPressed = false;
                }
            }
        }

        ItemStack armorStack = this.getEquippedStack(EquipmentSlot.CHEST);
        Item armorItem = armorStack.getItem();

        if (armorItem != null && this.isSaddled() && player != null) {
            if (armorItem == Items.LEATHER_HORSE_ARMOR) {
                this.setVariant(PegasusVariant.LEATHER_SADDLED_MOUNTED);
            } else if (armorItem == Items.IRON_HORSE_ARMOR) {
                this.setVariant(PegasusVariant.IRON_SADDLED_MOUNTED);
            } else if (armorItem == Items.GOLDEN_HORSE_ARMOR) {
                this.setVariant(PegasusVariant.GOLD_SADDLED_MOUNTED);
            } else if (armorItem == Items.DIAMOND_HORSE_ARMOR) {
                this.setVariant(PegasusVariant.DIAMOND_SADDLED_MOUNTED);
            }
        } else if (armorItem != null && this.isSaddled() && player == null) {
            if (armorItem == Items.LEATHER_HORSE_ARMOR) {
                this.setVariant(PegasusVariant.LEATHER_SADDLED_UNMOUNTED);
            } else if (armorItem == Items.IRON_HORSE_ARMOR) {
                this.setVariant(PegasusVariant.IRON_SADDLED_UNMOUNTED);
            } else if (armorItem == Items.GOLDEN_HORSE_ARMOR) {
                this.setVariant(PegasusVariant.GOLD_SADDLED_UNMOUNTED);
            } else if (armorItem == Items.DIAMOND_HORSE_ARMOR) {
                this.setVariant(PegasusVariant.DIAMOND_SADDLED_UNMOUNTED);
            }
        } else if (armorItem != null && !this.isSaddled()) {
            if (armorItem == Items.LEATHER_HORSE_ARMOR) {
                this.setVariant(PegasusVariant.LEATHER_UNMOUNT);
            } else if (armorItem == Items.IRON_HORSE_ARMOR) {
                this.setVariant(PegasusVariant.IRON_UNMOUNT);
            } else if (armorItem == Items.GOLDEN_HORSE_ARMOR) {
                this.setVariant(PegasusVariant.GOLD_UNMOUNT);
            } else if (armorItem == Items.DIAMOND_HORSE_ARMOR) {
                this.setVariant(PegasusVariant.DIAMOND_UNMOUNT);
            }
        }
        assert armorItem != null;
        if (Objects.equals(armorItem.toString(), "air") && this.isSaddled()) {
            if (player != null) {
                this.setVariant(PegasusVariant.CLOAK);
            } else {
                this.setVariant(PegasusVariant.UNMOUNT);
            }
        } else if (Objects.equals(armorItem.toString(), "air") && !this.isSaddled()) {
            this.setVariant(PegasusVariant.DEFAULT);
        }

        if (player != null && this.isSaddled()) {
            if (!this.isOnGround() && !isGroundClose && !this.isGliding) {
                this.transition++;
            } else if (this.isGliding && (this.isOnGround() || isGroundClose) && !transitioningToFly) {
                this.isGliding = false;
                transitioningToGround = true;
                this.setNoGravity(false);
            } else if (this.age <= 2 && (this.isOnGround() || isGroundClose)) {
                this.isGliding = false;
                this.setNoGravity(false);
                this.jumping = false;
            }
            if (this.isOnGround() && isGroundClose && this.transition != 0) {
                this.transition = 0;
            }

            if (this.transition == 20) {
                transitioningToFly = true;
                this.isGliding = true;
                this.transition = 0;
            }

            // Détection de la libération de la touche de saut
            if (wasJumpPressed && !isJumpPressed) {
                this.jumping = true;
                this.jump(0, Vec3d.ZERO);
            }

            if (this.isGliding && ticksSinceJump > 20) {
                double forward = player.forwardSpeed;
                double sideways = player.sidewaysSpeed; // Prendre en compte le mouvement latéral
                Vec3d lookVector = player.getRotationVector();

                if (forward != 0.0 || sideways != 0.0) {
                    double speed = 0.4;
                    double yVec = lookVector.y;
                    if (lookVector.y > 0)
                        yVec = 0;

                    Vec3d moveVec = new Vec3d(lookVector.x, yVec, lookVector.z).normalize().multiply(speed);
                    Vec3d strafeVec = new Vec3d(lookVector.z, 0, -lookVector.x).normalize().multiply(speed);

                    if (forward < 0.0) {
                        moveVec = moveVec.multiply(1 / 3.0);
                    }

                    Vec3d finalMoveVec = moveVec.multiply(forward).add(strafeVec.multiply(sideways * 0.5));

                    this.setVelocity(finalMoveVec);
                    this.move(MovementType.SELF, this.getVelocity());
                } else {
                    this.setVelocity(0, -0.05, 0);
                    this.move(MovementType.SELF, this.getVelocity());
                }
            } else if (ticksSinceJump <= 20 && isGliding) {
                this.setVelocity(vec3d.x, 0.6, vec3d.z);
            }
            ++ticksSinceJump;
        } else {
            if (this.isGliding) {
                this.setNoGravity(false);
                this.isGliding = false;
            }
        }
        if (this.age < 3) {
            this.setVelocity(vec3d.x, -0.01, vec3d.z);
            transitioningToGround = false;
            transitioningToFly = false;
        }

        // Mise à jour de l'état précédent de la touche de saut
        wasJumpPressed = isJumpPressed;
    }
    public boolean damage(DamageSource source, float amount) {
        if (source.isOf(DamageTypes.FALL)) {
            return false;
        }
        return super.damage(source, amount);
    }

    @Override
    public void fall(double heightDifference, boolean onGround, BlockState state, BlockPos pos) {
        if (this.getFirstPassenger() instanceof PlayerEntity) {
            return;
        }
        super.fall(heightDifference, onGround, state, pos);
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
            DataTracker.registerData(PegasusEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public PegasusVariant getPegasusVariant() {
        return PegasusVariant.byId(this.getTypeVariant() & 255);
    }

    private int getTypeVariant() {
        return this.dataTracker.get(DATA_ID_TYPE_VARIANT);
    }

    private void setVariant(PegasusVariant variant) {
        this.dataTracker.set(DATA_ID_TYPE_VARIANT, variant.getId() & 255);
    }

    private boolean isClientSide() {
        return this.getWorld().isClient;
    }
    @Nullable
    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        if (spawnReason != SpawnReason.SPAWN_EGG && spawnReason != SpawnReason.COMMAND && spawnReason != SpawnReason.SPAWNER
                && spawnReason != SpawnReason.EVENT ) {
            int randomValue = this.random.nextInt(10);
            if (randomValue >= ModSpawnRateConfig.pegasusSpawnRate) {
                this.remove(Entity.RemovalReason.DISCARDED);
            }
            if (this.isBaby()) {
                this.setScale(0.5F); // Force la taille à 0.5 au spawn
            }
        }
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    @Override
    @Nullable
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
            PegasusEntity horseEntity2 = (PegasusEntity) ModEntities.PEGASUS.create(world);
            this.setChildAttributes(entity, horseEntity2);
            return horseEntity2;
    }
}
