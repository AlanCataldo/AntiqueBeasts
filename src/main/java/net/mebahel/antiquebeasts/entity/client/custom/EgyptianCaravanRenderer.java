package net.mebahel.antiquebeasts.entity.client.custom;

import net.mebahel.antiquebeasts.entity.custom.CyclopsEntity;
import net.mebahel.antiquebeasts.entity.custom.egyptian.EgyptianCaravanEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EgyptianCaravanRenderer extends GeoEntityRenderer<EgyptianCaravanEntity> {
    public EgyptianCaravanRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new EgyptianCaravanModel());
        this.shadowRadius = 0.7f;
    }
    @Override
    public RenderLayer getRenderType(EgyptianCaravanEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }
    @Override
    public float getMotionAnimThreshold(EgyptianCaravanEntity animatable) {
        return 0.008F;
    }
}
