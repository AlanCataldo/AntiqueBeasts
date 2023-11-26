package net.mebahel.antiquebeasts.entity.projectiles;

import net.mebahel.antiquebeasts.entity.ModEntities;
import net.mebahel.antiquebeasts.item.custom.ModItems;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.Objects;

public class ThrowingHopliteSpearEntity extends PersistentProjectileEntity implements IAnimatable {
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

    @Override
    public void registerControllers(AnimationData data) {
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    private static final TrackedData<Byte> LOYALTY;
    private static final TrackedData<Boolean> ENCHANTED;
    private ItemStack tridentStack;
    private boolean dealtDamage;
    public int returnTimer;
    String type = "";

    public ThrowingHopliteSpearEntity(EntityType<? extends ThrowingHopliteSpearEntity> entityType, World world) {
        super(entityType, world);
        this.tridentStack = new ItemStack(ModItems.DIAMOND_HOPLITE_SPEAR);
    }

    public ThrowingHopliteSpearEntity(World world, LivingEntity owner, ItemStack stack, String type) {
        super(getModEntityByType(type), owner, world);
        this.type = type;
        this.tridentStack = new ItemStack(getModItemByType(type));
        this.tridentStack = stack.copy();
        this.dataTracker.set(LOYALTY, (byte) EnchantmentHelper.getLoyalty(stack));
        this.dataTracker.set(ENCHANTED, stack.hasGlint());
    }

    private static EntityType<ThrowingHopliteSpearEntity> getModEntityByType(String type) {
        return switch (type) {
            case "iron" -> ModEntities.IRON_THROWING_HOPLITE_SPEAR;
            case "gold" -> ModEntities.GOLD_THROWING_HOPLITE_SPEAR;
            case "diamond" -> ModEntities.DIAMOND_THROWING_HOPLITE_SPEAR;
            case "netherite" -> ModEntities.NETHERITE_THROWING_HOPLITE_SPEAR;
            default -> throw new IllegalArgumentException("Type non pris en charge : " + type);
        };
    }

    private static Item getModItemByType(String type) {
        return switch (type) {
            case "iron" -> ModItems.IRON_HOPLITE_SPEAR;
            case "gold" -> ModItems.GOLD_HOPLITE_SPEAR;
            case "diamond" -> ModItems.DIAMOND_HOPLITE_SPEAR;
            case "netherite" -> ModItems.NETHERITE_HOPLITE_SPEAR;
            default -> throw new IllegalArgumentException("Type non pris en charge : " + type);
        };
    }

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(LOYALTY, (byte)0);
        this.dataTracker.startTracking(ENCHANTED, false);
    }

    public void tick() {
        if (this.inGroundTime > 4) {
            this.dealtDamage = true;
        }

        Entity entity = this.getOwner();
        int i = (Byte)this.dataTracker.get(LOYALTY);
        if (i > 0 && (this.dealtDamage || this.isNoClip()) && entity != null) {
            if (!this.isOwnerAlive()) {
                if (!this.world.isClient && this.pickupType == PickupPermission.ALLOWED) {
                    this.dropStack(this.asItemStack(), 0.1F);
                }

                this.discard();
            } else {
                this.setNoClip(true);
                Vec3d vec3d = entity.getEyePos().subtract(this.getPos());
                this.setPos(this.getX(), this.getY() + vec3d.y * 0.015 * (double)i, this.getZ());
                if (this.world.isClient) {
                    this.lastRenderY = this.getY();
                }

                double d = 0.05 * (double)i;
                this.setVelocity(this.getVelocity().multiply(0.95).add(vec3d.normalize().multiply(d)));
                if (this.returnTimer == 0) {
                    this.playSound(SoundEvents.ITEM_TRIDENT_RETURN, 10.0F, 1.0F);
                }

                ++this.returnTimer;
            }
        }

        super.tick();
    }

    private boolean isOwnerAlive() {
        Entity entity = this.getOwner();
        if (entity != null && entity.isAlive()) {
            return !(entity instanceof ServerPlayerEntity) || !entity.isSpectator();
        } else {
            return false;
        }
    }

    protected ItemStack asItemStack() {
        return this.tridentStack.copy();
    }

    public boolean isEnchanted() {
        return (Boolean)this.dataTracker.get(ENCHANTED);
    }

    @Nullable
    protected EntityHitResult getEntityCollision(Vec3d currentPosition, Vec3d nextPosition) {
        return this.dealtDamage ? null : super.getEntityCollision(currentPosition, nextPosition);
    }

    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();
        float f = 10.0F;
        if (entity instanceof LivingEntity livingEntity) {
            f += EnchantmentHelper.getAttackDamage(this.tridentStack, livingEntity.getGroup());
        }

        Entity entity2 = this.getOwner();
        DamageSource damageSource = DamageSource.trident(this, entity2 == null ? this : entity2);
        this.dealtDamage = true;

        SoundEvent soundEvent = SoundEvents.ITEM_TRIDENT_HIT;
        if (entity.damage(damageSource, f)) {
            if (entity.getType() == EntityType.ENDERMAN) {
                return;
            }

            if (entity instanceof LivingEntity livingEntity2) {
                if (entity2 instanceof LivingEntity) {
                    EnchantmentHelper.onUserDamaged(livingEntity2, entity2);
                    EnchantmentHelper.onTargetDamaged((LivingEntity)entity2, livingEntity2);
                }
                if (Objects.equals(this.type, "netherite"))
                    livingEntity2.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 140, 0));
                this.onHit(livingEntity2);
            }
        }

        this.setVelocity(this.getVelocity().multiply(-0.01, -0.1, -0.01));
        float g = 1.0F;
        if (this.world instanceof ServerWorld && this.world.isThundering() && this.hasChanneling()) {
            BlockPos blockPos = entity.getBlockPos();
            if (this.world.isSkyVisible(blockPos)) {
                LightningEntity lightningEntity = EntityType.LIGHTNING_BOLT.create(this.world);
                lightningEntity.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(blockPos));
                lightningEntity.setChanneler(entity2 instanceof ServerPlayerEntity ? (ServerPlayerEntity)entity2 : null);
                this.world.spawnEntity(lightningEntity);
                soundEvent = SoundEvents.ITEM_TRIDENT_THUNDER;
                g = 5.0F;
            }
        }

        this.playSound(soundEvent, g, 1.0F);
    }

    public boolean hasChanneling() {
        return EnchantmentHelper.hasChanneling(this.tridentStack);
    }

    protected boolean tryPickup(PlayerEntity player) {
        return super.tryPickup(player) || this.isNoClip() && this.isOwner(player) && player.getInventory().insertStack(this.asItemStack());
    }

    protected SoundEvent getHitSound() {
        return SoundEvents.ITEM_TRIDENT_HIT_GROUND;
    }

    public void onPlayerCollision(PlayerEntity player) {
        if (this.isOwner(player) || this.getOwner() == null) {
            super.onPlayerCollision(player);
        }

    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("throwing_hoplite_spear", 10)) {
            this.tridentStack = ItemStack.fromNbt(nbt.getCompound("throwing_hoplite_spear"));
        }

        this.dealtDamage = nbt.getBoolean("DealtDamage");
        this.dataTracker.set(LOYALTY, (byte)EnchantmentHelper.getLoyalty(this.tridentStack));
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.put("throwing_hoplite_spear", this.tridentStack.writeNbt(new NbtCompound()));
        nbt.putBoolean("DealtDamage", this.dealtDamage);
    }

    public void age() {
        int i = (Byte)this.dataTracker.get(LOYALTY);
        if (this.pickupType != PickupPermission.ALLOWED || i <= 0) {
            super.age();
        }

    }

    protected float getDragInWater() {
        return 0.99F;
    }

    public boolean shouldRender(double cameraX, double cameraY, double cameraZ) {
        return true;
    }

    static {
        LOYALTY = DataTracker.registerData(ThrowingHopliteSpearEntity.class, TrackedDataHandlerRegistry.BYTE);
        ENCHANTED = DataTracker.registerData(ThrowingHopliteSpearEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    }
}
