package net.mebahel.antiquebeasts.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.mebahel.antiquebeasts.block.ModBlockEntities;
import net.mebahel.antiquebeasts.block.entity.base.BaseChestBlockEntity;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.minecraft.block.BlockState;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class DwemerChestBlockEntity extends BaseChestBlockEntity {
    public DwemerChestBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DWEMER_CHEST_ENTITY, pos, state, 36);
    }

    @Override
    protected Text getChestTitle() {
        return Text.translatable("Dwemer Chest");
    }

    @Override
    protected String getLogPrefix() {
        return "[DwemerChest]";
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void playOpenSound() {
        playChestSound(ModSounds.DWARVEN_CHEST_OPEN);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void playCloseSound() {
        playChestSound(ModSounds.DWARVEN_CHEST_CLOSE);
    }
}