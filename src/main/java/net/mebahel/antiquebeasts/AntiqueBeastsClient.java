package net.mebahel.antiquebeasts;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.mebahel.antiquebeasts.entity.ModEntities;
import net.mebahel.antiquebeasts.entity.client.cyclops.CyclopsRenderer;
import net.mebahel.antiquebeasts.entity.client.cyclops.FrostCyclopsRenderer;
import net.mebahel.antiquebeasts.entity.client.projectiles.ThrowingRockRenderer;
import net.mebahel.antiquebeasts.entity.client.projectiles.ThrowingSnowRockRenderer;
import net.mebahel.antiquebeasts.particle.ModParticles;
import net.mebahel.antiquebeasts.particle.custom.RockSplashParticle;
import net.mebahel.antiquebeasts.particle.custom.SnowRockSplashParticle;

public class AntiqueBeastsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.CYCLOPS, CyclopsRenderer::new);
        EntityRendererRegistry.register(ModEntities.FROST_CYCLOPS, FrostCyclopsRenderer::new);
        EntityRendererRegistry.register(ModEntities.THROWINGROCK, ThrowingRockRenderer::new);
        EntityRendererRegistry.register(ModEntities.THROWINGSNOWROCK, ThrowingSnowRockRenderer::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.ROCKSPLASH_PARTICLE, RockSplashParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.SNOWROCKSPLASH_PARTICLE, SnowRockSplashParticle.Factory::new);
    }
}
