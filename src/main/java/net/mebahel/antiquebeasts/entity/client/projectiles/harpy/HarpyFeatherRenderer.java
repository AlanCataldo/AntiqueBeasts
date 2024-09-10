package net.mebahel.antiquebeasts.entity.client.projectiles.harpy;

import net.mebahel.antiquebeasts.entity.projectiles.HarpyFeatherEntity;
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

public class HarpyFeatherRenderer extends GeoEntityRenderer<HarpyFeatherEntity> {
    public HarpyFeatherRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new HarpyFeatherModel());
    }
    @Override
    public RenderLayer getRenderType(HarpyFeatherEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }
    @Override
    public void preRender(MatrixStack poseStack, HarpyFeatherEntity animatable, BakedGeoModel model, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue,
                          float alpha) {
        poseStack.scale(1f, 1f, 1f);
        RenderUtils.faceRotation(poseStack, animatable, partialTick);
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}

