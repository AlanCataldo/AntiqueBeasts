package net.mebahel.antiquebeasts.mixin;

import net.mebahel.antiquebeasts.util.ProjectileDataAccessor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.util.hit.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PersistentProjectileEntity.class)
public abstract class FrostBowProjectileMixin {
    @Inject(method = "onEntityHit", at = @At("HEAD"))
    private void applyWeaknessOnHit(EntityHitResult entityHitResult, CallbackInfo ci) {
        if (entityHitResult.getEntity() instanceof LivingEntity target) {
            PersistentProjectileEntity projectile = (PersistentProjectileEntity) (Object) this;

            if (((ProjectileDataAccessor) projectile).isFrostBowShot()) {
                target.setFrozenTicks(400);
            }
        }
    }
}
