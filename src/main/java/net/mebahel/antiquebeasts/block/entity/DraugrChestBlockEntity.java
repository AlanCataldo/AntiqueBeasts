package net.mebahel.antiquebeasts.block.entity;

import net.mebahel.antiquebeasts.block.ModBlockEntities;
import net.mebahel.antiquebeasts.block.entity.base.BaseChestBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class DraugrChestBlockEntity extends BaseChestBlockEntity {
    public DraugrChestBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DRAUGR_CHEST_ENTITY, pos, state, 36);
    }

    @Override
    protected Text getChestTitle() {
        return Text.translatable("Draugr Chest");
    }

    @Override
    protected String getLogPrefix() {
        return "[DraugrChest]";
    }
}