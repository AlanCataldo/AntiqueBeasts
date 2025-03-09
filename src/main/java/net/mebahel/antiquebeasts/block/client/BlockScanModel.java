package net.mebahel.antiquebeasts.block.client;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.block.entity.BlockScanEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class BlockScanModel extends DefaultedEntityGeoModel<BlockScanEntity> {
    public BlockScanModel() {
        super(new Identifier(AntiqueBeasts.MOD_ID, "block_scan_entity"));
    }
}