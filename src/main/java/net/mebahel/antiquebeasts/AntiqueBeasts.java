package net.mebahel.antiquebeasts;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.mebahel.antiquebeasts.block.ModBlockEntities;
import net.mebahel.antiquebeasts.block.ModBlocks;
import net.mebahel.antiquebeasts.block.screenhandlers.ModScreenHandlers;
import net.mebahel.antiquebeasts.entity.ModEntities;
import net.mebahel.antiquebeasts.entity.custom.*;
import net.mebahel.antiquebeasts.entity.custom.dwemer.DwemerCenturionEntity;
import net.mebahel.antiquebeasts.entity.custom.dwemer.DwemerSpiderEntity;
import net.mebahel.antiquebeasts.entity.custom.dwemer.DwemerSpiderGuardianEntity;
import net.mebahel.antiquebeasts.entity.custom.egyptian.*;
import net.mebahel.antiquebeasts.entity.custom.greek.CentaurEntity;
import net.mebahel.antiquebeasts.entity.custom.greek.ChampionHopliteEntity;
import net.mebahel.antiquebeasts.entity.custom.greek.EliteHopliteEntity;
import net.mebahel.antiquebeasts.entity.custom.greek.HeroHopliteEntity;
import net.mebahel.antiquebeasts.entity.custom.norse.*;
import net.mebahel.antiquebeasts.entity.custom.other.*;
import net.mebahel.antiquebeasts.item.TickScheduler;
import net.mebahel.antiquebeasts.item.custom.ModArmors;
import net.mebahel.antiquebeasts.item.custom.ModItemGroups;
import net.mebahel.antiquebeasts.item.custom.ModItems;
import net.mebahel.antiquebeasts.item.custom.ModSpawnEggs;
import net.mebahel.antiquebeasts.particle.ModParticles;
import net.mebahel.antiquebeasts.potion.ModPotions;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.mebahel.antiquebeasts.util.MyProcessors;
import net.mebahel.antiquebeasts.util.PatrolManager;
import net.mebahel.antiquebeasts.util.WaterRemovalScheduler;
import net.mebahel.antiquebeasts.util.books.AddBookToLootTableUtil;
import net.mebahel.antiquebeasts.util.books.LootTableGroups;
import net.mebahel.antiquebeasts.util.config.ModArmorValueConfig;
import net.mebahel.antiquebeasts.util.config.ModBonusHealthConfig;
import net.mebahel.antiquebeasts.util.config.ModConfig;
import net.mebahel.antiquebeasts.util.config.ModSpawnRateConfig;
import net.mebahel.antiquebeasts.util.packet.ChestOpenSync;
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
import software.bernie.geckolib.network.GeckoLibNetwork;

import java.io.File;
import java.util.*;

public class AntiqueBeasts implements ModInitializer {
	public static final String MOD_ID = "antiquebeasts";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private static final TickScheduler tickScheduler = new TickScheduler();
	private static final AntiquebeastsDifficultyState antiquebeastsDifficultyState = new AntiquebeastsDifficultyState(1);
	private static final WaterRemovalScheduler waterRemovalScheduler = new WaterRemovalScheduler();
	private static final Map<ServerWorld, ServerTickEvents.EndTick> registeredListeners = new HashMap<>();
	private static final Map<ServerWorld, ServerPlayConnectionEvents.Join> registeredJoinEventListeners = new HashMap<>();
	public static final Map<ServerWorld, Integer> worldDifficultyLevels = new HashMap<>();
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

		FabricDefaultAttributeRegistry.register(ModEntities.DWEMER_CENTURION, DwemerCenturionEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.DWEMER_SPIDER, DwemerSpiderEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.DWEMER_SPIDER_GUARDIAN, DwemerSpiderGuardianEntity.setAttributes());
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

		ModArmors.registerModArmors();
		ModSounds.registerSounds();
		ModBlocks.registerModBlocks();
		ModBlockEntities.registerModBlockEntities();
		ModWorldGen.generateWorldGen();

		ModItems.registerModItems();
		ModSpawnEggs.registerModItems();
		ModParticles.registerParticles();
		ModPotions.registerPotionRecipe();
		PatrolManager.register();
		ModScreenHandlers.registerScreenHandlers();
		MyProcessors.init();
		RaidManager.registerEvents();
		ModNetworking.registerReceivers();
		AddBookToLootTableUtil bookUtil = new AddBookToLootTableUtil();
		ChestOpenSync.registerServerReceiver();

		bookUtil.addBookFromString(
				"villager_entry_2",
				"More like Zeusless",
				"Unknown Witness",
				"Those ploughing hoplites, will they not leave us be? We're but a small village, they have the luxury of those grand eyesores they call home. I suppose that's not enough for those whoresons, attacking us, wanting our land too. If Zeus truly is for the people then why must he let this happen? \"I gave you people your golems, what more do you want?\" I don't know Zeus, maybe some thick walls where it's nice and safe from monsters like your dearly beloved hoplites get. Maybe tell them to stop trying to take our land for once. I say, Zeus could probably fart on us and he'd expect us to kneel and praise him for that too. I've had enough of this farcical display. The only good those hoplites are for is when the draugr and zombies bother them instead of us. Come morn, I'm moving away from this village in search for another. Damn Zeus and damn those bloody hoplites, may they never know peace in their miserable lives.",
				LootTableGroups.VILLAGE_ALL.get()
		);

		bookUtil.addBookFromString(
				"villager_entry_1",
				"A Mysterious Event",
				"Unknown Witness",
				"Mary won't believe me, but I know what I saw was no jest. Some knight, with some sort of stone or rune, casting a spell to what appeared to be summoning waves of those vile draugr creatures. I made sure not to get too close, I only witnessed it from afar, but I definitely witnessed truth before my eyes, not some mere hoax or illusion. Luckily, whoever this brave soul was, he was able to slay all the draugr, then received what I can only describe as some sort of green, wide storage container, similar to a chest. I didn't get close enough to ascertain for certain, as reason told me I shan't get too close, lest I wished my early death. But whatever it was, it seemed to hold all sorts of treasures. Perhaps tomorrow I shall go back to the spot and see what remains. My mind tells me I shan't see anything there anymore, not even that brave knight who took off into the night once his deed was fulfilled, but who knows, perhaps Odin will bless me and has left a few valuable trinkets for me and Mary to ogle. I'll set foot there once again tomorrow, I'm certain of that at least... ",
				LootTableGroups.VILLAGE_ALL.get()
		);

		bookUtil.addBookFromString(
				"hoplite_new_recruit_entry_1",
				"Rumbling Temple",
				"Hoplite New Recrute",
				"What the gods is that noise down below? I may be new to the ranks but I feel as though I have a right to know what pest keeps me up at nights in the temple. Sokratis tells me it is not for me to know, but he can't punish me for having a hint of curiosity - the bloody thing keeps me awake at night! I took one look down into the deeper parts of our temple and saw nothing friendly down there. The others tell me to stay away and truth be told I'd be a fool to not listen, but Zeus bless any poor soul unfortunate enough to be forced down there. Only the sounds of monsters and waning cries for help echo through those walls.",
				List.of(new Identifier("antiquebeasts", "chests/greek/temple_commun"))
		);

		bookUtil.registerModifyLootTable();

		ServerWorldEvents.LOAD.register((server, world) -> {
			waterRemovalScheduler.addWorld(world);
			antiquebeastsDifficultyState.registerDifficultyState(world);
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
				antiquebeastsDifficultyState.updateDifficultyState(world);
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
