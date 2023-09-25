package net.mebahel.antiquebeasts.entity.client.projectiles;

import net.mebahel.antiquebeasts.entity.projectiles.HopliteSpearEntity;
import net.mebahel.antiquebeasts.entity.projectiles.ThrowingSnowRockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer;

public class HopliteSpearRenderer extends GeoProjectilesRenderer<HopliteSpearEntity> {

    public HopliteSpearRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new HopliteSpearModel());
    }

    @Override
    public RenderLayer getRenderType(HopliteSpearEntity animatable, float partialTick, MatrixStack poseStack,
                                     @Nullable VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer,
                                     int packedLight, Identifier texture) {
        poseStack.scale(1.35f, 1.35f, 1.35f);
        return super.getRenderType(animatable, partialTick, poseStack, bufferSource, buffer, packedLight, texture);
    }

}

