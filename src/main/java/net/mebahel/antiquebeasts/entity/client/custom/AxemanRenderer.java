package net.mebahel.antiquebeasts.entity.client.custom;

import net.mebahel.antiquebeasts.entity.custom.egyptian.AxemanEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class AxemanRenderer extends GeoEntityRenderer<AxemanEntity> {
    public AxemanRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new AxemanModel());
        this.shadowRadius = 0.25f;
    }
    @Override
    public RenderLayer getRenderType(AxemanEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }
}
