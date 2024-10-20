package net.mebahel.antiquebeasts;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.mebahel.antiquebeasts.block.ModBlocks;
import net.mebahel.antiquebeasts.entity.ModEntities;
import net.mebahel.antiquebeasts.entity.custom.*;
import net.mebahel.antiquebeasts.entity.custom.egyptian.*;
import net.mebahel.antiquebeasts.entity.custom.greek.CentaurEntity;
import net.mebahel.antiquebeasts.entity.custom.greek.ChampionHopliteEntity;
import net.mebahel.antiquebeasts.entity.custom.greek.EliteHopliteEntity;
import net.mebahel.antiquebeasts.entity.custom.greek.HeroHopliteEntity;
import net.mebahel.antiquebeasts.entity.custom.norse.*;
import net.mebahel.antiquebeasts.entity.custom.other.*;
import net.mebahel.antiquebeasts.item.TickScheduler;
import net.mebahel.antiquebeasts.item.custom.ModItemGroups;
import net.mebahel.antiquebeasts.item.custom.ModItems;
import net.mebahel.antiquebeasts.particle.ModParticles;
import net.mebahel.antiquebeasts.potion.ModPotions;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.mebahel.antiquebeasts.util.config.ModBonusHealthConfig;
import net.mebahel.antiquebeasts.util.config.ModConfig;
import net.mebahel.antiquebeasts.util.MyProcessors;
import net.mebahel.antiquebeasts.util.PatrolManager;
import net.mebahel.antiquebeasts.util.WaterRemovalScheduler;
import net.mebahel.antiquebeasts.util.config.ModSpawnRateConfig;
import net.mebahel.antiquebeasts.world.gen.ModWorldGen;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bernie.geckolib.GeckoLib;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class AntiqueBeasts implements ModInitializer {
	public static final String MOD_ID = "antiquebeasts";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private static final TickScheduler tickScheduler = new TickScheduler();
	private static final WaterRemovalScheduler waterRemovalScheduler = new WaterRemovalScheduler();
	private static final Map<ServerWorld, ServerTickEvents.EndTick> registeredListeners = new HashMap<>();

	@Override
	public void onInitialize() {
		File configDir = new File(FabricLoader.getInstance().getConfigDir().toFile(), "antiquebeasts");
		ModConfig.loadConfig(configDir);
		ModSpawnRateConfig.loadConfig(configDir);
		ModBonusHealthConfig.loadConfig(configDir);
		ModItemGroups.registerItemGroups();

		FabricDefaultAttributeRegistry.register(ModEntities.DRAUGR_SCOURGE, DraugrScourgeEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.DRAUGR_WIGHT, DraugrWightEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.DRAUGR_ARCHER, DraugrArcherEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.MUMMY_BOSS, MummyBossEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.HARPY, HarpyEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.DRAUGR, DraugrEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.CENTAUR, CentaurEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.EGYPTIAN_CARAVAN, EgyptianCaravanEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.PEGASUS, PegasusEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.CHIMERA, ChimeraEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.ELEPHANT_RIDER, ElephantRiderEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.SERVANT, ServantEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.MUMMY, MummyEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.CAMELRY, CamelryEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.AXEMAN, AxemanEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.WADJET, WadjetEntity.setAttributes());
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
		FabricDefaultAttributeRegistry.register(ModEntities.EINHERJAR, EinherjarEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.VALKYRIE, ValkyrieEntity.setAttributes());
		ModSounds.registerSounds();
		ModBlocks.registerModBlocks();
		ModWorldGen.generateWorldGen();
		ModItems.registerModItems();
		ModParticles.registerParticles();
		ModPotions.registerPotionRecipe();
		PatrolManager.register();
		MyProcessors.init();

		ServerWorldEvents.LOAD.register((server, world) -> {
				waterRemovalScheduler.addWorld(world);

				ServerTickEvents.EndTick listener = serverTick -> {
					tickScheduler.tick();
					waterRemovalScheduler.tick();
					serverTick.getWorlds().forEach(serverWorld -> {
						serverWorld.getPlayers().forEach(player -> {
							if (isMummyBossNearby(player, 15.0)) {
								triggerAdvancement(player);
							}
						});
					});
				};
				ServerTickEvents.END_SERVER_TICK.register(listener);
				registeredListeners.put(world, listener);
		});

		ServerWorldEvents.UNLOAD.register((server, world) -> {
			waterRemovalScheduler.removeWorld(world);
			registeredListeners.remove(world);
		});
	}
	public static TickScheduler getTickScheduler() {
		return tickScheduler;
	}

	public static WaterRemovalScheduler getWaterRemovalScheduler() {
		return waterRemovalScheduler;
	}
	private boolean isMummyBossNearby(PlayerEntity player, double radius) {
		Box box = new Box(player.getX() - radius, player.getY() - radius, player.getZ() - radius,
				player.getX() + radius, player.getY() + radius, player.getZ() + radius);

		return player.getWorld().getEntitiesByClass(Entity.class, box, entity -> {
			return entity.getType() == ModEntities.MUMMY_BOSS;
		}).size() > 0;
	}
	public static final Identifier ADVANCEMENT_ID = new Identifier("antiquebeasts", "see_mummy_boss");
	private void triggerAdvancement(ServerPlayerEntity player) {
		Advancement advancement = player.getServer().getAdvancementLoader().get(ADVANCEMENT_ID);
		if (advancement != null) {
			AdvancementProgress progress = player.getAdvancementTracker().getProgress(advancement);
			if (!progress.isDone()) {
				player.getAdvancementTracker().grantCriterion(advancement, "see_mummy_boss");
			}
		}
	}
}
