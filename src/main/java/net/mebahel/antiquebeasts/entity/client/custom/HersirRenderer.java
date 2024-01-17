package net.mebahel.antiquebeasts.entity.client.custom;

import net.mebahel.antiquebeasts.entity.custom.HersirEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;


public class HersirRenderer extends GeoEntityRenderer<HersirEntity> {
    public HersirRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new HersirModel());
        this.shadowRadius = 0.5f;
    }

    @Override
    public RenderLayer getRenderType(HersirEntity animatable, float partialTick, MatrixStack poseStack,
                                     @Nullable VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer,
                                     int packedLight, Identifier texture) {

        poseStack.scale(1f, 1f, 1f);
        return super.getRenderType(animatable, partialTick, poseStack, bufferSource, buffer, packedLight, texture);
    }

}
