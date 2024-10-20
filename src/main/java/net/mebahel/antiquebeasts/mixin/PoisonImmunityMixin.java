package net.mebahel.antiquebeasts.mixin;

import net.mebahel.antiquebeasts.util.ArmorUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class PoisonImmunityMixin {
    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    public void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof PlayerEntity player && (ArmorUtils.isWearingValkyrieArmor(player))) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 60, 0));
        }
        if (entity.hasStatusEffect(StatusEffects.POISON)) {
            if (entity instanceof PlayerEntity player && (ArmorUtils.isWearingFullDiamondScaleArmor(player) ||
                    ArmorUtils.isWearingFullGoldScaleArmor(player) || ArmorUtils.isWearingFullIronScaleArmor(player))) {
                if (isPoisonDamageSource(source)) {
                    cir.setReturnValue(false);
                }
            }
        }
    }
    @Unique
    private boolean isPoisonDamageSource(DamageSource source) {
        return source.getName().equals("magic") ;
    }
}
