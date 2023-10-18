package net.mebahel.antiquebeasts.item;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

public class CookedCyclopsMeat extends Item {

    public CookedCyclopsMeat() {
        super(new Settings()
                .group(ItemGroup.FOOD)
                .food(new FoodComponent.Builder()
                        .hunger(8)
                        .saturationModifier(8)
                        .meat()
                        //.statusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 600, 0), 1)
                        .build())
        );
    }
}
