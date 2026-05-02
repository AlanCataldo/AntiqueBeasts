package net.mebahel.antiquebeasts.item.food;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class MagmaSoupItem extends Item {

    public MagmaSoupItem(Settings settings) {
        super(settings);
    }

    // IMPORTANT: déclenche la consommation au clic droit
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

    // Animation de "boire" type soupe (comme mushroom stew / suspicious stew)
    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.EAT;
    }

    // Temps de consommation standard
    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 32;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        // IMPORTANT: appelle le vanilla pour hunger/saturation + stats + son de fin de conso
        ItemStack result = super.finishUsing(stack, world, user);

        if (!world.isClient) {
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 20 * 120, 1)); // 2 min
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 20 * 240, 0)); // 4 min
            user.setOnFireFor(3);
        }

        // Comportement "soupe": rendre un bol en survie
        if (user instanceof PlayerEntity player) {
            if (player.getAbilities().creativeMode) {
                return result; // créatif: pas de bol, pas de consommation forcée
            }

            ItemStack bowl = new ItemStack(Items.BOWL);

            // Si le stack est vide après le vanilla, on remplace par le bol
            if (result.isEmpty()) {
                return bowl;
            }

            // Sinon on ajoute/drop le bol et on garde le stack résultant
            if (!player.getInventory().insertStack(bowl)) {
                player.dropItem(bowl, false);
            }
        }

        return result;
    }
}
