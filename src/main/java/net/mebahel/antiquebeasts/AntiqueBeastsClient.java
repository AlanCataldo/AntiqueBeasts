package net.mebahel.antiquebeasts;

import mod.azure.azurelib.AzureLib;
import mod.azure.azurelib.rewrite.render.armor.AzArmorRendererRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.mebahel.antiquebeasts.block.ModBlockEntities;
import net.mebahel.antiquebeasts.block.client.*;
import net.mebahel.antiquebeasts.block.screenhandlers.DraugrChestScreen;
import net.mebahel.antiquebeasts.block.screenhandlers.ModScreenHandlers;
import net.mebahel.antiquebeasts.entity.ModEntities;
import net.mebahel.antiquebeasts.entity.armor.DiamondPlateArmor.AzDiamondPlateArmorRenderer;
import net.mebahel.antiquebeasts.entity.armor.DiamondScaleArmor.AzDiamondScaleArmorRenderer;
import net.mebahel.antiquebeasts.entity.armor.GoldPlateArmor.AzGoldPlateArmorRenderer;
import net.mebahel.antiquebeasts.entity.armor.GoldScaleArmor.AzGoldScaleArmorRenderer;
import net.mebahel.antiquebeasts.entity.armor.IronPlateArmor.AzIronPlateArmorRenderer;
import net.mebahel.antiquebeasts.entity.armor.IronScaleArmor.AzIronScaleArmorRenderer;
import net.mebahel.antiquebeasts.entity.armor.NetheritePlateArmor.AzNetheritePlateArmorRenderer;
import net.mebahel.antiquebeasts.entity.armor.ValkyrieArmor.AzValkyrieArmorRenderer;
import net.mebahel.antiquebeasts.entity.client.custom.*;
import net.mebahel.antiquebeasts.entity.client.custom.draugr.DraugrRenderer;
import net.mebahel.antiquebeasts.entity.client.custom.draugr_archer.DraugrArcherRenderer;
import net.mebahel.antiquebeasts.entity.client.custom.draugr_overlord.DraugrOverlordRenderer;
import net.mebahel.antiquebeasts.entity.client.custom.draugr_scourge.DraugrScourgeRenderer;
import net.mebahel.antiquebeasts.entity.client.custom.draugr_wight.DraugrWightRenderer;
import net.mebahel.antiquebeasts.entity.client.custom.dwemer_centurion.DwemerCenturionRenderer;
import net.mebahel.antiquebeasts.entity.client.custom.dwemer_spider.DwemerSpiderRenderer;
import net.mebahel.antiquebeasts.entity.client.custom.dwemer_spider_guardian.DwemerSpiderGuardianRenderer;
import net.mebahel.antiquebeasts.entity.client.custom.harpy.HarpyRenderer;
import net.mebahel.antiquebeasts.entity.client.custom.mummy_boss.MummyBossRenderer;
import net.mebahel.antiquebeasts.entity.client.custom.skeleton_warrior.SkeletonWarriorHeadRenderer;
import net.mebahel.antiquebeasts.entity.client.custom.skeleton_warrior.SkeletonWarriorRenderer;
import net.mebahel.antiquebeasts.entity.client.projectiles.*;
import net.mebahel.antiquebeasts.entity.client.projectiles.draugr.DraugrWightProjectileRenderer;
import net.mebahel.antiquebeasts.entity.client.projectiles.draugr.FrostSpikeProjectileRenderer;
import net.mebahel.antiquebeasts.entity.client.projectiles.dwemer_centurion.SteamProjectileRenderer;
import net.mebahel.antiquebeasts.entity.client.projectiles.harpy.HarpyFeatherRenderer;
import net.mebahel.antiquebeasts.item.custom.ModItems;
import net.mebahel.antiquebeasts.particle.ModParticles;
import net.mebahel.antiquebeasts.particle.custom.*;
import net.mebahel.antiquebeasts.util.*;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import software.bernie.geckolib.GeckoLib;

public class AntiqueBeastsClient implements ClientModInitializer {
    private boolean initialized = false;
    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!initialized) {
                GeckoLib.initialize();
                AzureLib.initialize();
                initialized = true;
            }
        });
        EntityRendererRegistry.register(ModEntities.STEAM_PROJECTILE, SteamProjectileRenderer::new);
        EntityRendererRegistry.register(ModEntities.DWEMER_CENTURION, DwemerCenturionRenderer::new);
        EntityRendererRegistry.register(ModEntities.DWEMER_SPIDER, DwemerSpiderRenderer::new);
        EntityRendererRegistry.register(ModEntities.DWEMER_SPIDER_GUARDIAN, DwemerSpiderGuardianRenderer::new);
        EntityRendererRegistry.register(ModEntities.DRAUGR_OVERLORD, DraugrOverlordRenderer::new);
        EntityRendererRegistry.register(ModEntities.SKELETON_WARRIOR_HEAD, SkeletonWarriorHeadRenderer::new);
        EntityRendererRegistry.register(ModEntities.SKELETON_WARRIOR, SkeletonWarriorRenderer::new);
        EntityRendererRegistry.register(ModEntities.DRAUGR_SCOURGE, DraugrScourgeRenderer::new);
        EntityRendererRegistry.register(ModEntities.DRAUGR_WIGHT, DraugrWightRenderer::new);
        EntityRendererRegistry.register(ModEntities.DRAUGR_ARCHER, DraugrArcherRenderer::new);
        EntityRendererRegistry.register(ModEntities.MUMMY_BOSS, MummyBossRenderer::new);
        EntityRendererRegistry.register(ModEntities.HARPY, HarpyRenderer::new);
        EntityRendererRegistry.register(ModEntities.DRAUGR, DraugrRenderer::new);
        EntityRendererRegistry.register(ModEntities.CENTAUR, CentaurRenderer::new);
        EntityRendererRegistry.register(ModEntities.EGYPTIAN_CARAVAN, EgyptianCaravanRenderer::new);
        EntityRendererRegistry.register(ModEntities.PEGASUS, PegasusRenderer::new);
        EntityRendererRegistry.register(ModEntities.CHIMERA, ChimeraRenderer::new);
        EntityRendererRegistry.register(ModEntities.ELEPHANT_RIDER, ElephantRiderRenderer::new);
        EntityRendererRegistry.register(ModEntities.SERVANT, ServantRenderer::new);
        EntityRendererRegistry.register(ModEntities.MUMMY, MummyRenderer::new);
        EntityRendererRegistry.register(ModEntities.CAMELRY, CamelryRenderer::new);
        EntityRendererRegistry.register(ModEntities.AXEMAN, AxemanRenderer::new);
        EntityRendererRegistry.register(ModEntities.WADJET, WadjetRenderer::new);
        EntityRendererRegistry.register(ModEntities.CYCLOPS, CyclopsRenderer::new);
        EntityRendererRegistry.register(ModEntities.FROST_CYCLOPS, FrostCyclopsRenderer::new);
        EntityRendererRegistry.register(ModEntities.CHAMPION_HOPLITE, ChampionHopliteRenderer::new);
        EntityRendererRegistry.register(ModEntities.ELITE_HOPLITE, EliteHopliteRenderer::new);
        EntityRendererRegistry.register(ModEntities.HERO_HOPLITE, HeroHopliteRenderer::new);
        EntityRendererRegistry.register(ModEntities.HADES_CHOSEN, HadesChosenRenderer::new);
        EntityRendererRegistry.register(ModEntities.HADES_SHADE, HadesShadeRenderer::new);
        EntityRendererRegistry.register(ModEntities.HERSIR, HersirRenderer::new);
        EntityRendererRegistry.register(ModEntities.HUSKARL, HuskarlRenderer::new);
        EntityRendererRegistry.register(ModEntities.EINHERJAR, EinherjarRenderer::new);
        EntityRendererRegistry.register(ModEntities.VALKYRIE, ValkyrieRenderer::new);
        EntityRendererRegistry.register(ModEntities.THROWING_AXEMAN, ThrowingAxeManRenderer::new);
        EntityRendererRegistry.register(ModEntities.THROWINGROCK, ThrowingRockRenderer::new);
        EntityRendererRegistry.register(ModEntities.HOPLITE_SPEAR, HopliteSpearRenderer::new);
        EntityRendererRegistry.register(ModEntities.HADES_CHOSEN_SPEAR, HadesChosenSpearRenderer::new);
        EntityRendererRegistry.register(ModEntities.THROWINGSNOWROCK, ThrowingSnowRockRenderer::new);
        EntityRendererRegistry.register(ModEntities.THROWING_AXE, ThrowingAxeRenderer::new);
        EntityRendererRegistry.register(ModEntities.HARPY_FEATHER, HarpyFeatherRenderer::new);
        EntityRendererRegistry.register(ModEntities.VENOM, VenomRenderer::new);
        EntityRendererRegistry.register(ModEntities.VENOM_SLOW, VenomSlowRenderer::new);
        EntityRendererRegistry.register(ModEntities.MUMMY_PROJECTILE, MummyProjectileRenderer::new);
        EntityRendererRegistry.register(ModEntities.PHARAOH_SCEPTER_PROJECTILE, PharaohScepterProjectileRenderer::new);
        EntityRendererRegistry.register(ModEntities.DRAUGR_WIGHT_PROJECTILE, DraugrWightProjectileRenderer::new);
        EntityRendererRegistry.register(ModEntities.FROST_SPIKE, FrostSpikeProjectileRenderer::new);
        EntityRendererRegistry.register(ModEntities.CHIMERA_PROJECTILE, ChimeraProjectileRenderer::new);
        EntityRendererRegistry.register(ModEntities.VALKYRIE_SPEAR, ValkyrieSpearRenderer::new);
        EntityRendererRegistry.register(ModEntities.IRON_THROWING_HOPLITE_SPEAR, (context) -> new ThrowingHopliteSpearEntityRenderer(context, "iron"));
        EntityRendererRegistry.register(ModEntities.DIAMOND_THROWING_HOPLITE_SPEAR, (context) -> new ThrowingHopliteSpearEntityRenderer(context, "diamond"));
        EntityRendererRegistry.register(ModEntities.GOLD_THROWING_HOPLITE_SPEAR, (context) -> new ThrowingHopliteSpearEntityRenderer(context, "gold"));
        EntityRendererRegistry.register(ModEntities.NETHERITE_THROWING_HOPLITE_SPEAR, (context) -> new ThrowingHopliteSpearEntityRenderer(context, "netherite"));
        ParticleFactoryRegistry.getInstance().register(ModParticles.STEAM_PARTICLE, SteamParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.ROCKSPLASH_PARTICLE, RockSplashParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.SNOWROCKSPLASH_PARTICLE, SnowRockSplashParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.SNOWFLAKE_PARTICLE, SnowFlakeParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.SNOWFLAKE_HAND_PARTICLE, SnowFlakeParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.VENOM_PARTICLE, SnowRockSplashParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.VENOM_SLOW_PARTICLE, SnowRockSplashParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.MUMMY_PROJECTILE_PARTICLE, MummyProjectileParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.MUMMY_HOVERING_PARTICLE, MummyHoveringParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.HEALING_PARTICLE, HealingParticle.Factory::new);
        EntityRendererRegistry.register(ModEntities.BLOCK_SCAN_ENTITY, BlockScanRenderer::new);

        BlockEntityRendererFactories.register(ModBlockEntities.DWEMER_SPIDER_BLOCK_ENTITY, DwemerSpiderBlockRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.DRAUGR_CHEST_ENTITY, DraugrChestRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.GREEK_CHEST_ENTITY, GreekChestRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.DWEMER_CHEST_ENTITY, DwemerChestRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.DWARVEN_METAL_PIPE_GEAR, DwarvenMetalPipeGearRenderer::new);

        AzArmorRendererRegistry.register(AzGoldScaleArmorRenderer::new, ModItems.GOLD_SCALE_HELMET,
                ModItems.GOLD_SCALE_CHESTPLATE,
                ModItems.GOLD_SCALE_LEGGINGS,
                ModItems.GOLD_SCALE_BOOTS);

        AzArmorRendererRegistry.register(AzDiamondScaleArmorRenderer::new, ModItems.DIAMOND_SCALE_HELMET,
                ModItems.DIAMOND_SCALE_CHESTPLATE,
                ModItems.DIAMOND_SCALE_LEGGINGS,
                ModItems.DIAMOND_SCALE_BOOTS);

        AzArmorRendererRegistry.register(AzIronScaleArmorRenderer::new, ModItems.IRON_SCALE_HELMET,
                ModItems.IRON_SCALE_CHESTPLATE,
                ModItems.IRON_SCALE_LEGGINGS,
                ModItems.IRON_SCALE_BOOTS);

        AzArmorRendererRegistry.register(AzGoldPlateArmorRenderer::new, ModItems.GOLD_PLATE_HELMET,
                ModItems.GOLD_PLATE_CHESTPLATE,
                ModItems.GOLD_PLATE_LEGGINGS,
                ModItems.GOLD_PLATE_BOOTS);

        AzArmorRendererRegistry.register(AzDiamondPlateArmorRenderer::new, ModItems.DIAMOND_PLATE_HELMET,
                ModItems.DIAMOND_PLATE_CHESTPLATE,
                ModItems.DIAMOND_PLATE_LEGGINGS,
                ModItems.DIAMOND_PLATE_BOOTS);

        AzArmorRendererRegistry.register(AzIronPlateArmorRenderer::new, ModItems.IRON_PLATE_HELMET,
                ModItems.IRON_PLATE_CHESTPLATE,
                ModItems.IRON_PLATE_LEGGINGS,
                ModItems.IRON_PLATE_BOOTS);

        AzArmorRendererRegistry.register(AzNetheritePlateArmorRenderer::new, ModItems.NETHERITE_PLATE_HELMET,
                ModItems.NETHERITE_PLATE_CHESTPLATE,
                ModItems.NETHERITE_PLATE_LEGGINGS,
                ModItems.NETHERITE_PLATE_BOOTS);

        AzArmorRendererRegistry.register(AzValkyrieArmorRenderer::new, ModItems.VALKYRIE_HELMET,
                ModItems.VALKYRIE_CHESTPLATE,
                ModItems.VALKYRIE_LEGGINGS,
                ModItems.VALKYRIE_BOOTS);

        HandledScreens.register(ModScreenHandlers.DRAUGR_CHEST_SCREEN_HANDLER, DraugrChestScreen::new);

        BowPredicate.registerBowModels();

        ModModelPredicate.registerStaffModels();
        ShieldModelPredicate.registerShieldModels();
        SpearModelPredicate.registerSpearModels();
        HornModelPredicate.registerHornModels();
    }
}