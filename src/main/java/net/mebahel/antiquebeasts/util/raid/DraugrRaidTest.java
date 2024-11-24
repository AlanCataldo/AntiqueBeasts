package net.mebahel.antiquebeasts.util.raid;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.custom.other.DraugrEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.*;

import static net.mebahel.antiquebeasts.util.raid.DraugrRaidHelper.findGroundPosition;

public class DraugrRaidTest {
    private ServerWorld world;
    int currentWave = 1;
    int draugrKilledInWave = 0;
    private static final int MAX_WAVES = 3;
    private static final int DRAUGR_PER_WAVE = 3;
    BlockPos centralPos;
    private PlayerEntity targetPlayer;
    float initialMaxHealth;
    public boolean raidInProgress;
    boolean waveEntitiesSpawned;
    private ServerBossBar raidBossBar;
    static final HashMap<UUID, DraugrRaid> raids = new HashMap<>();
    List<DraugrEntity> activeMobs;
    public List<UUID> raidEntityUuid;
    public UUID raidUuid;
    UUID targetPlayerUuid;
    boolean raidCompleted = false;
    public boolean raidHasBeenResume = false;
    // Constructor
    public DraugrRaidTest(PlayerEntity player, ServerWorld world) {
        this.raidUuid = UUID.randomUUID();
        this.raidEntityUuid = new ArrayList<>();
        this.world = world;
        this.targetPlayer = player;
        this.targetPlayerUuid = player.getUuid();
        this.waveEntitiesSpawned = false;
        this.activeMobs = new ArrayList<>();
        this.raidBossBar = new ServerBossBar(Text.translatable("Raid Draugr en cours..."), BossBar.Color.RED, BossBar.Style.NOTCHED_10);
        this.raidInProgress = false;
        raidBossBar.addPlayer((ServerPlayerEntity) targetPlayer);
        raidBossBar.setVisible(true);
        raidBossBar.setName(Text.translatable("Raid Draugr - Vague " + currentWave));

        System.out.println("- INITIALISATIOJN DU RAID -");
        registerEvents();
    }

    public boolean isRaidInProgress() {
        return raidInProgress;
    }
    public boolean isRaidCompleted() {
        return raidCompleted;
    }
    public void setTargetPlayer(PlayerEntity player) {
        this.targetPlayer = player;
    }

    public void startRaid() {
        System.out.println("- START RAID -");
        this.raidInProgress = true;
        spawnNextWave(targetPlayer, world);
    }

    void registerEvents() {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            if (entity instanceof DraugrEntity draugr && draugr.isPartOfRaid()) {
                this.setWorld((ServerWorld) entity.getWorld());
                activeMobs.removeIf(draugrEntity -> !draugrEntity.isAlive());

                updateRaidHealthBar();
            }
        });

        ServerTickEvents.END_SERVER_TICK.register((serverTick) -> {
            ServerWorld serverWorld = serverTick.getOverworld();

            // Vérifiez si le raid est terminé ou inactif
            if (!raidInProgress || raidCompleted) return;

            // Ajout du raid à la liste des raids en cours, si ce n'est pas déjà fait
            if (AntiqueBeasts.ongoingRaids.stream().noneMatch(raid -> raid.raidUuid.equals(this.raidUuid))) {
                AntiqueBeasts.ongoingRaids.add(this);
                System.out.println("Raid ajouté à ongoingRaids : " + raidUuid);
            }

            // Forcer une mise à jour initiale de la barre si elle est vide
            if (raidBossBar.getPlayers().isEmpty()) {
               // System.out.println("Mise à jour initiale de la barre de raid pour les joueurs.");
                updateBossBarForPlayers(serverWorld);
            }

            // Mise à jour de la proximité des joueurs et gestion de la barre de raid
            updateBossBarProximity(serverWorld);
        });
    }

    private void updateBossBarForPlayers(ServerWorld world) {
        List<ServerPlayerEntity> playersInWorld = world.getPlayers();

        for (ServerPlayerEntity player : playersInWorld) {
            if (!raidBossBar.getPlayers().contains(player)) {
                raidBossBar.addPlayer(player);
                //System.out.println("Ajout initial du joueur " + player.getName().getString() + " à la barre de boss du raid.");
            }
        }
    }

    private void updateBossBarProximity(ServerWorld world) {
        List<ServerPlayerEntity> playersInWorld = world.getPlayers();
        List<ServerPlayerEntity> playersInBossBar = new ArrayList<>(raidBossBar.getPlayers());

        for (ServerPlayerEntity player : playersInWorld) {
            boolean isWithinRange = false;

            // Vérifiez si le joueur est à portée d'une entité du raid
            for (UUID entityUuid : raidEntityUuid) {
                DraugrEntity draugr = findDraugrByUUID(world, entityUuid);
                if (draugr != null && draugr.isAlive()) {
                    double distanceSquared = player.squaredDistanceTo(draugr.getX(), draugr.getY(), draugr.getZ());
                    if (distanceSquared <= 20 * 20) {
                        isWithinRange = true; // Joueur proche d'une entité
                        break;
                    }
                }
            }

            // Ajout ou retrait du joueur
            if (isWithinRange) {
                if (!raidBossBar.getPlayers().contains(player)) {
                    raidBossBar.addPlayer(player);
                    //System.out.println("Ajout du joueur " + player.getName().getString() + " à la barre de boss du raid.");
                }
            } else {
                if (raidBossBar.getPlayers().contains(player)) {
                    raidBossBar.removePlayer(player);
                    //System.out.println("Retrait du joueur " + player.getName().getString() + " de la barre de boss du raid.");
                }
            }

            playersInBossBar.remove(player);
        }

        // Retirer les joueurs restants dans `playersInBossBar`
        for (ServerPlayerEntity playerToRemove : playersInBossBar) {
            raidBossBar.removePlayer(playerToRemove);
            //System.out.println("Retrait du joueur " + playerToRemove.getName().getString() + " qui n'est plus dans le monde.");
        }
    }

    public void updateBossBar() {
        raidBossBar.setName(Text.translatable("Raid Draugr - Vague " + currentWave));
        float totalHealth = 0.0F;
        for (int i = 0; i < this.raidEntityUuid.size(); i++) {
            DraugrEntity draugr = this.findDraugrByUUID(world, this.raidEntityUuid.get(i));
            if (draugr != null) {
                if (draugr.isAlive()) {
                    totalHealth += draugr.getHealth();
                }
            }
        }
        raidBossBar.setPercent(totalHealth / initialMaxHealth);
    }

    public void setWorld(ServerWorld world) {
        this.world = world;
    }

    public UUID getTargetPlayerUuid() {
        return targetPlayerUuid;
    }

    public DraugrEntity findDraugrByUUID(ServerWorld world, UUID uuid) {
        Entity entity = world.getEntity(uuid);

        if (entity instanceof DraugrEntity) {
            return (DraugrEntity) entity;
        } else {
            return null;
        }
    }


    private void spawnNextWave(PlayerEntity player, ServerWorld world) {
        System.out.println("- APPARITION D'UNE NOUVELLE VAGUE -");
        UUID targetPlayerUuid = player.getUuid();
        if (player.isRemoved()) {
            System.out.println("Le joueur est marqué comme 'removed'. Tentative de récupération d'une nouvelle instance du joueur...");

            // Récupérez la nouvelle instance du joueur depuis le monde
            player = world.getPlayerByUuid(targetPlayerUuid);
            if (player != null) {
                targetPlayer = player; // Mettez à jour targetPlayer avec la nouvelle instance
                System.out.println("Nouvelle instance du joueur récupérée : " + player.getName().getString());
            } else {
                System.out.println("Impossible de récupérer une nouvelle instance du joueur. Abandon de la génération de la vague.");
                return;
            }
        }
        if (currentWave > MAX_WAVES) {
            endRaid(player);
            return;
        }
        raidBossBar.setName(Text.translatable("Raid Draugr - Vague " + currentWave));

        Random random = new Random();
        double angle = random.nextDouble() * 2 * Math.PI;
        double distance = 10;
        this.centralPos = new BlockPos(
                (int) (targetPlayer.getX() + Math.cos(angle) * distance),
                (int) targetPlayer.getY(),
                (int) (targetPlayer.getZ() + Math.sin(angle) * distance)
        );

        draugrKilledInWave = 0;
        initialMaxHealth = 0;

        raidEntityUuid.clear();
        activeMobs.clear();


        for (int i = 0; i < DRAUGR_PER_WAVE; i++) {
            double xOffset = random.nextDouble() * 20 - 10;
            double zOffset = random.nextDouble() * 20 - 10;
            BlockPos spawnPos = centralPos.add((int) xOffset, 0, (int) zOffset);
            spawnPos = findGroundPosition(world, spawnPos);
            EntityType entityType = DraugrRaidHelper.chooseEntityType(random, currentWave, 1);

            DraugrEntity draugr = (DraugrEntity) entityType.create(world);
            if (draugr != null) {
                draugr.refreshPositionAndAngles(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), random.nextFloat() * 360F, 0);
                draugr.initialize(world, world.getLocalDifficulty(spawnPos), SpawnReason.EVENT, null, null);
                draugr.setPartOfRaid(true);
                //draugr.setRaidUuid(raidUuid);
                raidEntityUuid.add(draugr.getUuid());
                activeMobs.add(draugr);
                world.spawnEntity(draugr);
                draugr.setTarget(targetPlayer);

                initialMaxHealth += (float) draugr.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH);
                //System.out.println("Spawned " + entityType.getName().getString() + " " + draugr.getUuid() + " at " + spawnPos);
            } else {
                System.out.println("Failed to spawn DraugrEntity at " + spawnPos);
            }
        }

        waveEntitiesSpawned = true;
        updateRaidHealthBar();
    }

    private void updateRaidHealthBar() {
        if (!waveEntitiesSpawned || raidBossBar == null) return;

        float totalHealth = 0.0F;

        activeMobs.clear();
        for (UUID uuid : raidEntityUuid) {
            DraugrEntity draugr = findDraugrByUUID(world, uuid);
            if (draugr != null && draugr.isAlive()) {
                activeMobs.add(draugr);
                totalHealth += draugr.getHealth(); // Ajoute la santé si l'entité est vivante
            }
        }
        //System.out.println(activeMobs);
        //System.out.println(totalHealth);
        //System.out.println(currentWave);
        // Mise à jour de la barre de santé
        if (totalHealth > 0) {
            raidBossBar.setPercent(totalHealth / initialMaxHealth);
            raidBossBar.setName(Text.translatable("Raid Draugr - Vague " + currentWave));
            //System.out.println("Barre mise à jour : santé totale = " + totalHealth);
        } else {
            // Si aucune entité vivante, fin du raid ou prochaine vague
            raidBossBar.setPercent(0.0F);
            if (currentWave >= MAX_WAVES) {
                endRaid(targetPlayer);
            } else {
                currentWave++;
                waveEntitiesSpawned = false;
                spawnNextWave(targetPlayer, world);
            }
        }
    }

    public void endRaid(PlayerEntity player) {
        // Effectuer le nettoyage existant
        raidBossBar.removePlayer((ServerPlayerEntity) player);
        raidBossBar.setVisible(false);

        // Marquer le raid comme terminé
        this.raidCompleted = true;
        this.raidInProgress = false;
        this.removeRaid();
        if (AntiqueBeasts.ongoingRaids.remove(this)) {
            System.out.println("Raid supprimé de ongoingRaids : " + this.raidUuid);
        } else {
            System.out.println("Raid non trouvé dans ongoingRaids : " + this.raidUuid);
        }

        System.out.println("Raid ended for player: " + player.getName().getString());
    }



    public void saveRaid() {
        // Récupérer l'instance de PersistentRaidData
        PersistentRaidData data = PersistentRaidData.get(world);

        if (data != null) {
            // Vérifiez si un raid existe déjà avec le même raidUuid
            DraugrRaidTest existingRaid = data.getRaidByUuid(raidUuid);
            if (existingRaid != null) {
                // Si un raid existe avec le même UUID, mettez-le à jour
                if (existingRaid != this) {
                    System.out.println("[DraugrRaidTest] Un raid existant a été trouvé avec le même raidUuid : " + raidUuid);
                    System.out.println("[DraugrRaidTest] Mise à jour du raid existant.");
                }
            } else {
                // Si aucun raid n'existe avec ce raidUuid
                System.out.println("[DraugrRaidTest] Aucun raid existant trouvé avec le raidUuid. Création d'un nouveau raid.");
            }

            // Ajouter ou mettre à jour le raid dans les données persistantes
            data.addRaidByUuid(raidUuid, this);
            data.markDirty(); // Marquer les données comme modifiées pour les sauvegarder
            System.out.println("[DraugrRaidTest] Raid sauvegardé avec raidUuid : " + raidUuid);
            //System.out.println("-- DRAUGR IN RAID -- ");
            //System.out.println(activeMobs);
        } else {
            System.out.println("[DraugrRaidTest] Impossible de trouver PersistentRaidData pour sauvegarder le raid.");
        }

        // Loguer tous les raids après sauvegarde
        HashMap<UUID, DraugrRaidTest> allRaids = data.getAllRaids();
        System.out.println("- saveRaid allRaid by raidUuid - " + allRaids);
    }


    public void removeRaid() {
        System.out.println("- REMOVE RAID -");
        PersistentRaidData data = PersistentRaidData.get(world);

        if (data != null) {
            // Ajouter ou mettre à jour le raid dans les données persistantes
            data.removeRaidByRaid(this);
            data.markDirty(); // Marquer les données comme modifiées pour les sauvegarder
            System.out.println("- REMOVE RAID CONFIRME - ");
        } else {
            System.out.println("[DraugrRaidTest] Impossible de trouver PersistentRaidData pour sauvegarder le raid.");
        }
    }
    public ServerBossBar getRaidBossBar() {
        return this.raidBossBar;
    }

    public NbtCompound writeNbt(NbtCompound nbt) {
        if (raidCompleted) {
            return new NbtCompound(); // Return an empty NBT if the raid is completed
        }

        // Existing logic to write raid data to NBT
        nbt.putInt("currentWave", currentWave);
        nbt.putFloat("initialMaxHealth", initialMaxHealth);
        nbt.putInt("draugrKilledInWave", draugrKilledInWave);
        nbt.putString("raidUuid", raidUuid.toString());
        nbt.putUuid("targetPlayerUuid", targetPlayer.getUuid());
        nbt.putBoolean("raidCompleted", raidCompleted);
        nbt.putBoolean("raidInProgress", raidInProgress);
        nbt.putBoolean("waveEntitiesSpawned", waveEntitiesSpawned);

        if (centralPos != null) {
            nbt.putInt("centralPosX", centralPos.getX());
            nbt.putInt("centralPosY", centralPos.getY());
            nbt.putInt("centralPosZ", centralPos.getZ());
        }

        NbtList uuidList = new NbtList();
        for (UUID uuid : raidEntityUuid) {
            NbtCompound uuidCompound = new NbtCompound();
            uuidCompound.putUuid("UUID", uuid);
            uuidList.add(uuidCompound);
        }
        nbt.put("raidEntityUuid", uuidList); // Sauvegarder la liste des UUID

        NbtList activeMobsList = new NbtList();
        for (DraugrEntity draugr : activeMobs) {
            if (draugr != null) {
                NbtCompound draugrCompound = new NbtCompound();
                draugr.saveSelfNbt(draugrCompound); // Utilisez saveSelfNbt pour sauvegarder l'état complet de l'entité
                activeMobsList.add(draugrCompound);
            }
        }
        nbt.put("activeMobs", activeMobsList);

        return nbt;
    }
    public static DraugrRaidTest fromNbt(NbtCompound nbt, ServerWorld world) {
        UUID playerUuid = nbt.getUuid("targetPlayerUuid");
        PlayerEntity player = world.getPlayerByUuid(playerUuid);
        System.out.println("- FROM NBT LISTE JOUEUR -");
        System.out.println(world.getPlayers());

        if (player == null) {
            player = world.getPlayers().get(0);
            System.out.println("[DraugrRaid] Joueur introuvable pour UUID : " + playerUuid);
        }

        DraugrRaidTest raid = new DraugrRaidTest(player, world);
        raid.raidUuid = UUID.fromString(nbt.getString("raidUuid"));
        raid.currentWave = nbt.getInt("currentWave");
        raid.draugrKilledInWave = nbt.getInt("draugrKilledInWave");
        raid.raidInProgress = nbt.getBoolean("raidInProgress");
        raid.raidCompleted = nbt.getBoolean("raidCompleted");
        raid.initialMaxHealth = nbt.getFloat("initialMaxHealth");
        raid.waveEntitiesSpawned = nbt.getBoolean("waveEntitiesSpawned");

        if (nbt.contains("centralPosX") && nbt.contains("centralPosY") && nbt.contains("centralPosZ")) {
            raid.centralPos = new BlockPos(
                    nbt.getInt("centralPosX"),
                    nbt.getInt("centralPosY"),
                    nbt.getInt("centralPosZ")
            );
        }

        NbtList uuidList = nbt.getList("raidEntityUuid", NbtCompound.COMPOUND_TYPE);
        for (int i = 0; i < uuidList.size(); i++) {
            NbtCompound uuidCompound = uuidList.getCompound(i);
            raid.raidEntityUuid.add(uuidCompound.getUuid("UUID"));
        }

        for (UUID uuid : raid.raidEntityUuid) {
            DraugrEntity draugr = raid.findDraugrByUUID(world, uuid);
            System.out.println("- fromNbt draugr -" + draugr);
            if (draugr != null && !raid.activeMobs.contains(draugr)) {
                raid.activeMobs.add(draugr);
            }
        }

        return raid;
    }

    public ServerWorld getWorld() {
        return this.world;
    }
}