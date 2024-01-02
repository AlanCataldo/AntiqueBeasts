package net.mebahel.antiquebeasts.entity.client.custom;

import net.mebahel.antiquebeasts.entity.custom.ThrowingAxeManEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ThrowingAxeManRenderer extends GeoEntityRenderer<ThrowingAxeManEntity> {
    public ThrowingAxeManRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new ThrowingAxeManModel());
        this.shadowRadius = 0.25f;
    }
    @Override
    public RenderLayer getRenderType(ThrowingAxeManEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }
}
