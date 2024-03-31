package net.mebahel.antiquebeasts.util;

import net.mebahel.antiquebeasts.item.custom.ModItems;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public class HornModelPredicate {
    public static void registerHornModels() {
        registerHorn(ModItems.EINHERJAR_HORN);
    }
    private static void registerHorn(Item horn) {
        ModelPredicateProviderRegistry.register(
                horn, new Identifier("tooting"),
                (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F
        );
    }
}
