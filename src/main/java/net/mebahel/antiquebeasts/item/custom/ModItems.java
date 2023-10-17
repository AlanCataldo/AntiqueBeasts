package net.mebahel.antiquebeasts.item.custom;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.ModEntities;
import net.mebahel.antiquebeasts.item.*;
import net.minecraft.entity.EquipmentSlot;
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

    public static final Item CHAMPION_HOPLITE_SPAWN_EGG = registerItem("champion_hoplite_spawn_egg",
            new SpawnEggItem(ModEntities.CHAMPION_HOPLITE,16763422, 7498774,
                    new FabricItemSettings().group(ItemGroup.MISC)));

    public static final Item ELITE_HOPLITE_SPAWN_EGG = registerItem("elite_hoplite_spawn_egg",
            new SpawnEggItem(ModEntities.ELITE_HOPLITE,15592942, 8487298,
                    new FabricItemSettings().group(ItemGroup.MISC)));

    public static final Item HERO_HOPLITE_SPAWN_EGG = registerItem("hero_hoplite_spawn_egg",
            new SpawnEggItem(ModEntities.HERO_HOPLITE,5109471, 1334867,
                    new FabricItemSettings().group(ItemGroup.MISC)));

    public static final Item FROST_SWORD = registerItem("frost_sword",
            new FrostSword(ModToolMaterial.FROST_WEAPON,6, -2.4f,
                    new FabricItemSettings().group(ItemGroup.COMBAT)));

    public static final Item BLOOD_STAINED_FROST_SWORD = registerItem("blood_stained_frost_sword",
            new BloodStainedFrostSword(ModToolMaterial.FROST_WEAPON,6, -2.8f,
                    new FabricItemSettings().group(ItemGroup.COMBAT)) {
                @Override
                public boolean hasGlint(ItemStack stack) {
                    return true;
                }
            });

    public static final Item FROST_SHARD = registerItem("frost_shard",
            new Item(new FabricItemSettings().group(ItemGroup.MISC)));

    public static final Item CYCLOPS_BLOOD = registerItem("cyclops_blood",
            new CyclopsBloodItem(new FabricItemSettings().group(ItemGroup.FOOD)));

    public static final Item CONCENTRATED_CYCLOPS_BLOOD = registerItem("concentrated_cyclops_blood",
            new Item(new FabricItemSettings().group(ItemGroup.MISC)) {
                @Override
                public boolean hasGlint(ItemStack stack) {
                    return true;
                }
            });

    public static final Item CYCLOPS_EYE = registerItem("cyclops_eye",
            new Item(new FabricItemSettings().group(ItemGroup.MISC)));

    public static final Item IRON_PLATE = registerItem("iron_plate",
            new Item(new FabricItemSettings().group(ItemGroup.MATERIALS)));

    public static final Item GOLD_PLATE = registerItem("gold_plate",
            new Item(new FabricItemSettings().group(ItemGroup.MATERIALS)));

    public static final Item DIAMOND_PLATE = registerItem("diamond_plate",
            new Item(new FabricItemSettings().group(ItemGroup.MATERIALS)));

    public static final Item IRON_PLATE_HELMET = registerItem("iron_plate_helmet",
            new IronPlateArmorItem(ModArmorMaterials.IRON_PLATE, EquipmentSlot.HEAD, new FabricItemSettings().group(ItemGroup.COMBAT)));
    public static final Item IRON_PLATE_CHESTPLATE = registerItem("iron_plate_chestplate",
            new IronPlateArmorItem(ModArmorMaterials.IRON_PLATE, EquipmentSlot.CHEST, new FabricItemSettings().group(ItemGroup.COMBAT)));
    public static final Item IRON_PLATE_LEGGINGS = registerItem("iron_plate_leggings",
            new IronPlateArmorItem(ModArmorMaterials.IRON_PLATE, EquipmentSlot.LEGS, new FabricItemSettings().group(ItemGroup.COMBAT)));
    public static final Item IRON_PLATE_BOOTS = registerItem("iron_plate_boots",
            new IronPlateArmorItem(ModArmorMaterials.IRON_PLATE, EquipmentSlot.FEET, new FabricItemSettings().group(ItemGroup.COMBAT)));

    public static final Item GOLD_PLATE_HELMET = registerItem("gold_plate_helmet",
            new GoldPlateArmorItem(ModArmorMaterials.GOLD_PLATE, EquipmentSlot.HEAD, new FabricItemSettings().group(ItemGroup.COMBAT)));
    public static final Item GOLD_PLATE_CHESTPLATE = registerItem("gold_plate_chestplate",
            new GoldPlateArmorItem(ModArmorMaterials.GOLD_PLATE, EquipmentSlot.CHEST, new FabricItemSettings().group(ItemGroup.COMBAT)));
    public static final Item GOLD_PLATE_LEGGINGS = registerItem("gold_plate_leggings",
            new GoldPlateArmorItem(ModArmorMaterials.GOLD_PLATE, EquipmentSlot.LEGS, new FabricItemSettings().group(ItemGroup.COMBAT)));
    public static final Item GOLD_PLATE_BOOTS = registerItem("gold_plate_boots",
            new GoldPlateArmorItem(ModArmorMaterials.GOLD_PLATE, EquipmentSlot.FEET, new FabricItemSettings().group(ItemGroup.COMBAT)));


    public static final Item DIAMOND_PLATE_HELMET = registerItem("diamond_plate_helmet",
            new DiamondPlateArmorItem(ModArmorMaterials.DIAMOND_PLATE, EquipmentSlot.HEAD, new FabricItemSettings().group(ItemGroup.COMBAT)));
    public static final Item DIAMOND_PLATE_CHESTPLATE = registerItem("diamond_plate_chestplate",
            new DiamondPlateArmorItem(ModArmorMaterials.DIAMOND_PLATE, EquipmentSlot.CHEST, new FabricItemSettings().group(ItemGroup.COMBAT)));
    public static final Item DIAMOND_PLATE_LEGGINGS = registerItem("diamond_plate_leggings",
            new DiamondPlateArmorItem(ModArmorMaterials.DIAMOND_PLATE, EquipmentSlot.LEGS, new FabricItemSettings().group(ItemGroup.COMBAT)));
    public static final Item DIAMOND_PLATE_BOOTS = registerItem("diamond_plate_boots",
            new DiamondPlateArmorItem(ModArmorMaterials.DIAMOND_PLATE, EquipmentSlot.FEET, new FabricItemSettings().group(ItemGroup.COMBAT)));


    public static final Item RAW_CYCLOPS_MEAT = registerItem("raw_cyclops_meat",
            new RawCyclopsMeat());

    public static final Item COOKED_CYCLOPS_MEAT = registerItem("cooked_cyclops_meat",
            new CookedCyclopsMeat());

    public static final Item THROWINGROCK = registerItem("throwingrock",
            new ThrowingRockItem(new FabricItemSettings().group(ItemGroup.MISC)));


    private static Item registerItem(String name, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(AntiqueBeasts.MOD_ID, name), item);
    }

    public static void registerModItems() {
        AntiqueBeasts.LOGGER.debug("Registering Mod Items for " + AntiqueBeasts.MOD_ID);
    }
}

