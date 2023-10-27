package net.mebahel.antiquebeasts.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.custom.*;
import net.mebahel.antiquebeasts.entity.projectiles.HopliteSpearEntity;
import net.mebahel.antiquebeasts.entity.projectiles.ThrowingRockEntity;
import net.mebahel.antiquebeasts.entity.projectiles.ThrowingSnowRockEntity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static software.bernie.example.registry.EntityRegistry.buildEntity;


public class ModEntities {
    public static final EntityType<CyclopsEntity> CYCLOPS = Registry.register(
            Registry.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "cyclops"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, CyclopsEntity::new)
                    .dimensions(EntityDimensions.fixed(0.95f, 4.75f)).build());

    public static final EntityType<FrostCyclopsEntity> FROST_CYCLOPS = Registry.register(
            Registry.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "frost-cyclops"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, FrostCyclopsEntity::new)
                    .dimensions(EntityDimensions.fixed(0.95f, 4.75f)).build());

    public static final EntityType<ChampionHopliteEntity> CHAMPION_HOPLITE = Registry.register(
            Registry.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "champion_hoplite"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, ChampionHopliteEntity::new)
                    .dimensions(EntityDimensions.fixed(0.85f, 2f)).build());

    public static final EntityType<EliteHopliteEntity> ELITE_HOPLITE = Registry.register(
            Registry.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "elite_hoplite"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, EliteHopliteEntity::new)
                    .dimensions(EntityDimensions.fixed(0.85f, 2f)).build());

    public static final EntityType<HeroHopliteEntity> HERO_HOPLITE = Registry.register(
            Registry.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "hero_hoplite"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, HeroHopliteEntity::new)
                    .dimensions(EntityDimensions.fixed(0.85f, 2f)).build());

    public static final EntityType<ThrowingRockEntity> THROWINGROCK = Registry.register(
            Registry.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "throwingrock"),
            FabricEntityTypeBuilder.<ThrowingRockEntity>create(SpawnGroup.MISC, ThrowingRockEntity::new)
                    .dimensions(EntityDimensions.fixed(2f, 2f)).build());

    public static final EntityType<ThrowingSnowRockEntity> THROWINGSNOWROCK = Registry.register(
            Registry.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "throwingsnowrock"),
            FabricEntityTypeBuilder.<ThrowingSnowRockEntity>create(SpawnGroup.MISC, ThrowingSnowRockEntity::new)
                    .dimensions(EntityDimensions.fixed(2f, 2f)).build());

    public static final EntityType<HopliteSpearEntity> HOPLITE_SPEAR = Registry.register(
            Registry.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "hoplite_spear"),
            FabricEntityTypeBuilder.<HopliteSpearEntity>create(SpawnGroup.MISC, HopliteSpearEntity::new)
                    .dimensions(EntityDimensions.fixed(12f, 2f)).build());
}
