package net.mebahel.antiquebeasts.block.custom;

import net.mebahel.antiquebeasts.block.ModBlockEntities;
import net.mebahel.antiquebeasts.block.base.BaseChestBlock;
import net.mebahel.antiquebeasts.block.entity.GreekChestBlockEntity;

public class GreekChestBlock extends BaseChestBlock {
    public GreekChestBlock(Settings settings) {
        super(
                settings,
                () -> ModBlockEntities.GREEK_CHEST_ENTITY,
                GreekChestBlockEntity::new
        );
    }
}