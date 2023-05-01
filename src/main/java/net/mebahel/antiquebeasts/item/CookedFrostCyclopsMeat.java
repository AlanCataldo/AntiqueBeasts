package net.mebahel.antiquebeasts.item;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

public class CookedFrostCyclopsMeat extends Item {

    public CookedFrostCyclopsMeat() {
        super(new Settings()
                .group(ItemGroup.FOOD)
                .food(new FoodComponent.Builder()
                        .hunger(10)
                        .saturationModifier(6)
                        .meat()
                        .statusEffect(new StatusEffectInstance(StatusEffects.SPEED, 600, 0), 1)
                        .build())
        );
    }
}
