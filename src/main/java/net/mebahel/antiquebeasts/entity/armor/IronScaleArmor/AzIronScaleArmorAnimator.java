package net.mebahel.antiquebeasts.entity.armor.IronScaleArmor;

import mod.azure.azurelib.rewrite.animation.controller.AzAnimationController;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.rewrite.animation.impl.AzItemAnimator;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class AzIronScaleArmorAnimator extends AzItemAnimator {
    private static final Identifier ANIMATIONS = new Identifier(
            AntiqueBeasts.MOD_ID,
            "animations/item/gold_plate_armor.animation.json"
    );

    @Override
    public void registerControllers(AzAnimationControllerContainer<ItemStack> animationControllerContainer) {
        animationControllerContainer.add(
                AzAnimationController.builder(this, "base_controller").build()
        );
    }

    @Override
    public @NotNull Identifier getAnimationLocation(ItemStack animatable) {
        return ANIMATIONS;
    }
}
