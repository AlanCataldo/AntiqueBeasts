package net.mebahel.antiquebeasts.item.custom;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.ModEntities;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModSpawnEggs {
    public static final Item DWARVEN_SPIDER_SPAWN_EGG = registerSpawnEgg("spawn_egg/dwarven_spider_spawn_egg",
            new SpawnEggItem(ModEntities.DWARVEN_SPIDER,15978112, 11234357,  new Item.Settings()));
    public static final Item DWARVEN_SPIDER_GUARDIAN_SPAWN_EGG = registerSpawnEgg("spawn_egg/dwarven_spider_guardian_spawn_egg",
            new SpawnEggItem(ModEntities.DWARVEN_SPIDER_GUARDIAN,13671235, 10181160,  new Item.Settings()));
    private static Item registerSpawnEgg(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(AntiqueBeasts.MOD_ID, name), item);
    }

    public static void registerModItems() {
        AntiqueBeasts.LOGGER.info("[Mebahel's Antique Beasts] Registering Mod Spawn Eggs.");

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(entries -> {
            entries.add(ModSpawnEggs.DWARVEN_SPIDER_SPAWN_EGG);
            entries.add(ModSpawnEggs.DWARVEN_SPIDER_GUARDIAN_SPAWN_EGG);
        });
    }
}