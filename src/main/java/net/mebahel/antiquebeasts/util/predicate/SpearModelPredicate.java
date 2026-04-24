package net.mebahel.antiquebeasts.util.predicate;

import net.mebahel.antiquebeasts.item.custom.ModItems;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public final class SpearModelPredicate {
    private SpearModelPredicate() {}

    private static final Identifier THROWING = new Identifier("minecraft", "throwing");

    public static void registerSpearModels() {
        register(ModItems.IRON_HOPLITE_SPEAR);
        register(ModItems.GOLD_HOPLITE_SPEAR);
        register(ModItems.DIAMOND_HOPLITE_SPEAR);
        register(ModItems.NETHERITE_HOPLITE_SPEAR);
        register(ModItems.VALKYRIE_SPEAR);
    }

    private static void register(Item spear) {
        PredicateBridge.register(spear, THROWING,
                (stack, world, entity, seed) -> PredicateBridge.isUsingSameItem(stack, entity) ? 1.0F : 0.0F
        );
    }
}