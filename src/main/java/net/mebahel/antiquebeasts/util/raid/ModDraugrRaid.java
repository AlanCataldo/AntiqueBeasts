package net.mebahel.antiquebeasts.util.raid;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.ModEntities;
import net.mebahel.antiquebeasts.entity.custom.other.DraugrEntity;
import net.mebahel.antiquebeasts.item.TickScheduler;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import static net.mebahel.antiquebeasts.AntiqueBeasts.worldDifficultyLevels;

public class ModDraugrRaid {
    private static int NUMBER_OF_WAVES = 3;
    private final PlayerEntity targetPlayer;
    private final ServerWorld world;
    private List<DraugrEntity> activeMobs = new CopyOnWriteArrayList<>();
    private int currentWave;
    private boolean raidInProgress;
    private boolean waveEntitiesSpawned;
    private final ServerBossBar raidBossBar;
    private float initialMaxHealth;
    private final TickScheduler tickScheduler;
    private BlockPos centralPos;
    private BlockPos chestPos;
    int difficultyLevel;
    private boolean rewardChestSpawned;
    private List<UUID> activeMobUUIDs = new ArrayList<>();
    private static List<ModDraugrRaid> ACTIVE_RAIDS = new ArrayList<>();

    public static List<ModDraugrRaid> getActiveRaids() {
        return Collections.unmodifiableList(ACTIVE_RAIDS);
    }

    public ModDraugrRaid(PlayerEntity player, ServerWorld world) {
        this.targetPlayer = player;
        this.world = world;
        this.activeMobs = new ArrayList<>();
        this.currentWave = 0;
        this.raidInProgress = false;
        this.waveEntitiesSpawned = false;
        this.raidBossBar = new ServerBossBar(Text.translatable("Raid Draugr en cours..."), BossBar.Color.RED, BossBar.Style.NOTCHED_10);
        this.tickScheduler = AntiqueBeasts.getTickScheduler();
        this.rewardChestSpawned = false;

        // Charger l'état du raid depuis le monde
        RaidState raidState = RaidStateManager.getRaidState(world);
        this.raidInProgress = raidState.isRaidInProgress();
        this.currentWave = raidState.getCurrentWave();
        this.centralPos = raidState.getCentralPos();

        // Si le raid est en cours, restaurez la barre de boss, le nom et les entités
        if (raidInProgress) {
            raidBossBar.addPlayer((ServerPlayerEntity) targetPlayer);
            raidBossBar.setVisible(true);
            raidBossBar.setName(Text.translatable("Raid Draugr - Vague " + currentWave)); // Affichez le nom correct de la vague
            // Si nécessaire, restaurer les entités de la vague en cours
            if (!activeMobs.isEmpty()) {
                waveEntitiesSpawned = true;
            } else {
                // Si aucune entité active, redémarrez la vague en cours
                spawnWaveMobs(world);
            }
        }

        // Événements pour gérer la reconnexion du joueur
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            PlayerEntity playerEntity = handler.getPlayer();
            if (playerEntity.getUuid().equals(targetPlayer.getUuid()) && raidInProgress) {
                // Réassocier la barre de boss au joueur
                raidBossBar.addPlayer((ServerPlayerEntity) playerEntity);
                raidBossBar.setVisible(true);
                raidBossBar.setName(Text.translatable("Raid Draugr - Vague " + currentWave));
                System.out.println("Restored raid boss bar for player " + playerEntity.getName().getString());

                // Vérifiez l'état des entités actives

                // Si les entités sont marquées comme "UNLOADED_TO_CHUNK", il faut les surveiller
                activeMobs.forEach(draugr -> {
                    if (draugr.isRemoved()) {
                        System.out.println("[DraugrRaid] L'entité " + draugr.getUuid() + " est actuellement déchargée. Surveillez jusqu'à ce qu'elle soit active.");
                    } else {
                        // Entité active, on peut la réintégrer
                        draugr.setTarget(targetPlayer); // S'assurer que la cible est correcte
                        System.out.println("[DraugrRaid] L'entité " + draugr.getUuid() + " est active et réintégrée dans le raid.");
                    }
                });

                // Ajoutez un TickEvent pour surveiller périodiquement les entités "UNLOADED_TO_CHUNK"
            }
        });

        ServerTickEvents.END_WORLD_TICK.register(serverWorld -> {
            System.out.println("END_WORLD_TICK");
            if (serverWorld == world) {
                activeMobs.forEach(draugr -> {
                    // Vérifiez si l'entité était déchargée et si elle est maintenant active
                    if (draugr.isRemoved() && draugr.getRemovalReason() == Entity.RemovalReason.UNLOADED_TO_CHUNK) {
                        if (!draugr.isRemoved()) { // L'entité est maintenant active
                            draugr.setTarget(targetPlayer); // Assurez-vous que la cible est correcte
                            System.out.println("[DraugrRaid] L'entité " + draugr.getUuid() + " est maintenant active et a été réintégrée.");

                            // Si l'entité n'est pas encore dans la barre de boss, ajoutez-la
                            if (!raidBossBar.getPlayers().contains(targetPlayer)) {
                                raidBossBar.addPlayer((ServerPlayerEntity) targetPlayer);
                            }
                        }
                    }
                });

                // Nettoyage des entités mortes ou supprimées
                activeMobs.removeIf(draugr -> {
                    boolean shouldRemove = draugr.isRemoved() || !draugr.isAlive();
                    if (shouldRemove) {
                        System.out.println("[DraugrRaid] Draugr " + draugr.getUuid() + " est supprimé ou mort. Nettoyage.");
                    }
                    return shouldRemove;
                });

                updateBossBar();
            }
        });


        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            PlayerEntity playerEntity = handler.getPlayer();
            if (playerEntity.getUuid().equals(targetPlayer.getUuid())) {
                raidBossBar.removePlayer((ServerPlayerEntity) playerEntity);
                System.out.println("Removed player " + playerEntity.getName().getString() + " from raid boss bar on disconnect");
            }
        });

        // Register a death listener for Draugr entities
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            if (entity instanceof DraugrEntity) {
                DraugrEntity draugr = (DraugrEntity) entity;
                if (activeMobs.contains(draugr)) {
                    System.out.println("Draugr " + draugr.getUuid() + " has died. Removing from activeMobs.");
                    activeMobs.remove(draugr);
                    updateBossBar();
                }
            }
        });

        // Clean up dead Draugr every tick
        ServerTickEvents.END_WORLD_TICK.register(serverWorld -> {
            if (serverWorld == this.world) {
                activeMobs.removeIf(draugr -> {
                    boolean shouldRemove = draugr.isRemoved() || !draugr.isAlive();
                    if (shouldRemove) {
                        System.out.println("Draugr " + draugr.getUuid() + " is removed or dead. Cleaning up.");
                    }
                    return shouldRemove;
                });
                updateBossBar();
            }
        });
    }

    public void startRaid() {
        System.out.println("startRaid() called.");
        if (raidInProgress) return;

        raidInProgress = true;
        currentWave = 0;
        raidBossBar.addPlayer((ServerPlayerEntity) targetPlayer);
        raidBossBar.setVisible(true);
        this.difficultyLevel = worldDifficultyLevels.getOrDefault(world, 1);
        if (this.difficultyLevel == 2)
            NUMBER_OF_WAVES = 4;

        RaidState raidState = RaidStateManager.getRaidState(world);
        raidState.setRaidInProgress(true);
        raidState.setCurrentWave(currentWave);
        raidState.setCentralPos(centralPos);

        spawnNextWave();
    }

    private void spawnNextWave() {
        System.out.println("spawnNextWave() called. Current wave: " + currentWave);
        if (currentWave >= NUMBER_OF_WAVES) {
            endRaid();
            return;
        }

        currentWave++;
        waveEntitiesSpawned = false;
        System.out.println("Wave " + currentWave + " of the Draugr raid for " + targetPlayer.getName().getString());

        raidBossBar.setName(Text.translatable("Raid Draugr - Vague " + currentWave));

        Random random = new Random();
        double angle = random.nextDouble() * 2 * Math.PI;
        double distance = 30 + random.nextDouble() * 20;
        this.centralPos = new BlockPos((int) (targetPlayer.getX() + Math.cos(angle) * distance),
                (int) targetPlayer.getY(),
                (int) (targetPlayer.getZ() + Math.sin(angle) * distance));

        tickScheduler.schedule(world, 5, this::spawnLightning);
        tickScheduler.schedule(world, 20, this::spawnWaveMobs);
    }

    private void spawnLightning(ServerWorld world) {
        for (int i = 0; i < 3; i++) {
            LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, world);
            lightning.setPos(centralPos.getX(), centralPos.getY(), centralPos.getZ());
            world.spawnEntity(lightning);
        }
    }

    private void spawnWaveMobs(ServerWorld world) {
        activeMobs.clear();
        activeMobUUIDs.clear();
        Random random = new Random();
        int mobsToSpawn = 1 + currentWave * 2 + difficultyLevel * 2;
        initialMaxHealth = 0.0F;

        for (int i = 0; i < mobsToSpawn; i++) {
            double xOffset = random.nextDouble() * 20 - 10;
            double zOffset = random.nextDouble() * 20 - 10;
            BlockPos spawnPos = centralPos.add((int) xOffset, 0, (int) zOffset);
            spawnPos = findGroundPosition(world, spawnPos);
            EntityType entityType = chooseEntityType(random);

            DraugrEntity draugr = (DraugrEntity) entityType.create(world);
            if (draugr != null) {
                draugr.refreshPositionAndAngles(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), random.nextFloat() * 360F, 0);
                draugr.initialize(world, world.getLocalDifficulty(spawnPos), SpawnReason.EVENT, null, null);
                draugr.getDataTracker().set(DraugrEntity.IS_PART_OF_RAID, true);
                activeMobs.add(draugr);
                world.spawnEntity(draugr);
                draugr.setTarget(targetPlayer);
                activeMobUUIDs.add(draugr.getUuid());

                System.out.println("Spawned " + entityType.getName().getString() + " " + draugr.getUuid() + " at " + spawnPos);
                initialMaxHealth += (float) draugr.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH);
            } else {
                System.out.println("Failed to spawn DraugrEntity at " + spawnPos);
            }
        }
        RaidState raidState = RaidStateManager.getRaidState(world);
        raidState.setActiveMobUUIDs(new ArrayList<>(activeMobUUIDs));
        raidState.markDirty(); // Marquer l'état comme "sale" pour assurer sa sauvegarde
        System.out.println("[DraugrRaid] RaidState mis à jour avec les nouveaux UUID des Draugr.");
        waveEntitiesSpawned = true;
        updateBossBar();
    }

    // Fonction pour choisir une entité de manière pondérée
    private EntityType chooseEntityType(Random random) {
        int draugrWeight = Math.max(16 - currentWave, 1);
        int skeletonWarriorWeight = Math.max(6 - currentWave, 1);
        int draugrArcherWeight = Math.max(4 - currentWave, 1);
        int draugrWightWeight = Math.min(3 + currentWave, 10);
        int draugrScourgeWeight = Math.min(1 + currentWave, 10);

        if (difficultyLevel == 1) {
            draugrScourgeWeight = 0;
        }

        int totalWeight = draugrWeight + skeletonWarriorWeight + draugrArcherWeight + draugrWightWeight + draugrScourgeWeight;
        int choice = random.nextInt(totalWeight);

        if (choice < draugrWeight) {
            return ModEntities.DRAUGR;
        } else if (choice < draugrWeight + skeletonWarriorWeight) {
            return ModEntities.SKELETON_WARRIOR;
        } else if (choice < draugrWeight + skeletonWarriorWeight + draugrArcherWeight) {
            return ModEntities.DRAUGR_ARCHER;
        } else if (choice < draugrWeight + skeletonWarriorWeight + draugrArcherWeight + draugrWightWeight) {
            return ModEntities.DRAUGR_WIGHT;
        } else {
            return ModEntities.DRAUGR_SCOURGE;
        }
    }

    private void updateBossBar() {
        if (!waveEntitiesSpawned) return;

        float totalHealth = 0.0F;
        for (DraugrEntity draugr : activeMobs) {
            if (draugr.isAlive()) {
                totalHealth += draugr.getHealth();
            }
        }

        if (totalHealth > 0) {
            raidBossBar.setPercent(totalHealth / initialMaxHealth);
        } else {
            raidBossBar.setPercent(0.0F);

            if (currentWave >= NUMBER_OF_WAVES) {
                endRaid();
                if (!rewardChestSpawned) {
                    spawnRewardChest();
                    rewardChestSpawned = true;
                }
            } else {
                waveEntitiesSpawned = false;
                spawnNextWave();
            }
        }
    }

    private static BlockPos findGroundPosition(ServerWorld world, BlockPos pos) {
        return world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos);
    }

    private void endRaid() {
        raidInProgress = false;
        System.out.println("The Draugr raid has ended!");
        raidBossBar.setVisible(false);
        raidBossBar.clearPlayers();

        RaidState raidState = RaidStateManager.getRaidState(world);
        raidState.setRaidInProgress(false);
        raidState.setCurrentWave(0);
        raidState.setCentralPos(BlockPos.ORIGIN);
    }

    private void enRaidSound(ServerWorld world) {
        world.playSound(null, chestPos, ModSounds.WINNING_RAID_1, targetPlayer.getSoundCategory(), 1.0F, 1.0F);
    }

    private void spawnRewardChest() {
        if (!world.isClient) {
            BlockPos playerPos = targetPlayer.getBlockPos();
            chestPos = playerPos.add(targetPlayer.getHorizontalFacing().getVector());

            while (world.isAir(chestPos.down()) && chestPos.getY() > 0) {
                chestPos = chestPos.down();
            }

            world.setBlockState(chestPos, Blocks.CHEST.getDefaultState());
            BlockEntity blockEntity = world.getBlockEntity(chestPos);
            if (blockEntity instanceof ChestBlockEntity chestBlockEntity) {
                chestBlockEntity.setLootTable(new Identifier("antiquebeasts", "chests/egyptian/egyptian_caravan"), world.getRandom().nextLong());
            }

            System.out.println("A reward chest has spawned at " + chestPos);
            tickScheduler.schedule(world, 15, this::enRaidSound);
        }
    }
}