package net.mebahel.antiquebeasts.util.packet;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.mebahel.antiquebeasts.block.entity.base.BaseChestBlockEntity;
import net.minecraft.block.entity.BlockEntity;
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

                BlockEntity be = client.world.getBlockEntity(pos);
                if (be instanceof BaseChestBlockEntity draugr) {
                    draugr.isOpened = open;
                    draugr.hasBeenOpened = true;
                    draugr.markDirty();

                    if (open) draugr.playOpenSound();
                    else draugr.playCloseSound();
                }
            });
        });
    }
}
