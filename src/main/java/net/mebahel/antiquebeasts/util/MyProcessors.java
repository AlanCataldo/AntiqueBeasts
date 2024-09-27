package net.mebahel.antiquebeasts.util;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.Identifier;

public class MyProcessors {
    public static final StructureProcessorType<WaterRemovalProcessor> WATER_REMOVAL_PROCESSOR =
            Registry.register(
                    Registries.STRUCTURE_PROCESSOR,
                    new Identifier("antiquebeasts", "water_removal_processor"),
                    () -> WaterRemovalProcessor.CODEC
            );

    public static void init() {
        System.out.println("[Antique Beasts] Register structure processors");
    }
}
