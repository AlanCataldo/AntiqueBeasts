package net.mebahel.antiquebeasts.util.predicate;

import net.mebahel.antiquebeasts.item.custom.ModItems;
import net.minecraft.client.item.ClampedModelPredicateProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

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
        PredicateBridge.register(spear, THROWING, new ThrowingPredicate());
    }

    private static final class ThrowingPredicate implements ClampedModelPredicateProvider {
        @Override
        public float unclampedCall(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed) {
            if (entity == null) return 0.0F;
            return (entity.isUsingItem() && entity.getActiveItem() == stack) ? 1.0F : 0.0F;
        }
    }
}
