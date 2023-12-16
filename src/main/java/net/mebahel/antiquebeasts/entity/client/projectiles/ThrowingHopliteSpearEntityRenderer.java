package net.mebahel.antiquebeasts.entity.client.projectiles;

import net.mebahel.antiquebeasts.entity.projectiles.ThrowingHopliteSpearEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer;

public class ThrowingHopliteSpearEntityRenderer extends GeoProjectilesRenderer<ThrowingHopliteSpearEntity> {

    public ThrowingHopliteSpearEntityRenderer(Object renderManager, String type) {
        super((EntityRendererFactory.Context) renderManager, new ThrowingHopliteSpearEntityModel(type));
    }

    @Override
    public RenderLayer getRenderType(ThrowingHopliteSpearEntity animatable, float partialTick, MatrixStack poseStack,
                                     @Nullable VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer,
                                     int packedLight, Identifier texture) {
        poseStack.scale(0.8f, 0.8f, 0.8f);
        return super.getRenderType(animatable, partialTick, poseStack, bufferSource, buffer, packedLight, texture);
    }
}