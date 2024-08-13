package net.mebahel.antiquebeasts;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.mebahel.antiquebeasts.entity.ModEntities;
import net.mebahel.antiquebeasts.entity.armor.DiamondPlateArmorRenderer;
import net.mebahel.antiquebeasts.entity.armor.GoldPlateArmorRenderer;
import net.mebahel.antiquebeasts.entity.armor.IronPlateArmorRenderer;
import net.mebahel.antiquebeasts.entity.armor.NetheritePlateArmorRenderer;
import net.mebahel.antiquebeasts.entity.client.custom.*;
import net.mebahel.antiquebeasts.entity.client.projectiles.*;
import net.mebahel.antiquebeasts.item.custom.ModItems;
import net.mebahel.antiquebeasts.particle.ModParticles;
import net.mebahel.antiquebeasts.particle.custom.MummyProjectileParticle;
import net.mebahel.antiquebeasts.particle.custom.RockSplashParticle;
import net.mebahel.antiquebeasts.particle.custom.SnowRockSplashParticle;
import net.mebahel.antiquebeasts.particle.custom.VenomParticle;
import net.mebahel.antiquebeasts.util.HornModelPredicate;
import net.mebahel.antiquebeasts.util.ShieldModelPredicate;
import net.mebahel.antiquebeasts.util.SpearModelPredicate;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

public class AntiqueBeastsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.EGYPTIAN_CARAVAN, EgyptianCaravanRenderer::new);
        EntityRendererRegistry.register(ModEntities.ELEPHANT_RIDER, ElephantRiderRenderer::new);
        EntityRendererRegistry.register(ModEntities.CAMELRY, CamelryRenderer::new);
        EntityRendererRegistry.register(ModEntities.PEGASUS, PegasusRenderer::new);
        EntityRendererRegistry.register(ModEntities.CHIMERA, ChimeraRenderer::new);
        EntityRendererRegistry.register(ModEntities.WADJET, WadjetRenderer::new);
        EntityRendererRegistry.register(ModEntities.SERVANT, ServantRenderer::new);
        EntityRendererRegistry.register(ModEntities.MUMMY, MummyRenderer::new);
        EntityRendererRegistry.register(ModEntities.CENTAUR, CentaurRenderer::new);
        EntityRendererRegistry.register(ModEntities.AXEMAN, AxemanRenderer::new);
        EntityRendererRegistry.register(ModEntities.CYCLOPS, CyclopsRenderer::new);
        EntityRendererRegistry.register(ModEntities.FROST_CYCLOPS, FrostCyclopsRenderer::new);
        EntityRendererRegistry.register(ModEntities.CHAMPION_HOPLITE, ChampionHopliteRenderer::new);
        EntityRendererRegistry.register(ModEntities.ELITE_HOPLITE, EliteHopliteRenderer::new);
        EntityRendererRegistry.register(ModEntities.HERO_HOPLITE, HeroHopliteRenderer::new);
        EntityRendererRegistry.register(ModEntities.HADES_CHOSEN, HadesChosenRenderer::new);
        EntityRendererRegistry.register(ModEntities.HADES_SHADE, HadesShadeRenderer::new);
        EntityRendererRegistry.register(ModEntities.HERSIR, HersirRenderer::new);
        EntityRendererRegistry.register(ModEntities.HUSKARL, HuskarlRenderer::new);
        EntityRendererRegistry.register(ModEntities.THROWING_AXEMAN, ThrowingAxeManRenderer::new);
        EntityRendererRegistry.register(ModEntities.THROWINGROCK, ThrowingRockRenderer::new);
        EntityRendererRegistry.register(ModEntities.HOPLITE_SPEAR, HopliteSpearRenderer::new);
        EntityRendererRegistry.register(ModEntities.HADES_CHOSEN_SPEAR, HadesChosenSpearRenderer::new);
        EntityRendererRegistry.register(ModEntities.THROWINGSNOWROCK, ThrowingSnowRockRenderer::new);
        EntityRendererRegistry.register(ModEntities.THROWING_AXE, ThrowingAxeRenderer::new);
        EntityRendererRegistry.register(ModEntities.EINHERJAR, EinherjarRenderer::new);
        EntityRendererRegistry.register(ModEntities.VALKYRIE, ValkyrieRenderer::new);
        EntityRendererRegistry.register(ModEntities.VENOM, VenomRenderer::new);
        EntityRendererRegistry.register(ModEntities.VENOM_SLOW, VenomSlowRenderer::new);
        EntityRendererRegistry.register(ModEntities.MUMMY_PROJECTILE, MummyProjectileRenderer::new);
        EntityRendererRegistry.register(ModEntities.CHIMERA_PROJECTILE, ChimeraProjectileRenderer::new);
        EntityRendererRegistry.register(ModEntities.VALKYRIE_SPEAR, ValkyrieSpearRenderer::new);
        EntityRendererRegistry.register(ModEntities.IRON_THROWING_HOPLITE_SPEAR, (context) -> new ThrowingHopliteSpearEntityRenderer(context, "iron"));
        EntityRendererRegistry.register(ModEntities.DIAMOND_THROWING_HOPLITE_SPEAR, (context) -> new ThrowingHopliteSpearEntityRenderer(context, "diamond"));
        EntityRendererRegistry.register(ModEntities.GOLD_THROWING_HOPLITE_SPEAR, (context) -> new ThrowingHopliteSpearEntityRenderer(context, "gold"));
        EntityRendererRegistry.register(ModEntities.NETHERITE_THROWING_HOPLITE_SPEAR, (context) -> new ThrowingHopliteSpearEntityRenderer(context, "netherite"));
        ParticleFactoryRegistry.getInstance().register(ModParticles.ROCKSPLASH_PARTICLE, RockSplashParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.SNOWROCKSPLASH_PARTICLE, SnowRockSplashParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.VENOM_PARTICLE, VenomParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.VENOM_SLOW_PARTICLE, VenomParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.MUMMY_PROJECTILE_PARTICLE, MummyProjectileParticle.Factory::new);
        GeoArmorRenderer.registerArmorRenderer(new IronPlateArmorRenderer(), ModItems.IRON_PLATE_BOOTS,
                ModItems.IRON_PLATE_LEGGINGS, ModItems.IRON_PLATE_CHESTPLATE, ModItems.IRON_PLATE_HELMET);
        GeoArmorRenderer.registerArmorRenderer(new GoldPlateArmorRenderer(), ModItems.GOLD_PLATE_BOOTS,
                ModItems.GOLD_PLATE_LEGGINGS, ModItems.GOLD_PLATE_CHESTPLATE, ModItems.GOLD_PLATE_HELMET);
        GeoArmorRenderer.registerArmorRenderer(new DiamondPlateArmorRenderer(), ModItems.DIAMOND_PLATE_BOOTS,
                ModItems.DIAMOND_PLATE_LEGGINGS, ModItems.DIAMOND_PLATE_CHESTPLATE, ModItems.DIAMOND_PLATE_HELMET);
        GeoArmorRenderer.registerArmorRenderer(new NetheritePlateArmorRenderer(), ModItems.NETHERITE_PLATE_BOOTS,
                ModItems.NETHERITE_PLATE_LEGGINGS, ModItems.NETHERITE_PLATE_CHESTPLATE, ModItems.NETHERITE_PLATE_HELMET);
        ShieldModelPredicate.registerShieldModels();
        SpearModelPredicate.registerSpearModels();
        HornModelPredicate.registerHornModels();
    }
}