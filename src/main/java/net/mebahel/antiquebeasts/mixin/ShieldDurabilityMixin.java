package net.mebahel.antiquebeasts.mixin;

import net.mebahel.antiquebeasts.item.CustomShieldItem;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(PlayerEntity.class)
public class ShieldDurabilityMixin {
    @Inject(at = @At(value = "HEAD"), method = "damageShield(F)V", locals = LocalCapture.CAPTURE_FAILHARD)
    private void damageShield(float amount, CallbackInfo callBackInfo) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        ItemStack activeItem = player.getActiveItem();

        if (activeItem.getItem() instanceof CustomShieldItem) {
            if (amount >= 3.0F) {
                int i = 1 + MathHelper.floor(amount);
                Hand hand = player.getActiveHand();
                activeItem.damage(i, (LivingEntity) player, ((playerEntity) -> player.sendToolBreakStatus(hand)));
                if (activeItem.isEmpty()) {
                    if (hand == Hand.MAIN_HAND) {
                        player.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                    } else {
                        player.equipStack(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
                    }
                }
            }
        }
    }
}