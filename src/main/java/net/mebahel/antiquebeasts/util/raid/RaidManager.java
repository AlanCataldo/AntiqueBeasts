package net.mebahel.antiquebeasts.util.raid;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.custom.other.DraugrEntity;
import net.mebahel.antiquebeasts.item.TickScheduler;
import net.mebahel.antiquebeasts.mixin.PersistentStateManagerAccessor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class RaidManager {
    public static void registerWorldLoadEvents() {
        // Événement de chargement du monde
            // Événement de chargement du monde
            ServerWorldEvents.LOAD.register((server, world) -> {

            });

            // Événement de connexion du joueur

    }
    public static void registerEvents() {
        // Enregistrez l'événement AFTER_DEATH
        System.out.println("- REGISTER AFTERDEATH -");
        ServerLivingEntityEvents.AFTER_DEATH.register((LivingEntity entity, DamageSource source) -> {

            if (entity instanceof DraugrEntity) {
                // Vérifiez si l'attaquant est un joueur
                if (source.getAttacker() instanceof PlayerEntity player) {
                    DraugrKillTracker.incrementKillCount(player, entity);
                }
            }
        });
    }
}

/*public class RaidManager {
    private static final int KILL_THRESHOLD = 3;  // Seuil de kills pour démarrer un raid
    private static final HashMap<UUID, Integer> playerKillCounts = new HashMap<>();  // Compteur de kills par joueur
    private static final HashMap<UUID, DraugrRaid> activeRaids = new HashMap<>();  // Raids actifs
    private static boolean hasOverworldEventRegistered = false;

    public static void registerEvents() {
        // Enregistre l'événement pour suivre les morts des Draugr
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            if (entity instanceof DraugrEntity && source.getAttacker() instanceof ServerPlayerEntity && !((DraugrEntity) entity).isPartOfRaid()) {
                ServerPlayerEntity player = (ServerPlayerEntity) source.getAttacker();
                UUID playerUUID = player.getUuid();

                // Incrémente le compteur de kills pour le joueur
                playerKillCounts.put(playerUUID, playerKillCounts.getOrDefault(playerUUID, 0) + 1);
                System.out.println("Player " + player.getName().getString() + " has killed a Draugr. Kill count: " + playerKillCounts.get(playerUUID));
                System.out.println("[DraugrRaid] Liste des PersistentState lors de la connexion du joueur " + player.getName().getString() + ":");
                ServerWorld world = (ServerWorld) entity.getWorld();
                PersistentStateManager stateManager = world.getPersistentStateManager();
                PersistentStateManagerAccessor accessor = (PersistentStateManagerAccessor) stateManager;
                Map<String, PersistentState> loadedStates = accessor.getLoadedStates();


                // Vérifie si le joueur a atteint le seuil de kills
                if (playerKillCounts.get(playerUUID) >= KILL_THRESHOLD) {
                    playerKillCounts.put(playerUUID, 0);  // Réinitialise le compteur

                    startNewRaid(player, (ServerWorld) player.getWorld());  // Démarre un raid pour le joueur
                }
            }
        });
    }

    // Méthode pour démarrer un nouveau raid pour un joueur
    private static void startNewRaid(ServerPlayerEntity player, ServerWorld world) {
        PersistentStateManager stateManager = world.getPersistentStateManager();
        PersistentStateManagerAccessor accessor = (PersistentStateManagerAccessor) stateManager;
        Map<String, PersistentState> loadedStates = accessor.getLoadedStates();

        // Vérifiez si un état de raid existe déjà pour ce joueur
        String raidKey = "draugr_raid";
        DraugrRaid existingRaid = (DraugrRaid) loadedStates.get(raidKey);

        if (existingRaid != null) {
            if (!existingRaid.isRaidCompleted()) {
                // Si le raid existant n'est pas terminé, restaurez-le
                System.out.println("[DraugrRaid] Raid existant trouvé pour le joueur : " + player.getName().getString());
                existingRaid.setWorld(world);
                existingRaid.resumeRaid(player, world);
                return;
            } else {
                // Si le raid existant est terminé, archivez-le avec une nouvelle clé
                String completedRaidKey = "completed_draugr_raid_" + existingRaid.getRaidUuid();
                stateManager.set(completedRaidKey, existingRaid);
                System.out.println("[DraugrRaid] Raid terminé archivé avec la clé : " + completedRaidKey);
            }
        }

        // Créer un nouveau raid pour le joueur
        DraugrRaid newRaid = new DraugrRaid(player, world);
        newRaid.startRaid();
        newRaid.markDirty();
        stateManager.set(raidKey, newRaid); // Enregistrer le nouveau raid
        //System.out.println("[DraugrRaid] Nouveau raid créé pour le joueur : " + player.getName().getString());

        // Marquer l'état comme modifié et sauvegarder

        stateManager.save();
        loadedStates = accessor.getLoadedStates();
        loadedStates.forEach((key, state) -> {
            //System.out.println(" - Clé : " + key + ", État : " + state);
        });
    }


    private static ServerWorld lastOverworld = null; // Variable pour suivre le dernier Overworld

    public static void registerWorldLoadEvents() {
        ServerWorldEvents.LOAD.register((server, world) -> {
            //System.out.println("LOAD " + world.getRegistryKey().getValue());

            // Vérifiez si le monde est l'Overworld
            if (Objects.equals(world.getRegistryKey().getValue().toString(), "minecraft:overworld")) {
                lastOverworld = world; // Mettez à jour la variable avec le dernier Overworld
            }

            // Exécutez la logique uniquement si c'est le dernier Overworld chargé
            server.execute(() -> {
                if (lastOverworld == world) {
                    //System.out.println("Dernier Overworld détecté, enregistrement des événements.");

                    ServerPlayConnectionEvents.JOIN.register((handler, sender, server2) -> {
                        ServerPlayerEntity player = handler.getPlayer();
                        PersistentStateManager stateManager = world.getPersistentStateManager();
                        TickScheduler tickScheduler = AntiqueBeasts.getTickScheduler();

                        tickScheduler.schedule(world, 0, w -> {
                            //System.out.println("JE SCHEDULE LE RESTORE");
                            restoreRaidForPlayer(player, stateManager, world);
                        });
                    });
                }
            });
        });
    }
    public static void registerPlayerConnectionEvents() {

    }
    private static void restoreRaidForPlayer(ServerPlayerEntity player, PersistentStateManager stateManager, ServerWorld world) {
        PersistentStateManagerAccessor accessor = (PersistentStateManagerAccessor) stateManager;
        Map<String, PersistentState> loadedStates = accessor.getLoadedStates();

        String raidKey = "draugr_raid";
        Object existingRaid = loadedStates.get(raidKey);
        //System.out.println("restoreRaidForPlayer LOADEDSTATE : " + existingRaid);
        //System.out.println("restoreRaidForPlayer EXISTINGRAID : " + loadedStates);
        // Charger ou créer un nouveau raid basé sur l'UUID du joueur
        if (existingRaid != null) {
            DraugrRaid raid = stateManager.getOrCreate(
                    nbt -> DraugrRaid.fromNbt(nbt, world),
                    () -> new DraugrRaid(player, world),
                    "draugr_raid"
            );
            if (raid == null || !raid.raidInProgress || raid.isRaidCompleted()) {
                raid.endRaid(player);
                //System.out.println("[DraugrRaid] Le raid est terminé pour le joueur : " + player.getName().getString());
                // Supprimez le raid de l'état persistant si terminé
                stateManager.set("draugr_raid", null);
                return;
            }
            //System.out.println("[restoreRaidForPlayer] Tentative de getOrCreate : " + raid);
            raid.setWorld(world);
            raid.resumeRaid(player, world);
            //System.out.println("[DraugrRaid] Raid repris pour le joueur : " + player.getName().getString());
        }
    }
    public static void registerWorldUnloadEvents() {
        ServerWorldEvents.UNLOAD.register((server, world) -> {
            //System.out.println("[RaidManager] Monde en cours de déchargement, sauvegarde des états persistants.");
            PersistentStateManager stateManager = world.getPersistentStateManager();
            PersistentStateManagerAccessor accessor = (PersistentStateManagerAccessor) stateManager;
            Map<String, PersistentState> loadedStates = accessor.getLoadedStates();

            String raidKey = "draugr_raid";
            Object existingRaid = loadedStates.get(raidKey);
            System.out.println("restoreRaidForPlayer LOADEDSTATE : " + existingRaid);
            System.out.println("restoreRaidForPlayer EXISTINGRAID : " + loadedStates);
            stateManager.save(); // Sauvegarde explicite avant la fermeture
        });
    }
}*/