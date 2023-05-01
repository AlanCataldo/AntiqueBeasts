package net.mebahel.antiquebeasts.item;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;

public class RawCyclopsMeat extends Item {

    public RawCyclopsMeat() {
        super(new Item.Settings()
                .group(ItemGroup.FOOD)
                .food(new FoodComponent.Builder()
                        .hunger(3)
                        .saturationModifier(2f)
                        .meat()
                        .statusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 200, 0), 1)
                        .build())
        );
    }
}
