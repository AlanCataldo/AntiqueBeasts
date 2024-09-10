package net.mebahel.antiquebeasts.entity.projectiles;

import net.mebahel.antiquebeasts.item.custom.ModItems;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

import static net.mebahel.antiquebeasts.entity.ModEntities.HARPY_FEATHER;

public class HarpyFeatherEntity extends PersistentProjectileEntity implements GeoEntity {
    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);
    float damage;

    public HarpyFeatherEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
        this.damage = 8.0f;  // Valeur par défaut pour le projectile
    }

    public HarpyFeatherEntity(World world, LivingEntity owner, float damage) {
        super(HARPY_FEATHER, owner, world);
        this.damage = damage;
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        // Ne pas appeler super.onEntityHit() pour empêcher le son par défaut
        if (!this.getWorld().isClient) {
            // Infliger des dégâts et jouer le son personnalisé à l'impact avec une entité
            if (entityHitResult.getEntity() instanceof LivingEntity) {
                LivingEntity target = (LivingEntity) entityHitResult.getEntity();
                target.damage(this.getDamageSources().thrown(this, this.getOwner()), this.damage);
            }

            // Jouer le son personnalisé
            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), ModSounds.HARPY_FEATHER_HIT,
                    SoundCategory.NEUTRAL, 0.65f, 0.80f);
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        // Ne pas appeler super.onBlockHit() pour empêcher le son par défaut
        if (!this.getWorld().isClient) {
            // Jouer le son personnalisé à l'impact avec un bloc
            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), ModSounds.HARPY_FEATHER_HIT,
                    SoundCategory.NEUTRAL, 0.65f, 0.80f);
        }
        this.discard();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController(this, "controller", 0, this::predicate));
    }

    private PlayState predicate(AnimationState animationState) {
        animationState.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    @Override
    protected ItemStack asItemStack() {
        return new ItemStack(ModItems.HARPY_FEATHER);  // Définir l'item comme une plume
    }
}
