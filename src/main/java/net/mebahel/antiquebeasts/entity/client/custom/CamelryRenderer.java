package net.mebahel.antiquebeasts.entity.client.custom;

import net.mebahel.antiquebeasts.entity.custom.CamelryEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CamelryRenderer extends GeoEntityRenderer<CamelryEntity> {
    public CamelryRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new CamelryModel());
        this.shadowRadius = 0.4f;
    }
    @Override
    public RenderLayer getRenderType(CamelryEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }
}
