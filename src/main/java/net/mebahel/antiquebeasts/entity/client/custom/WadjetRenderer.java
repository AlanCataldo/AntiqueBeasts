package net.mebahel.antiquebeasts.entity.client.custom;

import net.mebahel.antiquebeasts.entity.custom.egyptian.WadjetEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class WadjetRenderer extends GeoEntityRenderer<WadjetEntity> {
    public WadjetRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new WadjetModel());
        this.shadowRadius = 0.25f;
    }
    @Override
    public RenderLayer getRenderType(WadjetEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }
}
