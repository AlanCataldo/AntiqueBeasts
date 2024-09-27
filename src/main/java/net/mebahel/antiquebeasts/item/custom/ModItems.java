package net.mebahel.antiquebeasts.item.custom;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.block.ModBlocks;
import net.mebahel.antiquebeasts.entity.ModEntities;
import net.mebahel.antiquebeasts.item.*;
import net.mebahel.antiquebeasts.sound.ModSounds;
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

import static net.minecraft.registry.tag.InstrumentTags.SCREAMING_GOAT_HORNS;

public class ModItems {
    public static final Item N_D_NILE_MUSIC_DISC = registerItem("music_disc/n_d_nile_music_disc",
            new MusicDiscItem(7, ModSounds.N_D_NILE_MUSIC,
                    new FabricItemSettings().maxCount(1), 62));
    public static final Item BEHOLD_THE_GREAT_SCIENCE_FI_MUSIC_DISC = registerItem("music_disc/behold_the_great_science_fi_music_disc",
            new MusicDiscItem(7, ModSounds.BEHOLD_THE_GREAT_SCIENCE_FI_MUSIC,
                    new FabricItemSettings().maxCount(1), 149));

    public static final Item DRAUGR_ARCHER_SPAWN_EGG = registerItem("spawn_egg/draugr_archer_spawn_egg",
            new SpawnEggItem(ModEntities.DRAUGR_ARCHER,4399360, 1926344,
                    new FabricItemSettings()));

    public static final Item HARPY_SPAWN_EGG = registerItem("spawn_egg/harpy_spawn_egg",
            new SpawnEggItem(ModEntities.HARPY,2699360, 866344,
                    new FabricItemSettings()));
    public static final Item DRAUGR_SPAWN_EGG = registerItem("spawn_egg/draugr_spawn_egg",
            new SpawnEggItem(ModEntities.DRAUGR,4399360, 526344,
                    new FabricItemSettings()));
    public static final Item CENTAUR_SPAWN_EGG = registerItem("spawn_egg/centaur_spawn_egg",
            new SpawnEggItem(ModEntities.CENTAUR,13342315, 15789267,
                    new FabricItemSettings()));
    public static final Item PEGASUS_SPAWN_EGG = registerItem("spawn_egg/pegasus_spawn_egg",
            new SpawnEggItem(ModEntities.PEGASUS,15658734, 15066597,
                    new FabricItemSettings()));
    public static final Item CHIMERA_SPAWN_EGG = registerItem("spawn_egg/chimera_spawn_egg",
            new SpawnEggItem(ModEntities.CHIMERA,15090958, 1703936,
                    new FabricItemSettings()));
    public static final Item ELEPHANT_RIDER_SPAWN_EGG = registerItem("spawn_egg/elephant_rider_spawn_egg",
            new SpawnEggItem(ModEntities.ELEPHANT_RIDER,5526612, 10658466,
                    new FabricItemSettings()));
    public static final Item WADJET_SPAWN_EGG = registerItem("spawn_egg/wadjet_spawn_egg",
            new SpawnEggItem(ModEntities.WADJET,8612925, 15527888,
                    new FabricItemSettings()));
    public static final Item AXEMAN_SPAWN_EGG = registerItem("spawn_egg/axeman_spawn_egg",
            new SpawnEggItem(ModEntities.AXEMAN,15783026, 16711422,
                    new FabricItemSettings()));
    public static final Item CAMELRY_SPAWN_EGG = registerItem("spawn_egg/camelry_spawn_egg",
            new SpawnEggItem(ModEntities.CAMELRY,15783026, 15314250,
                    new FabricItemSettings()));
    public static final Item SERVANT_SPAWN_EGG = registerItem("spawn_egg/servant_spawn_egg",
            new SpawnEggItem(ModEntities.SERVANT,11375453, 7889747,
                    new FabricItemSettings()));
    public static final Item MUMMY_SPAWN_EGG = registerItem("spawn_egg/mummy_spawn_egg",
            new SpawnEggItem(ModEntities.MUMMY,11375453, 87,
                    new FabricItemSettings()));

    public static final Item MUMMY_BOSS_SPAWN_EGG = registerItem("spawn_egg/mummy_boss_spawn_egg",
            new SpawnEggItem(ModEntities.MUMMY_BOSS,11375453, 6821916,
                    new FabricItemSettings()));
    public static final Item CYCLOPS_SPAWN_EGG = registerItem("spawn_egg/cyclops_spawn_egg",
            new SpawnEggItem(ModEntities.CYCLOPS,10053120, 6821916,
                    new FabricItemSettings()));
    public static final Item FROST_CYCLOPS_SPAWN_EGG = registerItem("spawn_egg/frost-cyclops_spawn_egg",
            new SpawnEggItem(ModEntities.FROST_CYCLOPS,6532039, 6821916,
                    new FabricItemSettings()));
    public static final Item CHAMPION_HOPLITE_SPAWN_EGG = registerItem("spawn_egg/champion_hoplite_spawn_egg",
            new SpawnEggItem(ModEntities.CHAMPION_HOPLITE,16763422, 7498774,
                    new FabricItemSettings()));
    public static final Item ELITE_HOPLITE_SPAWN_EGG = registerItem("spawn_egg/elite_hoplite_spawn_egg",
            new SpawnEggItem(ModEntities.ELITE_HOPLITE,15592942, 8487298,
                    new FabricItemSettings()));
    public static final Item HERO_HOPLITE_SPAWN_EGG = registerItem("spawn_egg/hero_hoplite_spawn_egg",
            new SpawnEggItem(ModEntities.HERO_HOPLITE,5109471, 1334867,
                    new FabricItemSettings()));
    public static final Item HADES_SHADE_SPAWN_EGG = registerItem("spawn_egg/hades_shade_spawn_egg",
            new SpawnEggItem(ModEntities.HADES_SHADE,3815994, 14342874,
                    new FabricItemSettings()));
    public static final Item HADES_CHOSEN_SPAWN_EGG = registerItem("spawn_egg/hades_chosen_spawn_egg",
            new SpawnEggItem(ModEntities.HADES_CHOSEN,2563101, 13355979,
                    new FabricItemSettings()));
    public static final Item HERSIR_SPAWN_EGG = registerItem("spawn_egg/hersir_spawn_egg",
            new SpawnEggItem(ModEntities.HERSIR,5062969, 8684678,
                    new FabricItemSettings()));
    public static final Item HUSKARL_SPAWN_EGG = registerItem("spawn_egg/huskarl_spawn_egg",
            new SpawnEggItem(ModEntities.HUSKARL,5790044, 11046446,
                    new FabricItemSettings()));
    public static final Item THROWING_AXEMAN_SPAWN_EGG = registerItem("spawn_egg/throwing_axeman_spawn_egg",
            new SpawnEggItem(ModEntities.THROWING_AXEMAN,5062969, 6900268,
                    new FabricItemSettings()));

    public static final Item EINHERJAR_SPAWN_EGG = registerItem("spawn_egg/einherjar_spawn_egg",
            new SpawnEggItem(ModEntities.EINHERJAR,5062969, 11046446,
                    new FabricItemSettings()));

    public static final Item VALKYRIE_SPAWN_EGG = registerItem("spawn_egg/valkyrie_spawn_egg",
            new SpawnEggItem(ModEntities.VALKYRIE,16449021, 16506719,
                    new FabricItemSettings()));
    public static final Item FROST_SWORD = registerItem("frost_sword",
            new FrostSword(ModToolMaterial.FROST_WEAPON,6, -2.8f,
                    new FabricItemSettings()));
    public static final Item BLOOD_STAINED_FROST_SWORD = registerItem("blood_stained_frost_sword",
            new BloodStainedFrostSword(ModToolMaterial.FROST_WEAPON,7, -3f,
                    new FabricItemSettings()));

    public static final Item EINHERJAR_HORN = registerItem("einherjar_horn",
            new EinherjarHorn(new FabricItemSettings().maxDamage(7), SCREAMING_GOAT_HORNS));
    public static final Item FROST_SHARD = registerItem("frost_shard",
            new Item(new FabricItemSettings()));
    public static final Item CYCLOPS_BLOOD = registerItem("cyclops_blood",
            new CyclopsBloodItem(new FabricItemSettings()));
    public static final Item CONCENTRATED_CYCLOPS_BLOOD = registerItem("concentrated_cyclops_blood",
            new ConcentratedCyclopsBloodItem(new FabricItemSettings()));
    public static final Item IRON_PLATE = registerItem("iron_plate",
            new Item(new FabricItemSettings()));
    public static final Item HIGH_IRON_INGOT = registerItem("high_iron_ingot",
            new Item(new FabricItemSettings()));
    public static final Item HIGH_IRON_SCRAP = registerItem("high_iron_scrap",
            new Item(new FabricItemSettings()));

    public static final Item ANKH = registerItem("ankh",
            new AnkhItem(new FabricItemSettings().maxCount(1)));

    public static final Item ANKH_FRAGMENT = registerItem("ankh_fragment",
            new Item(new FabricItemSettings()));
    public static final Item IRON_EGYPTIAN_SHIELD = registerItem("shield/iron_egyptian_shield",
            new CustomShieldItem(new FabricItemSettings().maxDamage(550)));
    public static final Item GOLD_EGYPTIAN_SHIELD = registerItem("shield/gold_egyptian_shield",
            new CustomShieldItem(new FabricItemSettings().maxDamage(700)));
    public static final Item IRON_PLATE_SHIELD = registerItem("shield/iron_plate_shield",
            new CustomShieldItem(new FabricItemSettings().maxDamage(550)));
    public static final Item GOLD_PLATE_SHIELD = registerItem("shield/gold_plate_shield",
            new CustomShieldItem(new FabricItemSettings().maxDamage(700)));
    public static final Item HIGH_IRON_SHIELD = registerItem("shield/high_iron_shield",
            new CustomShieldItem(new FabricItemSettings().maxDamage(750)));
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
    public static final Item IRON_SCALE_HELMET = registerItem("iron_scale_helmet",
            new IronScaleArmorItem(ModArmorMaterials.IRON_SCALE, ArmorItem.Type.HELMET, new FabricItemSettings()));
    public static final Item IRON_SCALE_CHESTPLATE = registerItem("iron_scale_chestplate",
            new IronScaleArmorItem(ModArmorMaterials.IRON_SCALE, ArmorItem.Type.CHESTPLATE, new FabricItemSettings()));
    public static final Item IRON_SCALE_LEGGINGS = registerItem("iron_scale_leggings",
            new IronScaleArmorItem(ModArmorMaterials.IRON_SCALE, ArmorItem.Type.LEGGINGS, new FabricItemSettings()));
    public static final Item IRON_SCALE_BOOTS = registerItem("iron_scale_boots",
            new IronScaleArmorItem(ModArmorMaterials.IRON_SCALE, ArmorItem.Type.BOOTS, new FabricItemSettings()));
    public static final Item GOLD_SCALE_HELMET = registerItem("gold_scale_helmet",
            new GoldScaleArmorItem(ModArmorMaterials.GOLD_SCALE, ArmorItem.Type.HELMET, new FabricItemSettings()));
    public static final Item GOLD_SCALE_CHESTPLATE = registerItem("gold_scale_chestplate",
            new GoldScaleArmorItem(ModArmorMaterials.GOLD_SCALE, ArmorItem.Type.CHESTPLATE, new FabricItemSettings()));
    public static final Item GOLD_SCALE_LEGGINGS = registerItem("gold_scale_leggings",
            new GoldScaleArmorItem(ModArmorMaterials.GOLD_SCALE, ArmorItem.Type.LEGGINGS, new FabricItemSettings()));
    public static final Item GOLD_SCALE_BOOTS = registerItem("gold_scale_boots",
            new GoldScaleArmorItem(ModArmorMaterials.GOLD_SCALE, ArmorItem.Type.BOOTS, new FabricItemSettings()));
    public static final Item DIAMOND_SCALE_HELMET = registerItem("diamond_scale_helmet",
            new DiamondScaleArmorItem(ModArmorMaterials.DIAMOND_SCALE, ArmorItem.Type.HELMET, new FabricItemSettings()));
    public static final Item DIAMOND_SCALE_CHESTPLATE = registerItem("diamond_scale_chestplate",
            new DiamondScaleArmorItem(ModArmorMaterials.DIAMOND_SCALE, ArmorItem.Type.CHESTPLATE, new FabricItemSettings()));
    public static final Item DIAMOND_SCALE_LEGGINGS = registerItem("diamond_scale_leggings",
            new DiamondScaleArmorItem(ModArmorMaterials.DIAMOND_SCALE, ArmorItem.Type.LEGGINGS, new FabricItemSettings()));
    public static final Item DIAMOND_SCALE_BOOTS = registerItem("diamond_scale_boots",
            new DiamondScaleArmorItem(ModArmorMaterials.DIAMOND_SCALE, ArmorItem.Type.BOOTS, new FabricItemSettings()));
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
    public static final NetheritePlateArmorItem NETHERITE_PLATE_HELMET = registerItem("netherite_plate_helmet",
            new NetheritePlateArmorItem(ModArmorMaterials.NETHERITE_PLATE, ArmorItem.Type.HELMET, new FabricItemSettings()));
    public static final NetheritePlateArmorItem NETHERITE_PLATE_CHESTPLATE = registerItem("netherite_plate_chestplate",
            new NetheritePlateArmorItem(ModArmorMaterials.NETHERITE_PLATE, ArmorItem.Type.CHESTPLATE, new FabricItemSettings()));
    public static final NetheritePlateArmorItem NETHERITE_PLATE_LEGGINGS = registerItem("netherite_plate_leggings",
            new NetheritePlateArmorItem(ModArmorMaterials.NETHERITE_PLATE, ArmorItem.Type.LEGGINGS, new FabricItemSettings()));
    public static final NetheritePlateArmorItem NETHERITE_PLATE_BOOTS = registerItem("netherite_plate_boots",
            new NetheritePlateArmorItem(ModArmorMaterials.NETHERITE_PLATE, ArmorItem.Type.BOOTS, new FabricItemSettings()));

    public static final Item PHARAOH_SCEPTER = registerItem("weapon/pharaoh_scepter_staff",
            new PharaohScepterItem(AntiqueBeasts.getTickScheduler(), ModToolMaterial.FROST_WEAPON,5, -2.8f,
                    new FabricItemSettings()));
    public static final Item RAW_CYCLOPS_MEAT = registerItem("raw_cyclops_meat",
            new RawCyclopsMeat());
    public static final Item COOKED_CYCLOPS_MEAT = registerItem("cooked_cyclops_meat",
            new CookedCyclopsMeat());
    public static final Item IRON_HOPLITE_SPEAR = registerItem("weapon/spear/iron_hoplite_spear",
            new ThrowingHopliteSpear(new FabricItemSettings().maxDamage(300), "iron"));
    public static final Item GOLD_HOPLITE_SPEAR = registerItem("weapon/spear/gold_hoplite_spear",
            new ThrowingHopliteSpear(new FabricItemSettings().maxDamage(450), "gold"));
    public static final Item DIAMOND_HOPLITE_SPEAR = registerItem("weapon/spear/diamond_hoplite_spear",
            new ThrowingHopliteSpear(new FabricItemSettings().maxDamage(600), "diamond"));
    public static final Item NETHERITE_HOPLITE_SPEAR = registerItem("weapon/spear/netherite_hoplite_spear", new ThrowingHopliteSpear(new FabricItemSettings().maxDamage(800), "netherite") {
        @Override
        public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
            tooltip.add(Text.translatable("item.antiquebeasts.netherite_hoplite_spear.tooltip").formatted(Formatting.GRAY, Formatting.ITALIC));
        }
    });
    public static final ThrowingAxeItem THROWING_AXE_ITEM = registerItem("weapon/throwing_axe_item",
            new ThrowingAxeItem(ModToolMaterial.THROWING_AXE,4.5f, -2.8f, new FabricItemSettings()));

    public static final HarpyFeatherItem HARPY_FEATHER = registerItem("weapon/harpy_feather",
            new HarpyFeatherItem(new FabricItemSettings().maxCount(64)));

    public static final Item HERSIR_AXE = registerItem("weapon/hersir_axe",
            new HersirAxe(ModToolMaterial.HERSIR_AXE,8, -3.1f,
                    new FabricItemSettings()));

    public static final Item HIGH_IRON_SWORD = registerItem("weapon/high_iron_sword",
            new SwordItem(ModToolMaterial.HIGH_IRON,4, -2.4f,
                    new FabricItemSettings()));
    public static final Item WOOD_KHOPESH = registerItem("weapon/khopesh/wood_khopesh_sickle",
            new SwordItem(ToolMaterials.WOOD,3, -2.2f,
                    new FabricItemSettings()));
    public static final Item STONE_KHOPESH = registerItem("weapon/khopesh/stone_khopesh_sickle",
            new SwordItem(ToolMaterials.STONE,3, -2.2f,
                    new FabricItemSettings()));
    public static final Item IRON_KHOPESH = registerItem("weapon/khopesh/iron_khopesh_sickle",
            new SwordItem(ModToolMaterial.IRON_KHOPESH,4, -2.2f,
                    new FabricItemSettings()));
    public static final Item GOLD_KHOPESH = registerItem("weapon/khopesh/gold_khopesh_sickle",
            new SwordItem(ModToolMaterial.GOLD_KHOPESH,5, -2.2f,
                    new FabricItemSettings()));
    public static final Item DIAMOND_KHOPESH = registerItem("weapon/khopesh/diamond_khopesh_sickle",
            new SwordItem(ModToolMaterial.DIAMOND_KHOPESH,5, -2.2f,
                    new FabricItemSettings()));
    public static final Item NETHERITE_KHOPESH = registerItem("weapon/khopesh/netherite_khopesh_sickle",
            new SwordItem(ModToolMaterial.NETHERITE_KHOPESH,6, -2.2f,
                    new FabricItemSettings()));
    public static final Item IRON_EGYPTIAN_HALBERD = registerItem("weapon/egyptian_halberd/iron_egyptian_halberd",
            new SwordItem(ModToolMaterial.IRON_KHOPESH,5, -2.5f,
                    new FabricItemSettings()));
    public static final Item GOLD_EGYPTIAN_HALBERD = registerItem("weapon/egyptian_halberd/gold_egyptian_halberd",
            new SwordItem(ModToolMaterial.GOLD_KHOPESH,6, -2.5f,
                    new FabricItemSettings()));
    public static final Item DIAMOND_EGYPTIAN_HALBERD = registerItem("weapon/egyptian_halberd/diamond_egyptian_halberd",
            new SwordItem(ModToolMaterial.DIAMOND_KHOPESH,6, -2.5f,
                    new FabricItemSettings()));
    public static final Item NETHERITE_EGYPTIAN_HALBERD = registerItem("weapon/egyptian_halberd/netherite_egyptian_halberd",
            new SwordItem(ModToolMaterial.NETHERITE_KHOPESH,7, -2.5f,
                    new FabricItemSettings()));
    public static final Item GOLD_SCARAB = registerItem("gold_scarab",
            new Item(new FabricItemSettings()));
    public static final Item IRON_SCARAB = registerItem("iron_scarab",
            new Item(new FabricItemSettings()));
    public static final Item DIAMOND_SCARAB = registerItem("diamond_scarab",
            new Item(new FabricItemSettings()));
    public static final Item CURSED_SCARAB = registerItem("cursed_scarab",
            new Item(new FabricItemSettings()));
    public static final Item EGYPTIAN_RECURVE_BOW = registerItem("bow/egyptian_recurve_bow",
            new EgyptianRecurveBow(new FabricItemSettings().maxDamage(640)));
    public static final Item GREEK_COMPOSITE_BOW = registerItem("bow/greek_composite_bow",
            new GreekCompositeBow(new FabricItemSettings().maxDamage(640)));
    public static final Item CHIMERA_HAIR = registerItem("chimera_hair",
            new Item(new FabricItemSettings()));
    public static final Item VALKYRIE_SPEAR = registerItem("weapon/valkyrie_spear",
            new ValkyrieSpear(new FabricItemSettings().maxDamage(600)));

    public static final Item IRON_CENTAUR_SWORD = registerItem("weapon/iron_centaur_sword",
            new SwordItem(ModToolMaterial.IRON_CENTAUR,3, -2.4f,
                    new FabricItemSettings()));
    public static <I extends Item> I registerItem(String name, I item) {
        return Registry.register(Registries.ITEM, new Identifier(AntiqueBeasts.MOD_ID, name), item);
    }
    public static void addItemToSpawnEggItemGroup(FabricItemGroupEntries entries) {
        entries.add(ELEPHANT_RIDER_SPAWN_EGG);
        entries.add(CAMELRY_SPAWN_EGG);
        entries.add(AXEMAN_SPAWN_EGG);
        entries.add(WADJET_SPAWN_EGG);
        entries.add(MUMMY_SPAWN_EGG);
        entries.add(MUMMY_BOSS_SPAWN_EGG);
        entries.add(SERVANT_SPAWN_EGG);

        entries.add(CENTAUR_SPAWN_EGG);
        entries.add(CHIMERA_SPAWN_EGG);
        entries.add(CYCLOPS_SPAWN_EGG);
        entries.add(FROST_CYCLOPS_SPAWN_EGG);
        entries.add(ELITE_HOPLITE_SPAWN_EGG);
        entries.add(CHAMPION_HOPLITE_SPAWN_EGG);
        entries.add(HERO_HOPLITE_SPAWN_EGG);
        entries.add(HADES_CHOSEN_SPAWN_EGG);
        entries.add(HADES_SHADE_SPAWN_EGG);
        entries.add(PEGASUS_SPAWN_EGG);

        entries.add(HERSIR_SPAWN_EGG);
        entries.add(HUSKARL_SPAWN_EGG);
        entries.add(THROWING_AXEMAN_SPAWN_EGG);
        entries.add(EINHERJAR_SPAWN_EGG);
        entries.add(VALKYRIE_SPAWN_EGG);

        entries.add(DRAUGR_SPAWN_EGG);
        entries.add(HARPY_SPAWN_EGG);
        entries.add(DRAUGR_ARCHER_SPAWN_EGG);
    }
    public static void addItemToIngredientItemGroup(FabricItemGroupEntries entries) {
        entries.add(IRON_PLATE);
        entries.add(GOLD_PLATE);
        entries.add(DIAMOND_PLATE);
        entries.add(FROST_SHARD);
        entries.add(HIGH_IRON_SCRAP);
        entries.add(HIGH_IRON_INGOT);
        entries.add(IRON_SCARAB);
        entries.add(GOLD_SCARAB);
        entries.add(DIAMOND_SCARAB);
        entries.add(CURSED_SCARAB);
        entries.add(CHIMERA_HAIR);
        entries.add(ANKH_FRAGMENT);
    }
    public static void addItemToFoodItemGroup(FabricItemGroupEntries entries) {
        entries.add(COOKED_CYCLOPS_MEAT);
        entries.add(RAW_CYCLOPS_MEAT);
        entries.add(CONCENTRATED_CYCLOPS_BLOOD);
        entries.add(CYCLOPS_BLOOD);
    }

    public static void addItemToToolsAndUtilitiesGroup(FabricItemGroupEntries entries) {
        entries.add(ModItems.N_D_NILE_MUSIC_DISC);
        entries.add(ModItems.BEHOLD_THE_GREAT_SCIENCE_FI_MUSIC_DISC);
    }

    public static void addItemToFunctionalItemGroup(FabricItemGroupEntries entries) {
        entries.add(ModBlocks.AMPHORA);
        entries.add(ModBlocks.CURSED_GOLDEN_BLOCK);
        entries.add(ModBlocks.MUMMY_BOSS_ALTAR);
    }
    public static void addItemToCombatItemGroup(FabricItemGroupEntries entries) {
        entries.add(IRON_SCALE_HELMET);
        entries.add(IRON_SCALE_CHESTPLATE);
        entries.add(IRON_SCALE_LEGGINGS);
        entries.add(IRON_SCALE_BOOTS);
        entries.add(GOLD_SCALE_HELMET);
        entries.add(GOLD_SCALE_CHESTPLATE);
        entries.add(GOLD_SCALE_LEGGINGS);
        entries.add(GOLD_SCALE_BOOTS);
        entries.add(DIAMOND_SCALE_HELMET);
        entries.add(DIAMOND_SCALE_CHESTPLATE);
        entries.add(DIAMOND_SCALE_LEGGINGS);
        entries.add(DIAMOND_SCALE_BOOTS);
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
        entries.add(NETHERITE_PLATE_HELMET);
        entries.add(NETHERITE_PLATE_CHESTPLATE);
        entries.add(NETHERITE_PLATE_LEGGINGS);
        entries.add(NETHERITE_PLATE_BOOTS);

        entries.add(FROST_SWORD);
        entries.add(BLOOD_STAINED_FROST_SWORD);

        entries.add(IRON_EGYPTIAN_SHIELD);
        entries.add(GOLD_EGYPTIAN_SHIELD);
        entries.add(IRON_PLATE_SHIELD);
        entries.add(GOLD_PLATE_SHIELD);
        entries.add(DIAMOND_PLATE_SHIELD);
        entries.add(NETHERITE_PLATE_SHIELD);

        entries.add(HIGH_IRON_SWORD);
        entries.add(HIGH_IRON_SHIELD);
        entries.add(VALKYRIE_SPEAR);
        entries.add(HERSIR_AXE);
        entries.add(THROWING_AXE_ITEM);
        entries.add(EINHERJAR_HORN);

        entries.add(HARPY_FEATHER);

        entries.add(IRON_HOPLITE_SPEAR);
        entries.add(GOLD_HOPLITE_SPEAR);
        entries.add(DIAMOND_HOPLITE_SPEAR);
        entries.add(NETHERITE_HOPLITE_SPEAR);
        entries.add(IRON_CENTAUR_SWORD);

        entries.add(WOOD_KHOPESH);
        entries.add(STONE_KHOPESH);
        entries.add(IRON_KHOPESH);
        entries.add(GOLD_KHOPESH);
        entries.add(DIAMOND_KHOPESH);
        entries.add(NETHERITE_KHOPESH);
        entries.add(IRON_EGYPTIAN_HALBERD);
        entries.add(GOLD_EGYPTIAN_HALBERD);
        entries.add(DIAMOND_EGYPTIAN_HALBERD);
        entries.add(NETHERITE_EGYPTIAN_HALBERD);
        entries.add(EGYPTIAN_RECURVE_BOW);
        entries.add(GREEK_COMPOSITE_BOW);
        entries.add(PHARAOH_SCEPTER);
        entries.add(ANKH);
    }


    public static void registerModItems() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(ModItems::addItemToSpawnEggItemGroup);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(ModItems::addItemToCombatItemGroup);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(ModItems::addItemToIngredientItemGroup);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(ModItems::addItemToFoodItemGroup);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(ModItems::addItemToFunctionalItemGroup);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(ModItems::addItemToToolsAndUtilitiesGroup);
        AntiqueBeasts.LOGGER.info("[AntiqueBeasts] Registering items for " + AntiqueBeasts.MOD_ID + ".");
    }
}

