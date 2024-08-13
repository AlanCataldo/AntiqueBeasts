package net.mebahel.antiquebeasts.util;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.ModEntities;
import net.mebahel.antiquebeasts.entity.custom.egyptian.AxemanEntity;
import net.mebahel.antiquebeasts.entity.custom.egyptian.CamelryEntity;
import net.mebahel.antiquebeasts.entity.custom.egyptian.EgyptianCaravanEntity;
import net.mebahel.antiquebeasts.entity.custom.greek.CentaurEntity;
import net.mebahel.antiquebeasts.entity.custom.greek.ChampionHopliteEntity;
import net.mebahel.antiquebeasts.entity.custom.greek.EliteHopliteEntity;
import net.mebahel.antiquebeasts.entity.custom.norse.EinherjarEntity;
import net.mebahel.antiquebeasts.entity.custom.norse.HersirEntity;
import net.mebahel.antiquebeasts.entity.custom.norse.HuskarlEntity;
import net.mebahel.antiquebeasts.entity.custom.norse.ValkyrieEntity;
import net.mebahel.antiquebeasts.entity.custom.patrol.ModPatrolEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class PatrolManager {
    private static final int CHECK_INTERVAL = 20 * 60 * ModConfig.patrolSpawnDelay;
    private static int patrolCheckCounter = 0;

    public static void register() {
        if (!ModConfig.patrolSpawning) {
            AntiqueBeasts.LOGGER.info("[AntiqueBeasts] Patrol spawning is disabled in config file.");
            return;
        }
        AntiqueBeasts.LOGGER.info("[AntiqueBeasts] Registering patrol spawning for " + AntiqueBeasts.MOD_ID + ".");
        ServerWorldEvents.LOAD.register((server, world) -> {
            if (world.getRegistryKey() == World.OVERWORLD) {
                ServerTickEvents.START_SERVER_TICK.register(serverTick -> {
                    patrolCheckCounter++;
                    if (patrolCheckCounter >= CHECK_INTERVAL) {
                        patrolCheckCounter = 0;
                        checkAndSpawnPatrol(world);
                    }
                });
            }
        });
    }

    private static void checkAndSpawnPatrol(ServerWorld world) {
        List<ServerPlayerEntity> players = world.getPlayers();
        System.out.println(players);

        if (!players.isEmpty()) {
            Random random = new Random();
            PlayerEntity randomPlayer = players.get(random.nextInt(players.size()));

            if (randomPlayer.isAlive()) {
                BlockPos spawnPos = findSpawnPosition(world, randomPlayer);
                if (spawnPos != null) {
                    UUID patrolId = UUID.randomUUID();
                    spawnPatrol(world, spawnPos, patrolId, randomPlayer);
                    System.out.println("Patrol spawning for " + AntiqueBeasts.MOD_ID + " at : " + spawnPos);
                }
            }
        }
    }

    private static BlockPos findSpawnPosition(ServerWorld world, PlayerEntity player) {
        return player.getBlockPos().add(world.random.nextInt(100) - 50, 0, world.random.nextInt(100) - 50);
    }

    public static BlockPos setRandomPatrolTarget(ServerWorld world, BlockPos pos) {
        int x = 150 + world.random.nextInt(150);
        int z = 150 + world.random.nextInt(150);

        if (world.random.nextBoolean())
            x = -x;
        if (world.random.nextBoolean())
            z = -z;

        BlockPos distantTarget = pos.add(x, 0, z);
        distantTarget = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, distantTarget);

        return distantTarget;
    }

    private static BlockPos findGroundPosition(ServerWorld world, BlockPos pos) {
        return world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos);
    }

    private static BlockPos getOffsetPosition(BlockPos basePos, Random random) {
        int offsetX = -2 + random.nextInt(5);
        int offsetZ = -2 + random.nextInt(5);
        return basePos.add(offsetX, 0, offsetZ);
    }

    private static void spawnPatrol(ServerWorld world, BlockPos pos, UUID patrolId, PlayerEntity player) {
        Random random = new Random();
        BlockPos groundPos = findGroundPosition(world, pos);
        BlockPos leaderPos = getOffsetPosition(groundPos, random);
        BlockPos distantTarget = setRandomPatrolTarget(world, groundPos);
        RegistryEntry<Biome> biome = world.getBiome(player.getBlockPos());

        if (biome.isIn(ConventionalBiomeTags.PLAINS) || biome.isIn(ConventionalBiomeTags.FOREST)) {
            spawnPatrolLeader(world, ModEntities.CHAMPION_HOPLITE, leaderPos, distantTarget, random, patrolId);

            int numFollowers = 2 + random.nextInt(2);
            spawnPatrolMember(world, ModEntities.CENTAUR, 1, groundPos, distantTarget, random, patrolId);
            spawnPatrolMember(world, ModEntities.ELITE_HOPLITE, numFollowers, groundPos, distantTarget, random, patrolId);
        } else if (biome.isIn(ConventionalBiomeTags.DESERT) || biome.isIn(ConventionalBiomeTags.BADLANDS)) {
            spawnPatrolLeader(world, ModEntities.CAMELRY, leaderPos, distantTarget, random, patrolId);

            int numFollowers = random.nextInt(2);
            if (numFollowers == 1)
                spawnPatrolMember(world, ModEntities.EGYPTIAN_CARAVAN, 1, groundPos, distantTarget, random, patrolId);
            else
                numFollowers += 1;
            spawnPatrolMember(world, ModEntities.AXEMAN, numFollowers + 2, groundPos, distantTarget, random, patrolId);
        } else if (biome.isIn(ConventionalBiomeTags.CLIMATE_COLD) || biome.isIn(ConventionalBiomeTags.SNOWY) ||
                biome.isIn(ConventionalBiomeTags.ICY) || biome.isIn(ConventionalBiomeTags.AQUATIC_ICY)
                || biome.isIn(ConventionalBiomeTags.TAIGA)) {
            spawnPatrolLeader(world, ModEntities.HERSIR, leaderPos, distantTarget, random, patrolId);

            int numFollowers = 2 + random.nextInt(2);
            spawnPatrolMember(world, ModEntities.EINHERJAR, 1, groundPos, distantTarget, random, patrolId);
            spawnPatrolMember(world, ModEntities.HUSKARL, numFollowers, groundPos, distantTarget, random, patrolId);
        }
        else {
            spawnPatrolLeader(world, ModEntities.CHAMPION_HOPLITE, leaderPos, distantTarget, random, patrolId);

            int numFollowers = 2 + random.nextInt(2);
            spawnPatrolMember(world, ModEntities.CENTAUR, 1, groundPos, distantTarget, random, patrolId);
            spawnPatrolMember(world, ModEntities.ELITE_HOPLITE, numFollowers, groundPos, distantTarget, random, patrolId);
        }
    }

    private static void spawnPatrolMember(ServerWorld world, EntityType entityType, int entityNumber,
                                          BlockPos groundPos, BlockPos distantTarget, Random random, UUID patrolId) {
        for (int i = 0; i < entityNumber; i++) {
            ModPatrolEntity member = createPatrolUnit(entityType, world);

            member.setPatrolId(patrolId.toString());
            BlockPos memberPos = getOffsetPosition(groundPos, random);
            member.setPosition(memberPos.getX() + 0.5, memberPos.getY(), memberPos.getZ() + 0.5);
            member.setPatrolTarget(distantTarget);
            member.setWasInitiallyInPatrol(true);
            member.initialize(world, world.getLocalDifficulty(groundPos), SpawnReason.EVENT, null, null);

            world.spawnEntity(member);
        }
    }

    private static void spawnPatrolLeader(ServerWorld world, EntityType entityType,
                                          BlockPos groundPos, BlockPos distantTarget, Random random, UUID patrolId) {
        ModPatrolEntity leader = createPatrolUnit(entityType, world);;

        leader.setPatrolId(patrolId.toString());
        BlockPos leaderPos = getOffsetPosition(groundPos, random);
        leader.setPosition(leaderPos.getX() + 0.5, leaderPos.getY(), leaderPos.getZ() + 0.5);
        leader.setPatrolLeader(true);
        leader.setPatrolTarget(distantTarget);
        leader.setWasInitiallyInPatrol(true);
        leader.initialize(world, world.getLocalDifficulty(groundPos), SpawnReason.EVENT, null, null);
        world.spawnEntity(leader);
    }

    private static ModPatrolEntity createPatrolUnit(EntityType entityType, ServerWorld world) {
        return switch (entityType.getTranslationKey()) {
            case "entity.antiquebeasts.champion_hoplite" -> new ChampionHopliteEntity(ModEntities.CHAMPION_HOPLITE, world);
            case "entity.antiquebeasts.centaur" -> new CentaurEntity(ModEntities.CENTAUR, world);
            case "entity.antiquebeasts.axeman" -> new AxemanEntity(ModEntities.AXEMAN, world);
            case "entity.antiquebeasts.camelry" -> new CamelryEntity(ModEntities.CAMELRY, world);
            case "entity.antiquebeasts.hersir" -> new HersirEntity(ModEntities.HERSIR, world);
            case "entity.antiquebeasts.huskarl" -> new HuskarlEntity(ModEntities.HUSKARL, world);
            case "entity.antiquebeasts.einherjar" -> new EinherjarEntity(ModEntities.EINHERJAR, world);
            case "entity.antiquebeasts.valkyrie" -> new ValkyrieEntity(ModEntities.VALKYRIE, world);
            case "entity.antiquebeasts.egyptian_caravan" -> new EgyptianCaravanEntity(ModEntities.EGYPTIAN_CARAVAN, world);
            default -> new EliteHopliteEntity(ModEntities.ELITE_HOPLITE, world);
        };
    }
}
