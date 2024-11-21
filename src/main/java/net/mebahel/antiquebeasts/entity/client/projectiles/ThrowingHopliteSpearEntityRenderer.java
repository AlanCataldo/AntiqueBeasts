package net.mebahel.antiquebeasts.entity.client.projectiles;

import net.mebahel.antiquebeasts.entity.projectiles.ThrowingHopliteSpearEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.util.RenderUtils;


public class ThrowingHopliteSpearEntityRenderer extends GeoEntityRenderer<ThrowingHopliteSpearEntity> {
    public ThrowingHopliteSpearEntityRenderer(Object renderManager, String type) {
        super((EntityRendererFactory.Context) renderManager, new ThrowingHopliteSpearEntityModel(type));
    }
    @Override
    public RenderLayer getRenderType(ThrowingHopliteSpearEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }
    @Override
    public void preRender(MatrixStack poseStack, ThrowingHopliteSpearEntity animatable, BakedGeoModel model, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue,
                          float alpha) {
        RenderUtils.faceRotation(poseStack, animatable, partialTick);
        poseStack.scale(0.75f, 0.75f, 0.75f);
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}