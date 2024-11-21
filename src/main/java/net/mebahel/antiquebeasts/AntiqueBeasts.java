package net.mebahel.antiquebeasts;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
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
import net.mebahel.antiquebeasts.util.raid.*;
import net.mebahel.antiquebeasts.util.config.ModArmorValueConfig;
import net.mebahel.antiquebeasts.util.config.ModBonusHealthConfig;
import net.mebahel.antiquebeasts.util.config.ModConfig;
import net.mebahel.antiquebeasts.util.MyProcessors;
import net.mebahel.antiquebeasts.util.PatrolManager;
import net.mebahel.antiquebeasts.util.WaterRemovalScheduler;
import net.mebahel.antiquebeasts.util.config.ModSpawnRateConfig;
import net.mebahel.antiquebeasts.world.gen.ModWorldGen;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
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

	private static final List<UUID> playersPendingRaidCheck = new ArrayList<>();
	private static final Set<UUID> processedPlayers = new HashSet<>();
	private boolean playerHasArrived = false;

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
		//RaidManager.registerWorldLoadEvents();
		RaidManager.registerEvents();

		ServerWorldEvents.LOAD.register((server, world) -> {
			waterRemovalScheduler.addWorld(world);


			/*if (world.getRegistryKey().equals(World.OVERWORLD)) {
				System.out.println("World loaded: " + world.getRegistryKey());
				PersistentRaidData raidData = PersistentRaidData.get(world);
				System.out.println("- LOAD PersistentRaidData - " + raidData);

				ServerTickEvents.END_WORLD_TICK.register(serverWorld -> {
					if (serverWorld.equals(world)) {
						HashMap<UUID, DraugrRaidTest> allRaids = raidData.getAllRaids();
						System.out.println("[Debug] Raids après quelques ticks : " + allRaids);
					}
				});
			}*/

			ServerPlayConnectionEvents.Join joinEventListener = (handler, sender, server2) -> {
				if (world.getRegistryKey().equals(World.OVERWORLD)) {
					System.out.println("- PLAYER HAS ARRIVED - ");
					playerHasArrived = true;
				}
			};
			registeredJoinEventListeners.put(world, joinEventListener);

			PersistentStateManager stateManager = world.getPersistentStateManager();
			AntiquebeastsDifficultyState difficultyState = stateManager.getOrCreate(
					AntiquebeastsDifficultyState::fromNbt,
					() -> new AntiquebeastsDifficultyState(1),
					"antique_beasts_difficulty_state"
			);
			int difficultyLevel = difficultyState.getDifficultyLevel();
			worldDifficultyLevels.put(world, difficultyLevel);
			System.out.println("[Mebahel's Antique Beasts] Loaded difficulty level for world " + world.getRegistryKey().getValue() + ": " + difficultyLevel);

			ServerTickEvents.EndTick listener = serverTick -> {
				if (serverTick.getWorld(World.OVERWORLD) == world) {
					if (playerHasArrived) {

						List<ServerPlayerEntity> playerList = world.getPlayers();
						ServerPlayerEntity player = playerList.get(0);
						PersistentRaidData data = PersistentRaidData.get(world);
						DraugrRaidTest raid = data.getRaid(player.getUuid());
						System.out.println("- DATA GET ALL RAIDS ON JOIN - ");
						System.out.println(data.getAllRaids());

						if (raid != null) {
							System.out.println("Raid trouvé pour le joueur : " + player.getName().getString());
							raidsPendingResume.add(raid);
						} else {
							System.out.println("Aucun raid en cours trouvé pour le joueur : " + player.getName().getString());
						}
						System.out.println("RAID PENDING :");
						System.out.println(raidsPendingResume);
						Set<UUID> seenRaidUuids = new HashSet<>();
						raidsPendingResume.removeIf(existingRaid -> !seenRaidUuids.add(existingRaid.raidUuid));
						System.out.println("RAID PENDING AFTER FILTRE :");
						System.out.println(raidsPendingResume);
						playerHasArrived = false;
					}


					if (!raidsPendingResume.isEmpty()) {
						Iterator<DraugrRaidTest> iterator = raidsPendingResume.iterator();
						while (iterator.hasNext()) {
							DraugrRaidTest raid = iterator.next();
							if (raid.isRaidCompleted()) {
								PersistentRaidData data = PersistentRaidData.get(world);
								if (data != null) {
									data.removeRaid(raid.getTargetPlayerUuid());
									data.markDirty();
								}
								iterator.remove();
								continue;
							}
							PlayerEntity player = world.getPlayerByUuid(raid.getTargetPlayerUuid());
							if (player != null && !raid.isRaidCompleted() && !raid.raidHasBeenResume) {
								raid.setTargetPlayer(player);
								raid.resumeRaid(player, world);
						}}
						raidsPendingResume.clear();
					}

					if (ModConfig.enableDifficultySystem) {
						if (worldDifficultyLevels.get(world) == 1) {
							netherCheckCounter++;
							if (netherCheckCounter >= NETHER_CHECK_INTERVAL) {
								netherCheckCounter = 0;
								checkNetherVisit(world, difficultyState);
							}
						}
					} else {
						worldDifficultyLevels.put(world, 1);
					}
				}
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
			ServerPlayConnectionEvents.JOIN.register(joinEventListener);
			registeredListeners.put(world, listener);
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
