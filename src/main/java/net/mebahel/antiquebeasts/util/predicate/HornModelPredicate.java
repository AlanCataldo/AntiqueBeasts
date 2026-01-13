package net.mebahel.antiquebeasts.util.predicate;

import net.mebahel.antiquebeasts.item.custom.ModItems;
import net.minecraft.client.item.ClampedModelPredicateProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public final class HornModelPredicate {
    private HornModelPredicate() {}

    private static final Identifier TOOTING = new Identifier("minecraft", "tooting");

    public static void registerHornModels() {
        register(ModItems.EINHERJAR_HORN);
    }

    private static void register(Item horn) {
        PredicateBridge.register(horn, TOOTING, new TootingPredicate());
    }

    private static final class TootingPredicate implements ClampedModelPredicateProvider {
        @Override
        public float unclampedCall(ItemStack stack,
                                   @Nullable ClientWorld world,
                                   @Nullable LivingEntity entity,
                                   int seed) {
            if (entity == null) return 0.0F;
            return (entity.isUsingItem() && entity.getActiveItem() == stack) ? 1.0F : 0.0F;
        }
    }
}
