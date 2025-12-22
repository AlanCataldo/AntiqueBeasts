package net.mebahel.antiquebeasts.util;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.item.custom.ModItems;
import net.minecraft.client.item.ClampedModelPredicateProvider;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class BowPredicate {
    private static final Identifier PULL = new Identifier("minecraft", "pull");
    private static final Identifier PULLING = new Identifier("minecraft", "pulling");

    public static void registerBowModels() {
        registerSafe(ModItems.EGYPTIAN_RECURVE_BOW);
        registerSafe(ModItems.GREEK_COMPOSITE_BOW);
        registerSafe(ModItems.DRAUGR_BOW);
        registerSafe(ModItems.EBONY_BOW);
        registerSafe(ModItems.FROST_BOW);
    }

    private static void registerSafe(Item bow) {
        try {
            register(bow);
        } catch (Throwable t) {
            AntiqueBeasts.LOGGER.warn(
                    "Bow predicates disabled for {} (Connector compatibility).",
                    bow, t
            );
        }
    }

    private static void register(Item bow) {
        ModelPredicateProviderRegistry.register(bow, PULL, new PullPredicate());
        ModelPredicateProviderRegistry.register(bow, PULLING, new PullingPredicate());
    }

    private static final class PullPredicate implements ClampedModelPredicateProvider {
        @Override
        public float unclampedCall(ItemStack stack,
                                   @Nullable ClientWorld world,
                                   @Nullable LivingEntity entity,
                                   int seed) {
            if (entity == null) return 0.0F;
            if (entity.getActiveItem() != stack) return 0.0F;

            return (float) (stack.getMaxUseTime() - entity.getItemUseTimeLeft()) / 20.0F;
        }
    }

    private static final class PullingPredicate implements ClampedModelPredicateProvider {
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
