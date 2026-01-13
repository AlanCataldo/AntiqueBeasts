package net.mebahel.antiquebeasts.util.predicate;

import net.mebahel.antiquebeasts.item.custom.ModItems;
import net.minecraft.client.item.ClampedModelPredicateProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

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
        PredicateBridge.register(shield, BLOCKING, new BlockingPredicate());
    }

    private static final class BlockingPredicate implements ClampedModelPredicateProvider {
        @Override
        public float unclampedCall(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed) {
            if (entity == null) return 0.0F;
            return (entity.isUsingItem() && entity.getActiveItem() == stack) ? 1.0F : 0.0F;
        }
    }
}
