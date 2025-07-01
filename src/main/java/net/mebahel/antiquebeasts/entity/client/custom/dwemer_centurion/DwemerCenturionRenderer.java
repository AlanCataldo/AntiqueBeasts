package net.mebahel.antiquebeasts.entity.client.custom.dwemer_centurion;

import net.mebahel.antiquebeasts.entity.custom.dwemer.DwemerCenturionEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DwemerCenturionRenderer extends GeoEntityRenderer<DwemerCenturionEntity> {

    public DwemerCenturionRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new DwemerCenturionModel());
        this.shadowRadius = 0.4f;
    }

    @Override
    public RenderLayer getRenderType(DwemerCenturionEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }

    @Override
    public float getMotionAnimThreshold(DwemerCenturionEntity animatable) {
        return 0.008F;
    }
}
