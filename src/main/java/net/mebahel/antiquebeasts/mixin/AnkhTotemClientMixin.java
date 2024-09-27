package net.mebahel.antiquebeasts.mixin;

import net.mebahel.antiquebeasts.item.custom.ModItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ClientPlayNetworkHandler.class)
public class AnkhTotemClientMixin {
    @Inject(method = "onEntityStatus", at = @At("HEAD"), cancellable = true)
    private void onEntityStatus(EntityStatusS2CPacket packet, CallbackInfo ci) {
        // Utiliser le contexte Minecraft pour obtenir l'entité
        MinecraftClient client = MinecraftClient.getInstance();

        // Vérifier si le monde est chargé et non null
        if (client.world != null) {
            Entity entity = packet.getEntity(client.world);

            if (entity != null) {
                // Vérifier si le statut correspond à l'activation du Totem of Undying (status 35)
                if (packet.getStatus() == 35) {
                    // Vérifier si l'entité est le joueur local et si le joueur tient l'item ModItems.ANKH dans une main
                    if (entity == client.player) {
                        // Vérifie si l'item dans la main principale ou secondaire est ModItems.ANKH
                        ItemStack mainHandStack = client.player.getMainHandStack();
                        ItemStack offHandStack = client.player.getOffHandStack();

                        if (mainHandStack.isOf(ModItems.ANKH) || offHandStack.isOf(ModItems.ANKH)) {
                            // Ajout des particules personnalisées
                            client.particleManager.addEmitter(entity, ParticleTypes.TOTEM_OF_UNDYING, 30);

                            // Jouer le son personnalisé
                            client.world.playSound(entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ITEM_TOTEM_USE, entity.getSoundCategory(), 1.0f, 1.0f, false);

                            // Remplacer l'item affiché par ModItems.ANKH
                            ItemStack customItem = new ItemStack(ModItems.ANKH);
                            client.gameRenderer.showFloatingItem(customItem);

                            // Annule l'exécution par défaut pour empêcher les particules et sons du Totem of Undying
                            ci.cancel();
                        }
                    }
                }
            }
        }
    }
}


