package net.mebahel.antiquebeasts.potion;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.item.custom.ModItems;
import net.mebahel.antiquebeasts.mixin.BrewingRecipeRegistryMixin;
import net.minecraft.potion.Potions;

public class ModPotions {
    public static void registerPotionRecipe() {
        BrewingRecipeRegistryMixin.invokeRegisterPotionRecipe(Potions.WATER, ModItems.CYCLOPS_BLOOD, Potions.STRENGTH);
        BrewingRecipeRegistryMixin.invokeRegisterPotionRecipe(Potions.AWKWARD, ModItems.CYCLOPS_BLOOD, Potions.STRENGTH);
        BrewingRecipeRegistryMixin.invokeRegisterPotionRecipe(Potions.WATER, ModItems.CONCENTRATED_CYCLOPS_BLOOD, Potions.STRENGTH);
        BrewingRecipeRegistryMixin.invokeRegisterPotionRecipe(Potions.AWKWARD, ModItems.CONCENTRATED_CYCLOPS_BLOOD, Potions.STRENGTH);
        AntiqueBeasts.LOGGER.info("[AntiqueBeasts] Registering potion recipes for " + AntiqueBeasts.MOD_ID + ".");
    }
}
