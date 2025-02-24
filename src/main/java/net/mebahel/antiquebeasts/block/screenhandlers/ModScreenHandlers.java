package net.mebahel.antiquebeasts.block.screenhandlers;


import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlers {
    public static final ScreenHandlerType<StaffEnchantingTableScreenHandler> STAFF_ENCHANTING_STAFF_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier(AntiqueBeasts.MOD_ID, "staff_enchanting_table"),
                    new ExtendedScreenHandlerType<>(StaffEnchantingTableScreenHandler::new));
    public static void registerScreenHandlers() {
        ScreenRegistry.<ChestScreenHandler, CottonInventoryScreen<ChestScreenHandler>>register(ModScreenHandlerType.DRAUGR_CHEST,
                (desc, inventory, title) -> new CottonInventoryScreen<>(desc, inventory.player, title));
    }
}