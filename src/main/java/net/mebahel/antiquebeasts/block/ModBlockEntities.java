package net.mebahel.antiquebeasts.block;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.block.entity.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static final BlockEntityType<DwemerChestBlockEntity> DWEMER_CHEST_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "dwemer_chest"),
                    FabricBlockEntityTypeBuilder.create(DwemerChestBlockEntity::new,
                            ModBlocks.DWEMER_CHEST).build());

    public static final BlockEntityType<DraugrChestBlockEntity> DRAUGR_CHEST_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "draugr_chest"),
                    FabricBlockEntityTypeBuilder.create(DraugrChestBlockEntity::new,
                            ModBlocks.DRAUGR_CHEST).build());

    public static final BlockEntityType<GreekChestBlockEntity> GREEK_CHEST_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "greek_chest"),
                    FabricBlockEntityTypeBuilder.create(GreekChestBlockEntity::new,
                            ModBlocks.GREEK_CHEST).build());

    public static final BlockEntityType<DwarvenMetalPipeGearBlockEntity> DWARVEN_METAL_PIPE_GEAR =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "dwarven_metal_pipe_gear"),
                    FabricBlockEntityTypeBuilder.create(DwarvenMetalPipeGearBlockEntity::new,
                            ModBlocks.DWEMER_METAL_PIPE_GEAR).build());

    public static final BlockEntityType<DwemerSpiderBlockEntity> DWEMER_SPIDER_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "dwemer_spider_block"),
                    FabricBlockEntityTypeBuilder.create(DwemerSpiderBlockEntity::new,
                            ModBlocks.DWEMER_SPIDER_BLOCK).build());

    public static void registerModBlockEntities() {
        System.out.println("Registering BlockEntities for " + AntiqueBeasts.MOD_ID);
    }
}

