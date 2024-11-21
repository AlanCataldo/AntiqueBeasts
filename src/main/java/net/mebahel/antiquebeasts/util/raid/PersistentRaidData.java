package net.mebahel.antiquebeasts.util.raid;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import java.util.HashMap;
import java.util.UUID;

public class PersistentRaidData extends PersistentState {
    private final HashMap<UUID, DraugrRaidTest> raids = new HashMap<>();
    private final ServerWorld world;

    public PersistentRaidData(ServerWorld world) {
        this.world = world;
    }

    public void addRaid(UUID playerUuid, DraugrRaidTest raid) {
        raids.put(playerUuid, raid);
        markDirty(); // Marquer les données comme "dirty" pour forcer la sauvegarde
    }

    public DraugrRaidTest getRaid(UUID playerUuid) {
        return raids.get(playerUuid);
    }
    public DraugrRaidTest getRaidByRaid(DraugrRaidTest raid) {
        return raids.get(raid);
    }

    public HashMap<UUID, DraugrRaidTest> getAllRaids() {
        return raids;
    }

    public void removeRaid(UUID playerUuid) {
        // Supprimer le raid de la carte des raids
        if (raids.containsKey(playerUuid)) {
            raids.remove(playerUuid);
            System.out.println("[PersistentRaidData] Raid supprimé pour le joueur : " + playerUuid);
        }
    }
    public void removeRaidByRaid(DraugrRaidTest raid) {
        raids.remove(raid);
        markDirty();
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound raidsNbt = new NbtCompound();
        for (UUID uuid : raids.keySet()) {
            DraugrRaidTest raid = raids.get(uuid);
            NbtCompound raidNbt = new NbtCompound();
            raid.writeNbt(raidNbt);
            raidsNbt.put(uuid.toString(), raidNbt);
        }
        nbt.put("Raids", raidsNbt);
        return nbt;
    }

    public static PersistentRaidData fromNbt(NbtCompound nbt, ServerWorld world) {
        PersistentRaidData data = new PersistentRaidData(world);
        NbtCompound raidsNbt = nbt.getCompound("Raids");
        for (String uuidStr : raidsNbt.getKeys()) {
            System.out.println("- PersistentRaidData fromNbt - " + raidsNbt.getKeys());
            UUID uuid = UUID.fromString(uuidStr);
            DraugrRaidTest raid = DraugrRaidTest.fromNbt(raidsNbt.getCompound(uuidStr), world);
            if (raid != null) {
                data.raids.put(uuid, raid);
            }
        }
        return data;
    }

    public static PersistentRaidData get(ServerWorld world) {
        return world.getPersistentStateManager().getOrCreate(
                nbt -> fromNbt(nbt, world),
                () -> new PersistentRaidData(world),
                "draugr_raids"
        );
    }
}
