package net.mebahel.antiquebeasts.entity.client.custom.dwemer_spider;

import net.mebahel.antiquebeasts.entity.custom.dwemer.DwemerSpiderEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DwemerSpiderRenderer extends GeoEntityRenderer<DwemerSpiderEntity> {

    public DwemerSpiderRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new DwemerSpidersModel());
        this.shadowRadius = 0.32f;
    }

    @Override
    public RenderLayer getRenderType(DwemerSpiderEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }

    @Override
    public float getMotionAnimThreshold(DwemerSpiderEntity animatable) {
        return 0.008F;
    }
}
