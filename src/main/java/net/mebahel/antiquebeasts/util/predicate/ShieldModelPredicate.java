package net.mebahel.antiquebeasts.util.predicate;

import net.mebahel.antiquebeasts.item.custom.ModItems;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public final class ShieldModelPredicate {
    private ShieldModelPredicate() {}

    private static final Identifier BLOCKING = new Identifier("minecraft", "blocking");

    public static void registerShieldModels() {
        register(ModItems.IRON_PLATE_SHIELD);
        register(ModItems.GOLD_PLATE_SHIELD);
        register(ModItems.DIAMOND_PLATE_SHIELD);
        register(ModItems.NETHERITE_PLATE_SHIELD);
        register(ModItems.HIGH_IRON_SHIELD);
        register(ModItems.IRON_EGYPTIAN_SHIELD);
        register(ModItems.GOLD_EGYPTIAN_SHIELD);
    }

    private static void register(Item shield) {
        PredicateBridge.register(shield, BLOCKING,
                (stack, world, entity, seed) -> PredicateBridge.isUsingSameItem(stack, entity) ? 1.0F : 0.0F
        );
    }
}