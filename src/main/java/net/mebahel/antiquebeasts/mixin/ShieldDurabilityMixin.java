package net.mebahel.antiquebeasts.mixin;

import net.mebahel.antiquebeasts.entity.custom.CyclopsEntity;
import net.mebahel.antiquebeasts.entity.custom.EinherjarEntity;
import net.mebahel.antiquebeasts.entity.custom.FrostCyclopsEntity;
import net.mebahel.antiquebeasts.entity.custom.HersirEntity;
import net.mebahel.antiquebeasts.item.CustomShieldItem;
import net.mebahel.antiquebeasts.item.custom.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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
        LivingEntity attacker = player.getAttacker();

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
                if (attacker != null) {
                    ItemStack attackerItem = attacker.getMainHandStack();
                    if (attackerItem.getItem() instanceof AxeItem || attacker instanceof HersirEntity || attacker instanceof CyclopsEntity
                            || attacker instanceof EinherjarEntity) {
                        player.getItemCooldownManager().set(activeItem.getItem(), 100);
                        player.clearActiveItem();
                        player.getWorld().sendEntityStatus(player, (byte)30);
                    }
                }
            }
        }
    }
}