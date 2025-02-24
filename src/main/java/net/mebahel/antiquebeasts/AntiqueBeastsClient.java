package net.mebahel.antiquebeasts;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.mebahel.antiquebeasts.block.ModBlockEntities;

import net.mebahel.antiquebeasts.block.client.DraugrChestRenderer;
import net.mebahel.antiquebeasts.block.entity.DraugrChestBlockEntity;
import net.mebahel.antiquebeasts.block.screenhandlers.ModScreenHandlerType;
import net.mebahel.antiquebeasts.block.screenhandlers.ModScreenHandlers;
import net.mebahel.antiquebeasts.block.screenhandlers.StaffEnchantingTableScreen;
import net.mebahel.antiquebeasts.entity.ModEntities;
import net.mebahel.antiquebeasts.entity.client.custom.*;
import net.mebahel.antiquebeasts.entity.client.custom.draugr.DraugrRenderer;
import net.mebahel.antiquebeasts.entity.client.custom.draugr_archer.DraugrArcherRenderer;
import net.mebahel.antiquebeasts.entity.client.custom.draugr_overlord.DraugrOverlordRenderer;
import net.mebahel.antiquebeasts.entity.client.custom.draugr_scourge.DraugrScourgeRenderer;
import net.mebahel.antiquebeasts.entity.client.custom.draugr_wight.DraugrWightRenderer;
import net.mebahel.antiquebeasts.entity.client.custom.harpy.HarpyRenderer;
import net.mebahel.antiquebeasts.entity.client.custom.mummy_boss.MummyBossRenderer;
import net.mebahel.antiquebeasts.entity.client.custom.skeleton_warrior.SkeletonWarriorHeadRenderer;
import net.mebahel.antiquebeasts.entity.client.custom.skeleton_warrior.SkeletonWarriorRenderer;
import net.mebahel.antiquebeasts.entity.client.projectiles.*;
import net.mebahel.antiquebeasts.entity.client.projectiles.draugr.DraugrWightProjectileRenderer;
import net.mebahel.antiquebeasts.entity.client.projectiles.draugr.FrostSpikeProjectileRenderer;
import net.mebahel.antiquebeasts.entity.client.projectiles.harpy.HarpyFeatherRenderer;
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
                initialized = true;
            }
        });
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
        ParticleFactoryRegistry.getInstance().register(ModParticles.ROCKSPLASH_PARTICLE, RockSplashParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.SNOWROCKSPLASH_PARTICLE, SnowRockSplashParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.SNOWFLAKE_PARTICLE, SnowFlakeParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.SNOWFLAKE_HAND_PARTICLE, SnowFlakeParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.VENOM_PARTICLE, SnowRockSplashParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.VENOM_SLOW_PARTICLE, SnowRockSplashParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.MUMMY_PROJECTILE_PARTICLE, MummyProjectileParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.MUMMY_HOVERING_PARTICLE, MummyHoveringParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.HEALING_PARTICLE, HealingParticle.Factory::new);

        BlockEntityRendererFactories.register(ModBlockEntities.DRAUGR_CHEST_ENTITY, DraugrChestRenderer::new);
        //HandledScreens.register(ModScreenHandlers.STAFF_ENCHANTING_STAFF_SCREEN_HANDLER, StaffEnchantingTableScreen::new);

        ModScreenHandlers.registerScreenHandlers();

        BowPredicate.registerBowModels();

        ModModelPredicate.registerStaffModels();
        ShieldModelPredicate.registerShieldModels();
        SpearModelPredicate.registerSpearModels();
        HornModelPredicate.registerHornModels();
    }
}