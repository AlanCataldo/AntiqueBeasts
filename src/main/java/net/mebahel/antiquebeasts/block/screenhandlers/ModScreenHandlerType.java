package net.mebahel.antiquebeasts.block.screenhandlers;

import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlerType {
    public static ScreenHandlerType<ChestScreenHandler> DRAUGR_CHEST;

    public static void registerScreenHandlers() {
        DRAUGR_CHEST = ScreenHandlerRegistry.registerSimple(new Identifier(AntiqueBeasts.MOD_ID, "draugr_chest"), (syncId, inventory) ->
                new ChestScreenHandler(DRAUGR_CHEST, syncId, inventory, ScreenHandlerContext.EMPTY));
    }
}