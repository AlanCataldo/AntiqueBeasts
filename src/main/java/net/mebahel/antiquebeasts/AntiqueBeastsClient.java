package net.mebahel.antiquebeasts;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.mebahel.antiquebeasts.entity.ModEntities;
import net.mebahel.antiquebeasts.entity.client.custom.*;
import net.mebahel.antiquebeasts.entity.client.projectiles.*;
import net.mebahel.antiquebeasts.particle.ModParticles;
import net.mebahel.antiquebeasts.particle.custom.RockSplashParticle;
import net.mebahel.antiquebeasts.particle.custom.SnowRockSplashParticle;
import net.mebahel.antiquebeasts.util.HornModelPredicate;
import net.mebahel.antiquebeasts.util.ShieldModelPredicate;
import net.mebahel.antiquebeasts.util.SpearModelPredicate;

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
        EntityRendererRegistry.register(ModEntities.IRON_THROWING_HOPLITE_SPEAR, (context) -> new ThrowingHopliteSpearEntityRenderer(context, "iron"));
        EntityRendererRegistry.register(ModEntities.DIAMOND_THROWING_HOPLITE_SPEAR, (context) -> new ThrowingHopliteSpearEntityRenderer(context, "diamond"));
        EntityRendererRegistry.register(ModEntities.GOLD_THROWING_HOPLITE_SPEAR, (context) -> new ThrowingHopliteSpearEntityRenderer(context, "gold"));
        EntityRendererRegistry.register(ModEntities.NETHERITE_THROWING_HOPLITE_SPEAR, (context) -> new ThrowingHopliteSpearEntityRenderer(context, "netherite"));
        ParticleFactoryRegistry.getInstance().register(ModParticles.ROCKSPLASH_PARTICLE, RockSplashParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.SNOWROCKSPLASH_PARTICLE, SnowRockSplashParticle.Factory::new);
        ShieldModelPredicate.registerShieldModels();
        SpearModelPredicate.registerSpearModels();
        HornModelPredicate.registerHornModels();
    }
}