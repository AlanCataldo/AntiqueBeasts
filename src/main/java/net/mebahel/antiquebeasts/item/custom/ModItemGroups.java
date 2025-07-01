package net.mebahel.antiquebeasts.item.custom;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.block.ModBlocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;


public class ModItemGroups {
    public static final ItemGroup ANTIQUE_BEASTS_GROUP = Registry.register(Registries.ITEM_GROUP,
            new Identifier(AntiqueBeasts.MOD_ID, "antiquebeasts"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.antiquebeasts"))
                    .icon(()-> new ItemStack(ModItems.GOLD_PLATE_HELMET)).entries((displayContext, entries) -> {
                        entries.add(ModBlocks.AMPHORA);
                        entries.add(ModBlocks.CURSED_GOLDEN_BLOCK);
                        entries.add(ModBlocks.MUMMY_BOSS_ALTAR);
                        entries.add(ModBlocks.DRAUGR_CHEST);
                        entries.add(ModBlocks.GREEK_CHEST);
                        entries.add(ModBlocks.DWEMER_CHEST);
                        entries.add(ModBlocks.DWEMER_METAL_BLOCK);
                        entries.add(ModBlocks.DWEMER_METAL_PIPE);
                        entries.add(ModBlocks.DWEMER_METAL_PIPE_GEAR);
                        entries.add(ModItems.DWEMER_METAL_GEAR);
                        entries.add(ModItems.DWEMER_METAL_SCRAP);
                        entries.add(ModItems.DWEMER_METAL_INGOT);

                        entries.add(ModItems.DWEMER_DAGGER);
                        entries.add(ModItems.DWEMER_SWORD);
                        entries.add(ModItems.DWEMER_MACE);
                        entries.add(ModItems.DWEMER_WARHAMMER);
                        entries.add(ModItems.DWEMER_AXE);
                        entries.add(ModItems.DWEMER_WAR_AXE);

                        entries.add(ModItems.WOOD_KHOPESH);
                        entries.add(ModItems.STONE_KHOPESH);
                        entries.add(ModItems.IRON_KHOPESH);
                        entries.add(ModItems.GOLD_KHOPESH);
                        entries.add(ModItems.DIAMOND_KHOPESH);
                        entries.add(ModItems.NETHERITE_KHOPESH);
                        entries.add(ModItems.IRON_EGYPTIAN_HALBERD);
                        entries.add(ModItems.GOLD_EGYPTIAN_HALBERD);
                        entries.add(ModItems.DIAMOND_EGYPTIAN_HALBERD);
                        entries.add(ModItems.NETHERITE_EGYPTIAN_HALBERD);
                        entries.add(ModItems.IRON_EGYPTIAN_SHIELD);
                        entries.add(ModItems.GOLD_EGYPTIAN_SHIELD);
                        entries.add(ModItems.PHARAOH_SCEPTER);
                        entries.add(ModItems.IRON_HOPLITE_SPEAR);
                        entries.add(ModItems.GOLD_HOPLITE_SPEAR);
                        entries.add(ModItems.DIAMOND_HOPLITE_SPEAR);
                        entries.add(ModItems.NETHERITE_HOPLITE_SPEAR);
                        entries.add(ModItems.IRON_CENTAUR_SWORD);
                        entries.add(ModItems.HIGH_IRON_SWORD);
                        entries.add(ModItems.THROWING_AXE_ITEM);
                        entries.add(ModItems.HERSIR_AXE);
                        entries.add(ModItems.VALKYRIE_SPEAR);
                        entries.add(ModItems.FROST_SWORD);
                        entries.add(ModItems.FROST_BOW);
                        entries.add(ModItems.BLOOD_STAINED_FROST_SWORD);
                        entries.add(ModItems.EGYPTIAN_RECURVE_BOW);
                        entries.add(ModItems.GREEK_COMPOSITE_BOW);
                        entries.add(ModItems.DRAUGR_BOW);
                        entries.add(ModItems.EBONY_BOW);
                        entries.add(ModItems.EBONY_GREATSWORD);
                        entries.add(ModItems.GREEK_STAFF_HANDLE);
                        entries.add(ModItems.FROST_BITE_GREEK_STAFF);
                        entries.add(ModItems.FROST_SPIKE_GREEK_STAFF);
                        entries.add(ModItems.FROST_BITE_MAGICAL_STONE);
                        entries.add(ModItems.FROST_SPIKE_MAGICAL_STONE);

                        entries.add(ModItems.VALKYRIE_HELMET);
                        entries.add(ModItems.VALKYRIE_CHESTPLATE);
                        entries.add(ModItems.VALKYRIE_LEGGINGS);
                        entries.add(ModItems.VALKYRIE_BOOTS);
                        entries.add(ModItems.IRON_PLATE_HELMET);
                        entries.add(ModItems.IRON_PLATE_CHESTPLATE);
                        entries.add(ModItems.IRON_PLATE_LEGGINGS);
                        entries.add(ModItems.IRON_PLATE_BOOTS);
                        entries.add(ModItems.GOLD_PLATE_HELMET);
                        entries.add(ModItems.GOLD_PLATE_CHESTPLATE);
                        entries.add(ModItems.GOLD_PLATE_LEGGINGS);
                        entries.add(ModItems.GOLD_PLATE_BOOTS);
                        entries.add(ModItems.DIAMOND_PLATE_HELMET);
                        entries.add(ModItems.DIAMOND_PLATE_CHESTPLATE);
                        entries.add(ModItems.DIAMOND_PLATE_LEGGINGS);
                        entries.add(ModItems.DIAMOND_PLATE_BOOTS);
                        entries.add(ModItems.NETHERITE_PLATE_HELMET);
                        entries.add(ModItems.NETHERITE_PLATE_CHESTPLATE);
                        entries.add(ModItems.NETHERITE_PLATE_LEGGINGS);
                        entries.add(ModItems.NETHERITE_PLATE_BOOTS);
                        entries.add(ModItems.IRON_SCALE_HELMET);
                        entries.add(ModItems.IRON_SCALE_CHESTPLATE);
                        entries.add(ModItems.IRON_SCALE_LEGGINGS);
                        entries.add(ModItems.IRON_SCALE_BOOTS);
                        entries.add(ModItems.GOLD_SCALE_HELMET);
                        entries.add(ModItems.GOLD_SCALE_CHESTPLATE);
                        entries.add(ModItems.GOLD_SCALE_LEGGINGS);
                        entries.add(ModItems.GOLD_SCALE_BOOTS);
                        entries.add(ModItems.DIAMOND_SCALE_HELMET);
                        entries.add(ModItems.DIAMOND_SCALE_CHESTPLATE);
                        entries.add(ModItems.DIAMOND_SCALE_LEGGINGS);
                        entries.add(ModItems.DIAMOND_SCALE_BOOTS);

                        entries.add(ModItems.IRON_PLATE_SHIELD);
                        entries.add(ModItems.GOLD_PLATE_SHIELD);
                        entries.add(ModItems.DIAMOND_PLATE_SHIELD);
                        entries.add(ModItems.NETHERITE_PLATE_SHIELD);
                        entries.add(ModItems.HIGH_IRON_SHIELD);

                        entries.add(ModItems.IRON_PLATE);
                        entries.add(ModItems.GOLD_PLATE);
                        entries.add(ModItems.DIAMOND_PLATE);
                        entries.add(ModItems.IRON_SCARAB);
                        entries.add(ModItems.GOLD_SCARAB);
                        entries.add(ModItems.DIAMOND_SCARAB);
                        entries.add(ModItems.CURSED_SCARAB);
                        entries.add(ModItems.HIGH_IRON_SCRAP);
                        entries.add(ModItems.HIGH_IRON_INGOT);
                        entries.add(ModItems.EBONY_SCRAP);
                        entries.add(ModItems.EBONY_INGOT);
                        entries.add(ModItems.CHIMERA_HAIR);
                        entries.add(ModItems.FROST_SHARD);
                        entries.add(ModItems.CYCLOPS_BLOOD);
                        entries.add(ModItems.CONCENTRATED_CYCLOPS_BLOOD);
                        entries.add(ModItems.RAW_CYCLOPS_MEAT);
                        entries.add(ModItems.COOKED_CYCLOPS_MEAT);
                        entries.add(ModItems.ANKH);
                        entries.add(ModItems.ANKH_FRAGMENT);
                        entries.add(ModItems.THURISAZ_RUNE);

                        entries.add(ModItems.HARPY_FEATHER);

                        entries.add(ModItems.N_D_NILE_MUSIC_DISC);
                        entries.add(ModItems.BEHOLD_THE_GREAT_SCIENCE_FI_MUSIC_DISC);

                        entries.add(ModItems.ELEPHANT_RIDER_SPAWN_EGG);
                        entries.add(ModItems.CAMELRY_SPAWN_EGG);
                        entries.add(ModItems.AXEMAN_SPAWN_EGG);
                        entries.add(ModItems.WADJET_SPAWN_EGG);
                        entries.add(ModItems.MUMMY_SPAWN_EGG);
                        entries.add(ModItems.SERVANT_SPAWN_EGG);
                        entries.add(ModItems.MUMMY_BOSS_SPAWN_EGG);

                        entries.add(ModItems.CENTAUR_SPAWN_EGG);
                        entries.add(ModItems.CHIMERA_SPAWN_EGG);
                        entries.add(ModItems.CYCLOPS_SPAWN_EGG);
                        entries.add(ModItems.FROST_CYCLOPS_SPAWN_EGG);
                        entries.add(ModItems.ELITE_HOPLITE_SPAWN_EGG);
                        entries.add(ModItems.CHAMPION_HOPLITE_SPAWN_EGG);
                        entries.add(ModItems.HERO_HOPLITE_SPAWN_EGG);
                        entries.add(ModItems.HADES_CHOSEN_SPAWN_EGG);
                        entries.add(ModItems.HADES_SHADE_SPAWN_EGG);
                        entries.add(ModItems.PEGASUS_SPAWN_EGG);

                        entries.add(ModItems.HERSIR_SPAWN_EGG);
                        entries.add(ModItems.HUSKARL_SPAWN_EGG);
                        entries.add(ModItems.THROWING_AXEMAN_SPAWN_EGG);
                        entries.add(ModItems.EINHERJAR_SPAWN_EGG);
                        entries.add(ModItems.VALKYRIE_SPAWN_EGG);

                        entries.add(ModItems.SKELETON_WARRIOR);
                        entries.add(ModItems.SKELETON_WARRIOR_HEAD);
                        entries.add(ModItems.DRAUGR_SPAWN_EGG);
                        entries.add(ModItems.DRAUGR_OVERLORD_SPAWN_EGG);
                        entries.add(ModItems.DRAUGR_WIGHT_SPAWN_EGG);
                        entries.add(ModItems.DRAUGR_SCOURGE_SPAWN_EGG);
                        entries.add(ModItems.DRAUGR_ARCHER_SPAWN_EGG);
                        entries.add(ModItems.HARPY_SPAWN_EGG);

                        entries.add(ModSpawnEggs.DWEMER_SPIDER_SPAWN_EGG);
                        entries.add(ModSpawnEggs.DWEMER_SPIDER_GUARDIAN_SPAWN_EGG);
                        entries.add(ModSpawnEggs.DWEMER_CENTURION_SPAWN_EGG);

                    }).build());
    public static void registerItemGroups() {
        AntiqueBeasts.LOGGER.info("[Mebahel's Antique Beasts] Registering item groups");
    }
}
