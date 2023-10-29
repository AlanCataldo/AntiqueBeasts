package net.mebahel.antiquebeasts.item;

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
                        .build())
        );
    }
}
