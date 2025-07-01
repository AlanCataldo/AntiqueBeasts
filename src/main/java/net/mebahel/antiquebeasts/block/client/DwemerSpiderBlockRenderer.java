package net.mebahel.antiquebeasts.block.client;

import net.mebahel.antiquebeasts.block.entity.DwemerSpiderBlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class DwemerSpiderBlockRenderer extends GeoBlockRenderer<DwemerSpiderBlockEntity> {
    public DwemerSpiderBlockRenderer(BlockEntityRendererFactory.Context context) {
        super(new DwemerSpiderBlockModel());
    }
}

