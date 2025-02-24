package net.mebahel.antiquebeasts.util;

import com.eliotlash.mclib.math.functions.classic.Mod;
import net.mebahel.antiquebeasts.item.custom.ModItems;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public class SpearModelPredicate {
    public static void registerSpearModels() {
        registerSpear(ModItems.IRON_HOPLITE_SPEAR);
        registerSpear(ModItems.GOLD_HOPLITE_SPEAR);
        registerSpear(ModItems.DIAMOND_HOPLITE_SPEAR);
        registerSpear(ModItems.NETHERITE_HOPLITE_SPEAR);
        registerSpear(ModItems.VALKYRIE_SPEAR);
        registerSpear(ModItems.FROST_SPIKE_GREEK_STAFF);
        registerSpear(ModItems.FROST_BITE_GREEK_STAFF);
    }
    private static void registerSpear(Item spear) {
        ModelPredicateProviderRegistry.register(
                spear, new Identifier("throwing"),
                (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F
        );
    }
}
