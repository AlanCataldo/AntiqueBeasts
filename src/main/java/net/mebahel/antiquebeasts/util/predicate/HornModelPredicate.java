package net.mebahel.antiquebeasts.util.predicate;

import net.mebahel.antiquebeasts.item.custom.ModItems;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public final class HornModelPredicate {
    private HornModelPredicate() {}

    private static final Identifier TOOTING = new Identifier("minecraft", "tooting");

    public static void registerHornModels() {
        register(ModItems.EINHERJAR_HORN);
    }

    private static void register(Item horn) {
        PredicateBridge.register(horn, TOOTING,
                (stack, world, entity, seed) -> PredicateBridge.isUsingSameItem(stack, entity) ? 1.0F : 0.0F
        );
    }
}