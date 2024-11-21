package net.mebahel.antiquebeasts.util.raid;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class RaidState extends PersistentState {
    private boolean raidInProgress;
    private int currentWave;
    private BlockPos centralPos;
    private List<UUID> activeMobUUIDs;
    private UUID targetPlayerUUID;  // Ajouter le UUID du joueur cible

    public RaidState() {
        // Initialisation par défaut
        this.raidInProgress = false;
        this.currentWave = 0;
        this.centralPos = BlockPos.ORIGIN;
        this.activeMobUUIDs = new ArrayList<>();
        this.targetPlayerUUID = null;  // Initialisation du UUID du joueur cible
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putBoolean("RaidInProgress", raidInProgress);
        nbt.putInt("CurrentWave", currentWave);
        nbt.putLong("CentralPos", centralPos.asLong());

        // Sauvegarder les UUID des mobs
        NbtList mobUUIDList = new NbtList();
        for (UUID uuid : activeMobUUIDs) {
            mobUUIDList.add(NbtString.of(uuid.toString()));
        }
        nbt.put("ActiveMobUUIDs", mobUUIDList);

        // Sauvegarder l'UUID du joueur cible
        if (targetPlayerUUID != null) {
            nbt.putString("TargetPlayerUUID", targetPlayerUUID.toString());
        }

        return nbt;
    }

    public static RaidState fromNbt(NbtCompound nbt) {
        RaidState state = new RaidState();
        state.raidInProgress = nbt.getBoolean("RaidInProgress");
        state.currentWave = nbt.getInt("CurrentWave");
        state.centralPos = BlockPos.fromLong(nbt.getLong("CentralPos"));

        // Charger les UUID des mobs
        NbtList mobUUIDList = nbt.getList("ActiveMobUUIDs", 8);
        state.activeMobUUIDs = mobUUIDList.stream()
                .map(nbtElement -> UUID.fromString(nbtElement.asString()))
                .collect(Collectors.toList());

        // Charger l'UUID du joueur cible
        if (nbt.contains("TargetPlayerUUID")) {
            state.targetPlayerUUID = UUID.fromString(nbt.getString("TargetPlayerUUID"));
        }

        return state;
    }

    // Getters et setters pour l'UUID du joueur cible
    public UUID getTargetPlayerUUID() {
        return targetPlayerUUID;
    }

    public void setTargetPlayerUUID(UUID targetPlayerUUID) {
        this.targetPlayerUUID = targetPlayerUUID;
    }

    // Getters et setters pour l'état du raid
    public boolean isRaidInProgress() {
        return raidInProgress;
    }

    public void setRaidInProgress(boolean raidInProgress) {
        this.raidInProgress = raidInProgress;
    }

    public int getCurrentWave() {
        return currentWave;
    }

    public void setCurrentWave(int currentWave) {
        this.currentWave = currentWave;
    }

    public BlockPos getCentralPos() {
        return centralPos;
    }

    public void setCentralPos(BlockPos centralPos) {
        this.centralPos = centralPos;
    }

    public List<UUID> getActiveMobUUIDs() {
        return activeMobUUIDs;
    }

    public void setActiveMobUUIDs(List<UUID> activeMobUUIDs) {
        this.activeMobUUIDs = activeMobUUIDs;
    }
}

