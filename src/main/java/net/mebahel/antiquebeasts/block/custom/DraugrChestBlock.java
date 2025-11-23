package net.mebahel.antiquebeasts.block.custom;

import net.mebahel.antiquebeasts.block.ModBlockEntities;
import net.mebahel.antiquebeasts.block.base.BaseChestBlock;
import net.mebahel.antiquebeasts.block.entity.DraugrChestBlockEntity;

public class DraugrChestBlock extends BaseChestBlock {
    public DraugrChestBlock(Settings settings) {
        super(
                settings,
                () -> ModBlockEntities.DRAUGR_CHEST_ENTITY,
                DraugrChestBlockEntity::new
        );
    }
}