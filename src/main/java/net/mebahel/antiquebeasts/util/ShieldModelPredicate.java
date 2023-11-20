package net.mebahel.antiquebeasts.util;

import net.mebahel.antiquebeasts.item.custom.ModItems;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public class ShieldModelPredicate {
    public static void registerShieldModels() {
        registerShield(ModItems.IRON_PLATE_SHIELD);
        registerShield(ModItems.GOLD_PLATE_SHIELD);
        registerShield(ModItems.DIAMOND_PLATE_SHIELD);
        registerShield(ModItems.NETHERITE_PLATE_SHIELD);
    }
    private static void registerShield(Item shield) {
        ModelPredicateProviderRegistry.register(
                shield, new Identifier("blocking"),
                (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F
        );
    }
}
