package net.mebahel.antiquebeasts.item.custom;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.ModEntities;
import net.mebahel.antiquebeasts.item.*;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

import static net.minecraft.tag.InstrumentTags.GOAT_HORNS;
import static net.minecraft.tag.InstrumentTags.SCREAMING_GOAT_HORNS;

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

    public static final Item HADES_SHADE_SPAWN_EGG = registerItem("hades_shade_spawn_egg",
            new SpawnEggItem(ModEntities.HADES_SHADE,3815994, 14342874,
                    new FabricItemSettings().group(ItemGroup.MISC)));


    public static final Item HADES_CHOSEN_SPAWN_EGG = registerItem("hades_chosen_spawn_egg",
            new SpawnEggItem(ModEntities.HADES_CHOSEN,2563101, 13355979,
                    new FabricItemSettings().group(ItemGroup.MISC)));

    public static final Item HERSIR_SPAWN_EGG = registerItem("hersir_spawn_egg",
            new SpawnEggItem(ModEntities.HERSIR,5062969, 8684678,
                    new FabricItemSettings().group(ItemGroup.MISC)));
    public static final Item HUSKARL_SPAWN_EGG = registerItem("huskarl_spawn_egg",
            new SpawnEggItem(ModEntities.HUSKARL,5790044, 11046446,
                    new FabricItemSettings().group(ItemGroup.MISC)));
    public static final Item THROWING_AXEMAN_SPAWN_EGG = registerItem("throwing_axeman_spawn_egg",
            new SpawnEggItem(ModEntities.THROWING_AXEMAN,5062969, 6900268,
                    new FabricItemSettings().group(ItemGroup.MISC)));

    public static final Item FROST_SWORD = registerItem("frost_sword",
            new FrostSword(ModToolMaterial.FROST_WEAPON,6, -2.8f,
                    new FabricItemSettings().group(ItemGroup.COMBAT)));

    public static final Item BLOOD_STAINED_FROST_SWORD = registerItem("blood_stained_frost_sword",
            new BloodStainedFrostSword(ModToolMaterial.FROST_WEAPON,7, -3f,
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
            new ConcentratedCyclopsBloodItem(new FabricItemSettings().group(ItemGroup.FOOD)));

    public static final Item IRON_PLATE = registerItem("iron_plate",
            new Item(new FabricItemSettings().group(ItemGroup.MATERIALS)));

    public static final Item IRON_PLATE_SHIELD = registerItem("shield/iron_plate_shield",
            new CustomShieldItem(new FabricItemSettings().maxDamage(550).group(ItemGroup.COMBAT)));

    public static final Item GOLD_PLATE_SHIELD = registerItem("shield/gold_plate_shield",
            new CustomShieldItem(new FabricItemSettings().maxDamage(700).group(ItemGroup.COMBAT)));
    public static final Item DIAMOND_PLATE_SHIELD = registerItem("shield/diamond_plate_shield",
            new CustomShieldItem(new FabricItemSettings().maxDamage(850).group(ItemGroup.COMBAT)) {
                @Override
                public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
                    tooltip.add(Text.translatable("item.antiquebeasts.diamond_plate_shield.tooltip").formatted(Formatting.GRAY, Formatting.ITALIC));
                }
            });
    public static final Item NETHERITE_PLATE_SHIELD = registerItem("shield/netherite_plate_shield",
            new CustomShieldItem(new FabricItemSettings().maxDamage(1000).group(ItemGroup.COMBAT)){
                @Override
                public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
                    tooltip.add(Text.translatable("item.antiquebeasts.netherite_plate_shield.tooltip").formatted(Formatting.GRAY, Formatting.ITALIC));
                }
            });

    public static final Item GOLD_PLATE = registerItem("gold_plate",
            new Item(new FabricItemSettings().group(ItemGroup.MATERIALS)));

    public static final Item DIAMOND_PLATE = registerItem("diamond_plate",
            new Item(new FabricItemSettings().group(ItemGroup.MATERIALS)));

    public static final Item HIGH_IRON_INGOT = registerItem("high_iron_ingot",
            new Item(new FabricItemSettings().group(ItemGroup.MATERIALS)));
    public static final Item HIGH_IRON_SCRAP = registerItem("high_iron_scrap",
            new Item(new FabricItemSettings().group(ItemGroup.MATERIALS)));

    public static final Item THROWING_AXE_ITEM = registerItem("weapon/throwing_axe_item",
            new ThrowingAxeItem(ModToolMaterial.THROWING_AXE,4.5f, -2.8f,
                    new FabricItemSettings().group(ItemGroup.COMBAT)));
    public static final Item HERSIR_AXE = registerItem("weapon/hersir_axe",
            new HersirAxe(ModToolMaterial.HERSIR_AXE,8, -3.25f,
                    new FabricItemSettings().group(ItemGroup.COMBAT)));

    public static final Item HIGH_IRON_SWORD = registerItem("weapon/high_iron_sword",
            new SwordItem(ModToolMaterial.HIGH_IRON,4, -2.4f,
                    new FabricItemSettings().group(ItemGroup.COMBAT)));

    public static final Item HIGH_IRON_SHIELD = registerItem("shield/high_iron_shield",
            new CustomShieldItem(new FabricItemSettings().maxDamage(750).group(ItemGroup.COMBAT)));

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
    public static final Item NETHERITE_PLATE_HELMET = registerItem("netherite_plate_helmet",
            new NetheritePlateArmorItem(ModArmorMaterials.NETHERITE_PLATE, EquipmentSlot.HEAD, new FabricItemSettings().group(ItemGroup.COMBAT)));
    public static final Item NETHERITE_PLATE_CHESTPLATE = registerItem("netherite_plate_chestplate",
            new NetheritePlateArmorItem(ModArmorMaterials.NETHERITE_PLATE, EquipmentSlot.CHEST, new FabricItemSettings().group(ItemGroup.COMBAT)));
    public static final Item NETHERITE_PLATE_LEGGINGS = registerItem("netherite_plate_leggings",
            new NetheritePlateArmorItem(ModArmorMaterials.NETHERITE_PLATE, EquipmentSlot.LEGS, new FabricItemSettings().group(ItemGroup.COMBAT)));
    public static final Item NETHERITE_PLATE_BOOTS = registerItem("netherite_plate_boots",
            new NetheritePlateArmorItem(ModArmorMaterials.NETHERITE_PLATE, EquipmentSlot.FEET, new FabricItemSettings().group(ItemGroup.COMBAT)));
    public static final Item RAW_CYCLOPS_MEAT = registerItem("raw_cyclops_meat",
            new RawCyclopsMeat());
    public static final Item COOKED_CYCLOPS_MEAT = registerItem("cooked_cyclops_meat",
            new CookedCyclopsMeat());

    public static final Item IRON_HOPLITE_SPEAR = registerItem("weapon/iron_hoplite_spear",
            new ThrowingHopliteSpear(new FabricItemSettings().group(ItemGroup.COMBAT).maxDamage(300), "iron"));
    public static final Item GOLD_HOPLITE_SPEAR = registerItem("weapon/gold_hoplite_spear",
            new ThrowingHopliteSpear(new FabricItemSettings().group(ItemGroup.COMBAT).maxDamage(450), "gold"));
    public static final Item DIAMOND_HOPLITE_SPEAR = registerItem("weapon/diamond_hoplite_spear",
            new ThrowingHopliteSpear(new FabricItemSettings().group(ItemGroup.COMBAT).maxDamage(600), "diamond"));
    public static final Item NETHERITE_HOPLITE_SPEAR = registerItem("weapon/netherite_hoplite_spear",
            new ThrowingHopliteSpear(new FabricItemSettings().group(ItemGroup.COMBAT).maxDamage(800), "netherite") {
        @Override
        public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
            tooltip.add(Text.translatable("item.antiquebeasts.netherite_hoplite_spear.tooltip").formatted(Formatting.GRAY, Formatting.ITALIC));
        }
    });

    public static final Item EINHERJAR_SPAWN_EGG = registerItem("einherjar_spawn_egg",
            new SpawnEggItem(ModEntities.EINHERJAR,5062969, 11046446,
                    new FabricItemSettings().group(ItemGroup.MISC)));
    public static final Item VALKYRIE_SPAWN_EGG = registerItem("valkyrie_spawn_egg",
            new SpawnEggItem(ModEntities.VALKYRIE,16449021, 16506719,
                    new FabricItemSettings().group(ItemGroup.MISC)));
    public static final Item EINHERJAR_HORN = registerItem("einherjar_horn",
            new EinherjarHorn(new FabricItemSettings().maxDamage(7).group(ItemGroup.COMBAT), SCREAMING_GOAT_HORNS));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(AntiqueBeasts.MOD_ID, name), item);
    }

    public static void registerModItems() {
        AntiqueBeasts.LOGGER.debug("Registering Mod Items for " + AntiqueBeasts.MOD_ID);
    }
}

