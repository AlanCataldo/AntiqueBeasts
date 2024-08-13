package net.mebahel.antiquebeasts.item.custom;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.ModEntities;
import net.mebahel.antiquebeasts.item.*;
import net.mebahel.antiquebeasts.sound.ModSounds;
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
    public static final Item N_D_NILE_MUSIC_DISC = registerItem("music_disc/n_d_nile_music_disc",
            new MusicDiscItem(7, ModSounds.N_D_NILE_MUSIC,
                    new FabricItemSettings().maxCount(1).group(ItemGroup.MISC), 62));
    public static final Item BEHOLD_THE_GREAT_SCIENCE_FI_MUSIC_DISC = registerItem("music_disc/behold_the_great_science_fi_music_disc",
            new MusicDiscItem(7, ModSounds.BEHOLD_THE_GREAT_SCIENCE_FI_MUSIC,
                    new FabricItemSettings().maxCount(1).group(ItemGroup.MISC), 149));

    public static final Item EINHERJAR_SPAWN_EGG = registerItem("spawn_egg/einherjar_spawn_egg",
            new SpawnEggItem(ModEntities.EINHERJAR,5062969, 11046446,
                    new FabricItemSettings().group(ItemGroup.MISC)));
    public static final Item VALKYRIE_SPAWN_EGG = registerItem("spawn_egg/valkyrie_spawn_egg",
            new SpawnEggItem(ModEntities.VALKYRIE,16449021, 16506719,
                    new FabricItemSettings().group(ItemGroup.MISC)));
    public static final Item CENTAUR_SPAWN_EGG = registerItem("spawn_egg/centaur_spawn_egg",
            new SpawnEggItem(ModEntities.CENTAUR,13342315, 15789267,
                    new FabricItemSettings().group(ItemGroup.MISC).group(ItemGroup.MISC)));
    public static final Item PEGASUS_SPAWN_EGG = registerItem("spawn_egg/pegasus_spawn_egg",
            new SpawnEggItem(ModEntities.PEGASUS,15658734, 15066597,
                    new FabricItemSettings().group(ItemGroup.MISC)));
    public static final Item CHIMERA_SPAWN_EGG = registerItem("spawn_egg/chimera_spawn_egg",
            new SpawnEggItem(ModEntities.CHIMERA,15090958, 1703936,
                    new FabricItemSettings().group(ItemGroup.MISC)));
    public static final Item ELEPHANT_RIDER_SPAWN_EGG = registerItem("spawn_egg/elephant_rider_spawn_egg",
            new SpawnEggItem(ModEntities.ELEPHANT_RIDER,5526612, 10658466,
                    new FabricItemSettings().group(ItemGroup.MISC)));
    public static final Item WADJET_SPAWN_EGG = registerItem("spawn_egg/wadjet_spawn_egg",
            new SpawnEggItem(ModEntities.WADJET,8612925, 15527888,
                    new FabricItemSettings().group(ItemGroup.MISC)));
    public static final Item AXEMAN_SPAWN_EGG = registerItem("spawn_egg/axeman_spawn_egg",
            new SpawnEggItem(ModEntities.AXEMAN,15783026, 16711422,
                    new FabricItemSettings().group(ItemGroup.MISC)));
    public static final Item CAMELRY_SPAWN_EGG = registerItem("spawn_egg/camelry_spawn_egg",
            new SpawnEggItem(ModEntities.CAMELRY,15783026, 15314250,
                    new FabricItemSettings().group(ItemGroup.MISC)));
    public static final Item SERVANT_SPAWN_EGG = registerItem("spawn_egg/servant_spawn_egg",
            new SpawnEggItem(ModEntities.SERVANT,11375453, 7889747,
                    new FabricItemSettings().group(ItemGroup.MISC)));
    public static final Item MUMMY_SPAWN_EGG = registerItem("spawn_egg/mummy_spawn_egg",
            new SpawnEggItem(ModEntities.MUMMY,11375453, 87,
                    new FabricItemSettings().group(ItemGroup.MISC)));
    public static final Item CYCLOPS_SPAWN_EGG = registerItem("spawn_egg/cyclops_spawn_egg",
            new SpawnEggItem(ModEntities.CYCLOPS,10053120, 6821916,
                    new FabricItemSettings().group(ItemGroup.MISC)));

    public static final Item FROST_CYCLOPS_SPAWN_EGG = registerItem("spawn_egg/frost-cyclops_spawn_egg",
            new SpawnEggItem(ModEntities.FROST_CYCLOPS,6532039, 6821916,
                    new FabricItemSettings().group(ItemGroup.MISC)));

    public static final Item CHAMPION_HOPLITE_SPAWN_EGG = registerItem("spawn_egg/champion_hoplite_spawn_egg",
            new SpawnEggItem(ModEntities.CHAMPION_HOPLITE,16763422, 7498774,
                    new FabricItemSettings().group(ItemGroup.MISC)));

    public static final Item ELITE_HOPLITE_SPAWN_EGG = registerItem("spawn_egg/elite_hoplite_spawn_egg",
            new SpawnEggItem(ModEntities.ELITE_HOPLITE,15592942, 8487298,
                    new FabricItemSettings().group(ItemGroup.MISC)));

    public static final Item HERO_HOPLITE_SPAWN_EGG = registerItem("spawn_egg/hero_hoplite_spawn_egg",
            new SpawnEggItem(ModEntities.HERO_HOPLITE,5109471, 1334867,
                    new FabricItemSettings().group(ItemGroup.MISC)));

    public static final Item HADES_SHADE_SPAWN_EGG = registerItem("spawn_egg/hades_shade_spawn_egg",
            new SpawnEggItem(ModEntities.HADES_SHADE,3815994, 14342874,
                    new FabricItemSettings().group(ItemGroup.MISC)));


    public static final Item HADES_CHOSEN_SPAWN_EGG = registerItem("spawn_egg/hades_chosen_spawn_egg",
            new SpawnEggItem(ModEntities.HADES_CHOSEN,2563101, 13355979,
                    new FabricItemSettings().group(ItemGroup.MISC)));

    public static final Item HERSIR_SPAWN_EGG = registerItem("spawn_egg/hersir_spawn_egg",
            new SpawnEggItem(ModEntities.HERSIR,5062969, 8684678,
                    new FabricItemSettings().group(ItemGroup.MISC)));
    public static final Item HUSKARL_SPAWN_EGG = registerItem("spawn_egg/huskarl_spawn_egg",
            new SpawnEggItem(ModEntities.HUSKARL,5790044, 11046446,
                    new FabricItemSettings().group(ItemGroup.MISC)));
    public static final Item THROWING_AXEMAN_SPAWN_EGG = registerItem("spawn_egg/throwing_axeman_spawn_egg",
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

    public static final Item IRON_HOPLITE_SPEAR = registerItem("weapon/spear/iron_hoplite_spear",
            new ThrowingHopliteSpear(new FabricItemSettings().group(ItemGroup.COMBAT).maxDamage(300), "iron"));
    public static final Item GOLD_HOPLITE_SPEAR = registerItem("weapon/spear/gold_hoplite_spear",
            new ThrowingHopliteSpear(new FabricItemSettings().group(ItemGroup.COMBAT).maxDamage(450), "gold"));
    public static final Item DIAMOND_HOPLITE_SPEAR = registerItem("weapon/spear/diamond_hoplite_spear",
            new ThrowingHopliteSpear(new FabricItemSettings().group(ItemGroup.COMBAT).maxDamage(600), "diamond"));
    public static final Item NETHERITE_HOPLITE_SPEAR = registerItem("weapon/spear/netherite_hoplite_spear",
            new ThrowingHopliteSpear(new FabricItemSettings().group(ItemGroup.COMBAT).maxDamage(800), "netherite") {
        @Override
        public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
            tooltip.add(Text.translatable("item.antiquebeasts.netherite_hoplite_spear.tooltip").formatted(Formatting.GRAY, Formatting.ITALIC));
        }
    });
    public static final Item EINHERJAR_HORN = registerItem("einherjar_horn",
            new EinherjarHorn(new FabricItemSettings().maxDamage(7).group(ItemGroup.COMBAT), SCREAMING_GOAT_HORNS));

    public static final Item WOOD_KHOPESH = registerItem("weapon/khopesh/wood_khopesh",
            new SwordItem(ToolMaterials.WOOD,3, -2.2f,
                    new FabricItemSettings().group(ItemGroup.COMBAT)));
    public static final Item STONE_KHOPESH = registerItem("weapon/khopesh/stone_khopesh",
            new SwordItem(ToolMaterials.STONE,3, -2.2f,
                    new FabricItemSettings().group(ItemGroup.COMBAT)));
    public static final Item IRON_KHOPESH = registerItem("weapon/khopesh/iron_khopesh",
            new SwordItem(ModToolMaterial.IRON_KHOPESH,4, -2.2f,
                    new FabricItemSettings().group(ItemGroup.COMBAT)));
    public static final Item GOLD_KHOPESH = registerItem("weapon/khopesh/gold_khopesh",
            new SwordItem(ModToolMaterial.GOLD_KHOPESH,5, -2.2f,
                    new FabricItemSettings().group(ItemGroup.COMBAT)));
    public static final Item DIAMOND_KHOPESH = registerItem("weapon/khopesh/diamond_khopesh",
            new SwordItem(ModToolMaterial.DIAMOND_KHOPESH,5, -2.2f,
                    new FabricItemSettings().group(ItemGroup.COMBAT)));
    public static final Item NETHERITE_KHOPESH = registerItem("weapon/khopesh/netherite_khopesh",
            new SwordItem(ModToolMaterial.NETHERITE_KHOPESH,6, -2.2f,
                    new FabricItemSettings().group(ItemGroup.COMBAT)));
    public static final Item IRON_EGYPTIAN_HALBERD = registerItem("weapon/egyptian_halberd/iron_egyptian_halberd",
            new SwordItem(ModToolMaterial.IRON_KHOPESH,5, -2.5f,
                    new FabricItemSettings().group(ItemGroup.COMBAT)));
    public static final Item GOLD_EGYPTIAN_HALBERD = registerItem("weapon/egyptian_halberd/gold_egyptian_halberd",
            new SwordItem(ModToolMaterial.GOLD_KHOPESH,6, -2.5f,
                    new FabricItemSettings().group(ItemGroup.COMBAT)));
    public static final Item DIAMOND_EGYPTIAN_HALBERD = registerItem("weapon/egyptian_halberd/diamond_egyptian_halberd",
            new SwordItem(ModToolMaterial.DIAMOND_KHOPESH,6, -2.5f,
                    new FabricItemSettings().group(ItemGroup.COMBAT)));
    public static final Item NETHERITE_EGYPTIAN_HALBERD = registerItem("weapon/egyptian_halberd/netherite_egyptian_halberd",
            new SwordItem(ModToolMaterial.NETHERITE_KHOPESH,7, -2.5f,
                    new FabricItemSettings().group(ItemGroup.COMBAT)));
    public static final Item GOLD_SCARAB = registerItem("gold_scarab",
            new Item(new FabricItemSettings().group(ItemGroup.MATERIALS)));
    public static final Item IRON_SCARAB = registerItem("iron_scarab",
            new Item(new FabricItemSettings().group(ItemGroup.MATERIALS)));
    public static final Item DIAMOND_SHARD = registerItem("diamond_scarab",
            new Item(new FabricItemSettings().group(ItemGroup.MATERIALS)));
    public static final Item EGYPTIAN_RECURVE_BOW = registerItem("bow/egyptian_recurve_bow",
            new EgyptianRecurveBow(new FabricItemSettings().maxDamage(640).group(ItemGroup.COMBAT)));
    public static final Item GREEK_COMPOSITE_BOW = registerItem("bow/greek_composite_bow",
            new GreekCompositeBow(new FabricItemSettings().maxDamage(640).group(ItemGroup.COMBAT)));
    public static final Item CHIMERA_HAIR = registerItem("chimera_hair",
            new Item(new FabricItemSettings().group(ItemGroup.MATERIALS)));;
    public static final Item VALKYRIE_SPEAR = registerItem("weapon/valkyrie_spear",
            new ValkyrieSpear(new FabricItemSettings().maxDamage(600).group(ItemGroup.COMBAT)));

    public static final Item IRON_CENTAUR_SWORD = registerItem("weapon/iron_centaur_sword",
            new SwordItem(ModToolMaterial.IRON_CENTAUR,3, -2.4f,
                    new FabricItemSettings()));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(AntiqueBeasts.MOD_ID, name), item);
    }

    public static void registerModItems() {
        AntiqueBeasts.LOGGER.debug("Registering Mod Items for " + AntiqueBeasts.MOD_ID);
    }
}

