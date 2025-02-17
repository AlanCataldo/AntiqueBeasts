package net.mebahel.antiquebeasts.block.client;

import net.mebahel.antiquebeasts.block.entity.DraugrChestBlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class DraugrChestRenderer extends GeoBlockRenderer<DraugrChestBlockEntity> {
    public DraugrChestRenderer(BlockEntityRendererFactory.Context context) {
        super(new DraugrChestModel());
    }
}

