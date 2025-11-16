package net.mebahel.antiquebeasts.util.packet;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.mebahel.antiquebeasts.block.entity.DraugrChestBlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class ChestOpenSync {
    public static final Identifier SYNC_ID = new Identifier("antiquebeasts", "sync_chest_open");

    public static void registerServerReceiver() {
        // rien côté serveur ici pour l’instant
    }

    public static void sendToAll(ServerWorld world, BlockPos pos, boolean open) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBlockPos(pos);
        buf.writeBoolean(open);

        world.getPlayers(p -> true).forEach(p ->
                ServerPlayNetworking.send(p, SYNC_ID, buf)
        );
    }

    @Environment(EnvType.CLIENT)
    public static void registerClientReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(SYNC_ID, (client, handler, buf, responseSender) -> {
            BlockPos pos = buf.readBlockPos();
            boolean open = buf.readBoolean();

            client.execute(() -> {
                if (client.world == null) return;
                if (!(client.world.getBlockEntity(pos) instanceof DraugrChestBlockEntity chest)) return;

                // ⚠️ ne plus changer d’état localement ailleurs que par ce packet !
                chest.isOpened = open;
                chest.hasBeenOpened = true;
                chest.markDirty();

                if (open) chest.playOpenSound();
                else chest.playCloseSound();
            });
        });
    }
}
