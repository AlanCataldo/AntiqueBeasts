package net.mebahel.antiquebeasts.mixin;

import net.mebahel.antiquebeasts.item.custom.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class WitherDamageMixin {
    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    public void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        Entity attacker = source.getAttacker();
        if (player.hasStatusEffect(StatusEffects.WITHER) && amount >= 1.0F && attacker == null
                && player.getOffHandStack() != null && player.getOffHandStack() != null
                && ModItems.NETHERITE_PLATE_SHIELD != null && player.getOffHandStack().getItem() == ModItems.NETHERITE_PLATE_SHIELD) {
            cir.setReturnValue(player.damage(source, amount / 2));
            cir.cancel();
        } else if (player.hasStatusEffect(StatusEffects.WITHER) && amount >= 1.0F && attacker == null
                && player.getMainHandStack() != null && player.getMainHandStack() != null
                && ModItems.NETHERITE_PLATE_SHIELD != null && player.getMainHandStack().getItem() == ModItems.NETHERITE_PLATE_SHIELD) {
            cir.setReturnValue(player.damage(source, amount / 2));
            cir.cancel();
        }
    }
}





