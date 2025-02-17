package net.mebahel.antiquebeasts.block.screenhandlers;


import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;

public class ModScreenHandlers {
    public static void registerScreenHandlers() {
        ScreenRegistry.<ChestScreenHandler, CottonInventoryScreen<ChestScreenHandler>>register(ModScreenHandlerType.DRAUGR_CHEST,
                (desc, inventory, title) -> new CottonInventoryScreen<>(desc, inventory.player, title));
    }
}