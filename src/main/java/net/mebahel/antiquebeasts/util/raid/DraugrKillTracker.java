package net.mebahel.antiquebeasts.util.raid;

import java.util.HashMap;
import net.mebahel.antiquebeasts.entity.custom.other.DraugrEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class DraugrKillTracker {
    private static final HashMap<PlayerEntity, Integer> killCount = new HashMap<>();
    private static final int KILL_THRESHOLD = 3;

    public static void incrementKillCount(PlayerEntity player, LivingEntity killedEntity) {
        if (killedEntity instanceof DraugrEntity draugr && !draugr.isPartOfRaid()) {
            int currentKillCount = killCount.getOrDefault(player, 0) + 1;
            killCount.put(player, currentKillCount);
            System.out.println("Kills for " + player.getName().getString() + ": " + currentKillCount);

            if (currentKillCount == (int) (KILL_THRESHOLD * 0.9)) {
                player.sendMessage(Text.literal("You've awoken something ...")
                        .styled(style -> style.withColor(Formatting.GOLD)), false);
            }

            if (currentKillCount >= KILL_THRESHOLD) {
                if (player.getWorld() instanceof ServerWorld serverWorld) {
                    PersistentRaidData raidData = PersistentRaidData.get(serverWorld);

                    // Vérifie si un raid est déjà en cours pour n'importe quel joueur
                    boolean anyRaidInProgress = raidData.getAllRaids().values().stream()
                            .anyMatch(DraugrRaidTest::isRaidInProgress);

                    if (anyRaidInProgress) {
                        System.out.println("A raid is already in progress for another player.");
                        player.sendMessage(Text.literal("A raid is already in progress! Please wait for it to finish.")
                                .styled(style -> style.withColor(Formatting.RED)), false);
                        return; // Ne démarre pas un nouveau raid
                    }

                    // Pas de raid en cours, démarre un nouveau raid
                    System.out.println("KILL COUNT ATTEINT.");
                    DraugrRaidTest raid = new DraugrRaidTest(player, serverWorld);
                    raid.startRaid();
                    raid.saveRaid();
                }
                killCount.put(player, 0); // Réinitialise le compteur
            }
        }
    }
}
