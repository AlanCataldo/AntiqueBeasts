package net.mebahel.antiquebeasts.entity.client.projectiles;

import net.mebahel.antiquebeasts.entity.projectiles.HadesChosenSpearEntity;
import net.mebahel.antiquebeasts.entity.projectiles.ThrowingSnowRockEntity;
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

public class ThrowingSnowRockRenderer extends GeoEntityRenderer<ThrowingSnowRockEntity> {
    public ThrowingSnowRockRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new ThrowingSnowRockModel());
    }
    @Override
    public RenderLayer getRenderType(ThrowingSnowRockEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }
    @Override
    public void preRender(MatrixStack poseStack, ThrowingSnowRockEntity animatable, BakedGeoModel model, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue,
                          float alpha) {
        RenderUtils.faceRotation(poseStack, animatable, partialTick);
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}

