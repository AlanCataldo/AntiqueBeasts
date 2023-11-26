package net.mebahel.antiquebeasts.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class ConcentratedCyclopsBloodItem extends Item {
    public ConcentratedCyclopsBloodItem(Settings settings) {
        super(settings.food(new FoodComponent.Builder()
                .hunger(4)
                .saturationModifier(2f)
                .meat()
                .build()));
    }
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        super.finishUsing(stack, world, user);
        if (!world.isClient) {
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 45 * 20, 1));
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 5 * 20, 0));
        }
        if (!stack.isEmpty()) {
            ItemStack stackCopy = stack.copy();
            if (!stackCopy.isEmpty()) {
                user.setStackInHand(Hand.MAIN_HAND, stackCopy);
            } else {
                user.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
            }
        }
        return stack;
    }
    public int getMaxUseTime(ItemStack stack) {
        return 40;
    }
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }
    public SoundEvent getDrinkSound() {
        return SoundEvents.ITEM_HONEY_BOTTLE_DRINK;
    }
    public SoundEvent getEatSound() {
        return SoundEvents.ITEM_HONEY_BOTTLE_DRINK;
    }
    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return ItemUsage.consumeHeldItem(world, user, hand);
    }
}
