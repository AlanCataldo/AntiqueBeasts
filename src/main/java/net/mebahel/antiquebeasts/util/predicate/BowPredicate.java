package net.mebahel.antiquebeasts.util.predicate;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.item.custom.ModItems;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public final class BowPredicate {
    private BowPredicate() {}

    private static final Identifier PULL = new Identifier("minecraft", "pull");
    private static final Identifier PULLING = new Identifier("minecraft", "pulling");

    public static void registerBowModels() {
        registerSafe(ModItems.EGYPTIAN_RECURVE_BOW);
        registerSafe(ModItems.GREEK_COMPOSITE_BOW);
        registerSafe(ModItems.ANCIENT_NORD_BOW);
        registerSafe(ModItems.EBONY_BOW);
        registerSafe(ModItems.STALHRIM_BOW);
    }

    private static void registerSafe(Item bow) {
        try {
            PredicateBridge.register(bow, PULL,
                    (stack, world, entity, seed) -> PredicateBridge.computeBowPull(stack, entity)
            );

            PredicateBridge.register(bow, PULLING,
                    (stack, world, entity, seed) -> PredicateBridge.isUsingSameItem(stack, entity) ? 1.0F : 0.0F
            );
        } catch (Throwable t) {
            AntiqueBeasts.LOGGER.warn("Bow predicates disabled for {} (Connector compatibility).", bow, t);
        }
    }
}