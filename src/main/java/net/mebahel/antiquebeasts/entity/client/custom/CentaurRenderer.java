package net.mebahel.antiquebeasts.entity.client.custom;

import net.mebahel.antiquebeasts.entity.custom.egyptian.CamelryEntity;
import net.mebahel.antiquebeasts.entity.custom.greek.CentaurEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CentaurRenderer extends GeoEntityRenderer<CentaurEntity> {

    public CentaurRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new CentaurModel());
        this.shadowRadius = 0.45f;
    }

    @Override
    public RenderLayer getRenderType(CentaurEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }

    public void preRender(MatrixStack poseStack, CentaurEntity animatable, BakedGeoModel model, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue,
                          float alpha) {
        poseStack.scale(1.1f, 1.1f, 1.1f);
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public float getMotionAnimThreshold(CentaurEntity animatable) {
        return 0.008F;
    }
}
