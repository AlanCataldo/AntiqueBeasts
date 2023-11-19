package net.mebahel.antiquebeasts.mixin;

import net.mebahel.antiquebeasts.item.CustomShieldItem;
import net.mebahel.antiquebeasts.item.custom.ModItems;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(PlayerEntity.class)
public class FireDamageMixin {
    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    public void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        Entity attacker = source.getAttacker();
        if (source.isFire() && amount >= 1.0F && attacker == null
                && player.getOffHandStack() != null && player.getOffHandStack() != null
                && ModItems.DIAMOND_PLATE_SHIELD != null && player.getOffHandStack().getItem() == ModItems.DIAMOND_PLATE_SHIELD) {
            cir.setReturnValue(player.damage(source, amount / 2));
            cir.cancel();
        } else if (source.isFire() && amount >= 1.0F && attacker == null
                && player.getMainHandStack() != null && player.getMainHandStack() != null
                && ModItems.DIAMOND_PLATE_SHIELD != null && player.getMainHandStack().getItem() == ModItems.DIAMOND_PLATE_SHIELD) {
            cir.setReturnValue(player.damage(source, amount / 2));
            cir.cancel();
        }
    }
}




