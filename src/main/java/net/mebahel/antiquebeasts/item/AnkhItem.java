package net.mebahel.antiquebeasts.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import net.minecraft.util.math.random.Random;

public class AnkhItem extends Item {

    public AnkhItem(Settings settings) {
        super(settings);
    }

    // Comportement du totem : régénération 2 pendant 5 secondes à l'activation
    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!world.isClient) {
            // Appliquer Régénération II pendant 5 secondes (5 * 20 = 100 ticks)
            if (user instanceof PlayerEntity) {
                ((PlayerEntity) user).addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 100, 1));

                // Jouer un son de totem classique
                world.playSound(null, user.getBlockPos(), SoundEvents.ITEM_TOTEM_USE, SoundCategory.PLAYERS, 1.0F, 1.0F);

                // Consommer l'Ankh après utilisation
                stack.decrement(1);
            }
        }
        return stack;
    }

    // Définir l'animation d'utilisation comme un totem
    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BLOCK;
    }

    // Vérifier si l'Ankh est dans la main toutes les 5 secondes, avec une chance sur 6 d'obtenir régénération I pendant 5 secondes
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient && entity instanceof PlayerEntity player) {
            // Si le joueur tient l'Ankh dans la main principale ou secondaire
            if (player.getMainHandStack() == stack || player.getOffHandStack() == stack) {
                // Vérifier toutes les 100 ticks (5 secondes)
                if (world.getTime() % 100 == 0) {
                    Random random = Random.create();
                    if (random.nextInt(6) == 0) { // Chance de 1/6
                        // Appliquer Régénération I pendant 5 secondes (5 * 20 = 100 ticks)
                        player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 100, 0));
                    }
                }
            }
        }
    }
}
