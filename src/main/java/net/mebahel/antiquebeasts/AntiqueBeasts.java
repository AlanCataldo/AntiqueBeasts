package net.mebahel.antiquebeasts;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.mebahel.antiquebeasts.entity.ModEntities;
import net.mebahel.antiquebeasts.entity.custom.CyclopsEntity;
import net.mebahel.antiquebeasts.item.ModItems;
import net.mebahel.antiquebeasts.particle.ModParticles;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.mebahel.antiquebeasts.world.gen.ModWorldGen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bernie.geckolib3.GeckoLib;

public class AntiqueBeasts implements ModInitializer {
	public static final String MOD_ID = "antiquebeasts";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		GeckoLib.initialize();
		FabricDefaultAttributeRegistry.register(ModEntities.CYCLOPS, CyclopsEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.FROST_CYCLOPS, CyclopsEntity.setAttributes());
		ModSounds.registerSounds();
		ModWorldGen.generateWorldGen();
		ModItems.registerModItems();
		ModParticles.registerParticles();
	}
}
