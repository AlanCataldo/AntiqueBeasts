package net.mebahel.antiquebeasts.world.gen;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.mebahel.antiquebeasts.entity.ModEntities;
import net.mebahel.antiquebeasts.entity.custom.ChampionHopliteEntity;
import net.mebahel.antiquebeasts.entity.custom.HeroHopliteEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.tag.BiomeTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.Heightmap;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.BiomeKeys;

public class ModEntitySpawn {
    public static void addEntitySpawn() {
        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(
                        BiomeKeys.PLAINS, BiomeKeys.SUNFLOWER_PLAINS, BiomeKeys.FOREST, BiomeKeys.FLOWER_FOREST, BiomeKeys.BIRCH_FOREST,
                        BiomeKeys.OLD_GROWTH_BIRCH_FOREST, BiomeKeys.TAIGA, BiomeKeys.OLD_GROWTH_PINE_TAIGA, BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA,
                        BiomeKeys.WINDSWEPT_FOREST, BiomeKeys.SAVANNA, BiomeKeys.MEADOW,
                        TerralithBiomeKeys.YELLOWSTONE, TerralithBiomeKeys.AMETHYST_CANYON, TerralithBiomeKeys.TEMPERATE_HIGHLANDS,
                        TerralithBiomeKeys.GRAVEL_BEACH, TerralithBiomeKeys.HOT_SHRUBLAND, TerralithBiomeKeys.ALPHA_ISLAND,
                        TerralithBiomeKeys.BLOOMING_VALLEY, TerralithBiomeKeys.LAVENDER_VALLEY, TerralithBiomeKeys.LAVENDER_FOREST,
                        TerralithBiomeKeys.MIRAGE_ISLES, TerralithBiomeKeys.MOONLIGHT_GROVE, TerralithBiomeKeys.MOONLIGHT_VALLEY,
                        TerralithBiomeKeys.SAKURA_GROVE, TerralithBiomeKeys.SAKURA_VALLEY, TerralithBiomeKeys.BLOOMING_PLATEAU,
                        TerralithBiomeKeys.SKYLANDS, TerralithBiomeKeys. SKYLANDS_AUTUMN, TerralithBiomeKeys.SKYLANDS_SPRING,
                        TerralithBiomeKeys.SKYLANDS_SUMMER, TerralithBiomeKeys.WARM_RIVER, TerralithBiomeKeys.VOLCANIC_PEAKS,
                        TerralithBiomeKeys.STONY_SPIRES, TerralithBiomeKeys.BASALT_CLIFFS),
                SpawnGroup.CREATURE, ModEntities.CYCLOPS, 3, 4, 4);
        SpawnRestriction.register(ModEntities.CYCLOPS, SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (type, world, spawnReason, pos, random) -> {
                    if (world.getDifficulty() == Difficulty.PEACEFUL) {
                        return false;
                    }
                    return HeroHopliteEntity.canMobSpawn(type, world, spawnReason, pos, random);
                });

        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(
                        BiomeKeys.ICE_SPIKES, BiomeKeys.SNOWY_PLAINS, BiomeKeys.SNOWY_TAIGA,
                        BiomeKeys.SNOWY_BEACH, BiomeKeys.SNOWY_SLOPES, BiomeKeys.GROVE, BiomeKeys.JAGGED_PEAKS,
                        BiomeKeys.FROZEN_PEAKS, BiomeKeys.FROZEN_OCEAN, BiomeKeys.FROZEN_RIVER,
                        TerralithBiomeKeys.ALPHA_ISLANDS_WINTERS, TerralithBiomeKeys.ALPINE_HIGHLANDS,
                        TerralithBiomeKeys.GRANITE_CLIFFS, TerralithBiomeKeys.HIGHLANDS,
                        TerralithBiomeKeys.SHIELD_CLEARING, TerralithBiomeKeys.SHIELD,
                        TerralithBiomeKeys.SIBERIAN_TAIGA, TerralithBiomeKeys.SIBERIAN_GROVE, TerralithBiomeKeys.BIRCH_TAIGA,
                        TerralithBiomeKeys.ICE_MARSH, TerralithBiomeKeys.SNOWY_CHERRY_GROVE, TerralithBiomeKeys.SNOWY_MAPLE_FOREST,
                        TerralithBiomeKeys.SNOWY_SHIELD, TerralithBiomeKeys.ALPHA_ISLANDS_WINTER, TerralithBiomeKeys.FROZEN_CLIFFS,
                        TerralithBiomeKeys.GLACIAL_CHASM, TerralithBiomeKeys.SNOWY_BADLANDS, TerralithBiomeKeys.ALPINE_GROVE,
                        TerralithBiomeKeys.WINTRY_FOREST,TerralithBiomeKeys.WINTRY_LOWLANDS),
                SpawnGroup.CREATURE, ModEntities.FROST_CYCLOPS, 2, 1, 1);
        SpawnRestriction.register(ModEntities.FROST_CYCLOPS, SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (type, world, spawnReason, pos, random) -> {
                    if (world.getDifficulty() == Difficulty.PEACEFUL) {
                        return false;
                    }
                    return HeroHopliteEntity.canMobSpawn(type, world, spawnReason, pos, random);
                });

        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(
                        BiomeKeys.PLAINS, BiomeKeys.SUNFLOWER_PLAINS, BiomeKeys.FOREST, BiomeKeys.FLOWER_FOREST, BiomeKeys.BIRCH_FOREST,
                        BiomeKeys.OLD_GROWTH_BIRCH_FOREST, BiomeKeys.TAIGA, BiomeKeys.OLD_GROWTH_PINE_TAIGA, BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA,
                        BiomeKeys.WINDSWEPT_FOREST, BiomeKeys.SAVANNA, BiomeKeys.MEADOW,
                        TerralithBiomeKeys.YELLOWSTONE, TerralithBiomeKeys.AMETHYST_CANYON, TerralithBiomeKeys.TEMPERATE_HIGHLANDS,
                        TerralithBiomeKeys.GRAVEL_BEACH, TerralithBiomeKeys.HOT_SHRUBLAND, TerralithBiomeKeys.ALPHA_ISLAND,
                        TerralithBiomeKeys.BLOOMING_VALLEY, TerralithBiomeKeys.LAVENDER_VALLEY, TerralithBiomeKeys.LAVENDER_FOREST,
                        TerralithBiomeKeys.MIRAGE_ISLES, TerralithBiomeKeys.MOONLIGHT_GROVE, TerralithBiomeKeys.MOONLIGHT_VALLEY,
                        TerralithBiomeKeys.SAKURA_GROVE, TerralithBiomeKeys.SAKURA_VALLEY, TerralithBiomeKeys.BLOOMING_PLATEAU,
                        TerralithBiomeKeys.SKYLANDS, TerralithBiomeKeys. SKYLANDS_AUTUMN, TerralithBiomeKeys.SKYLANDS_SPRING,
                        TerralithBiomeKeys.SKYLANDS_SUMMER, TerralithBiomeKeys.WARM_RIVER, TerralithBiomeKeys.VOLCANIC_PEAKS,
                        TerralithBiomeKeys.STONY_SPIRES, TerralithBiomeKeys.BASALT_CLIFFS
                ),
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
                        BiomeKeys.OLD_GROWTH_BIRCH_FOREST, BiomeKeys.TAIGA, BiomeKeys.OLD_GROWTH_PINE_TAIGA, BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA,
                        BiomeKeys.WINDSWEPT_FOREST, BiomeKeys.SAVANNA, BiomeKeys.MEADOW,
                        TerralithBiomeKeys.YELLOWSTONE, TerralithBiomeKeys.AMETHYST_CANYON, TerralithBiomeKeys.TEMPERATE_HIGHLANDS,
                        TerralithBiomeKeys.GRAVEL_BEACH, TerralithBiomeKeys.HOT_SHRUBLAND, TerralithBiomeKeys.ALPHA_ISLAND,
                        TerralithBiomeKeys.BLOOMING_VALLEY, TerralithBiomeKeys.LAVENDER_VALLEY, TerralithBiomeKeys.LAVENDER_FOREST,
                        TerralithBiomeKeys.MIRAGE_ISLES, TerralithBiomeKeys.MOONLIGHT_GROVE, TerralithBiomeKeys.MOONLIGHT_VALLEY,
                        TerralithBiomeKeys.SAKURA_GROVE, TerralithBiomeKeys.SAKURA_VALLEY, TerralithBiomeKeys.BLOOMING_PLATEAU,
                        TerralithBiomeKeys.SKYLANDS, TerralithBiomeKeys. SKYLANDS_AUTUMN, TerralithBiomeKeys.SKYLANDS_SPRING,
                        TerralithBiomeKeys.SKYLANDS_SUMMER, TerralithBiomeKeys.WARM_RIVER, TerralithBiomeKeys.VOLCANIC_PEAKS,
                        TerralithBiomeKeys.STONY_SPIRES, TerralithBiomeKeys.BASALT_CLIFFS
                ),
                SpawnGroup.CREATURE, ModEntities.CHAMPION_HOPLITE, 6, 1, 2);
        SpawnRestriction.register(ModEntities.CHAMPION_HOPLITE, SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (type, world, spawnReason, pos, random) -> {
                    if (world.getDifficulty() == Difficulty.PEACEFUL) {
                        return false;
                    }
                    return HeroHopliteEntity.canMobSpawn(type, world, spawnReason, pos, random);
                });
        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(
                        BiomeKeys.PLAINS, BiomeKeys.SUNFLOWER_PLAINS, BiomeKeys.FOREST, BiomeKeys.FLOWER_FOREST, BiomeKeys.BIRCH_FOREST,
                        BiomeKeys.OLD_GROWTH_BIRCH_FOREST, BiomeKeys.TAIGA, BiomeKeys.OLD_GROWTH_PINE_TAIGA, BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA,
                        BiomeKeys.WINDSWEPT_FOREST, BiomeKeys.SAVANNA, BiomeKeys.MEADOW,
                        TerralithBiomeKeys.YELLOWSTONE, TerralithBiomeKeys.AMETHYST_CANYON, TerralithBiomeKeys.TEMPERATE_HIGHLANDS,
                        TerralithBiomeKeys.GRAVEL_BEACH, TerralithBiomeKeys.HOT_SHRUBLAND, TerralithBiomeKeys.ALPHA_ISLAND,
                        TerralithBiomeKeys.BLOOMING_VALLEY, TerralithBiomeKeys.LAVENDER_VALLEY, TerralithBiomeKeys.LAVENDER_FOREST,
                        TerralithBiomeKeys.MIRAGE_ISLES, TerralithBiomeKeys.MOONLIGHT_GROVE, TerralithBiomeKeys.MOONLIGHT_VALLEY,
                        TerralithBiomeKeys.SAKURA_GROVE, TerralithBiomeKeys.SAKURA_VALLEY, TerralithBiomeKeys.BLOOMING_PLATEAU,
                        TerralithBiomeKeys.SKYLANDS, TerralithBiomeKeys. SKYLANDS_AUTUMN, TerralithBiomeKeys.SKYLANDS_SPRING,
                        TerralithBiomeKeys.SKYLANDS_SUMMER, TerralithBiomeKeys.WARM_RIVER, TerralithBiomeKeys.VOLCANIC_PEAKS,
                        TerralithBiomeKeys.STONY_SPIRES, TerralithBiomeKeys.BASALT_CLIFFS
                ),
                SpawnGroup.CREATURE, ModEntities.ELITE_HOPLITE, 8, 2, 3);
        SpawnRestriction.register(ModEntities.ELITE_HOPLITE, SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (type, world, spawnReason, pos, random) -> {
                    if (world.getDifficulty() == Difficulty.PEACEFUL) {
                        return false;
                    }
                    return HeroHopliteEntity.canMobSpawn(type, world, spawnReason, pos, random);
                });
    }
}
