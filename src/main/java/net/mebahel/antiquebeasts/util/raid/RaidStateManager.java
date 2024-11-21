package net.mebahel.antiquebeasts.util.raid;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentStateManager;

public class RaidStateManager {
    public static RaidState getRaidState(ServerWorld world) {
        PersistentStateManager stateManager = world.getPersistentStateManager();
        return stateManager.getOrCreate(RaidState::fromNbt, RaidState::new, "draugr_raid_state");
    }
}
