package net.mebahel.antiquebeasts.util.raid;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.custom.other.DraugrEntity;
import net.mebahel.antiquebeasts.item.TickScheduler;
import net.mebahel.antiquebeasts.util.raid.PersistentRaidData;
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
import net.minecraft.world.PersistentState;

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
    private List<DraugrEntity> activeMobs;
    List<UUID> raidEntityUuid;
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
        //saveRaid();
    }

    void registerEvents() {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            if (entity instanceof DraugrEntity draugr && draugr.isPartOfRaid()) {
                ServerWorld world = (ServerWorld) entity.getWorld();

                activeMobs.clear();
                for (UUID uuid : raidEntityUuid) {
                    DraugrEntity draugrEntity = findDraugrByUUID(world, uuid);
                    if (draugrEntity != null && !activeMobs.contains(draugrEntity)) {
                        activeMobs.add(draugrEntity);
                    }
                }

                activeMobs.removeIf(draugrEntity -> {
                    boolean shouldRemove = !draugrEntity.isAlive();
                    if (shouldRemove) {
                        System.out.println("Draugr " + draugrEntity.getUuid() + " is removed or dead. Cleaning up.");
                    }
                    return shouldRemove;
                });

                updateRaidHealthBar(world);
            }
        });
    }

    public void setWorld(ServerWorld world) {
        this.world = world;
    }

    public UUID getTargetPlayerUuid() {
        return targetPlayerUuid;
    }

    private DraugrEntity findDraugrByUUID(ServerWorld world, UUID uuid) {
        //System.out.println("JE CHERCHE");
        // Utilise getEntity pour obtenir l'entité par son UUID
        Entity entity = world.getEntity(uuid);
        //System.out.println("l'anti thé" + entity);

        // Vérifie si l'entité est une instance de DraugrEntity
        if (entity instanceof DraugrEntity) {
            //System.out.println("JE TROUVE");
            return (DraugrEntity) entity;
        } else {
            return null; // Retourne null si l'entité n'est pas un DraugrEntity
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
        updateRaidHealthBar(world);
        //saveRaid();
    }
    public void resumeRaid(PlayerEntity player, ServerWorld world) {
        raidHasBeenResume = true;
        TickScheduler tickScheduler = AntiqueBeasts.getTickScheduler();
        System.out.println("- resumeRaid resumeRaid -");
        tickScheduler.schedule(world, 0, w -> afterLoadResumeRaid(player, world));
    }
    public void afterLoadResumeRaid(PlayerEntity player, ServerWorld world) {
        this.targetPlayer = player;
        this.raidBossBar.addPlayer((ServerPlayerEntity) player);
        this.raidBossBar.setName(Text.translatable("Raid Draugr - Vague " + currentWave));
        this.raidBossBar.setVisible(true);
        this.raidInProgress = true;

        /*System.out.println("- resumeRaid targetPlayer -" + targetPlayer);
        System.out.println("- resumeRaid raidBossBar -" + raidBossBar);
        System.out.println("- resumeRaid currentWave -" + currentWave);
        System.out.println("- resumeRaid raidEntityUuid -" + raidEntityUuid);
        System.out.println("- resumeRaid activeMobs -" + activeMobs);*/

        raidEntityUuid.clear();  // Effacez les UUID précédents
        for (DraugrEntity draugr : activeMobs) {
            raidEntityUuid.add(draugr.getUuid());
            //System.out.println("- resumeRaid draugr dans activeMobs-" + draugr);
        }

        this.activeMobs.clear();

        for (UUID uuid : raidEntityUuid) {
            DraugrEntity draugr = findDraugrByUUID(world, uuid);
            System.out.println("- resumeRaid draugr -" + draugr);
            if (draugr != null && !activeMobs.contains(draugr)) {
                activeMobs.add(draugr);
            }
        }
        updateRaidHealthBar(world); // Actualisez la barre de vie*/
    }

    private void updateRaidHealthBar(ServerWorld world) {
        if (!waveEntitiesSpawned) return;
        //System.out.println("- updateRaidHealthBar player -" + world.getPlayers());
        //System.out.println("- updateRaidHealthBar targetplayer -" + targetPlayer);
        if (targetPlayer.isRemoved()) {
            System.out.println("Le joueur est marqué comme 'removed'. Tentative de récupération d'une nouvelle instance du joueur...");

            // Récupérez la nouvelle instance du joueur depuis le monde
            PlayerEntity player = world.getPlayerByUuid(targetPlayerUuid);
            if (player != null) {
                targetPlayer = player; // Mettez à jour targetPlayer avec la nouvelle instance
                System.out.println("Nouvelle instance du joueur récupérée : " + targetPlayer.getName().getString());
            } else {
                System.out.println("Impossible de récupérer une nouvelle instance du joueur. Abandon de la génération de la vague.");
                return;
            }
            this.raidBossBar = new ServerBossBar(Text.translatable("Raid Draugr en cours..."), BossBar.Color.RED, BossBar.Style.NOTCHED_10);

            raidBossBar.addPlayer((ServerPlayerEntity) targetPlayer);
            raidBossBar.setVisible(true);
            raidBossBar.setName(Text.translatable("Raid Draugr - Vague " + currentWave));
        }

        float totalHealth = 0.0F;
        for (int i = 0; i < this.raidEntityUuid.size(); i++) {
            DraugrEntity draugr = this.findDraugrByUUID(world, this.raidEntityUuid.get(i));
            if (draugr != null) {
                if (draugr.isAlive()) {
                    totalHealth += draugr.getHealth();
                }
            }
        }

        if (totalHealth > 0) {
            raidBossBar.setPercent(totalHealth / initialMaxHealth);
        } else {
            raidBossBar.setPercent(0.0F);
            System.out.println("All Draugr are dead.");

            if (currentWave >= MAX_WAVES) {
                endRaid(targetPlayer);
            } else {
                currentWave++;  // Increment the wave number
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
        // Supprimer le raid des données persistantes

        System.out.println("Raid ended for player: " + player.getName().getString());
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

        return nbt;
    }
    public static DraugrRaidTest fromNbt(NbtCompound nbt, ServerWorld world) {
        UUID playerUuid = nbt.getUuid("targetPlayerUuid");
        PlayerEntity player = world.getPlayerByUuid(playerUuid);
        System.out.println("- FROM NBT LISTE JOUEUR -");
        System.out.println(world.getPlayers());

        if (player == null) {
            System.out.println("[DraugrRaid] Joueur introuvable pour UUID : " + playerUuid);
            return null;
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

    public void saveRaid() {
        // Récupérer l'instance de PersistentRaidData
        PersistentRaidData data = PersistentRaidData.get(world);

        if (data != null) {
            // Ajouter ou mettre à jour le raid dans les données persistantes
            data.addRaid(targetPlayerUuid, this);
            data.markDirty(); // Marquer les données comme modifiées pour les sauvegarder
            System.out.println("[DraugrRaidTest] Raid sauvegardé pour le joueur : " + targetPlayerUuid);
        } else {
            System.out.println("[DraugrRaidTest] Impossible de trouver PersistentRaidData pour sauvegarder le raid.");
        }
        HashMap<UUID, DraugrRaidTest> allRaid = data.getAllRaids();
        System.out.println("- saveRaid allRaid - " + allRaid);
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
}