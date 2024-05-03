package net.mebahel.antiquebeasts.world.gen;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.mebahel.antiquebeasts.entity.ModEntities;
import net.mebahel.antiquebeasts.entity.custom.*;
import net.minecraft.entity.*;
import net.minecraft.world.Difficulty;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.BiomeKeys;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags;

public class ModEntitySpawn {
    public static void addEntitySpawn() {
        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(
                        BiomeKeys.PLAINS, BiomeKeys.SUNFLOWER_PLAINS, BiomeKeys.FOREST, BiomeKeys.FLOWER_FOREST, BiomeKeys.BIRCH_FOREST,
                        BiomeKeys.WINDSWEPT_FOREST, BiomeKeys.MEADOW, BiomeKeys.OLD_GROWTH_BIRCH_FOREST,
                        TerralithBiomeKeys.YELLOWSTONE, TerralithBiomeKeys.AMETHYST_CANYON, TerralithBiomeKeys.TEMPERATE_HIGHLANDS,
                        TerralithBiomeKeys.GRAVEL_BEACH, TerralithBiomeKeys.HOT_SHRUBLAND, TerralithBiomeKeys.ALPHA_ISLAND,
                        TerralithBiomeKeys.BLOOMING_VALLEY, TerralithBiomeKeys.LAVENDER_VALLEY, TerralithBiomeKeys.LAVENDER_FOREST,
                        TerralithBiomeKeys.MIRAGE_ISLES, TerralithBiomeKeys.MOONLIGHT_GROVE, TerralithBiomeKeys.MOONLIGHT_VALLEY,
                        TerralithBiomeKeys.SAKURA_GROVE, TerralithBiomeKeys.SAKURA_VALLEY, TerralithBiomeKeys.BLOOMING_PLATEAU,
                        TerralithBiomeKeys.SKYLANDS, TerralithBiomeKeys. SKYLANDS_AUTUMN, TerralithBiomeKeys.SKYLANDS_SPRING,
                        TerralithBiomeKeys.SKYLANDS_SUMMER, TerralithBiomeKeys.WARM_RIVER, TerralithBiomeKeys.VOLCANIC_PEAKS,
                        TerralithBiomeKeys.STONY_SPIRES, TerralithBiomeKeys.BASALT_CLIFFS)
                        .or(BiomeSelectors.tag(ConventionalBiomeTags.PLAINS)).or(BiomeSelectors.tag(ConventionalBiomeTags.FOREST))
                        .or(BiomeSelectors.tag(ConventionalBiomeTags.CLIMATE_TEMPERATE)),
                SpawnGroup.CREATURE, ModEntities.CYCLOPS, 3, 1, 1);
        SpawnRestriction.register(ModEntities.CYCLOPS, SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (type, world, spawnReason, pos, random) -> {
                    if (world.getDifficulty() == Difficulty.PEACEFUL) {
                        return false;
                    }
                    return CyclopsEntity.canMobSpawn(type, world, spawnReason, pos, random);
                });

        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(
                        BiomeKeys.ICE_SPIKES, BiomeKeys.SNOWY_PLAINS, BiomeKeys.SNOWY_TAIGA,
                        BiomeKeys.SNOWY_BEACH, BiomeKeys.SNOWY_SLOPES, BiomeKeys.GROVE, BiomeKeys.JAGGED_PEAKS,
                        BiomeKeys.FROZEN_PEAKS, BiomeKeys.FROZEN_OCEAN, BiomeKeys.FROZEN_RIVER, BiomeKeys.TAIGA,
                        TerralithBiomeKeys.ALPHA_ISLANDS_WINTERS, TerralithBiomeKeys.ALPINE_HIGHLANDS,
                        TerralithBiomeKeys.HIGHLANDS,
                        TerralithBiomeKeys.SHIELD_CLEARING, TerralithBiomeKeys.SHIELD,
                        TerralithBiomeKeys.SIBERIAN_TAIGA, TerralithBiomeKeys.SIBERIAN_GROVE,
                        TerralithBiomeKeys.ICE_MARSH, TerralithBiomeKeys.SNOWY_CHERRY_GROVE, TerralithBiomeKeys.SNOWY_MAPLE_FOREST,
                        TerralithBiomeKeys.SNOWY_SHIELD, TerralithBiomeKeys.ALPHA_ISLANDS_WINTER, TerralithBiomeKeys.FROZEN_CLIFFS,
                        TerralithBiomeKeys.GLACIAL_CHASM, TerralithBiomeKeys.SNOWY_BADLANDS, TerralithBiomeKeys.ALPINE_GROVE,
                        TerralithBiomeKeys.WINTRY_FOREST,TerralithBiomeKeys.WINTRY_LOWLANDS, TerralithBiomeKeys.COLD_SHRUBLAND,
                        TerralithBiomeKeys.GRAVEL_DESERT)
                        .or(BiomeSelectors.tag(ConventionalBiomeTags.CLIMATE_COLD)),
                SpawnGroup.CREATURE, ModEntities.FROST_CYCLOPS, 4, 1, 1);
        SpawnRestriction.register(ModEntities.FROST_CYCLOPS, SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (type, world, spawnReason, pos, random) -> {
                    if (world.getDifficulty() == Difficulty.PEACEFUL) {
                        return false;
                    }
                    return FrostCyclopsEntity.canMobSpawn(type, world, spawnReason, pos, random);
                });

        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(
                        BiomeKeys.ICE_SPIKES, BiomeKeys.SNOWY_PLAINS, BiomeKeys.SNOWY_TAIGA,
                        BiomeKeys.SNOWY_BEACH, BiomeKeys.SNOWY_SLOPES, BiomeKeys.GROVE, BiomeKeys.JAGGED_PEAKS,
                        BiomeKeys.FROZEN_PEAKS, BiomeKeys.FROZEN_OCEAN, BiomeKeys.FROZEN_RIVER, BiomeKeys.TAIGA,
                        BiomeKeys.OLD_GROWTH_PINE_TAIGA, BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA,
                        TerralithBiomeKeys.ALPHA_ISLANDS_WINTERS, TerralithBiomeKeys.ALPINE_HIGHLANDS,
                        TerralithBiomeKeys.HIGHLANDS,
                        TerralithBiomeKeys.SHIELD_CLEARING, TerralithBiomeKeys.SHIELD,
                        TerralithBiomeKeys.SIBERIAN_TAIGA, TerralithBiomeKeys.SIBERIAN_GROVE,
                        TerralithBiomeKeys.ICE_MARSH, TerralithBiomeKeys.SNOWY_CHERRY_GROVE, TerralithBiomeKeys.SNOWY_MAPLE_FOREST,
                        TerralithBiomeKeys.SNOWY_SHIELD, TerralithBiomeKeys.ALPHA_ISLANDS_WINTER, TerralithBiomeKeys.FROZEN_CLIFFS,
                        TerralithBiomeKeys.GLACIAL_CHASM, TerralithBiomeKeys.SNOWY_BADLANDS, TerralithBiomeKeys.ALPINE_GROVE,
                        TerralithBiomeKeys.WINTRY_FOREST,TerralithBiomeKeys.WINTRY_LOWLANDS, TerralithBiomeKeys.COLD_SHRUBLAND,
                        TerralithBiomeKeys.GRAVEL_DESERT)
                        .or(BiomeSelectors.tag(ConventionalBiomeTags.CLIMATE_COLD)),
                SpawnGroup.CREATURE, ModEntities.HERSIR, 20, 1, 2);
        SpawnRestriction.register(ModEntities.HERSIR, SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (type, world, spawnReason, pos, random) -> {
                    if (world.getDifficulty() == Difficulty.PEACEFUL) {
                        return false;
                    }
                    return HersirEntity.canMobSpawn(type, world, spawnReason, pos, random);
                });

        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(
                        BiomeKeys.ICE_SPIKES, BiomeKeys.SNOWY_PLAINS, BiomeKeys.SNOWY_TAIGA,
                        BiomeKeys.SNOWY_BEACH, BiomeKeys.SNOWY_SLOPES, BiomeKeys.GROVE, BiomeKeys.JAGGED_PEAKS,
                        BiomeKeys.FROZEN_PEAKS, BiomeKeys.FROZEN_OCEAN, BiomeKeys.FROZEN_RIVER, BiomeKeys.TAIGA,
                        BiomeKeys.OLD_GROWTH_PINE_TAIGA, BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA,
                        TerralithBiomeKeys.ALPHA_ISLANDS_WINTERS, TerralithBiomeKeys.ALPINE_HIGHLANDS,
                        TerralithBiomeKeys.HIGHLANDS,
                        TerralithBiomeKeys.SHIELD_CLEARING, TerralithBiomeKeys.SHIELD,
                        TerralithBiomeKeys.SIBERIAN_TAIGA, TerralithBiomeKeys.SIBERIAN_GROVE,
                        TerralithBiomeKeys.ICE_MARSH, TerralithBiomeKeys.SNOWY_CHERRY_GROVE, TerralithBiomeKeys.SNOWY_MAPLE_FOREST,
                        TerralithBiomeKeys.SNOWY_SHIELD, TerralithBiomeKeys.ALPHA_ISLANDS_WINTER, TerralithBiomeKeys.FROZEN_CLIFFS,
                        TerralithBiomeKeys.GLACIAL_CHASM, TerralithBiomeKeys.SNOWY_BADLANDS, TerralithBiomeKeys.ALPINE_GROVE,
                        TerralithBiomeKeys.WINTRY_FOREST,TerralithBiomeKeys.WINTRY_LOWLANDS, TerralithBiomeKeys.COLD_SHRUBLAND,
                        TerralithBiomeKeys.GRAVEL_DESERT)
                        .or(BiomeSelectors.tag(ConventionalBiomeTags.CLIMATE_COLD)),
                SpawnGroup.CREATURE, ModEntities.EINHERJAR, 6, 1, 1);
        SpawnRestriction.register(ModEntities.EINHERJAR, SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (type, world, spawnReason, pos, random) -> {
                    if (world.getDifficulty() == Difficulty.PEACEFUL) {
                        return false;
                    }
                    return EinherjarEntity.canMobSpawn(type, world, spawnReason, pos, random);
                });
        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(
                        BiomeKeys.ICE_SPIKES, BiomeKeys.SNOWY_PLAINS, BiomeKeys.SNOWY_TAIGA,
                        BiomeKeys.SNOWY_BEACH, BiomeKeys.SNOWY_SLOPES, BiomeKeys.GROVE, BiomeKeys.JAGGED_PEAKS,
                        BiomeKeys.FROZEN_PEAKS, BiomeKeys.FROZEN_OCEAN, BiomeKeys.FROZEN_RIVER, BiomeKeys.TAIGA,
                        BiomeKeys.OLD_GROWTH_PINE_TAIGA, BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA,
                        TerralithBiomeKeys.ALPHA_ISLANDS_WINTERS, TerralithBiomeKeys.ALPINE_HIGHLANDS,
                        TerralithBiomeKeys.HIGHLANDS,
                        TerralithBiomeKeys.SHIELD_CLEARING, TerralithBiomeKeys.SHIELD,
                        TerralithBiomeKeys.SIBERIAN_TAIGA, TerralithBiomeKeys.SIBERIAN_GROVE,
                        TerralithBiomeKeys.ICE_MARSH, TerralithBiomeKeys.SNOWY_CHERRY_GROVE, TerralithBiomeKeys.SNOWY_MAPLE_FOREST,
                        TerralithBiomeKeys.SNOWY_SHIELD, TerralithBiomeKeys.ALPHA_ISLANDS_WINTER, TerralithBiomeKeys.FROZEN_CLIFFS,
                        TerralithBiomeKeys.GLACIAL_CHASM, TerralithBiomeKeys.SNOWY_BADLANDS, TerralithBiomeKeys.ALPINE_GROVE,
                        TerralithBiomeKeys.WINTRY_FOREST,TerralithBiomeKeys.WINTRY_LOWLANDS, TerralithBiomeKeys.COLD_SHRUBLAND,
                        TerralithBiomeKeys.GRAVEL_DESERT)
                        .or(BiomeSelectors.tag(ConventionalBiomeTags.CLIMATE_COLD)),
                SpawnGroup.CREATURE, ModEntities.VALKYRIE, 8, 1, 1);
        SpawnRestriction.register(ModEntities.VALKYRIE, SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (type, world, spawnReason, pos, random) -> {
                    if (world.getDifficulty() == Difficulty.PEACEFUL) {
                        return false;
                    }
                    return ValkyrieEntity.canMobSpawn(type, world, spawnReason, pos, random);
                });

        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(
                        BiomeKeys.ICE_SPIKES, BiomeKeys.SNOWY_PLAINS, BiomeKeys.SNOWY_TAIGA,
                        BiomeKeys.SNOWY_BEACH, BiomeKeys.SNOWY_SLOPES, BiomeKeys.GROVE, BiomeKeys.JAGGED_PEAKS,
                        BiomeKeys.FROZEN_PEAKS, BiomeKeys.FROZEN_OCEAN, BiomeKeys.FROZEN_RIVER, BiomeKeys.TAIGA,
                        BiomeKeys.OLD_GROWTH_PINE_TAIGA, BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA,
                        TerralithBiomeKeys.ALPHA_ISLANDS_WINTERS, TerralithBiomeKeys.ALPINE_HIGHLANDS,
                        TerralithBiomeKeys.HIGHLANDS,
                        TerralithBiomeKeys.SHIELD_CLEARING, TerralithBiomeKeys.SHIELD,
                        TerralithBiomeKeys.SIBERIAN_TAIGA, TerralithBiomeKeys.SIBERIAN_GROVE,
                        TerralithBiomeKeys.ICE_MARSH, TerralithBiomeKeys.SNOWY_CHERRY_GROVE, TerralithBiomeKeys.SNOWY_MAPLE_FOREST,
                        TerralithBiomeKeys.SNOWY_SHIELD, TerralithBiomeKeys.ALPHA_ISLANDS_WINTER, TerralithBiomeKeys.FROZEN_CLIFFS,
                        TerralithBiomeKeys.GLACIAL_CHASM, TerralithBiomeKeys.SNOWY_BADLANDS, TerralithBiomeKeys.ALPINE_GROVE,
                        TerralithBiomeKeys.WINTRY_FOREST,TerralithBiomeKeys.WINTRY_LOWLANDS, TerralithBiomeKeys.COLD_SHRUBLAND,
                        TerralithBiomeKeys.GRAVEL_DESERT)
                        .or(BiomeSelectors.tag(ConventionalBiomeTags.CLIMATE_COLD)),
                SpawnGroup.CREATURE, ModEntities.HUSKARL, 12, 1, 2);
        SpawnRestriction.register(ModEntities.HUSKARL, SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (type, world, spawnReason, pos, random) -> {
                    if (world.getDifficulty() == Difficulty.PEACEFUL) {
                        return false;
                    }
                    return HuskarlEntity.canMobSpawn(type, world, spawnReason, pos, random);
                });

        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(
                        BiomeKeys.ICE_SPIKES, BiomeKeys.SNOWY_PLAINS, BiomeKeys.SNOWY_TAIGA,
                        BiomeKeys.SNOWY_BEACH, BiomeKeys.SNOWY_SLOPES, BiomeKeys.GROVE, BiomeKeys.JAGGED_PEAKS,
                        BiomeKeys.FROZEN_PEAKS, BiomeKeys.FROZEN_OCEAN, BiomeKeys.FROZEN_RIVER, BiomeKeys.TAIGA,
                        BiomeKeys.OLD_GROWTH_PINE_TAIGA, BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA,
                        TerralithBiomeKeys.ALPHA_ISLANDS_WINTERS, TerralithBiomeKeys.ALPINE_HIGHLANDS,
                        TerralithBiomeKeys.HIGHLANDS,
                        TerralithBiomeKeys.SHIELD_CLEARING, TerralithBiomeKeys.SHIELD,
                        TerralithBiomeKeys.SIBERIAN_TAIGA, TerralithBiomeKeys.SIBERIAN_GROVE,
                        TerralithBiomeKeys.ICE_MARSH, TerralithBiomeKeys.SNOWY_CHERRY_GROVE, TerralithBiomeKeys.SNOWY_MAPLE_FOREST,
                        TerralithBiomeKeys.SNOWY_SHIELD, TerralithBiomeKeys.ALPHA_ISLANDS_WINTER, TerralithBiomeKeys.FROZEN_CLIFFS,
                        TerralithBiomeKeys.GLACIAL_CHASM, TerralithBiomeKeys.SNOWY_BADLANDS, TerralithBiomeKeys.ALPINE_GROVE,
                        TerralithBiomeKeys.WINTRY_FOREST,TerralithBiomeKeys.WINTRY_LOWLANDS, TerralithBiomeKeys.COLD_SHRUBLAND,
                        TerralithBiomeKeys.GRAVEL_DESERT)
                        .or(BiomeSelectors.tag(ConventionalBiomeTags.CLIMATE_COLD)),
                SpawnGroup.CREATURE, ModEntities.THROWING_AXEMAN, 20, 1, 2);
        SpawnRestriction.register(ModEntities.THROWING_AXEMAN, SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (type, world, spawnReason, pos, random) -> {
                    if (world.getDifficulty() == Difficulty.PEACEFUL) {
                        return false;
                    }
                    return ThrowingAxeManEntity.canMobSpawn(type, world, spawnReason, pos, random);
                });

        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(
                        BiomeKeys.PLAINS, BiomeKeys.SUNFLOWER_PLAINS, BiomeKeys.FOREST, BiomeKeys.FLOWER_FOREST, BiomeKeys.BIRCH_FOREST,
                        BiomeKeys.WINDSWEPT_FOREST, BiomeKeys.MEADOW, BiomeKeys.OLD_GROWTH_BIRCH_FOREST,
                        TerralithBiomeKeys.YELLOWSTONE, TerralithBiomeKeys.AMETHYST_CANYON, TerralithBiomeKeys.TEMPERATE_HIGHLANDS,
                        TerralithBiomeKeys.GRAVEL_BEACH, TerralithBiomeKeys.HOT_SHRUBLAND, TerralithBiomeKeys.ALPHA_ISLAND,
                        TerralithBiomeKeys.BLOOMING_VALLEY, TerralithBiomeKeys.LAVENDER_VALLEY, TerralithBiomeKeys.LAVENDER_FOREST,
                        TerralithBiomeKeys.MIRAGE_ISLES, TerralithBiomeKeys.MOONLIGHT_GROVE, TerralithBiomeKeys.MOONLIGHT_VALLEY,
                        TerralithBiomeKeys.SAKURA_GROVE, TerralithBiomeKeys.SAKURA_VALLEY, TerralithBiomeKeys.BLOOMING_PLATEAU,
                        TerralithBiomeKeys.SKYLANDS, TerralithBiomeKeys. SKYLANDS_AUTUMN, TerralithBiomeKeys.SKYLANDS_SPRING,
                        TerralithBiomeKeys.SKYLANDS_SUMMER, TerralithBiomeKeys.WARM_RIVER, TerralithBiomeKeys.VOLCANIC_PEAKS,
                        TerralithBiomeKeys.STONY_SPIRES, TerralithBiomeKeys.BASALT_CLIFFS)
                        .or(BiomeSelectors.tag(ConventionalBiomeTags.PLAINS)).or(BiomeSelectors.tag(ConventionalBiomeTags.FOREST))
                        .or(BiomeSelectors.tag(ConventionalBiomeTags.CLIMATE_TEMPERATE)),
                SpawnGroup.CREATURE, ModEntities.HERO_HOPLITE, 4, 1, 1);
        SpawnRestriction.register(ModEntities.HERO_HOPLITE, SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (type, world, spawnReason, pos, random) -> {
                    if (world.getDifficulty() == Difficulty.PEACEFUL) {
                        return false;
                    }
                    return HeroHopliteEntity.canMobSpawn(type, world, spawnReason, pos, random);
                });

        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(
                        BiomeKeys.PLAINS, BiomeKeys.SUNFLOWER_PLAINS, BiomeKeys.FOREST, BiomeKeys.FLOWER_FOREST, BiomeKeys.BIRCH_FOREST,
                        BiomeKeys.WINDSWEPT_FOREST, BiomeKeys.MEADOW, BiomeKeys.OLD_GROWTH_BIRCH_FOREST,
                        TerralithBiomeKeys.YELLOWSTONE, TerralithBiomeKeys.AMETHYST_CANYON, TerralithBiomeKeys.TEMPERATE_HIGHLANDS,
                        TerralithBiomeKeys.GRAVEL_BEACH, TerralithBiomeKeys.HOT_SHRUBLAND, TerralithBiomeKeys.ALPHA_ISLAND,
                        TerralithBiomeKeys.BLOOMING_VALLEY, TerralithBiomeKeys.LAVENDER_VALLEY, TerralithBiomeKeys.LAVENDER_FOREST,
                        TerralithBiomeKeys.MIRAGE_ISLES, TerralithBiomeKeys.MOONLIGHT_GROVE, TerralithBiomeKeys.MOONLIGHT_VALLEY,
                        TerralithBiomeKeys.SAKURA_GROVE, TerralithBiomeKeys.SAKURA_VALLEY, TerralithBiomeKeys.BLOOMING_PLATEAU,
                        TerralithBiomeKeys.SKYLANDS, TerralithBiomeKeys. SKYLANDS_AUTUMN, TerralithBiomeKeys.SKYLANDS_SPRING,
                        TerralithBiomeKeys.SKYLANDS_SUMMER, TerralithBiomeKeys.WARM_RIVER, TerralithBiomeKeys.VOLCANIC_PEAKS,
                        TerralithBiomeKeys.STONY_SPIRES, TerralithBiomeKeys.BASALT_CLIFFS)
                        .or(BiomeSelectors.tag(ConventionalBiomeTags.PLAINS)).or(BiomeSelectors.tag(ConventionalBiomeTags.FOREST))
                        .or(BiomeSelectors.tag(ConventionalBiomeTags.CLIMATE_TEMPERATE)),
                SpawnGroup.CREATURE, ModEntities.CHAMPION_HOPLITE, 7, 1, 2);
        SpawnRestriction.register(ModEntities.CHAMPION_HOPLITE, SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (type, world, spawnReason, pos, random) -> {
                    if (world.getDifficulty() == Difficulty.PEACEFUL) {
                        return false;
                    }
                    return ChampionHopliteEntity.canMobSpawn(type, world, spawnReason, pos, random);
                });

        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(
                        BiomeKeys.PLAINS, BiomeKeys.SUNFLOWER_PLAINS, BiomeKeys.FOREST, BiomeKeys.FLOWER_FOREST, BiomeKeys.BIRCH_FOREST,
                        BiomeKeys.WINDSWEPT_FOREST, BiomeKeys.MEADOW, BiomeKeys.OLD_GROWTH_BIRCH_FOREST,
                        TerralithBiomeKeys.YELLOWSTONE, TerralithBiomeKeys.AMETHYST_CANYON, TerralithBiomeKeys.TEMPERATE_HIGHLANDS,
                        TerralithBiomeKeys.GRAVEL_BEACH, TerralithBiomeKeys.HOT_SHRUBLAND, TerralithBiomeKeys.ALPHA_ISLAND,
                        TerralithBiomeKeys.BLOOMING_VALLEY, TerralithBiomeKeys.LAVENDER_VALLEY, TerralithBiomeKeys.LAVENDER_FOREST,
                        TerralithBiomeKeys.MIRAGE_ISLES, TerralithBiomeKeys.MOONLIGHT_GROVE, TerralithBiomeKeys.MOONLIGHT_VALLEY,
                        TerralithBiomeKeys.SAKURA_GROVE, TerralithBiomeKeys.SAKURA_VALLEY, TerralithBiomeKeys.BLOOMING_PLATEAU,
                        TerralithBiomeKeys.SKYLANDS, TerralithBiomeKeys. SKYLANDS_AUTUMN, TerralithBiomeKeys.SKYLANDS_SPRING,
                        TerralithBiomeKeys.SKYLANDS_SUMMER, TerralithBiomeKeys.WARM_RIVER, TerralithBiomeKeys.VOLCANIC_PEAKS,
                        TerralithBiomeKeys.STONY_SPIRES, TerralithBiomeKeys.BASALT_CLIFFS)
                        .or(BiomeSelectors.tag(ConventionalBiomeTags.PLAINS)).or(BiomeSelectors.tag(ConventionalBiomeTags.FOREST))
                        .or(BiomeSelectors.tag(ConventionalBiomeTags.CLIMATE_TEMPERATE)),
                SpawnGroup.CREATURE, ModEntities.ELITE_HOPLITE, 8, 1, 3);
        SpawnRestriction.register(ModEntities.ELITE_HOPLITE, SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (type, world, spawnReason, pos, random) -> {
                    if (world.getDifficulty() == Difficulty.PEACEFUL) {
                        return false;
                    }
                    return EliteHopliteEntity.canMobSpawn(type, world, spawnReason, pos, random);
                });

        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(
                BiomeKeys.BASALT_DELTAS, BiomeKeys.SOUL_SAND_VALLEY, BiomeKeys.NETHER_WASTES,
                        IncendiumBiomeKeys.ASH_BARRENS, IncendiumBiomeKeys.QUARTZ_FLATS, IncendiumBiomeKeys.WEEPING_VALLEY,
                        IncendiumBiomeKeys.WITHERED_FOREST, IncendiumBiomeKeys.VOLCANIC_DELTAS),
                SpawnGroup.MONSTER, ModEntities.HADES_SHADE, 9, 1, 1);
        SpawnRestriction.register(ModEntities.HADES_SHADE, SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (type, world, spawnReason, pos, random) -> {
                    if (world.getDifficulty() == Difficulty.PEACEFUL) {
                        return false;
                    }
                    return HadesShadeEntity.canMobSpawn(type, world, spawnReason, pos, random);
            });

        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(
                        BiomeKeys.SOUL_SAND_VALLEY, BiomeKeys.NETHER_WASTES,
                        IncendiumBiomeKeys.ASH_BARRENS, IncendiumBiomeKeys.QUARTZ_FLATS, IncendiumBiomeKeys.WEEPING_VALLEY,
                        IncendiumBiomeKeys.WITHERED_FOREST),
            SpawnGroup.MONSTER, ModEntities.HADES_CHOSEN, 3, 1, 1);
        SpawnRestriction.register(ModEntities.HADES_CHOSEN, SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (type, world, spawnReason, pos, random) -> {
                    if (world.getDifficulty() == Difficulty.PEACEFUL) {
                        return false;
                    }
                    return HadesChosenEntity.canMobSpawn(type, world, spawnReason, pos, random);
                });
    }
}
