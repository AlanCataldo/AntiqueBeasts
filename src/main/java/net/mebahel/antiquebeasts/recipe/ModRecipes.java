package net.mebahel.antiquebeasts.recipe;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModRecipes {
    public static void registerRecipes() {
        Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(AntiqueBeasts.MOD_ID, BloodInfusingRecipe.Serializer.ID),
                BloodInfusingRecipe.Serializer.INSTANCE);
        Registry.register(Registry.RECIPE_TYPE, new Identifier(AntiqueBeasts.MOD_ID, BloodInfusingRecipe.Type.ID),
                BloodInfusingRecipe.Type.INSTANCE);
    }
}