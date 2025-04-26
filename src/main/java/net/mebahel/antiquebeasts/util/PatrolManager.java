package net.mebahel.antiquebeasts.util;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.ModEntities;
import net.mebahel.antiquebeasts.entity.custom.egyptian.AxemanEntity;
import net.mebahel.antiquebeasts.entity.custom.egyptian.CamelryEntity;
import net.mebahel.antiquebeasts.entity.custom.greek.CentaurEntity;
import net.mebahel.antiquebeasts.entity.custom.greek.ChampionHopliteEntity;
import net.mebahel.antiquebeasts.entity.custom.greek.EliteHopliteEntity;
import net.mebahel.antiquebeasts.entity.custom.patrol.ModPatrolEntity;
import net.mebahel.antiquebeasts.util.config.ModConfig;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.*;

import static net.mebahel.antiquebeasts.entity.ai.util.ModPatrolGoal.setRandomPatrolTarget;

public class PatrolManager {
    private static final int CHECK_INTERVAL = 20 * 60 * ModConfig.patrolSpawnDelay;
    private static int patrolCheckCounter = 0;
    private static final Map<ServerWorld, ServerTickEvents.EndTick> registeredListeners = new HashMap<>();

    public static void register() {
        if (!ModConfig.patrolSpawning) {
            AntiqueBeasts.LOGGER.info("[Mebahel's Antique Beasts] Patrol spawning is disabled in config file.");
            return;
        }
        AntiqueBeasts.LOGGER.info("[Mebahel's Antique Beasts] Registering patrol spawning for " + AntiqueBeasts.MOD_ID + ".");
        ServerWorldEvents.LOAD.register((server, world) -> {
            if (world.getRegistryKey() == World.OVERWORLD) {
                ServerTickEvents.EndTick listener = serverTick -> {
                    if (serverTick.getWorld(World.OVERWORLD) == world) {
                        patrolCheckCounter++;
                        if (patrolCheckCounter >= CHECK_INTERVAL) {
                            patrolCheckCounter = 0;
                            checkAndSpawnPatrol(world);
                        }
                    }
                };
                ServerTickEvents.END_SERVER_TICK.register(listener);
                registeredListeners.put(world, listener);
            }
        });
        ServerWorldEvents.UNLOAD.register((server, world) -> {
            if (world.getRegistryKey() == World.OVERWORLD) {
                registeredListeners.remove(world);
                AntiqueBeasts.LOGGER.info("[Mebahel's Antique Beasts] World unloaded, event listener removed.");
            }
        });
    }

    private static void checkAndSpawnPatrol(ServerWorld world) {
        List<ServerPlayerEntity> players = world.getPlayers();

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
        Random random = new Random();

        // Déterminer si X et Z seront positifs ou négatifs
        int signX = random.nextBoolean() ? 1 : -1;
        int signZ = random.nextBoolean() ? 1 : -1;

        // Générer un X et un Z dans les intervalles demandés
        int offsetX = signX * (35 + random.nextInt(11)); // Entre 40 et 50 ou -40 et -50
        int offsetZ = signZ * (35 + random.nextInt(11));

        BlockPos roughSpawnPos = player.getBlockPos().add(offsetX, 0, offsetZ);

        return findSafeSpawnPosition(world, roughSpawnPos);
    }

    private static BlockPos findSafeSpawnPosition(ServerWorld world, BlockPos pos) {
        int playerY = pos.getY();
        boolean foundWater = false;

        // 1. Chercher une position au sol en descendant
        for (int y = playerY; y >= world.getBottomY(); y--) {
            BlockPos checkPos = new BlockPos(pos.getX(), y, pos.getZ());

            if (world.getBlockState(checkPos).isLiquid()) {
                foundWater = true;
                break; // Stopper la descente dès qu'on trouve de l'eau
            }

            if (!world.getBlockState(checkPos).isAir() && world.getBlockState(checkPos.up()).isAir()) {
                return checkPos.up(); // Retourner le premier bloc solide avec un espace libre au-dessus
            }
        }

        // 2. Chercher une position en montant si on n’a rien trouvé en bas
        for (int y = playerY; y <= world.getTopY(); y++) {
            BlockPos checkPos = new BlockPos(pos.getX(), y, pos.getZ());

            if (world.getBlockState(checkPos).isLiquid()) {
                foundWater = true;
                break; // Stopper la montée dès qu'on trouve de l'eau
            }

            if (!world.getBlockState(checkPos).isAir() && world.getBlockState(checkPos.up()).isAir()) {
                return checkPos.up(); // Premier bloc solide avec espace libre
            }
        }

        // 3. Si on a trouvé de l'eau avant un sol solide, essayer avec X/Z inversés
        if (foundWater) {
            BlockPos mirroredPos = new BlockPos(-pos.getX(), pos.getY(), -pos.getZ());
            foundWater = false; // Réinitialiser la variable pour la nouvelle tentative

            // Répéter la recherche avec les nouvelles coordonnées
            for (int y = playerY; y >= world.getBottomY(); y--) {
                BlockPos checkPos = new BlockPos(mirroredPos.getX(), y, mirroredPos.getZ());

                if (world.getBlockState(checkPos).isLiquid()) {
                    foundWater = true;
                    break;
                }

                if (!world.getBlockState(checkPos).isAir() && world.getBlockState(checkPos.up()).isAir()) {
                    return checkPos.up();
                }
            }

            for (int y = playerY; y <= world.getTopY(); y++) {
                BlockPos checkPos = new BlockPos(mirroredPos.getX(), y, mirroredPos.getZ());

                if (world.getBlockState(checkPos).isLiquid()) {
                    foundWater = true;
                    break;
                }

                if (!world.getBlockState(checkPos).isAir() && world.getBlockState(checkPos.up()).isAir()) {
                    return checkPos.up();
                }
            }

            // 4. Si après l’inversion on est encore dans l’eau, on annule le spawn et on ajoute 2 minutes au délai
            if (foundWater) {
                System.out.println("Patrol spawn canceled: Water detected twice. Adding 1 minute to spawn delay.");
                patrolCheckCounter = -1 * 20 * 60;
                return null;
            }
        }

        // 5. Fallback si jamais on arrive ici sans résultat
        return world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos);
    }

    private static void spawnPatrol(ServerWorld world, BlockPos pos, UUID patrolId, PlayerEntity player) {
        Random random = new Random();
        BlockPos leaderPos = getOffsetPosition(pos, random);
        BlockPos distantTarget = setRandomPatrolTarget(world, pos);
        RegistryEntry<Biome> biome = world.getBiome(player.getBlockPos());

        if (biome.isIn(ConventionalBiomeTags.PLAINS) || biome.isIn(ConventionalBiomeTags.FOREST)) {
            spawnPatrolLeader(world, ModEntities.CHAMPION_HOPLITE, leaderPos, distantTarget, random, patrolId);
            spawnPatrolMember(world, ModEntities.CENTAUR, 1, pos, distantTarget, random, patrolId);
            spawnPatrolMember(world, ModEntities.ELITE_HOPLITE, 2 + random.nextInt(2), pos, distantTarget, random, patrolId);
        } else {
            spawnPatrolLeader(world, ModEntities.CAMELRY, leaderPos, distantTarget, random, patrolId);
            spawnPatrolMember(world, ModEntities.AXEMAN, 3 + random.nextInt(2), pos, distantTarget, random, patrolId);
        }
    }

    private static void spawnPatrolLeader(ServerWorld world, EntityType entityType,
                                          BlockPos pos, BlockPos target, Random random, UUID patrolId) {
        ModPatrolEntity leader = createPatrolUnit(entityType, world);
        leader.setPatrolId(patrolId.toString());
        leader.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        leader.setPatrolLeader(true);
        leader.setPatrolTarget(target);
        //leader.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 1000, 0, false, false));
        world.spawnEntity(leader);
    }

    private static void spawnPatrolMember(ServerWorld world, EntityType entityType, int count,
                                          BlockPos pos, BlockPos target, Random random, UUID patrolId) {
        for (int i = 0; i < count; i++) {
            ModPatrolEntity member = createPatrolUnit(entityType, world);
            BlockPos spawnPos = getOffsetPosition(pos, random);
            member.setPatrolId(patrolId.toString());
            member.setPosition(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);
            member.setPatrolTarget(target);
            //member.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 1000, 0, false, false));
            world.spawnEntity(member);
        }
    }

    private static ModPatrolEntity createPatrolUnit(EntityType entityType, ServerWorld world) {
        return switch (entityType.getTranslationKey()) {
            case "entity.antiquebeasts.champion_hoplite" -> new ChampionHopliteEntity(ModEntities.CHAMPION_HOPLITE, world);
            case "entity.antiquebeasts.centaur" -> new CentaurEntity(ModEntities.CENTAUR, world);
            case "entity.antiquebeasts.axeman" -> new AxemanEntity(ModEntities.AXEMAN, world);
            case "entity.antiquebeasts.camelry" -> new CamelryEntity(ModEntities.CAMELRY, world);
            default -> new EliteHopliteEntity(ModEntities.ELITE_HOPLITE, world);
        };
    }

    private static BlockPos getOffsetPosition(BlockPos basePos, Random random) {
        return basePos.add(-2 + random.nextInt(5), 0, -2 + random.nextInt(5));
    }
}
