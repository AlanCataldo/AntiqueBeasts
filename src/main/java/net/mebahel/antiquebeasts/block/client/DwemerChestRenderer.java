package net.mebahel.antiquebeasts.block.client;

import net.mebahel.antiquebeasts.block.entity.DwemerChestBlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class DwemerChestRenderer extends GeoBlockRenderer<DwemerChestBlockEntity> {
    public DwemerChestRenderer(BlockEntityRendererFactory.Context context) {
        super(new DwemerChestModel());
    }
}

