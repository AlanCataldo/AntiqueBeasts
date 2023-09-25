package net.mebahel.antiquebeasts.entity.projectiles;

import net.mebahel.antiquebeasts.item.custom.ModItems;
import net.mebahel.antiquebeasts.particle.ModParticles;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
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
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType.EDefaultLoopTypes;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.Iterator;

import static net.mebahel.antiquebeasts.entity.ModEntities.THROWINGSNOWROCK;

public class ThrowingSnowRockEntity extends ThrownItemEntity implements IAnimatable {

    private LivingEntity shooter;

    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("throwingrock.animation.idle", EDefaultLoopTypes.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    public ThrowingSnowRockEntity(EntityType<? extends ThrowingSnowRockEntity> entityType, World world) {
        super(entityType, world);
    }

    public ThrowingSnowRockEntity(World world, LivingEntity owner) {
        super(THROWINGSNOWROCK, owner, world);
    }

    protected Item getDefaultItem() {
        return ModItems.THROWINGROCK;
    }

    public void handleStatus(byte status) {
        if (status == 3) {
            ParticleEffect particleEffect = ModParticles.SNOWROCKSPLASH_PARTICLE;

            for(int i = 0; i < 8; ++i) {
                this.world.addParticle(particleEffect, this.getX(), this.getY(), this.getZ(),
                        0f, 0f, 0f);
            }
        }
    }

    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.world.isClient) {
            this.world.sendEntityStatus(this, (byte)3);
            this.discard();
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        LivingEntity target = (LivingEntity) entityHitResult.getEntity();
        target.damage(DamageSource.thrownProjectile(this, this.getOwner()), (float)15);
        playSound(ModSounds.CYCLOPS_FLESHCRUSH3, 1f, 1f);
        Entity entity = this.getEffectCause();

        ArrayList<StatusEffectInstance> effectList = new ArrayList<>();
        effectList.add(new StatusEffectInstance(StatusEffects.SLOWNESS, 160, 0));

        Iterator<StatusEffectInstance> effectIterator = effectList.iterator();
        target.setFrozenTicks(400);

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
        BlockState blockState = this.world.getBlockState(blockHitResult.getBlockPos());
        blockState.onProjectileHit(this.world, blockState, blockHitResult, this);
        playSound(ModSounds.CYCLOPS_ROCKCRUSH1, 1f, 1f);
    }

    @Override
    public void tick() {
        super.tick();
        HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
        boolean bl = false;
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockPos blockPos = ((BlockHitResult)hitResult).getBlockPos();
            BlockState blockState = this.world.getBlockState(blockPos);
            if (blockState.isOf(Blocks.NETHER_PORTAL)) {
                this.setInNetherPortal(blockPos);
                bl = true;
            } else if (blockState.isOf(Blocks.END_GATEWAY)) {
                BlockEntity blockEntity = this.world.getBlockEntity(blockPos);
                if (blockEntity instanceof EndGatewayBlockEntity && EndGatewayBlockEntity.canTeleport(this)) {
                    EndGatewayBlockEntity.tryTeleportingEntity(this.world, blockPos, blockState, this, (EndGatewayBlockEntity)blockEntity);
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
                this.world.addParticle(ParticleTypes.BUBBLE, d - vec3d.x * 0.25, e - vec3d.y * 0.25, f - vec3d.z * 0.25, vec3d.x, vec3d.y, vec3d.z);
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
