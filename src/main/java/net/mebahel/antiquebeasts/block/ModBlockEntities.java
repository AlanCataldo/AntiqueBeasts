package net.mebahel.antiquebeasts.block;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.block.entity.DraugrChestBlockEntity;
import net.mebahel.antiquebeasts.block.entity.StaffEnchantingTableBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {

    public static final BlockEntityType<DraugrChestBlockEntity> DRAUGR_CHEST_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "draugr_chest"),
                    FabricBlockEntityTypeBuilder.create(DraugrChestBlockEntity::new,
                            ModBlocks.DRAUGR_CHEST).build());

    public static final BlockEntityType<StaffEnchantingTableBlockEntity> STAFF_ENCHANTING_TABLE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "staff_enchanting_table"),
                    FabricBlockEntityTypeBuilder.create(StaffEnchantingTableBlockEntity::new,
                            ModBlocks.STAFF_ENCHANTING_TABLE).build());

    public static void registerModBlockEntities() {
        System.out.println("Registering BlockEntities for " + AntiqueBeasts.MOD_ID);
    }
}

