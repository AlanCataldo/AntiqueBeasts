package net.mebahel.antiquebeasts.entity.client.custom;

import net.mebahel.antiquebeasts.entity.custom.egyptian.ServantEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ServantRenderer extends GeoEntityRenderer<ServantEntity> {
    public ServantRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new ServantModel());
        this.shadowRadius = 0.25f;
    }
    @Override
    public RenderLayer getRenderType(ServantEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }
}
