package net.mebahel.antiquebeasts.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.block.entity.BlockScanEntity;
import net.mebahel.antiquebeasts.entity.custom.*;
import net.mebahel.antiquebeasts.entity.custom.dwemer.DwemerCenturionEntity;
import net.mebahel.antiquebeasts.entity.custom.dwemer.DwemerSpiderEntity;
import net.mebahel.antiquebeasts.entity.custom.dwemer.DwemerSpiderGuardianEntity;
import net.mebahel.antiquebeasts.entity.custom.egyptian.*;
import net.mebahel.antiquebeasts.entity.custom.greek.CentaurEntity;
import net.mebahel.antiquebeasts.entity.custom.greek.ChampionHopliteEntity;
import net.mebahel.antiquebeasts.entity.custom.greek.EliteHopliteEntity;
import net.mebahel.antiquebeasts.entity.custom.greek.HeroHopliteEntity;
import net.mebahel.antiquebeasts.entity.custom.norse.*;
import net.mebahel.antiquebeasts.entity.custom.other.*;
import net.mebahel.antiquebeasts.entity.projectiles.*;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static final EntityType<InfernalDraugrEntity> INFERNAL_DRAUGR = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "infernal_draugr"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, InfernalDraugrEntity::new)
                    .dimensions(EntityDimensions.fixed(0.65f, 1.99f)).build());

    public static final EntityType<FlameAtronachEntity> FLAME_ATRONACH = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "flame_atronach"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, FlameAtronachEntity::new)
                    .dimensions(EntityDimensions.fixed(0.8f, 2f)).build());

    public static final EntityType<FireboltEntity> FIREBOLT_PROJECTILE = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "firebolt_projectile"),
            FabricEntityTypeBuilder.<FireboltEntity>create(SpawnGroup.MISC, FireboltEntity::new)
                    .dimensions(EntityDimensions.fixed(0.5f, 0.5f)).build());

    public static final EntityType<BlockScanEntity> BLOCK_SCAN_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(AntiqueBeasts.MOD_ID, "block_scan_entity"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, BlockScanEntity::new)
                    .dimensions(EntityDimensions.fixed(0.75f, 1.0F)) // Set size to 1x1x1
                    .build()
    );
    public static final EntityType<SteamProjectileEntity> STEAM_PROJECTILE = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "steam_projectile"),
            FabricEntityTypeBuilder.<SteamProjectileEntity>create(SpawnGroup.MISC, SteamProjectileEntity::new)
                    .dimensions(EntityDimensions.fixed(1, 1)).build());
    public static final EntityType<DwemerCenturionEntity> DWEMER_CENTURION = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "dwemer_centurion"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, DwemerCenturionEntity::new)
                    .dimensions(EntityDimensions.fixed(1.75f, 3.99f)).build());
    public static final EntityType<DwemerSpiderGuardianEntity> DWEMER_SPIDER_GUARDIAN = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "dwemer_spider_guardian"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, DwemerSpiderGuardianEntity::new)
                    .dimensions(EntityDimensions.fixed(0.99f, 1.25f)).build());
    public static final EntityType<DwemerSpiderEntity> DWEMER_SPIDER = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "dwemer_spider"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, DwemerSpiderEntity::new)
                    .dimensions(EntityDimensions.fixed(0.8f, 1f)).build());
    public static final EntityType<DraugrOverlordEntity> DRAUGR_OVERLORD = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "draugr_overlord"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, DraugrOverlordEntity::new)
                    .dimensions(EntityDimensions.fixed(0.8f, 2.99f)).build());
    public static final EntityType<SkeletonWarriorHeadEntity> SKELETON_WARRIOR_HEAD = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "skeleton_warrior_head"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, SkeletonWarriorHeadEntity::new)
                    .dimensions(EntityDimensions.fixed(0.5f, 0.5f)).build());
    public static final EntityType<SkeletonWarriorEntity> SKELETON_WARRIOR = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "skeleton_warrior"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, SkeletonWarriorEntity::new)
                    .dimensions(EntityDimensions.fixed(0.65f, 1.99f)).build());
    public static final EntityType<DraugrScourgeEntity> DRAUGR_SCOURGE= Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "draugr_scourge"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, DraugrScourgeEntity::new)
                    .dimensions(EntityDimensions.fixed(0.65f, 1.99f)).build());
    public static final EntityType<FrostSpikeEntity> FROST_SPIKE = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "frost_spike"),
            FabricEntityTypeBuilder.<FrostSpikeEntity>create(SpawnGroup.MISC, FrostSpikeEntity::new)
                    .dimensions(EntityDimensions.fixed(0.5f, 0.5f)).build());
    public static final EntityType<DraugrWightEntity> DRAUGR_WIGHT= Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "draugr_wight"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, DraugrWightEntity::new)
                    .dimensions(EntityDimensions.fixed(0.65f, 1.99f)).build());
    public static final EntityType<DraugrWightProjectileEntity> DRAUGR_WIGHT_PROJECTILE = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "draugr_wight_projectile"),
            FabricEntityTypeBuilder.<DraugrWightProjectileEntity>create(SpawnGroup.MISC, DraugrWightProjectileEntity::new)
                    .dimensions(EntityDimensions.fixed(1.25f, 0.75f)).build());
    public static final EntityType<DraugrArcherEntity> DRAUGR_ARCHER = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "draugr_archer"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, DraugrArcherEntity::new)
                    .dimensions(EntityDimensions.fixed(0.65f, 1.99f)).build());
    public static final EntityType<HarpyEntity> HARPY = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "harpy"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, HarpyEntity::new)
                    .dimensions(EntityDimensions.fixed(0.85f, 1.99f)).build());
    public static final EntityType<DraugrEntity> DRAUGR = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "draugr"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, DraugrEntity::new)
                    .dimensions(EntityDimensions.fixed(0.65f, 1.99f)).build());
    public static final EntityType<CentaurEntity> CENTAUR = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "centaur"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, CentaurEntity::new)
                    .dimensions(EntityDimensions.fixed(0.85f, 2.99f)).build());
    public static final EntityType<EgyptianCaravanEntity> EGYPTIAN_CARAVAN = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "egyptian_caravan"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, EgyptianCaravanEntity::new)
                    .dimensions(EntityDimensions.fixed(0.85f, 2.99f)).build());

    public static final EntityType<PegasusEntity> PEGASUS = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "pegasus"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, PegasusEntity::new)
                    .dimensions(EntityDimensions.fixed(1.3965f, 1.6f)).build());

    public static final EntityType<ChimeraEntity> CHIMERA = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "chimera"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, ChimeraEntity::new)
                    .dimensions(EntityDimensions.fixed(1.49f, 1.99f)).build());

    public static final EntityType<ElephantRiderEntity> ELEPHANT_RIDER = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "elephant_rider"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, ElephantRiderEntity::create)
                    .dimensions(EntityDimensions.fixed(1.99f, 5.49f)).build());
    public static final EntityType<ServantEntity> SERVANT = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "servant"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, ServantEntity::new)
                    .dimensions(EntityDimensions.fixed(0.85f, 1.99f)).build());

    public static final EntityType<MummyEntity> MUMMY = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "mummy"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, MummyEntity::new)
                    .dimensions(EntityDimensions.fixed(0.75f, 2.49f)).build());

    public static final EntityType<MummyBossEntity> MUMMY_BOSS = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "mummy_boss"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, MummyBossEntity::new)
                    .dimensions(EntityDimensions.fixed(0.75f, 4f)).build());
    public static final EntityType<CamelryEntity> CAMELRY = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "camelry"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, CamelryEntity::create)
                    .dimensions(EntityDimensions.fixed(0.95f, 3.79f)).build());

    public static final EntityType<AxemanEntity> AXEMAN = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "axeman"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, AxemanEntity::create)
                    .dimensions(EntityDimensions.fixed(0.6f, 1.99f)).build());
    public static final EntityType<WadjetEntity> WADJET = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "wadjet"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, WadjetEntity::new)
                    .dimensions(EntityDimensions.fixed(1.49f, 2.49f)).build());
    public static final EntityType<CyclopsEntity> CYCLOPS = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "cyclops"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, CyclopsEntity::new)
                    .dimensions(EntityDimensions.fixed(0.95f, 4f)).build());

    public static final EntityType<FrostCyclopsEntity> FROST_CYCLOPS = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "frost_cyclops"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, FrostCyclopsEntity::new)
                    .dimensions(EntityDimensions.fixed(0.95f, 4f)).build());

    public static final EntityType<HersirEntity> HERSIR = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "hersir"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, HersirEntity::new)
                    .dimensions(EntityDimensions.fixed(0.85f, 1.99f)).build());

    public static final EntityType<HuskarlEntity> HUSKARL = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "huskarl"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, HuskarlEntity::new)
                    .dimensions(EntityDimensions.fixed(0.85f, 1.99f)).build());

    public static final EntityType<ThrowingAxeManEntity> THROWING_AXEMAN = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "throwing_axeman"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, ThrowingAxeManEntity::new)
                    .dimensions(EntityDimensions.fixed(0.85f, 1.99f)).build());

    public static final EntityType<EinherjarEntity> EINHERJAR = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "einherjar"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, EinherjarEntity::new)
                    .dimensions(EntityDimensions.fixed(0.85f, 2.49f)).build());

    public static final EntityType<ValkyrieEntity> VALKYRIE = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "valkyrie"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, ValkyrieEntity::new)
                    .dimensions(EntityDimensions.fixed(0.85f, 2.49f)).build());

    public static final EntityType<ChampionHopliteEntity> CHAMPION_HOPLITE = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "champion_hoplite"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, ChampionHopliteEntity::new)
                    .dimensions(EntityDimensions.fixed(0.85f, 1.99f)).build());

    public static final EntityType<EliteHopliteEntity> ELITE_HOPLITE = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "elite_hoplite"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, EliteHopliteEntity::new)
                    .dimensions(EntityDimensions.fixed(0.85f, 1.99f)).build());

    public static final EntityType<HeroHopliteEntity> HERO_HOPLITE = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "hero_hoplite"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, HeroHopliteEntity::new)
                    .dimensions(EntityDimensions.fixed(0.85f, 1.99f)).build());

    public static final EntityType<HadesChosenEntity> HADES_CHOSEN = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "hades_chosen"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, HadesChosenEntity::new)
                    .dimensions(EntityDimensions.fixed(0.85f, 1.99f)).build());

    public static final EntityType<HadesShadeEntity> HADES_SHADE = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "hades_shade"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, HadesShadeEntity::new)
                    .dimensions(EntityDimensions.fixed(0.85f, 1.99f)).build());

    public static final EntityType<ThrowingRockEntity> THROWINGROCK = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "throwingrock"),
            FabricEntityTypeBuilder.<ThrowingRockEntity>create(SpawnGroup.MISC, ThrowingRockEntity::new)
                    .dimensions(EntityDimensions.fixed(2f, 2f)).build());

    public static final EntityType<ThrowingAxeEntity> THROWING_AXE = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "throwing_axe"),
            FabricEntityTypeBuilder.<ThrowingAxeEntity>create(SpawnGroup.MISC, ThrowingAxeEntity::new)
                    .dimensions(EntityDimensions.fixed(0.5f, 0.5f)).build());

    public static final EntityType<HarpyFeatherEntity> HARPY_FEATHER = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "harpy_feather"),
            FabricEntityTypeBuilder.<HarpyFeatherEntity>create(SpawnGroup.MISC, HarpyFeatherEntity::new)
                    .dimensions(EntityDimensions.fixed(0.5f, 0.5f)).build());

    public static final EntityType<VenomEntity> VENOM = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "venom"),
            FabricEntityTypeBuilder.<VenomEntity>create(SpawnGroup.MISC, VenomEntity::new)
                    .dimensions(EntityDimensions.fixed(0.5f, 0.5f)).build());

    public static final EntityType<VenomSlowEntity> VENOM_SLOW = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "venom_slow"),
            FabricEntityTypeBuilder.<VenomSlowEntity>create(SpawnGroup.MISC, VenomSlowEntity::new)
                    .dimensions(EntityDimensions.fixed(0.5f, 0.5f)).build());
    public static final EntityType<MummyProjectileEntity> MUMMY_PROJECTILE = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "mummy_projectile"),
            FabricEntityTypeBuilder.<MummyProjectileEntity>create(SpawnGroup.MISC, MummyProjectileEntity::new)
                    .dimensions(EntityDimensions.fixed(1.25f, 0.75f)).build());

    public static final EntityType<PharaohScepterProjectileEntity> PHARAOH_SCEPTER_PROJECTILE = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "pharaoh_scepter_projectile"),
            FabricEntityTypeBuilder.<PharaohScepterProjectileEntity>create(SpawnGroup.MISC, PharaohScepterProjectileEntity::new)
                    .dimensions(EntityDimensions.fixed(1.25f, 0.75f)).build());

    public static final EntityType<ChimeraProjectileEntity> CHIMERA_PROJECTILE = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "chimera_projectile"),
            FabricEntityTypeBuilder.<ChimeraProjectileEntity>create(SpawnGroup.MISC, ChimeraProjectileEntity::new)
                    .dimensions(EntityDimensions.fixed(0.5f, 0.5f)).build());

    public static final EntityType<ThrowingSnowRockEntity> THROWINGSNOWROCK = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "throwingsnowrock"),
            FabricEntityTypeBuilder.<ThrowingSnowRockEntity>create(SpawnGroup.MISC, ThrowingSnowRockEntity::new)
                    .dimensions(EntityDimensions.fixed(2f, 2f)).build());

    public static final EntityType<HopliteSpearEntity> HOPLITE_SPEAR = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "hoplite_spear"),
            FabricEntityTypeBuilder.<HopliteSpearEntity>create(SpawnGroup.MISC, HopliteSpearEntity::new)
                    .dimensions(EntityDimensions.fixed(0.9f, 0.9f)).build());

    public static final EntityType<ValkyrieSpearEntity> VALKYRIE_SPEAR = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "valkyrie_spear"),
            FabricEntityTypeBuilder.<ValkyrieSpearEntity>create(SpawnGroup.MISC, ValkyrieSpearEntity::new)
                    .dimensions(EntityDimensions.fixed(0.9f, 0.9f)).build());

    public static final EntityType<HadesChosenSpearEntity> HADES_CHOSEN_SPEAR = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "hades_chosen_spear"),
            FabricEntityTypeBuilder.<HadesChosenSpearEntity>create(SpawnGroup.MISC, HadesChosenSpearEntity::new)
                    .dimensions(EntityDimensions.fixed(0.9f, 0.9f)).build());

    public static final EntityType<ThrowingHopliteSpearEntity> IRON_THROWING_HOPLITE_SPEAR = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "iron_throwing_hoplite_spear"),
            FabricEntityTypeBuilder.<ThrowingHopliteSpearEntity>create(SpawnGroup.MISC, ThrowingHopliteSpearEntity::new)
                    .dimensions(EntityDimensions.fixed(0.9f, 0.9f)).build());

    public static final EntityType<ThrowingHopliteSpearEntity> GOLD_THROWING_HOPLITE_SPEAR = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "gold_throwing_hoplite_spear"),
            FabricEntityTypeBuilder.<ThrowingHopliteSpearEntity>create(SpawnGroup.MISC, ThrowingHopliteSpearEntity::new)
                    .dimensions(EntityDimensions.fixed(0.9f, 0.9f)).build());
    public static final EntityType<ThrowingHopliteSpearEntity> DIAMOND_THROWING_HOPLITE_SPEAR = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "diamond_throwing_hoplite_spear"),
            FabricEntityTypeBuilder.<ThrowingHopliteSpearEntity>create(SpawnGroup.MISC, ThrowingHopliteSpearEntity::new)
                    .dimensions(EntityDimensions.fixed(0.9f, 0.9f)).build());

    public static final EntityType<ThrowingHopliteSpearEntity> NETHERITE_THROWING_HOPLITE_SPEAR = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "netherite_throwing_hoplite_spear"),
            FabricEntityTypeBuilder.<ThrowingHopliteSpearEntity>create(SpawnGroup.MISC, ThrowingHopliteSpearEntity::new)
                    .dimensions(EntityDimensions.fixed(0.9f, 0.9f)).build());
}
