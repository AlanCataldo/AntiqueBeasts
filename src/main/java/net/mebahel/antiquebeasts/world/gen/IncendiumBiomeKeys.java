package net.mebahel.antiquebeasts.world.gen;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

public class IncendiumBiomeKeys {
    public static final RegistryKey<Biome> ASH_BARRENS = register("incendium:ash_barrens");
    public static final RegistryKey<Biome> INFERNAL_DUNES = register("incendium:infernal_dunes");
    public static final RegistryKey<Biome> INVERTED_FOREST = register("incendium:inverted_forest");
    public static final RegistryKey<Biome> QUARTZ_FLATS = register("incendium:quartz_flats");
    public static final RegistryKey<Biome> TOXIC_HEAP = register("incendium:toxic_heap");
    public static final RegistryKey<Biome> VOLCANIC_DELTAS = register("incendium:volcanic_deltas");
    public static final RegistryKey<Biome> WEEPING_VALLEY = register("incendium:weeping_valley");
    public static final RegistryKey<Biome> WITHERED_FOREST = register("incendium:withered_forest");
    private static RegistryKey<Biome> register(String name) {
        return RegistryKey.of(Registry.BIOME_KEY, new Identifier(name));
    }
}
