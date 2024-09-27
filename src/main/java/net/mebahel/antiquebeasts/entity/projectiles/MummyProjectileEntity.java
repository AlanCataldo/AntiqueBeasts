package net.mebahel.antiquebeasts.entity.projectiles;

import net.mebahel.antiquebeasts.entity.custom.egyptian.EgyptianEntity;
import net.mebahel.antiquebeasts.entity.custom.egyptian.MummyBossEntity;
import net.mebahel.antiquebeasts.entity.custom.egyptian.MummyEntity;
import net.mebahel.antiquebeasts.entity.custom.egyptian.ServantEntity;
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
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleEffect;
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

import static net.mebahel.antiquebeasts.entity.ModEntities.MUMMY_PROJECTILE;

public class MummyProjectileEntity extends ThrownItemEntity implements GeoEntity {
    private static final int MAX_LIFETIME_TICKS = 120; // 7 secondes (1 seconde = 20 ticks)
    private int lifetime; // Compteur pour la durée de vie

    float damage;
    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);

    public MummyProjectileEntity(World world, LivingEntity owner, float damage) {
        super(MUMMY_PROJECTILE, owner, world);
        this.damage = damage;
        this.lifetime = 0;
        this.setNoGravity(true);
    }

    public MummyProjectileEntity(EntityType<? extends MummyProjectileEntity> entityType, World world) {
        super(entityType, world);
        this.lifetime = 0;
        this.setNoGravity(true);
    }

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
        controllers.add(new AnimationController(this, "controller", 0, this::predicate));
    }

    @Override
    protected Item getDefaultItem() {
        return null;
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        if (entityHitResult.getEntity() instanceof LivingEntity target) {
            // Ignorer certaines entités spécifiques
            if (target instanceof MummyEntity || target instanceof ServantEntity || target instanceof MummyBossEntity) {
                return; // Le projectile traverse ces entités sans les affecter
            }

            super.onEntityHit(entityHitResult);  // Continue with default behavior
            target.damage(this.getDamageSources().thrown(this, this.getOwner()), (float) 8);

            // Appliquer Wither si la cible ne bloque pas
            if (!target.isBlocking()) {
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 4 * 20, 0));
            }

            // Faire disparaître le projectile après avoir touché la cible
            this.discard();
        } else if (entityHitResult.getEntity() instanceof ItemFrameEntity itemFrame) {
            itemFrame.dropItem(itemFrame.getHeldItemStack().getItem());
            itemFrame.dropItem(new ItemStack(Items.ITEM_FRAME).getItem());
            itemFrame.kill();

            // Faire disparaître le projectile après avoir touché l'ItemFrame
            this.discard();
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        BlockState blockState = this.getWorld().getBlockState(blockHitResult.getBlockPos());
        blockState.onProjectileHit(this.getWorld(), blockState, blockHitResult, this);
    }

    // Suivre la cible de l'entité qui a lancé le projectile
    private void updateDirectionTowardsTarget() {
        EgyptianEntity owner = (EgyptianEntity) this.getOwner();
        if (owner != null && owner.getTarget() != null) {
            LivingEntity target = owner.getTarget(); // Utilise la cible de l'entité qui a lancé le projectile

            Vec3d targetPosition = target.getPos().add(0, target.getHeight(), 0); // Milieu du corps de la cible
            Vec3d currentPosition = this.getPos();
            Vec3d directionToTarget = targetPosition.subtract(currentPosition).normalize();

            // Réduire la vitesse du projectile
            double speed = 0.3;
            if (owner instanceof MummyBossEntity) {
                if (((MummyBossEntity) owner).secondPhase || ((MummyBossEntity) owner).thirdPhase) {
                    speed = 0.45;
                }
            }

            Vec3d newVelocity = directionToTarget.multiply(speed);
            this.setVelocity(newVelocity);
        }
    }

    private void generateParticles() {
        ParticleEffect particleEffect = ModParticles.MUMMY_PROJECTILE_PARTICLE;
        for (int i = 0; i < 25; i++) {
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

        // Vérifier si la durée de vie du projectile dépasse 7 secondes (140 ticks)
        if (this.lifetime >= MAX_LIFETIME_TICKS) {
            this.discard(); // Supprimer le projectile après 7 secondes
            return; // Sortir de la méthode pour éviter toute autre action
        }

        // Augmenter le compteur de durée de vie
        this.lifetime++;

        // Mettre à jour la direction du projectile vers la cible à chaque tick
        this.updateDirectionTowardsTarget();

        HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
        boolean bl = false;
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockPos blockPos = ((BlockHitResult) hitResult).getBlockPos();
            BlockState blockState = this.getWorld().getBlockState(blockPos);
            if (blockState.isOf(Blocks.NETHER_PORTAL)) {
                this.setInNetherPortal(blockPos);
                bl = true;
            } else if (blockState.isOf(Blocks.END_GATEWAY)) {
                BlockEntity blockEntity = this.getWorld().getBlockEntity(blockPos);
                if (blockEntity instanceof EndGatewayBlockEntity && EndGatewayBlockEntity.canTeleport(this)) {
                    EndGatewayBlockEntity.tryTeleportingEntity(this.getWorld(), blockPos, blockState, this, (EndGatewayBlockEntity) blockEntity);
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
        this.setPosition(d, e, f);
    }
}

