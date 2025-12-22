package net.mebahel.antiquebeasts.mixin;

import net.mebahel.antiquebeasts.item.custom.ModItems;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class CustomBowFovMixin {

    @Inject(method = "getFovMultiplier", at = @At("HEAD"), cancellable = true)
    private void mebahel$customBowFov(CallbackInfoReturnable<Float> cir) {
        AbstractClientPlayerEntity player = (AbstractClientPlayerEntity)(Object)this;

        if (!player.isUsingItem()) return;

        ItemStack active = player.getActiveItem();

        // ✅ Limité à TES arcs uniquement
        if (!(active.isOf(ModItems.DRAUGR_BOW)
                || active.isOf(ModItems.EGYPTIAN_RECURVE_BOW)
                || active.isOf(ModItems.GREEK_COMPOSITE_BOW)
                || active.isOf(ModItems.EBONY_BOW)
                || active.isOf(ModItems.FROST_BOW))) {
            return;
        }

        int useTicks = active.getMaxUseTime() - player.getItemUseTimeLeft();
        float t = (float)useTicks / 20.0F;
        if (t > 1.0F) t = 1.0F; else t *= t;

        float zoomStrength = 0.15F; // équivalent vanilla
        cir.setReturnValue(1.0F - t * zoomStrength);
    }
}

