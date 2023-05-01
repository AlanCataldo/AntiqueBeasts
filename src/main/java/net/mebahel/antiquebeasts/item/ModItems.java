package net.mebahel.antiquebeasts.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.ModEntities;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModItems {
    public static final Item CYCLOPS_SPAWN_EGG = registerItem("cyclops_spawn_egg",
            new SpawnEggItem(ModEntities.CYCLOPS,10053120, 6821916,
                    new FabricItemSettings().group(ItemGroup.MISC)));

    public static final Item FROST_CYCLOPS_SPAWN_EGG = registerItem("frost-cyclops_spawn_egg",
            new SpawnEggItem(ModEntities.FROST_CYCLOPS,6532039, 6821916,
                    new FabricItemSettings().group(ItemGroup.MISC)));

    public static final Item RAW_CYCLOPS_MEAT = registerItem("raw_cyclops_meat",
            new RawCyclopsMeat());

    public static final Item COOKED_CYCLOPS_MEAT = registerItem("cooked_cyclops_meat",
            new CookedCyclopsMeat());

    public static final Item RAW_FROSTCYCLOPS_MEAT = registerItem("raw_frost-cyclops_meat",
            new RawFrostCyclopsMeat());

    public static final Item COOKED_FROSTCYCLOPS_MEAT = registerItem("cooked_frost-cyclops_meat",
            new CookedFrostCyclopsMeat());

    public static final Item THROWINGROCK = registerItem("throwingrock",
            new ThrowingRockItem(new FabricItemSettings().group(ItemGroup.MISC)));


    private static Item registerItem(String name, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(AntiqueBeasts.MOD_ID, name), item);
    }

    public static void registerModItems() {
        AntiqueBeasts.LOGGER.debug("Registering Mod Items for " + AntiqueBeasts.MOD_ID);
    }
}

