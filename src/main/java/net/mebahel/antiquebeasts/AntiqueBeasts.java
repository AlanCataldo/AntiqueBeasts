package net.mebahel.antiquebeasts;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.mebahel.antiquebeasts.block.ModBlockEntities;
import net.mebahel.antiquebeasts.block.ModBlocks;
import net.mebahel.antiquebeasts.block.screenhandlers.ModScreenHandlerType;
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
import net.mebahel.antiquebeasts.util.MyProcessors;
import net.mebahel.antiquebeasts.util.PatrolManager;
import net.mebahel.antiquebeasts.util.WaterRemovalScheduler;
import net.mebahel.antiquebeasts.util.config.ModArmorValueConfig;
import net.mebahel.antiquebeasts.util.config.ModBonusHealthConfig;
import net.mebahel.antiquebeasts.util.config.ModConfig;
import net.mebahel.antiquebeasts.util.config.ModSpawnRateConfig;
import net.mebahel.antiquebeasts.util.packet.ModNetworking;
import net.mebahel.antiquebeasts.util.raid.AntiquebeastsDifficultyState;
import net.mebahel.antiquebeasts.util.raid.DraugrRaidTest;
import net.mebahel.antiquebeasts.util.raid.PersistentRaidData;
import net.mebahel.antiquebeasts.util.raid.RaidManager;
import net.mebahel.antiquebeasts.world.gen.ModWorldGen;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

public class AntiqueBeasts implements ModInitializer {
	public static final String MOD_ID = "antiquebeasts";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static final TickScheduler tickScheduler = new TickScheduler();

	private static final AntiquebeastsDifficultyState difficultyState = new AntiquebeastsDifficultyState(1);
	private static final WaterRemovalScheduler waterRemovalScheduler = new WaterRemovalScheduler();
	private static final Map<ServerWorld, ServerTickEvents.EndTick> registeredListeners = new HashMap<>();
	private static final Map<ServerWorld, ServerPlayConnectionEvents.Join> registeredJoinEventListeners = new HashMap<>();
	public static final Map<ServerWorld, Integer> worldDifficultyLevels = new HashMap<>();
	private static final List<DraugrRaidTest> raidsPendingResume = new ArrayList<>();
	public static final List<DraugrRaidTest> ongoingRaids = new ArrayList<>();
	private boolean playerHasArrived = false;
	private boolean shouldEnableLoad = true;
	private static final int ACHIEVEMENT_CHECK_INTERVAL = 20;
	private int achievementTickCounter = 0;
	private final Set<UUID> playersWithAchievement = new HashSet<>();

	@Override
	public void onInitialize() {
		File configDir = new File(FabricLoader.getInstance().getConfigDir().toFile(), "antiquebeasts");
		ModConfig.loadConfig(configDir);
		ModArmorValueConfig.loadConfig(configDir);
		ModSpawnRateConfig.loadConfig(configDir);
		ModBonusHealthConfig.loadConfig(configDir);
		ModItemGroups.registerItemGroups();

		FabricDefaultAttributeRegistry.register(ModEntities.DRAUGR_OVERLORD, DraugrOverlordEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.SKELETON_WARRIOR_HEAD, SkeletonWarriorHeadEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.SKELETON_WARRIOR, SkeletonWarriorEntity.setAttributes());
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
		ModBlockEntities.registerModBlockEntities();

		ModScreenHandlerType.registerScreenHandlers();

		ModWorldGen.generateWorldGen();
		ModItems.registerModItems();
		ModParticles.registerParticles();
		ModPotions.registerPotionRecipe();
		PatrolManager.register();
		MyProcessors.init();
		RaidManager.registerEvents();
		ModNetworking.registerReceivers();

		ServerWorldEvents.LOAD.register((server, world) -> {
			waterRemovalScheduler.addWorld(world);
			difficultyState.registerDifficultyState(world);
			ServerPlayConnectionEvents.Join joinEventListener = (handler, sender, server2) -> {
				if (world.getRegistryKey().equals(World.OVERWORLD)) {
					playerHasArrived = true;
				}
			};
			registeredJoinEventListeners.put(world, joinEventListener);

			ServerTickEvents.EndTick listener = serverTick -> {
				if (serverTick.getWorld(World.OVERWORLD) == world) {
					if (playerHasArrived && shouldEnableLoad) {
						tickScheduler.schedule(world, 20, w -> {
							PersistentRaidData.get(world);
						});
						shouldEnableLoad = false;
						playerHasArrived = false;
					}
					waterRemovalScheduler.tick();
				}
				checkMummyBossAchievement(world);
				difficultyState.updateDifficultyState(world);
				tickScheduler.tick();
			};

			ServerPlayConnectionEvents.JOIN.register(joinEventListener);
			ServerTickEvents.END_SERVER_TICK.register(listener);
		});

		ServerWorldEvents.UNLOAD.register((server, world) -> {
			waterRemovalScheduler.removeWorld(world);
			registeredListeners.remove(world);
			registeredJoinEventListeners.remove(world);
		});
	}

	private void checkMummyBossAchievement(ServerWorld world) {
		if (++achievementTickCounter % ACHIEVEMENT_CHECK_INTERVAL != 0) return;

		if (world.getPlayers().isEmpty()) return;

		List<? extends MummyBossEntity> mummyBosses = world.getEntitiesByType(ModEntities.MUMMY_BOSS, entity -> true);

		if (mummyBosses.isEmpty()) return;

		for (ServerPlayerEntity player : world.getPlayers()) {
			if (playersWithAchievement.contains(player.getUuid())) continue;

			boolean isNearby = mummyBosses.stream().anyMatch(mummy ->
					player.squaredDistanceTo(mummy.getX(), mummy.getY(), mummy.getZ()) <= 15 * 15);

			if (isNearby) {
				triggerAdvancement(player);
				playersWithAchievement.add(player.getUuid());
			}
		}
	}

	public static TickScheduler getTickScheduler() {
		return tickScheduler;
	}

	public static WaterRemovalScheduler getWaterRemovalScheduler() {
		return waterRemovalScheduler;
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
