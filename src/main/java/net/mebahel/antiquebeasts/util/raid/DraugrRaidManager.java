package net.mebahel.antiquebeasts.util.raid;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class DraugrRaidManager extends PersistentState {
    private static final String RAIDS_KEY = "draugr_raids";
    private final Map<UUID, DraugrRaidTest> raids = new HashMap<>();
    private final ServerWorld world;

    public DraugrRaidManager(ServerWorld world) {
        this.world = world;
    }

    public static DraugrRaidManager fromNbt(ServerWorld world, NbtCompound nbt) {
        DraugrRaidManager manager = new DraugrRaidManager(world);
        NbtCompound raidsNbt = nbt.getCompound(RAIDS_KEY);
        for (String uuidStr : raidsNbt.getKeys()) {
            UUID uuid = UUID.fromString(uuidStr);
            DraugrRaidTest raid = DraugrRaidTest.fromNbt(raidsNbt.getCompound(uuidStr), world);
            if (raid != null) {
                manager.raids.put(uuid, raid);
            }
        }
        return manager;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound raidsNbt = new NbtCompound();
        for (Map.Entry<UUID, DraugrRaidTest> entry : this.raids.entrySet()) {
            NbtCompound raidNbt = new NbtCompound();
            entry.getValue().writeNbt(raidNbt);
            raidsNbt.put(entry.getKey().toString(), raidNbt);
        }
        nbt.put(RAIDS_KEY, raidsNbt);
        return nbt;
    }

    public DraugrRaidTest getOrCreateRaid(ServerPlayerEntity player) {
        UUID playerUuid = player.getUuid();
        DraugrRaidTest existingRaid = this.raids.get(playerUuid);

        if (existingRaid == null) {
            BlockPos pos = player.getBlockPos();
            DraugrRaidTest newRaid = new DraugrRaidTest(player, this.world);
            this.raids.put(playerUuid, newRaid);
            this.markDirty();
            return newRaid;
        }

        return existingRaid;
    }

    public DraugrRaidTest getRaidForPlayer(UUID playerUuid) {
        return this.raids.get(playerUuid);
    }

    public void removeRaid(UUID playerUuid) {
        this.raids.remove(playerUuid);
        this.markDirty();
    }

    public void tick() {
        Iterator<Map.Entry<UUID, DraugrRaidTest>> iterator = this.raids.entrySet().iterator();
        while (iterator.hasNext()) {
            DraugrRaidTest raid = iterator.next().getValue();
            if (raid.isRaidCompleted()) {
                iterator.remove();
                this.markDirty();
            }
        }
    }
}
