package net.mebahel.antiquebeasts.block.client;

import net.mebahel.antiquebeasts.block.entity.GreekChestBlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class GreekChestRenderer extends GeoBlockRenderer<GreekChestBlockEntity> {
    public GreekChestRenderer(BlockEntityRendererFactory.Context context) {
        super(new GreekChestModel());
    }
}

