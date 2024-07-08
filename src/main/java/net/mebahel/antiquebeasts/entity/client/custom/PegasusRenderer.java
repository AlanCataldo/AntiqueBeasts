package net.mebahel.antiquebeasts.entity.client.custom;

import net.mebahel.antiquebeasts.entity.custom.PegasusEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PegasusRenderer extends GeoEntityRenderer<PegasusEntity> {
    public PegasusRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new PegasusModel());
        this.shadowRadius = 0.55f;
    }
    @Override
    public RenderLayer getRenderType(PegasusEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }
}
