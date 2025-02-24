package net.mebahel.antiquebeasts.entity.client.custom;

import net.mebahel.antiquebeasts.entity.custom.egyptian.ElephantRiderEntity;
import net.mebahel.antiquebeasts.entity.custom.norse.EinherjarEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ElephantRiderRenderer extends GeoEntityRenderer<ElephantRiderEntity> {
    public ElephantRiderRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new ElephantRiderModel());
        this.shadowRadius = 1f;
    }
    @Override
    public RenderLayer getRenderType(ElephantRiderEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }
    @Override
    public float getMotionAnimThreshold(ElephantRiderEntity animatable) {
        return 0.008F;
    }
}
