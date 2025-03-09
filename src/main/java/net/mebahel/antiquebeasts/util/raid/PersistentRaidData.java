package net.mebahel.antiquebeasts.util.raid;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
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
    public void addRaidByUuid(UUID raidUuid, DraugrRaidTest raid) {
        raids.put(raidUuid, raid); // Sauvegarde le raid en utilisant raidUuid comme clé
        this.markDirty();
    }
    public DraugrRaidTest getRaidByUuid(UUID raidUuid) {
        return raids.get(raidUuid); // Renvoie le raid correspondant au raidUuid
    }

    public DraugrRaidTest getRaid(UUID playerUuid) {
        return raids.get(playerUuid);
    }
    public DraugrRaidTest getRaidByWorld(ServerWorld world) {
        return raids.get(world);
    }

    public HashMap<UUID, DraugrRaidTest> getAllRaids() {
        return this.raids;
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
            UUID uuid = UUID.fromString(uuidStr);
            NbtCompound raidCompound = raidsNbt.getCompound(uuidStr);
            boolean raidAlreadyExists = AntiqueBeasts.ongoingRaids.stream()
                    .anyMatch(raid -> raid.raidUuid.equals(uuid));

            if (raidAlreadyExists) {
                System.out.println("Raid avec UUID " + uuid + " déjà présent dans ongoingRaids. Ignoré.");
                continue; // Ne recrée pas ce raid
            }

            // Vérifiez si "activeMobs" contient des entités et loggez leur état
            if (raidCompound.contains("activeMobs")) {
                NbtList activeMobsList = raidCompound.getList("activeMobs", 10); // 10 correspond au type Compound

                boolean hasAliveMobs = false;
                //System.out.println("- Vérification des activeMobs pour le raid : " + uuid);

                for (int i = 0; i < activeMobsList.size(); i++) {
                    NbtCompound mobCompound = activeMobsList.getCompound(i);
                    UUID mobUuid = mobCompound.getUuid("UUID");
                    Entity entity = world.getEntity(mobUuid);
                    //System.out.println("- J'ESSAIE DE CHARGER CE DRAUGR - ");
                    //System.out.println(entity);
                    if (entity != null && entity.isAlive()) {
                        hasAliveMobs = true;
                        //System.out.println("Entité vivante trouvée : " + entity.getType().getTranslationKey() + " (" + mobUuid + ")");
                    } else {
                        System.out.println("Entité introuvable ou morte : " + mobUuid);
                    }
                }

                if (!hasAliveMobs) {
                    System.out.println("Aucune entité active trouvée pour le raid : " + uuid + ". Ignoré.");
                    continue; // Ignorez ce raid s'il n'a pas d'entités vivantes
                }
            } else {
                System.out.println("Aucune activeMobs listée pour le raid : " + uuid + ". Ignoré.");
                continue; // Ignorez ce raid s'il n'a pas de clé "activeMobs"
            }

            // Restaurez le raid si les vérifications préalables sont réussies
            System.out.println("- Chargement du raid pour UUID : " + uuid);
            DraugrRaidTest raid = DraugrRaidTest.fromNbt(raidCompound, world);

            if (raid != null) {
                data.raids.put(uuid, raid);
            } else {
                System.out.println("Erreur : Impossible de charger le raid pour UUID : " + uuid);
            }
        }

        return data;
    }

    public static PersistentRaidData get(ServerWorld world) {
        //System.out.println("- PersistentRaidData get - ");
        return world.getPersistentStateManager().getOrCreate(
                nbt -> fromNbt(nbt, world),
                () -> new PersistentRaidData(world),
                "draugr_raids"
        );
    }
}
