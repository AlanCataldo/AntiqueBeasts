package net.mebahel.antiquebeasts.world.gen;

import net.mebahel.antiquebeasts.AntiqueBeasts;

public class ModWorldGen {
    public static void generateWorldGen() {
        ModEntitySpawn.addEntitySpawn();
        AntiqueBeasts.LOGGER.info("[AntiqueBeasts] Registering entities spawn for " + AntiqueBeasts.MOD_ID + ".");
    }
}
