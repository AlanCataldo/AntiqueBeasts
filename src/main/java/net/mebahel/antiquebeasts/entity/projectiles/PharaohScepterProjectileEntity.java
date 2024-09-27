package net.mebahel.antiquebeasts.entity.projectiles;

import net.mebahel.antiquebeasts.particle.ModParticles;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

import static net.mebahel.antiquebeasts.entity.ModEntities.PHARAOH_SCEPTER_PROJECTILE;
public class PharaohScepterProjectileEntity extends ThrownItemEntity implements GeoEntity {
    private static final int MAX_LIFETIME_TICKS = 160; // 7 secondes (1 seconde = 20 ticks)
    private int lifetime; // Compteur pour la durée de vie

    float damage;
    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);

    public PharaohScepterProjectileEntity(World world, LivingEntity owner, float damage) {
        super(PHARAOH_SCEPTER_PROJECTILE, owner, world);
        this.damage = damage;
        this.lifetime = 0;
    }

    public PharaohScepterProjectileEntity(EntityType<? extends PharaohScepterProjectileEntity> entityType, World world) {
        super(entityType, world);
        this.lifetime = 0;
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
            super.onEntityHit(entityHitResult);  // Continue with default behavior
            target.damage(this.getDamageSources().thrown(this, this.getOwner()), (float) 10);

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


    private void generateParticles() {
        if (!this.getWorld().isClient()) return; // Particules seulement côté client

        ParticleEffect particleEffect = ModParticles.MUMMY_PROJECTILE_PARTICLE;
        // Réduire le nombre de particules générées
        for (int i = 0; i < 15; i++) {
            double offsetX = this.random.nextGaussian() * 0.17;
            double offsetY = this.random.nextGaussian() * 0.17;
            double offsetZ = this.random.nextGaussian() * 0.17;
            this.getWorld().addParticle(particleEffect,
                    this.getX() + offsetX,
                    this.getY() + offsetY,
                    this.getZ() + offsetZ,
                    0, 0, 0);  // Pas de mouvement pour les particules
        }
    }

    @Override
    public void tick() {
        // Vérifier la durée de vie du projectile
        if (this.lifetime >= MAX_LIFETIME_TICKS) {
            this.discard(); // Supprimer le projectile après la durée maximale
            return;
        }

        // Augmenter la durée de vie du projectile
        this.lifetime++;

        // Si le propriétaire du projectile est un joueur, mettre à jour la direction
        if (this.getOwner() instanceof LivingEntity owner) {
            Vec3d lookDirection = owner.getRotationVec(1.0F).normalize(); // Direction du regard
            this.setVelocity(lookDirection.multiply(0.35));  // Ajuste la vitesse du projectile
        }

        // Générer les particules côté client
        if (this.getWorld().isClient) {
            this.generateParticles();
        }

        // Gérer la collision avec des entités ou des blocs
        HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);  // Détecter la collision
        if (hitResult.getType() != HitResult.Type.MISS) {
            this.onCollision(hitResult);  // Gérer l'impact
        }

        // Mettre à jour la position du projectile selon sa vélocité
        this.move(MovementType.SELF, this.getVelocity());

        // Appliquer un ralentissement progressif si nécessaire
        this.setVelocity(this.getVelocity().multiply(0.99));  // Ajoute un effet d'inertie
    }


}

