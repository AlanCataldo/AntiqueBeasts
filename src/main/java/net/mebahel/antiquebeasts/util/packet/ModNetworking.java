package net.mebahel.antiquebeasts.util.packet;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.mebahel.antiquebeasts.block.entity.DraugrChestBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class ModNetworking {
    public static void registerReceivers() {
        ServerPlayNetworking.registerGlobalReceiver(new Identifier("antiquebeasts", "update_chest"),
                (server, player, handler, buf, responseSender) -> {
                    BlockPos pos = buf.readBlockPos();
                    boolean shouldDoSpawnAnimation = buf.readBoolean();

                    server.execute(() -> {
                        BlockEntity be = player.getServerWorld().getBlockEntity(pos);
                        if (be instanceof DraugrChestBlockEntity chest) {
                            chest.shouldDoSpawnAnimation = shouldDoSpawnAnimation;
                            chest.markDirty(); // ✅ Enregistre la mise à jour côté serveur
                            chest.sync(); // ✅ Synchronisation avec le client
                        }
                    });
                });
    }
}