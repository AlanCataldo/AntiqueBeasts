package net.mebahel.antiquebeasts.block.screenhandlers;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlers {
    public static final ScreenHandlerType<DraugrChestScreenHandler> DRAUGR_CHEST_SCREEN_HANDLER =
            new ExtendedScreenHandlerType<>(DraugrChestScreenHandler::new);
    public static void registerScreenHandlers() {
        Registry.register(Registries.SCREEN_HANDLER, new Identifier(AntiqueBeasts.MOD_ID, "draugr_chest"),
                DRAUGR_CHEST_SCREEN_HANDLER);
    }
}