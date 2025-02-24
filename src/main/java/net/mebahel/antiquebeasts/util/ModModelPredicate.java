package net.mebahel.antiquebeasts.util;

import net.mebahel.antiquebeasts.item.custom.ModItems;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public class ModModelPredicate {
    public static void registerStaffModels() {
        //registerStaff(ModItems.ICE_SPIKE_GREEK_STAFF);
    }

    private static void registerStaff(Item staff) {
        // ✅ Predicate pour détecter si l'item est en train d'être chargé
        ModelPredicateProviderRegistry.register(staff, new Identifier("pull"),
                (stack, world, entity, seed) -> {
                    if (entity == null) return 0.0f;
                    if (entity.getActiveItem() != stack) return 0.0f;
                    return (float)(stack.getMaxUseTime() - entity.getItemUseTimeLeft()) / 20.0f;
                });

        // ✅ Predicate pour détecter si l'item est complètement chargé
        ModelPredicateProviderRegistry.register(staff, new Identifier("pulling"),
                (stack, world, entity, seed) -> entity != null && entity.isUsingItem()
                        && entity.getActiveItem() == stack
                        && (stack.getMaxUseTime() - entity.getItemUseTimeLeft()) >= 20 ? 1.0f : 0.0f);
    }
}
