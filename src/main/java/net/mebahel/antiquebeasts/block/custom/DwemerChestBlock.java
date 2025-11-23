package net.mebahel.antiquebeasts.block.custom;

import net.mebahel.antiquebeasts.block.ModBlockEntities;
import net.mebahel.antiquebeasts.block.base.BaseChestBlock;
import net.mebahel.antiquebeasts.block.entity.DwemerChestBlockEntity;

public class DwemerChestBlock extends BaseChestBlock {
    public DwemerChestBlock(Settings settings) {
        super(
                settings,
                () -> ModBlockEntities.DWEMER_CHEST_ENTITY,
                DwemerChestBlockEntity::new
        );
    }
}