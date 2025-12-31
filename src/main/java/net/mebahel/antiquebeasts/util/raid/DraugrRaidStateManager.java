package net.mebahel.antiquebeasts.util.raid;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentStateManager;

public class DraugrRaidStateManager {
    public static DraugrRaidState getRaidState(ServerWorld world) {
        PersistentStateManager stateManager = world.getPersistentStateManager();
        return stateManager.getOrCreate(DraugrRaidState::fromNbt, DraugrRaidState::new, "draugr_raid_state");
    }
}
