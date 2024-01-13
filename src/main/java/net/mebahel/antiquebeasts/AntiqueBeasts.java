package net.mebahel.antiquebeasts;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.mebahel.antiquebeasts.entity.ModEntities;
import net.mebahel.antiquebeasts.entity.custom.*;
import net.mebahel.antiquebeasts.item.custom.ModItems;
import net.mebahel.antiquebeasts.particle.ModParticles;
import net.mebahel.antiquebeasts.potion.ModPotions;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.mebahel.antiquebeasts.world.gen.ModWorldGen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bernie.geckolib.GeckoLib;

public class AntiqueBeasts implements ModInitializer {
	public static final String MOD_ID = "antiquebeasts";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		GeckoLib.initialize();
		FabricDefaultAttributeRegistry.register(ModEntities.CYCLOPS, CyclopsEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.FROST_CYCLOPS, FrostCyclopsEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.CHAMPION_HOPLITE, ChampionHopliteEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.ELITE_HOPLITE, EliteHopliteEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.HERO_HOPLITE, HeroHopliteEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.HADES_CHOSEN, HadesChosenEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.HADES_SHADE, HadesShadeEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.HERSIR, HersirEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.HUSKARL, HuskarlEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.THROWING_AXEMAN, ThrowingAxeManEntity.setAttributes());
		ModSounds.registerSounds();
		ModWorldGen.generateWorldGen();
		ModItems.registerModItems();
		ModParticles.registerParticles();
		ModPotions.registerPotionRecipe();
	}
}
