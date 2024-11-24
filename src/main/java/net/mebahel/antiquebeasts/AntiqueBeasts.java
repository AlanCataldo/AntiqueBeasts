package net.mebahel.antiquebeasts;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
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
import net.mebahel.antiquebeasts.util.MyProcessors;
import net.mebahel.antiquebeasts.util.PatrolManager;
import net.mebahel.antiquebeasts.util.WaterRemovalScheduler;
import net.mebahel.antiquebeasts.util.config.ModArmorValueConfig;
import net.mebahel.antiquebeasts.util.config.ModBonusHealthConfig;
import net.mebahel.antiquebeasts.util.config.ModConfig;
import net.mebahel.antiquebeasts.util.config.ModSpawnRateConfig;
import net.mebahel.antiquebeasts.util.raid.AntiquebeastsDifficultyState;
import net.mebahel.antiquebeasts.util.raid.DraugrRaidTest;
import net.mebahel.antiquebeasts.util.raid.PersistentRaidData;
import net.mebahel.antiquebeasts.util.raid.RaidManager;
import net.mebahel.antiquebeasts.world.gen.ModWorldGen;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

public class AntiqueBeasts implements ModInitializer {
	public static final String MOD_ID = "antiquebeasts";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private static int netherCheckCounter = 0;
	private static final int NETHER_CHECK_INTERVAL = 300;
	private static final TickScheduler tickScheduler = new TickScheduler();
	private static final WaterRemovalScheduler waterRemovalScheduler = new WaterRemovalScheduler();
	private static final Map<ServerWorld, ServerTickEvents.EndTick> registeredListeners = new HashMap<>();
	private static final Map<ServerWorld, ServerPlayConnectionEvents.Join> registeredJoinEventListeners = new HashMap<>();
	public static final Map<ServerWorld, Integer> worldDifficultyLevels = new HashMap<>();
	private static final List<DraugrRaidTest> raidsPendingResume = new ArrayList<>();
	public static final List<DraugrRaidTest> ongoingRaids = new ArrayList<>();
	private boolean playerHasArrived = false;
	private boolean shouldEnableLoad = true;


	@Override
	public void onInitialize() {
		File configDir = new File(FabricLoader.getInstance().getConfigDir().toFile(), "antiquebeasts");
		ModConfig.loadConfig(configDir);
		ModArmorValueConfig.loadConfig(configDir);
		ModSpawnRateConfig.loadConfig(configDir);
		ModBonusHealthConfig.loadConfig(configDir);
		ModItemGroups.registerItemGroups();

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
		ModWorldGen.generateWorldGen();
		ModItems.registerModItems();
		ModParticles.registerParticles();
		ModPotions.registerPotionRecipe();
		PatrolManager.register();
		MyProcessors.init();
		RaidManager.registerEvents();

		ServerWorldEvents.LOAD.register((server, world) -> {
			waterRemovalScheduler.addWorld(world);

			ServerPlayConnectionEvents.Join joinEventListener = (handler, sender, server2) -> {
				if (world.getRegistryKey().equals(World.OVERWORLD)) {
					playerHasArrived = true;
				}
			};
			registeredJoinEventListeners.put(world, joinEventListener);

			ServerTickEvents.EndTick listener = serverTick -> {
				if (serverTick.getWorld(World.OVERWORLD) == world) {
					if (ModConfig.enableDifficultySystem) {
						if (worldDifficultyLevels.get(world) == 1) {
							netherCheckCounter++;
							if (netherCheckCounter >= NETHER_CHECK_INTERVAL) {
								netherCheckCounter = 0;
								//checkNetherVisit(world, difficultyState);
							}
						}
					} else {
						worldDifficultyLevels.put(world, 1);
					}
				}
				tickScheduler.tick();
				waterRemovalScheduler.tick();
			};

			PersistentStateManager stateManager = world.getPersistentStateManager();
			AntiquebeastsDifficultyState difficultyState = stateManager.getOrCreate(
					AntiquebeastsDifficultyState::fromNbt,
					() -> new AntiquebeastsDifficultyState(1),
					"antique_beasts_difficulty_state"
			);
			int difficultyLevel = difficultyState.getDifficultyLevel();
			worldDifficultyLevels.put(world, difficultyLevel);
			System.out.println("[Mebahel's Antique Beasts] Loaded difficulty level for world " + world.getRegistryKey().getValue() + ": " + difficultyLevel);

			ServerPlayConnectionEvents.JOIN.register(joinEventListener);
			ServerTickEvents.END_SERVER_TICK.register(listener);
		});

		ServerWorldEvents.UNLOAD.register((server, world) -> {
			waterRemovalScheduler.removeWorld(world);
			registeredListeners.remove(world);
			registeredJoinEventListeners.remove(world);
		});
	}
	private static void checkNetherVisit(ServerWorld world, AntiquebeastsDifficultyState difficultyState) {
		List<ServerPlayerEntity> players = world.getPlayers();

		for (ServerPlayerEntity player : players) {
			if (player.getAdvancementTracker().getProgress(world.getServer().getAdvancementLoader().get(new Identifier("minecraft", "nether/root"))).isDone()) {
				int difficultyLevel = 2;
				worldDifficultyLevels.put(world, difficultyLevel);
				difficultyState.setDifficultyLevel(difficultyLevel);
				System.out.println("[Mebahel's Zombie Horde] Difficulty increased to 2 due to Nether visit by " + player.getName().getString());
				break;
			}
		}
	}
	private void checkPlayerProximityForRaids(ServerWorld world) {
		// Récupère les données persistantes du raid
		PersistentRaidData raidData = PersistentRaidData.get(world);
		if (raidData == null) {
			return;
		}

		HashMap<UUID, DraugrRaidTest> allRaids = raidData.getAllRaids();
		if (allRaids.isEmpty()) {
			return;
		}

		// Parcourt tous les raids
		for (DraugrRaidTest raid : allRaids.values()) {
			List<ServerPlayerEntity> playersInRange = new ArrayList<>();

			// Parcourt toutes les entités du raid
			for (UUID entityUuid : raid.raidEntityUuid) {
				Entity entity = world.getEntity(entityUuid);
				if (entity == null || !entity.isAlive()) continue;

				// Vérifie les joueurs dans un rayon de 70 blocs
				List<ServerPlayerEntity> nearbyPlayers = world.getPlayers(player ->
						player.squaredDistanceTo(entity.getX(), entity.getY(), entity.getZ()) <= 70 * 70
				);

				// Ajoute les joueurs trouvés à la liste des joueurs proches
				playersInRange.addAll(nearbyPlayers);
			}

			// Mise à jour de la barre de boss : ajout des joueurs proches
			for (ServerPlayerEntity player : playersInRange) {
				if (!raid.getRaidBossBar().getPlayers().contains(player)) {
					raid.getRaidBossBar().addPlayer(player);
					System.out.println("Ajout du joueur " + player.getName().getString() + " à la barre de boss du raid.");
				}
			}

			// Retire les joueurs trop éloignés de la barre de boss
			List<ServerPlayerEntity> playersToRemove = new ArrayList<>();
			for (ServerPlayerEntity player : raid.getRaidBossBar().getPlayers()) {
				boolean isStillNearby = playersInRange.contains(player);
				if (!isStillNearby) {
					playersToRemove.add(player);
					System.out.println("Retrait du joueur " + player.getName().getString() + " de la barre de boss du raid.");
				}
			}
			playersToRemove.forEach(raid.getRaidBossBar()::removePlayer);
		}
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
	private static void updateRaidPlayerProximity() {
		// Parcourir tous les raids en cours
		for (DraugrRaidTest raid : AntiqueBeasts.ongoingRaids) {
			if (!raid.isRaidInProgress() || raid.isRaidCompleted()) continue; // Ignorez les raids terminés ou inactifs

			List<ServerPlayerEntity> playersInRange = new ArrayList<>();

			// Parcourir toutes les entités du raid pour détecter les joueurs proches
			for (UUID entityUuid : raid.raidEntityUuid) {
				DraugrEntity draugr = raid.findDraugrByUUID(raid.getWorld(), entityUuid);
				if (draugr == null || !draugr.isAlive()) continue;

				// Trouver tous les joueurs dans un rayon de 20 blocs autour de cette entité
				List<ServerPlayerEntity> nearbyPlayers = raid.getWorld().getPlayers(player ->
						player.squaredDistanceTo(draugr.getX(), draugr.getY(), draugr.getZ()) <= 20 * 20
				);

				playersInRange.addAll(nearbyPlayers); // Ajouter les joueurs trouvés à la liste globale
			}

			// Ajouter les joueurs proches à la barre de boss du raid
			for (ServerPlayerEntity player : playersInRange) {
				if (!raid.getRaidBossBar().getPlayers().contains(player)) {
					raid.getRaidBossBar().addPlayer(player);
					System.out.println("Ajout du joueur " + player.getName().getString() + " à la barre de boss du raid.");
				}
			}

			// Retirer les joueurs trop éloignés de la barre de boss du raid
			List<ServerPlayerEntity> playersToRemove = new ArrayList<>();
			for (ServerPlayerEntity player : raid.getRaidBossBar().getPlayers()) {
				if (!playersInRange.contains(player)) {
					playersToRemove.add(player);
					System.out.println("Retrait du joueur " + player.getName().getString() + " de la barre de boss du raid.");
				}
			}
			playersToRemove.forEach(raid.getRaidBossBar()::removePlayer);
		}
	}
}
