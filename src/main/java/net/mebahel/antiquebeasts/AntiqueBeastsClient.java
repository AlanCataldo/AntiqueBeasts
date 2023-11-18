package net.mebahel.antiquebeasts;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.mebahel.antiquebeasts.entity.ModEntities;
import net.mebahel.antiquebeasts.entity.armor.DiamondPlateArmorRenderer;
import net.mebahel.antiquebeasts.entity.armor.GoldPlateArmorRenderer;
import net.mebahel.antiquebeasts.entity.armor.IronPlateArmorRenderer;
import net.mebahel.antiquebeasts.entity.client.custom.*;
import net.mebahel.antiquebeasts.entity.client.projectiles.HadesChosenSpearRenderer;
import net.mebahel.antiquebeasts.entity.client.projectiles.HopliteSpearRenderer;
import net.mebahel.antiquebeasts.entity.client.projectiles.ThrowingRockRenderer;
import net.mebahel.antiquebeasts.entity.client.projectiles.ThrowingSnowRockRenderer;
import net.mebahel.antiquebeasts.item.custom.ModItems;
import net.mebahel.antiquebeasts.particle.ModParticles;
import net.mebahel.antiquebeasts.particle.custom.RockSplashParticle;
import net.mebahel.antiquebeasts.particle.custom.SnowRockSplashParticle;
import net.mebahel.antiquebeasts.screen.BloodInfusingStationScreen;
import net.mebahel.antiquebeasts.screen.ModScreenHandlers;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

public class AntiqueBeastsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.CYCLOPS, CyclopsRenderer::new);
        EntityRendererRegistry.register(ModEntities.FROST_CYCLOPS, FrostCyclopsRenderer::new);
        EntityRendererRegistry.register(ModEntities.CHAMPION_HOPLITE, ChampionHopliteRenderer::new);
        EntityRendererRegistry.register(ModEntities.ELITE_HOPLITE, EliteHopliteRenderer::new);
        EntityRendererRegistry.register(ModEntities.HERO_HOPLITE, HeroHopliteRenderer::new);
        EntityRendererRegistry.register(ModEntities.HADES_CHOSEN, HadesChosenRenderer::new);
        EntityRendererRegistry.register(ModEntities.HADES_SHADE, HadesShadeRenderer::new);
        EntityRendererRegistry.register(ModEntities.THROWINGROCK, ThrowingRockRenderer::new);
        EntityRendererRegistry.register(ModEntities.HOPLITE_SPEAR, HopliteSpearRenderer::new);
        EntityRendererRegistry.register(ModEntities.HADES_CHOSEN_SPEAR, HadesChosenSpearRenderer::new);
        EntityRendererRegistry.register(ModEntities.THROWINGSNOWROCK, ThrowingSnowRockRenderer::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.ROCKSPLASH_PARTICLE, RockSplashParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.SNOWROCKSPLASH_PARTICLE, SnowRockSplashParticle.Factory::new);
        HandledScreens.register(ModScreenHandlers.BLOOD_INFUSING_STATION_SCREEN_HANDLER, BloodInfusingStationScreen::new);
        GeoArmorRenderer.registerArmorRenderer(new IronPlateArmorRenderer(), ModItems.IRON_PLATE_BOOTS,
                ModItems.IRON_PLATE_LEGGINGS, ModItems.IRON_PLATE_CHESTPLATE, ModItems.IRON_PLATE_HELMET);
        GeoArmorRenderer.registerArmorRenderer(new GoldPlateArmorRenderer(), ModItems.GOLD_PLATE_BOOTS,
                ModItems.GOLD_PLATE_LEGGINGS, ModItems.GOLD_PLATE_CHESTPLATE, ModItems.GOLD_PLATE_HELMET);
        GeoArmorRenderer.registerArmorRenderer(new DiamondPlateArmorRenderer(), ModItems.DIAMOND_PLATE_BOOTS,
                ModItems.DIAMOND_PLATE_LEGGINGS, ModItems.DIAMOND_PLATE_CHESTPLATE, ModItems.DIAMOND_PLATE_HELMET);
    }
}