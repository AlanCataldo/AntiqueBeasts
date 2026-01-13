package net.mebahel.antiquebeasts.block.entity;

import net.mebahel.antiquebeasts.block.ModBlockEntities;
import net.mebahel.antiquebeasts.block.entity.base.BaseChestBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class GreekChestBlockEntity extends BaseChestBlockEntity {
    public GreekChestBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GREEK_CHEST_ENTITY, pos, state, 36);
    }

    @Override
    protected Text getChestTitle() {
        return Text.translatable("block.antiquebeasts.greek_chest");
    }

    @Override
    protected String getLogPrefix() {
        return "[GreekChest]";
    }
}