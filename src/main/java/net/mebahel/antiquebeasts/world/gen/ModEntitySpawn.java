package net.mebahel.antiquebeasts.world.gen;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags;
import net.mebahel.antiquebeasts.entity.ModEntities;
import net.mebahel.antiquebeasts.entity.custom.*;
import net.mebahel.antiquebeasts.entity.custom.egyptian.CamelryEntity;
import net.mebahel.antiquebeasts.entity.custom.egyptian.MummyEntity;
import net.mebahel.antiquebeasts.entity.custom.egyptian.ServantEntity;
import net.mebahel.antiquebeasts.entity.custom.egyptian.WadjetEntity;
import net.mebahel.antiquebeasts.entity.custom.greek.CentaurEntity;
import net.mebahel.antiquebeasts.entity.custom.greek.ChampionHopliteEntity;
import net.mebahel.antiquebeasts.entity.custom.greek.EliteHopliteEntity;
import net.mebahel.antiquebeasts.entity.custom.greek.HeroHopliteEntity;
import net.mebahel.antiquebeasts.entity.custom.norse.*;
import net.mebahel.antiquebeasts.entity.custom.other.*;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.world.Difficulty;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.BiomeKeys;

public class ModEntitySpawn {
    public static void addEntitySpawn() {
        BiomeModifications.addSpawn(BiomeSelectors.foundInOverworld(),
                SpawnGroup.MONSTER, ModEntities.SKELETON_WARRIOR, 4, 1, 1);
        SpawnRestriction.register(ModEntities.SKELETON_WARRIOR, SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (type, world, spawnReason, pos, random) -> {
                    if (world.getDifficulty() == Difficulty.PEACEFUL) {
                        return false;
                    }
                    return SkeletonWarriorEntity.canSpawnInDark(type, world, spawnReason, pos, random);
                });

        BiomeModifications.addSpawn((BiomeSelectors.tag(ConventionalBiomeTags.MOUNTAIN)).or(BiomeSelectors.tag(ConventionalBiomeTags.MOUNTAIN_PEAK))
                        .or(BiomeSelectors.tag(ConventionalBiomeTags.MOUNTAIN_SLOPE)).or(BiomeSelectors.tag(ConventionalBiomeTags.SNOWY_PLAINS)),
                SpawnGroup.CREATURE, ModEntities.HARPY, 6, 1, 3);
        SpawnRestriction.register(ModEntities.HARPY, SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (type, world, spawnReason, pos, random) -> {
                    if (world.getDifficulty() == Difficulty.PEACEFUL) {
                        return false;
                    }
                    return HarpyEntity.canMobSpawn(type, world, spawnReason, pos, random);
                });

        BiomeModifications.addSpawn(BiomeSelectors.foundInOverworld(),
                SpawnGroup.MONSTER, ModEntities.DRAUGR_SCOURGE, 3, 1, 1);
        SpawnRestriction.register(ModEntities.DRAUGR_SCOURGE, SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (type, world, spawnReason, pos, random) -> {
                    if (world.getDifficulty() == Difficulty.PEACEFUL) {
                        return false;
                    }
                    return DraugrScourgeEntity.canSpawnIgnoreLightLevel(type, world, spawnReason, pos, random);
                });

        BiomeModifications.addSpawn(BiomeSelectors.foundInOverworld(),
                SpawnGroup.MONSTER, ModEntities.DRAUGR_WIGHT, 8, 1, 1);
        SpawnRestriction.register(ModEntities.DRAUGR_WIGHT, SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (type, world, spawnReason, pos, random) -> {
                    if (world.getDifficulty() == Difficulty.PEACEFUL) {
                        return false;
                    }
                    return DraugrWightEntity.canSpawnIgnoreLightLevel(type, world, spawnReason, pos, random);
                });

        BiomeModifications.addSpawn(BiomeSelectors.foundInOverworld(),
                SpawnGroup.MONSTER, ModEntities.DRAUGR_ARCHER, 10, 1, 2);
        SpawnRestriction.register(ModEntities.DRAUGR_ARCHER, SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (type, world, spawnReason, pos, random) -> {
                    if (world.getDifficulty() == Difficulty.PEACEFUL) {
                        return false;
                    }
                    return DraugrArcherEntity.canSpawnIgnoreLightLevel(type, world, spawnReason, pos, random);
                });

        BiomeModifications.addSpawn(BiomeSelectors.foundInOverworld(),
                SpawnGroup.MONSTER, ModEntities.DRAUGR, 10, 1, 2);
        SpawnRestriction.register(ModEntities.DRAUGR, SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (type, world, spawnReason, pos, random) -> {
                    if (world.getDifficulty() == Difficulty.PEACEFUL) {
                        return false;
                    }
                    return DraugrEntity.canSpawnInDark(type, world, spawnReason, pos, random);
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
                SpawnGroup.CREATURE, ModEntities.PEGASUS, 3, 1, 1);
        SpawnRestriction.register(ModEntities.PEGASUS, SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (type, world, spawnReason, pos, random) -> {
                    if (world.getDifficulty() == Difficulty.PEACEFUL) {
                        return false;
                    }
                    return PegasusEntity.canMobSpawn(type, world, spawnReason, pos, random);
                });

        /*BiomeModifications.addSpawn(BiomeSelectors.includeByKey(
                                BiomeKeys.DESERT, BiomeKeys.BADLANDS)
                        .or(BiomeSelectors.tag(ConventionalBiomeTags.DESERT)).or(BiomeSelectors.tag(ConventionalBiomeTags.BADLANDS)),
                SpawnGroup.CREATURE, ModEntities.ELEPHANT_RIDER, 4, 1, 1);
        SpawnRestriction.register(ModEntities.ELEPHANT_RIDER, SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (type, world, spawnReason, pos, random) -> {
                    if (world.getDifficulty() == Difficulty.PEACEFUL) {
                        return false;
                    }
                    return ElephantRiderEntity.canMobSpawn(type, world, spawnReason, pos, random);
                });*/

        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(
                                BiomeKeys.DESERT, BiomeKeys.BADLANDS)
                        .or(BiomeSelectors.tag(ConventionalBiomeTags.DESERT)).or(BiomeSelectors.tag(ConventionalBiomeTags.BADLANDS)),
                SpawnGroup.CREATURE, ModEntities.SERVANT, 12, 1, 3);
        SpawnRestriction.register(ModEntities.SERVANT, SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (type, world, spawnReason, pos, random) -> {
                    if (world.getDifficulty() == Difficulty.PEACEFUL) {
                        return false;
                    }
                    return ServantEntity.canMobSpawn(type, world, spawnReason, pos, random);
                });

        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(
                                BiomeKeys.DESERT, BiomeKeys.BADLANDS)
                        .or(BiomeSelectors.tag(ConventionalBiomeTags.DESERT)).or(BiomeSelectors.tag(ConventionalBiomeTags.BADLANDS)),
                SpawnGroup.CREATURE, ModEntities.MUMMY, 6, 1, 1);
        SpawnRestriction.register(ModEntities.MUMMY, SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (type, world, spawnReason, pos, random) -> {
                    if (world.getDifficulty() == Difficulty.PEACEFUL) {
                        return false;
                    }
                    return MummyEntity.canMobSpawn(type, world, spawnReason, pos, random);
                });

        /*BiomeModifications.addSpawn(BiomeSelectors.includeByKey(
                                BiomeKeys.DESERT, BiomeKeys.BADLANDS)
                        .or(BiomeSelectors.tag(ConventionalBiomeTags.DESERT)).or(BiomeSelectors.tag(ConventionalBiomeTags.BADLANDS)),
                SpawnGroup.CREATURE, ModEntities.CAMELRY, 10, 1, 2);
        SpawnRestriction.register(ModEntities.CAMELRY, SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (type, world, spawnReason, pos, random) -> {
                    if (world.getDifficulty() == Difficulty.PEACEFUL) {
                        return false;
                    }
                    return CamelryEntity.canMobSpawn(type, world, spawnReason, pos, random);
                });*/

        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(
                                BiomeKeys.DESERT, BiomeKeys.BADLANDS)
                        .or(BiomeSelectors.tag(ConventionalBiomeTags.DESERT)).or(BiomeSelectors.tag(ConventionalBiomeTags.BADLANDS)),
                SpawnGroup.CREATURE, ModEntities.EGYPTIAN_CARAVAN, 3, 1, 1);
        SpawnRestriction.register(ModEntities.EGYPTIAN_CARAVAN, SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (type, world, spawnReason, pos, random) -> {
                    if (world.getDifficulty() == Difficulty.PEACEFUL) {
                        return false;
                    }
                    return CamelryEntity.canMobSpawn(type, world, spawnReason, pos, random);
                });

        /*BiomeModifications.addSpawn(BiomeSelectors.includeByKey(
                                BiomeKeys.DESERT, BiomeKeys.BADLANDS)
                        .or(BiomeSelectors.tag(ConventionalBiomeTags.DESERT)).or(BiomeSelectors.tag(ConventionalBiomeTags.BADLANDS)),
                SpawnGroup.CREATURE, ModEntities.AXEMAN, 12, 1, 2);
        SpawnRestriction.register(ModEntities.AXEMAN, SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (type, world, spawnReason, pos, random) -> {
                    if (world.getDifficulty() == Difficulty.PEACEFUL) {
                        return false;
                    }
                    return AxemanEntity.canMobSpawn(type, world, spawnReason, pos, random);
                });*/

        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(
                                BiomeKeys.DESERT, BiomeKeys.BADLANDS)
                        .or(BiomeSelectors.tag(ConventionalBiomeTags.DESERT)).or(BiomeSelectors.tag(ConventionalBiomeTags.BADLANDS)),
                SpawnGroup.CREATURE, ModEntities.WADJET, 7, 1, 1);
        SpawnRestriction.register(ModEntities.WADJET, SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (type, world, spawnReason, pos, random) -> {
                    if (world.getDifficulty() == Difficulty.PEACEFUL) {
                        return false;
                    }
                    return WadjetEntity.canMobSpawn(type, world, spawnReason, pos, random);
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
                SpawnGroup.CREATURE, ModEntities.CHIMERA, 3, 1, 1);
        SpawnRestriction.register(ModEntities.CHIMERA, SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (type, world, spawnReason, pos, random) -> {
                    if (world.getDifficulty() == Difficulty.PEACEFUL) {
                        return false;
                    }
                    return ChimeraEntity.canMobSpawn(type, world, spawnReason, pos, random);
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
                SpawnGroup.CREATURE, ModEntities.CYCLOPS, 2, 1, 1);
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
                SpawnGroup.CREATURE, ModEntities.EINHERJAR, 8, 1, 1);
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
                SpawnGroup.CREATURE, ModEntities.VALKYRIE, 10, 1, 1);
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
                SpawnGroup.CREATURE, ModEntities.HERO_HOPLITE, 6, 1, 1);
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
                SpawnGroup.CREATURE, ModEntities.CHAMPION_HOPLITE, 12, 1, 2);
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
                SpawnGroup.CREATURE, ModEntities.CENTAUR, 7, 1, 2);
        SpawnRestriction.register(ModEntities.CENTAUR, SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (type, world, spawnReason, pos, random) -> {
                    if (world.getDifficulty() == Difficulty.PEACEFUL) {
                        return false;
                    }
                    return CentaurEntity.canMobSpawn(type, world, spawnReason, pos, random);
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
                SpawnGroup.CREATURE, ModEntities.ELITE_HOPLITE, 14, 1, 3);
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
                SpawnGroup.MONSTER, ModEntities.HADES_CHOSEN, 6, 1, 1);
        SpawnRestriction.register(ModEntities.HADES_CHOSEN, SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (type, world, spawnReason, pos, random) -> {
                    if (world.getDifficulty() == Difficulty.PEACEFUL) {
                        return false;
                    }
                    return HadesChosenEntity.canMobSpawn(type, world, spawnReason, pos, random);
                });
    }
}