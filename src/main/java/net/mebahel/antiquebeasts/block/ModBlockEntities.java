package net.mebahel.antiquebeasts.block;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.block.ModBlocks;
import net.mebahel.antiquebeasts.block.entity.BloodInfusingStationBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModBlockEntities {
    public static BlockEntityType<BloodInfusingStationBlockEntity> BLOOD_INFUSING_STATION;

    public static void registerBlockEntities() {
        BLOOD_INFUSING_STATION = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "blood_infusing_station"),
                FabricBlockEntityTypeBuilder.create(BloodInfusingStationBlockEntity::new,
                        ModBlocks.BLOOD_INFUSING_STATION).build(null));
    }
}
