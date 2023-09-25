package net.mebahel.antiquebeasts.screen;

import net.minecraft.screen.ScreenHandlerType;

public class ModScreenHandlers {
    public static ScreenHandlerType<BloodInfusingStationScreenHandler> BLOOD_INFUSING_STATION_SCREEN_HANDLER;

    public static void registerAllScreenHandlers() {
        BLOOD_INFUSING_STATION_SCREEN_HANDLER = new ScreenHandlerType<>(BloodInfusingStationScreenHandler::new);
    }
}
