package net.mebahel.antiquebeasts.item.custom;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.ModEntities;
import net.mebahel.antiquebeasts.item.*;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ModItems {
    public static final Item CYCLOPS_SPAWN_EGG = registerItem("cyclops_spawn_egg",
            new SpawnEggItem(ModEntities.CYCLOPS,10053120, 6821916,
                    new FabricItemSettings()));
    public static final Item FROST_CYCLOPS_SPAWN_EGG = registerItem("frost-cyclops_spawn_egg",
            new SpawnEggItem(ModEntities.FROST_CYCLOPS,6532039, 6821916,
                    new FabricItemSettings()));
    public static final Item CHAMPION_HOPLITE_SPAWN_EGG = registerItem("champion_hoplite_spawn_egg",
            new SpawnEggItem(ModEntities.CHAMPION_HOPLITE,16763422, 7498774,
                    new FabricItemSettings()));
    public static final Item ELITE_HOPLITE_SPAWN_EGG = registerItem("elite_hoplite_spawn_egg",
            new SpawnEggItem(ModEntities.ELITE_HOPLITE,15592942, 8487298,
                    new FabricItemSettings()));
    public static final Item HERO_HOPLITE_SPAWN_EGG = registerItem("hero_hoplite_spawn_egg",
            new SpawnEggItem(ModEntities.HERO_HOPLITE,5109471, 1334867,
                    new FabricItemSettings()));
    public static final Item HADES_SHADE_SPAWN_EGG = registerItem("hades_shade_spawn_egg",
            new SpawnEggItem(ModEntities.HADES_SHADE,3815994, 14342874,
                    new FabricItemSettings()));
    public static final Item HADES_CHOSEN_SPAWN_EGG = registerItem("hades_chosen_spawn_egg",
            new SpawnEggItem(ModEntities.HADES_CHOSEN,2563101, 13355979,
                    new FabricItemSettings()));
    public static final Item FROST_SWORD = registerItem("frost_sword",
            new FrostSword(ModToolMaterial.FROST_WEAPON,6, -2.8f,
                    new FabricItemSettings()));
    public static final Item BLOOD_STAINED_FROST_SWORD = registerItem("blood_stained_frost_sword",
            new BloodStainedFrostSword(ModToolMaterial.FROST_WEAPON,7, -3f,
                    new FabricItemSettings()) {
                @Override
                public boolean hasGlint(ItemStack stack) {
                    return true;
                }
            });
    public static final Item FROST_SHARD = registerItem("frost_shard",
            new Item(new FabricItemSettings()));
    public static final Item CYCLOPS_BLOOD = registerItem("cyclops_blood",
            new CyclopsBloodItem(new FabricItemSettings()));
    public static final Item CONCENTRATED_CYCLOPS_BLOOD = registerItem("concentrated_cyclops_blood",
            new ConcentratedCyclopsBloodItem(new FabricItemSettings()));
    public static final Item IRON_PLATE = registerItem("iron_plate",
            new Item(new FabricItemSettings()));
    public static final Item IRON_PLATE_SHIELD = registerItem("shield/iron_plate_shield",
            new CustomShieldItem(new FabricItemSettings().maxDamage(550)));
    public static final Item GOLD_PLATE_SHIELD = registerItem("shield/gold_plate_shield",
            new CustomShieldItem(new FabricItemSettings().maxDamage(700)));
    public static final Item DIAMOND_PLATE_SHIELD = registerItem("shield/diamond_plate_shield",
            new CustomShieldItem(new FabricItemSettings().maxDamage(850)) {
                @Override
                public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
                    tooltip.add(Text.translatable("item.antiquebeasts.diamond_plate_shield.tooltip").formatted(Formatting.GRAY, Formatting.ITALIC));
                }
            });
    public static final Item NETHERITE_PLATE_SHIELD = registerItem("shield/netherite_plate_shield",
            new CustomShieldItem(new FabricItemSettings().maxDamage(1000)){
                @Override
                public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
                    tooltip.add(Text.translatable("item.antiquebeasts.netherite_plate_shield.tooltip").formatted(Formatting.GRAY, Formatting.ITALIC));
                }
            });
    public static final Item GOLD_PLATE = registerItem("gold_plate",
            new Item(new FabricItemSettings()));
    public static final Item DIAMOND_PLATE = registerItem("diamond_plate",
            new Item(new FabricItemSettings()));
    public static final IronPlateArmorItem IRON_PLATE_HELMET = registerItem("iron_plate_helmet",
            new IronPlateArmorItem(ModArmorMaterials.IRON_PLATE, ArmorItem.Type.HELMET, new FabricItemSettings()));
    public static final IronPlateArmorItem IRON_PLATE_CHESTPLATE = registerItem("iron_plate_chestplate",
            new IronPlateArmorItem(ModArmorMaterials.IRON_PLATE, ArmorItem.Type.CHESTPLATE, new FabricItemSettings()));
    public static final IronPlateArmorItem IRON_PLATE_LEGGINGS = registerItem("iron_plate_leggings",
            new IronPlateArmorItem(ModArmorMaterials.IRON_PLATE, ArmorItem.Type.LEGGINGS, new FabricItemSettings()));
    public static final IronPlateArmorItem IRON_PLATE_BOOTS = registerItem("iron_plate_boots",
            new IronPlateArmorItem(ModArmorMaterials.IRON_PLATE, ArmorItem.Type.BOOTS, new FabricItemSettings()));
    public static final GoldPlateArmorItem GOLD_PLATE_HELMET = registerItem("gold_plate_helmet",
            new GoldPlateArmorItem(ModArmorMaterials.GOLD_PLATE, ArmorItem.Type.HELMET, new FabricItemSettings()));
    public static final GoldPlateArmorItem GOLD_PLATE_CHESTPLATE = registerItem("gold_plate_chestplate",
            new GoldPlateArmorItem(ModArmorMaterials.GOLD_PLATE, ArmorItem.Type.CHESTPLATE, new FabricItemSettings()));
    public static final GoldPlateArmorItem GOLD_PLATE_LEGGINGS = registerItem("gold_plate_leggings",
            new GoldPlateArmorItem(ModArmorMaterials.GOLD_PLATE, ArmorItem.Type.LEGGINGS, new FabricItemSettings()));
    public static final GoldPlateArmorItem GOLD_PLATE_BOOTS = registerItem("gold_plate_boots",
            new GoldPlateArmorItem(ModArmorMaterials.GOLD_PLATE, ArmorItem.Type.BOOTS, new FabricItemSettings()));
    public static final DiamondPlateArmorItem DIAMOND_PLATE_HELMET = registerItem("diamond_plate_helmet",
            new DiamondPlateArmorItem(ModArmorMaterials.DIAMOND_PLATE, ArmorItem.Type.HELMET, new FabricItemSettings()));
    public static final DiamondPlateArmorItem DIAMOND_PLATE_CHESTPLATE = registerItem("diamond_plate_chestplate",
            new DiamondPlateArmorItem(ModArmorMaterials.DIAMOND_PLATE, ArmorItem.Type.CHESTPLATE, new FabricItemSettings()));
    public static final DiamondPlateArmorItem DIAMOND_PLATE_LEGGINGS = registerItem("diamond_plate_leggings",
            new DiamondPlateArmorItem(ModArmorMaterials.DIAMOND_PLATE, ArmorItem.Type.LEGGINGS, new FabricItemSettings()));
    public static final DiamondPlateArmorItem DIAMOND_PLATE_BOOTS = registerItem("diamond_plate_boots",
            new DiamondPlateArmorItem(ModArmorMaterials.DIAMOND_PLATE, ArmorItem.Type.BOOTS, new FabricItemSettings()));
    public static final Item RAW_CYCLOPS_MEAT = registerItem("raw_cyclops_meat",
            new RawCyclopsMeat());
    public static final Item COOKED_CYCLOPS_MEAT = registerItem("cooked_cyclops_meat",
            new CookedCyclopsMeat());
    public static final Item IRON_HOPLITE_SPEAR = registerItem("weapon/iron_hoplite_spear",
            new ThrowingHopliteSpear(new FabricItemSettings().maxDamage(300), "iron"));
    public static final Item GOLD_HOPLITE_SPEAR = registerItem("weapon/gold_hoplite_spear",
            new ThrowingHopliteSpear(new FabricItemSettings().maxDamage(450), "gold"));
    public static final Item DIAMOND_HOPLITE_SPEAR = registerItem("weapon/diamond_hoplite_spear",
            new ThrowingHopliteSpear(new FabricItemSettings().maxDamage(600), "diamond"));
    public static final Item NETHERITE_HOPLITE_SPEAR = registerItem("weapon/netherite_hoplite_spear", new ThrowingHopliteSpear(new FabricItemSettings().maxDamage(800), "netherite") {
        @Override
        public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
            tooltip.add(Text.translatable("item.antiquebeasts.netherite_hoplite_spear.tooltip").formatted(Formatting.GRAY, Formatting.ITALIC));
        }
    });
    public static <I extends Item> I registerItem(String name, I item) {
        return Registry.register(Registries.ITEM, new Identifier(AntiqueBeasts.MOD_ID, name), item);
    }
    public static void addItemToSpawnEggItemGroup(FabricItemGroupEntries entries) {
        entries.add(CYCLOPS_SPAWN_EGG);
        entries.add(FROST_CYCLOPS_SPAWN_EGG);
        entries.add(ELITE_HOPLITE_SPAWN_EGG);
        entries.add(CHAMPION_HOPLITE_SPAWN_EGG);
        entries.add(HERO_HOPLITE_SPAWN_EGG);
        entries.add(HADES_CHOSEN_SPAWN_EGG);
        entries.add(HADES_SHADE_SPAWN_EGG);
    }
    public static void addItemToIngredientItemGroup(FabricItemGroupEntries entries) {
        entries.add(DIAMOND_PLATE);
        entries.add(GOLD_PLATE);
        entries.add(IRON_PLATE);
        entries.add(FROST_SHARD);
    }
    public static void addItemToFoodItemGroup(FabricItemGroupEntries entries) {
        entries.add(COOKED_CYCLOPS_MEAT);
        entries.add(RAW_CYCLOPS_MEAT);
        entries.add(CONCENTRATED_CYCLOPS_BLOOD);
        entries.add(CYCLOPS_BLOOD);
    }
    public static void addItemToCombatItemGroup(FabricItemGroupEntries entries) {
        entries.add(IRON_HOPLITE_SPEAR);
        entries.add(GOLD_HOPLITE_SPEAR);
        entries.add(DIAMOND_HOPLITE_SPEAR);
        entries.add(NETHERITE_HOPLITE_SPEAR);
        entries.add(IRON_PLATE_SHIELD);
        entries.add(GOLD_PLATE_SHIELD);
        entries.add(DIAMOND_PLATE_SHIELD);
        entries.add(NETHERITE_PLATE_SHIELD);
        entries.add(FROST_SWORD);
        entries.add(BLOOD_STAINED_FROST_SWORD);
        entries.add(GOLD_PLATE_HELMET);
        entries.add(GOLD_PLATE_CHESTPLATE);
        entries.add(GOLD_PLATE_LEGGINGS);
        entries.add(GOLD_PLATE_BOOTS);
        entries.add(IRON_PLATE_HELMET);
        entries.add(IRON_PLATE_CHESTPLATE);
        entries.add(IRON_PLATE_LEGGINGS);
        entries.add(IRON_PLATE_BOOTS);
        entries.add(DIAMOND_PLATE_HELMET);
        entries.add(DIAMOND_PLATE_CHESTPLATE);
        entries.add(DIAMOND_PLATE_LEGGINGS);
        entries.add(DIAMOND_PLATE_BOOTS);
    }


    public static void registerModItems() {
        AntiqueBeasts.LOGGER.debug("Registering Mod Items for " + AntiqueBeasts.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(ModItems::addItemToSpawnEggItemGroup);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(ModItems::addItemToCombatItemGroup);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(ModItems::addItemToIngredientItemGroup);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(ModItems::addItemToFoodItemGroup);
    }
}

