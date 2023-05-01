package net.mebahel.antiquebeasts.item;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

public class RawFrostCyclopsMeat extends Item {

    public RawFrostCyclopsMeat() {
        super(new Settings()
                .group(ItemGroup.FOOD)
                .food(new FoodComponent.Builder()
                        .hunger(3)
                        .saturationModifier(2f)
                        .meat()
                        .statusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 200, 0), 1)
                        .build())
        );
    }
}
