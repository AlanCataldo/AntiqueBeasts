package net.mebahel.antiquebeasts.entity.projectiles;

import net.mebahel.antiquebeasts.particle.ModParticles;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShieldItem;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

import static net.mebahel.antiquebeasts.entity.ModEntities.FROST_SPIKE;
import static net.mebahel.antiquebeasts.entity.ModEntities.VENOM;

public class FrostSpikeEntity extends ThrownItemEntity implements GeoEntity {
    float damage;
    private int ticksExisted = 0;
    public FrostSpikeEntity(EntityType<? extends FrostSpikeEntity> entityType, World world) {
        super(entityType, world);
    }
    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }
    private PlayState predicate(AnimationState animationState) {
        animationState.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController(this, "controller",0, this::predicate));
    }
    public FrostSpikeEntity(World world, LivingEntity owner, float damage) {
        super(FROST_SPIKE, owner, world);
        this.damage = damage;
    }

    protected Item getDefaultItem() {
        return null;
    }

    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.getWorld().isClient) {
            this.getWorld().sendEntityStatus(this, (byte)3);
            this.discard();
        }
    }

    private void breakShield(LivingEntity target) {
        if (target instanceof PlayerEntity player) {
            if (player.isBlocking()) {
                ItemStack activeItem = player.getActiveItem();
                if ((activeItem.getItem() instanceof ShieldItem)) {
                    player.disableShield(true);
                }
            }
        }
    }
    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        if (entityHitResult.getEntity() instanceof LivingEntity) {
            super.onEntityHit(entityHitResult);
            LivingEntity target = (LivingEntity) entityHitResult.getEntity();
            this.breakShield(target);

            // Inflige des dégâts
            target.damage(this.getDamageSources().thrown(this, this.getOwner()), damage);

            // Ajoute l'effet Slowness II pendant 3 secondes (60 ticks)
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 60, 1, false, false ,false));  // Slowness II (amplificateur 1)

            // Optionnel : Ajoute des ticks de gel si la cible n'est pas en train de bloquer
            if (!target.isBlocking()) {
                target.setFrozenTicks(320);
            }
        } else if (entityHitResult.getEntity() instanceof ItemFrameEntity itemFrame) {
            itemFrame.dropItem(itemFrame.getHeldItemStack().getItem());
            itemFrame.dropItem(new ItemStack(Items.ITEM_FRAME).getItem());
            itemFrame.kill();
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        BlockState blockState = this.getWorld().getBlockState(blockHitResult.getBlockPos());
        blockState.onProjectileHit(this.getWorld(), blockState, blockHitResult, this);
        this.playIceBreakSound();
    }

    private void generateParticles() {
        ParticleEffect particleEffect = ModParticles.SNOWFLAKE_PARTICLE;
        for (int i = 0; i < 6; i++) {
            double offsetX = this.random.nextGaussian() * 0.2;
            double offsetY = this.random.nextGaussian() * 0.2;
            double offsetZ = this.random.nextGaussian() * 0.2;
            this.getWorld().addParticle(particleEffect,
                    this.getX() + offsetX,
                    this.getY() + offsetY,
                    this.getZ() + offsetZ,
                    0, 0, 0);
        }
    }
    @Override
    public void tick() {
        super.tick();
        ticksExisted++; // Incrémente le compteur de ticks à chaque appel

        // Désactiver la gravité pendant les 5 premières secondes (100 ticks)
        if (ticksExisted <= 60) {
            this.setNoGravity(true);
        } else {
            this.setNoGravity(false);
        }

        // Reste de la logique de la méthode tick()...

        HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
        boolean bl = false;
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockPos blockPos = ((BlockHitResult)hitResult).getBlockPos();
            BlockState blockState = this.getWorld().getBlockState(blockPos);
            if (blockState.isOf(Blocks.NETHER_PORTAL)) {
                this.setInNetherPortal(blockPos);
                bl = true;
            } else if (blockState.isOf(Blocks.END_GATEWAY)) {
                BlockEntity blockEntity = this.getWorld().getBlockEntity(blockPos);
                if (blockEntity instanceof EndGatewayBlockEntity && EndGatewayBlockEntity.canTeleport(this)) {
                    EndGatewayBlockEntity.tryTeleportingEntity(this.getWorld(), blockPos, blockState, this, (EndGatewayBlockEntity)blockEntity);
                }
                bl = true;
            }
        }
        if (this.getWorld().isClient) {
            this.generateParticles();
        }

        if (hitResult.getType() != HitResult.Type.MISS && !bl) {
            this.onCollision(hitResult);
        }

        this.checkBlockCollision();
        Vec3d vec3d = this.getVelocity();
        double d = this.getX() + vec3d.x;
        double e = this.getY() + vec3d.y;
        double f = this.getZ() + vec3d.z;
        this.updateRotation();
        float h;
        if (this.isTouchingWater()) {
            for (int i = 0; i < 4; ++i) {
                this.getWorld().addParticle(ParticleTypes.BUBBLE, d - vec3d.x * 0.25, e - vec3d.y * 0.25, f - vec3d.z * 0.25, vec3d.x, vec3d.y, vec3d.z);
            }

            h = 0.8F;
        } else {
            h = 0.99F;
        }
        this.setVelocity(vec3d.multiply(h));
        if (!this.hasNoGravity()) {
            Vec3d vec3d2 = this.getVelocity();
            this.setVelocity(vec3d2.x, vec3d2.y - 0.05, vec3d2.z); // Appliquer la gravité si activée
        }
        this.setPosition(d, e, f);
    }
    private void playIceBreakSound() {
        this.getWorld().playSound(
                null, // Jouer le son pour tous les joueurs
                this.getX(), this.getY(), this.getZ(), // Position de l'entité
                SoundEvents.BLOCK_GLASS_BREAK, // Son de glace cassée
                SoundCategory.NEUTRAL, // Catégorie de son
                0.85F, // Volume
                1f // Hauteur (pitch)
        );
    }
}
