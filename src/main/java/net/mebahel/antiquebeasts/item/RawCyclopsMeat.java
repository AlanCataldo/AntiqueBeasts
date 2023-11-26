package net.mebahel.antiquebeasts.item;

import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

public class RawCyclopsMeat extends Item {

    public RawCyclopsMeat() {
        super(new Item.Settings()
                .food(new FoodComponent.Builder()
                        .hunger(4)
                        .saturationModifier(4f)
                        .meat()
                        .build())
        );
    }
}
