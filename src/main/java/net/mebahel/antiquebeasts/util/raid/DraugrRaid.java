package net.mebahel.antiquebeasts.util.raid;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
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
import net.minecraft.world.PersistentState;

import java.util.*;

import static net.mebahel.antiquebeasts.util.raid.DraugrRaidHelper.findGroundPosition;

public class DraugrRaid extends PersistentState {
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
    private final ServerBossBar raidBossBar;
    static final HashMap<UUID, DraugrRaid> raids = new HashMap<>();
    private List<DraugrEntity> activeMobs;
    List<UUID> raidEntityUuid;
    UUID raidUuid;
    boolean raidCompleted = false;
    // Constructor
    public DraugrRaid(PlayerEntity player, ServerWorld world) {
        this.raidUuid = UUID.randomUUID();
        this.raidEntityUuid = new ArrayList<>();
        this.world = world;
        this.targetPlayer = player;
        this.waveEntitiesSpawned = false;
        this.activeMobs = new ArrayList<>();
        this.raidBossBar = new ServerBossBar(Text.translatable("Raid Draugr en cours..."), BossBar.Color.RED, BossBar.Style.NOTCHED_10);

        raidBossBar.addPlayer((ServerPlayerEntity) targetPlayer);
        raidBossBar.setVisible(true);
        raidBossBar.setName(Text.translatable("Raid Draugr - Vague " + currentWave));

        //System.out.println("ACTIVE MOBS :" + activeMobs);
        registerEvents();
        // NE PAS appeler spawnNextWave ici
        this.markDirty();
    }

    public void startRaid() {
        spawnNextWave(targetPlayer, world);
        this.raidInProgress = true;
    }

    void registerEvents() {
        // Enregistre l'événement AFTER_DEATH pour gérer la mort des Draugr
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            if (entity instanceof DraugrEntity draugr && draugr.isPartOfRaid()) {
                ServerWorld world = (ServerWorld) entity.getWorld();

                // Parcourt la liste activeMobs pour trouver et supprimer l'entité morte
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

                // Met à jour la barre de santé après avoir retiré l'entité
                updateRaidHealthBar(world);
            }
        });
    }

    public void setWorld(ServerWorld world) {
        this.world = world;
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
        if (currentWave > MAX_WAVES) {
            endRaid(player);
            return;
        }
        raidBossBar.setName(Text.translatable("Raid Draugr - Vague " + currentWave));

        Random random = new Random();
        double angle = random.nextDouble() * 2 * Math.PI;
        double distance = 30 + random.nextDouble() * 20;
        this.centralPos = new BlockPos(
                (int) (targetPlayer.getX() + Math.cos(angle) * distance),
                (int) targetPlayer.getY(),
                (int) (targetPlayer.getZ() + Math.sin(angle) * distance)
        );

        draugrKilledInWave = 0;
        initialMaxHealth = 0;

        raidEntityUuid.clear();// Reset initialMaxHealth for the new wave
        //activeMobs.clear();    // Clear the list of active mobs for the new wave

        // Update the raid boss bar title with the current wave number

        // Spawn 3 Draugr entities for this wave
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
                //activeMobs.add(draugr);
                world.spawnEntity(draugr);
                draugr.setTarget(targetPlayer);

                initialMaxHealth += (float) draugr.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH);
                System.out.println("Spawned " + entityType.getName().getString() + " " + draugr.getUuid() + " at " + spawnPos);
            } else {
                System.out.println("Failed to spawn DraugrEntity at " + spawnPos);
            }
        }

        waveEntitiesSpawned = true;
        updateRaidHealthBar(world);
        this.markDirty();
    }
    public void resumeRaid(PlayerEntity player, ServerWorld world) {
        this.targetPlayer = player; // Mettre à jour le joueur cible
        raidBossBar.addPlayer((ServerPlayerEntity) player);
        raidBossBar.setName(Text.translatable("Raid Draugr - Vague " + currentWave));
        raidBossBar.setVisible(true);
        raidInProgress = true;

        activeMobs.clear();
        System.out.println("resumeRaid CURRENTWAVE : " + this.currentWave);
        System.out.println("resumeRaid RESUME RAID : LISTE UUID : " + raidEntityUuid);

        for (UUID uuid : raidEntityUuid) {
            DraugrEntity draugr = findDraugrByUUID(world, uuid);
            if (draugr != null && !activeMobs.contains(draugr)) {
                activeMobs.add(draugr);
            }
        }

        if (!activeMobs.isEmpty()) {
            for (DraugrEntity draugr : activeMobs) {
                if (draugr.isAlive()) {
                    initialMaxHealth += (float) draugr.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH);
                }
            }
        } else {
            this.endRaid(targetPlayer);
        }

        updateRaidHealthBar(world); // Actualisez la barre de vie
    }

    private void updateRaidHealthBar(ServerWorld world) {
        if (!waveEntitiesSpawned) return;
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
            if (currentWave >= MAX_WAVES) {
                endRaid(targetPlayer);
            } else {
                currentWave++;  // Increment the wave number
                waveEntitiesSpawned = false;
                spawnNextWave(targetPlayer, world);
            }
        }
    }

    void endRaid(PlayerEntity player) {
        // Perform the existing cleanup
        raidBossBar.removePlayer((ServerPlayerEntity) player);
        raidBossBar.setVisible(false);
        raids.remove(player.getUuid()); // Remove from active raids map
        this.raidCompleted = true; // Mark as completed

        this.markDirty(); // Mark the state as dirty so it will be saved

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



        // Ajouter les UUID des entités dans un NbtList
        NbtList uuidList = new NbtList();
        for (UUID uuid : raidEntityUuid) {
            NbtCompound uuidCompound = new NbtCompound();
            uuidCompound.putUuid("UUID", uuid);
            uuidList.add(uuidCompound);
        }
        nbt.put("raidEntityUuid", uuidList); // Sauvegarder la liste des UUID

        return nbt;
    }

    public UUID getRaidUuid() {
        return this.raidUuid;
    }
    // Méthode pour restaurer l'état depuis NBT
    public static DraugrRaid fromNbt(NbtCompound nbt, ServerWorld world) {
        UUID playerUuid = nbt.getUuid("targetPlayerUuid");
        PlayerEntity player = world.getPlayerByUuid(playerUuid);

        if (player == null) {
            return null;
        }

        DraugrRaid raid = new DraugrRaid(player, world);
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

        // Restaurer les UUID des entités
        NbtList uuidList = nbt.getList("raidEntityUuid", NbtCompound.COMPOUND_TYPE);
        for (int i = 0; i < uuidList.size(); i++) {
            NbtCompound uuidCompound = uuidList.getCompound(i);
            raid.raidEntityUuid.add(uuidCompound.getUuid("UUID"));
        }

        return raid;
    }

    public boolean isRaidCompleted() {
        return raidCompleted;
    }

    public void pauseRaid() {
        raidBossBar.setVisible(false); // Cache la barre de boss
        raidInProgress = false; // Marque le raid comme en pause
        // Vous pouvez également suspendre les comportements des entités si nécessaire
    }
}