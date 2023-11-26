package net.mebahel.antiquebeasts.entity.projectiles;

import net.mebahel.antiquebeasts.particle.ModParticles;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
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

import java.util.ArrayList;
import java.util.Iterator;

import static net.mebahel.antiquebeasts.entity.ModEntities.THROWINGROCK;

public class ThrowingRockEntity extends ThrownItemEntity implements GeoEntity {
    public ThrowingRockEntity(EntityType<? extends ThrowingRockEntity> entityType, World world) {
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


    public ThrowingRockEntity(World world, LivingEntity owner) {
        super(THROWINGROCK, owner, world);
    }

    protected Item getDefaultItem() {
        return null;
    }

    public void handleStatus(byte status) {
        if (status == 3) {
            ParticleEffect particleEffect = ModParticles.ROCKSPLASH_PARTICLE;

            for(int i = 0; i < 8; ++i) {
                this.getWorld().addParticle(particleEffect, this.getX(), this.getY(), this.getZ(),
                        0f, 0f, 0f);
            }
        }
    }

    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.getWorld().isClient) {
            this.getWorld().sendEntityStatus(this, (byte)3);
            this.discard();
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        LivingEntity target = (LivingEntity) entityHitResult.getEntity();
        target.damage(this.getDamageSources().thrown(this, this.getOwner()), (float)15);
        playSound(ModSounds.CYCLOPS_FLESHCRUSH3, 1f, 1f);
        Entity entity = this.getEffectCause();

        ArrayList<StatusEffectInstance> effectList = new ArrayList<>();
        effectList.add(new StatusEffectInstance(StatusEffects.NAUSEA, 140, 2));
        effectList.add(new StatusEffectInstance(StatusEffects.SLOWNESS, 140, 2));

        Iterator<StatusEffectInstance> effectIterator = effectList.iterator();
        StatusEffectInstance statusEffectInstance;
        while(effectIterator.hasNext()) {
            statusEffectInstance = effectIterator.next();
            target.addStatusEffect(new StatusEffectInstance(statusEffectInstance.getEffectType(),
                    statusEffectInstance.getDuration(), statusEffectInstance.getAmplifier(),
                    statusEffectInstance.isAmbient(), statusEffectInstance.shouldShowParticles()), entity);
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        BlockState blockState = this.getWorld().getBlockState(blockHitResult.getBlockPos());
        blockState.onProjectileHit(this.getWorld(), blockState, blockHitResult, this);
        playSound(ModSounds.CYCLOPS_ROCKCRUSH1, 1f, 1f);
    }

    @Override
    public void tick() {
        super.tick();
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
            for(int i = 0; i < 4; ++i) {
                this.getWorld().addParticle(ParticleTypes.BUBBLE, d - vec3d.x * 0.25, e - vec3d.y * 0.25, f - vec3d.z * 0.25, vec3d.x, vec3d.y, vec3d.z);
            }

            h = 0.8F;
        } else {
            h = 0.99F;
        }
        this.setVelocity(vec3d.multiply(h));
        if (!this.hasNoGravity()) {
            Vec3d vec3d2 = this.getVelocity();
            this.setVelocity(vec3d2.x, vec3d2.y, vec3d2.z);
        }
        this.setPosition(d, e, f);
    }
}
