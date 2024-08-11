package net.mebahel.antiquebeasts.entity.client.custom;

import net.mebahel.antiquebeasts.entity.custom.CyclopsEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CyclopsRenderer extends GeoEntityRenderer<CyclopsEntity> {

    public CyclopsRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new CyclopsModel());
        this.shadowRadius = 0.65f;
    }

    @Override
    public RenderLayer getRenderType(CyclopsEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }

    public void preRender(MatrixStack poseStack, CyclopsEntity animatable, BakedGeoModel model, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue,
                          float alpha) {
        poseStack.scale(1.5f, 1.5f, 1.5f);
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

}
