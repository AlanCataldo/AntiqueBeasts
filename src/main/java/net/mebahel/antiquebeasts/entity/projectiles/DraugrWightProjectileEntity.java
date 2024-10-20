package net.mebahel.antiquebeasts.entity.projectiles;

import net.mebahel.antiquebeasts.particle.ModParticles;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.entity.Entity;
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

import static net.mebahel.antiquebeasts.entity.ModEntities.DRAUGR_WIGHT_PROJECTILE;

public class DraugrWightProjectileEntity extends ThrownItemEntity implements GeoEntity {

    float damage;
    private int ticksExisted = 0;
    private Vec3d previousPosition; // Sauvegarde de la position précédente pour générer une traînée continue
    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);

    public DraugrWightProjectileEntity(World world, LivingEntity owner, float damage) {
        super(DRAUGR_WIGHT_PROJECTILE, owner, world);
        this.damage = damage;
    }

    public DraugrWightProjectileEntity(EntityType<? extends DraugrWightProjectileEntity> entityType, World world) {
        super(entityType, world);
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
            super.onEntityHit(entityHitResult);
            target.damage(this.getDamageSources().thrown(this, this.getOwner()), this.damage);

            // Appliquer l'effet de gel sur la cible
            if (!target.isBlocking()) {
                ArrayList<StatusEffectInstance> effectList = new ArrayList<>();
                effectList.add(new StatusEffectInstance(StatusEffects.SLOWNESS, 80, 0, false, false, false));
                target.setFrozenTicks(200);

                for (StatusEffectInstance statusEffect : effectList) {
                    target.addStatusEffect(new StatusEffectInstance(statusEffect.getEffectType(),
                            statusEffect.getDuration(), statusEffect.getAmplifier(),
                            statusEffect.isAmbient(), statusEffect.shouldShowParticles()), this.getEffectCause());
                }
            }

            this.discard();
        } else if (entityHitResult.getEntity() instanceof ItemFrameEntity itemFrame) {
            itemFrame.dropItem(itemFrame.getHeldItemStack().getItem());
            itemFrame.dropItem(new ItemStack(Items.ITEM_FRAME).getItem());
            itemFrame.kill();
            this.discard();
        }
    }

    private void generateParticles(Vec3d currentPosition) {
        if (!this.getWorld().isClient()) return;  // S'assurer que c'est exécuté côté client uniquement

        ParticleEffect particleEffect = ModParticles.SNOWFLAKE_PARTICLE;

        // Calculer le vecteur de déplacement (distance parcourue)
        if (this.previousPosition != null) {
            Vec3d deltaPosition = currentPosition.subtract(this.previousPosition);
            double distance = deltaPosition.length();

            // Si la distance est trop faible, on ne génère pas de particules (éviter les particules redondantes)
            if (distance < 0.05) {
                return;
            }

            // Calcul du nombre de "steps" pour générer des particules tout au long du chemin (densité de la traînée)
            int steps = (int) (distance / 0.1);  // Ajuster la densité en fonction de la distance (0.1 pour plus de particules)

            // Générer les particules entre la position précédente et la position actuelle
            for (int i = 0; i <= steps; i++) {
                // Calculer la position interpolée pour chaque step
                double t = i / (double) steps;
                Vec3d interpolatedPosition = this.previousPosition.lerp(currentPosition, t);

                // Générer plusieurs particules autour de chaque point interpolé
                for (int j = 0; j < 3; j++) {  // Ajuster le nombre de particules générées par point
                    double offsetX = (this.random.nextDouble() - 0.5) * 0.3;  // Dispersion latérale
                    double offsetY = (this.random.nextDouble() - 0.5) * 0.3;  // Dispersion verticale
                    double offsetZ = (this.random.nextDouble() - 0.5) * 0.3;  // Dispersion en profondeur

                    // Ajouter des particules avec une petite dispersion pour donner un effet de rayon plus épais
                    this.getWorld().addParticle(particleEffect,
                            interpolatedPosition.x + offsetX, interpolatedPosition.y + offsetY, interpolatedPosition.z + offsetZ,
                            0, 0, 0);  // Les particules ne bougent pas
                }
            }
        }

        this.previousPosition = currentPosition;
    }


    @Override
    public void tick() {
        super.tick();
        ticksExisted++; // Incrémente le compteur de ticks à chaque appel

        // Désactiver la gravité pendant les 5 premières secondes (100 ticks)
        if (ticksExisted <= 40) {
            this.setNoGravity(true);
        } else {
            this.setNoGravity(false);
        }

        // Générer la traînée de particules
        if (this.getWorld().isClient()) {
            generateParticles(this.getPos());  // Appel de la méthode modifiée
        }

        // Gérer la collision avec des entités ou des blocs
        HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
        if (hitResult.getType() != HitResult.Type.MISS) {
            this.onCollision(hitResult);
        }

        this.checkBlockCollision();
        Vec3d velocity = this.getVelocity();
        this.setPosition(this.getX() + velocity.x, this.getY() + velocity.y, this.getZ() + velocity.z);
        this.updateRotation();
        this.setVelocity(velocity.multiply(0.99));  // Réduire légèrement la vitesse à chaque tick
    }
}
