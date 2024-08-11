package net.mebahel.antiquebeasts.entity.client.custom;

import net.mebahel.antiquebeasts.entity.custom.norse.ValkyrieEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ValkyrieRenderer extends GeoEntityRenderer<ValkyrieEntity> {
    public ValkyrieRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new ValkyrieModel());
        this.shadowRadius = 0.75f;
    }
    @Override
    public RenderLayer getRenderType(ValkyrieEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }
}
