package net.mebahel.antiquebeasts.item.custom;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
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
    public static final Item INFERNAL_DRAUGR_SPAWN_EGG = registerSpawnEgg("spawn_egg/infernal_draugr_spawn_egg",
            new SpawnEggItem(ModEntities.INFERNAL_DRAUGR,0xFFFFFF, 0xFFFFFF,
                    new FabricItemSettings()));

    public static final Item FLAME_ATRONACH_SPAWN_EGG = registerSpawnEgg("spawn_egg/flame_atronach_spawn_egg",
            new SpawnEggItem(ModEntities.FLAME_ATRONACH,0xFFFFFF, 0xFFFFFF,
                    new FabricItemSettings()));

    public static final Item SKELETON_WARRIOR_SPAWN_EGG = registerSpawnEgg("spawn_egg/skeleton_warrior_spawn_egg",
            new SpawnEggItem(ModEntities.SKELETON_WARRIOR,0xFFFFFF, 0xFFFFFF,
                    new FabricItemSettings()));

    public static final Item SKELETON_WARRIOR_HEAD_SPAWN_EGG = registerSpawnEgg("spawn_egg/skeleton_warrior_head_spawn_egg",
            new SpawnEggItem(ModEntities.SKELETON_WARRIOR_HEAD,0xFFFFFF, 0xFFFFFF,
                    new FabricItemSettings()));

    public static final Item DRAUGR_SCOURGE_SPAWN_EGG = registerSpawnEgg("spawn_egg/draugr_scourge_spawn_egg",
            new SpawnEggItem(ModEntities.DRAUGR_SCOURGE,0xFFFFFF, 0xFFFFFF,
                    new FabricItemSettings()));

    public static final Item DRAUGR_ARCHER_SPAWN_EGG = registerSpawnEgg("spawn_egg/draugr_archer_spawn_egg",
            new SpawnEggItem(ModEntities.DRAUGR_ARCHER,0xFFFFFF, 0xFFFFFF,
                    new FabricItemSettings()));

    public static final Item DRAUGR_WIGHT_SPAWN_EGG = registerSpawnEgg("spawn_egg/draugr_wight_spawn_egg",
            new SpawnEggItem(ModEntities.DRAUGR_WIGHT,0xFFFFFF, 0xFFFFFF,
                    new FabricItemSettings()));

    public static final Item DRAUGR_SPAWN_EGG = registerSpawnEgg("spawn_egg/draugr_spawn_egg",
            new SpawnEggItem(ModEntities.DRAUGR,0xFFFFFF, 0xFFFFFF,
                    new FabricItemSettings()));

    public static final Item DRAUGR_OVERLORD_SPAWN_EGG = registerSpawnEgg("spawn_egg/draugr_overlord_spawn_egg",
            new SpawnEggItem(ModEntities.DRAUGR_OVERLORD,0xFFFFFF, 0xFFFFFF,
                    new FabricItemSettings()));

    public static final Item DWEMER_SPIDER_SPAWN_EGG = registerSpawnEgg("spawn_egg/dwarven_spider_spawn_egg",
            new SpawnEggItem(ModEntities.DWEMER_SPIDER,15978112, 11234357,  new Item.Settings()));

    public static final Item DWEMER_SPIDER_GUARDIAN_SPAWN_EGG = registerSpawnEgg("spawn_egg/dwarven_spider_guardian_spawn_egg",
            new SpawnEggItem(ModEntities.DWEMER_SPIDER_GUARDIAN,13671235, 10181160,  new Item.Settings()));

    public static final Item DWEMER_CENTURION_SPAWN_EGG = registerSpawnEgg("spawn_egg/dwarven_centurion_spawn_egg",
            new SpawnEggItem(ModEntities.DWEMER_CENTURION,13671235, 10181160,  new Item.Settings()));

    private static Item registerSpawnEgg(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(AntiqueBeasts.MOD_ID, name), item);
    }

    public static void registerModItems() {
        AntiqueBeasts.LOGGER.info("[Mebahel's Antique Beasts] Registering Mod Spawn Eggs.");

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(entries -> {
            entries.add(ModSpawnEggs.DWEMER_SPIDER_SPAWN_EGG);
            entries.add(ModSpawnEggs.DWEMER_SPIDER_GUARDIAN_SPAWN_EGG);
            entries.add(ModSpawnEggs.DWEMER_CENTURION_SPAWN_EGG);

            entries.add(ModSpawnEggs.SKELETON_WARRIOR_SPAWN_EGG);
            entries.add(ModSpawnEggs.SKELETON_WARRIOR_HEAD_SPAWN_EGG);
            entries.add(ModSpawnEggs.DRAUGR_SCOURGE_SPAWN_EGG);
            entries.add(ModSpawnEggs.DRAUGR_ARCHER_SPAWN_EGG);
            entries.add(ModSpawnEggs.DRAUGR_WIGHT_SPAWN_EGG);
            entries.add(ModSpawnEggs.DRAUGR_SPAWN_EGG);
            entries.add(ModSpawnEggs.DRAUGR_OVERLORD_SPAWN_EGG);
            entries.add(ModSpawnEggs.INFERNAL_DRAUGR_SPAWN_EGG);
            entries.add(ModSpawnEggs.FLAME_ATRONACH_SPAWN_EGG);
        });
    }
}