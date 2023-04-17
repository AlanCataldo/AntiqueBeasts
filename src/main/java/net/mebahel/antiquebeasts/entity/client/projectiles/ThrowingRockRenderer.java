package net.mebahel.antiquebeasts.entity.client.projectiles;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.projectiles.ThrowingRockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer;

public class ThrowingRockRenderer extends GeoProjectilesRenderer<ThrowingRockEntity> {

    public ThrowingRockRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new ThrowingRockModel());
    }

    @Override
    public Identifier getTextureResource(ThrowingRockEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/cyclops_texture.png");
    }

    @Override
    public RenderLayer getRenderType(ThrowingRockEntity animatable, float partialTick, MatrixStack poseStack,
                                     @Nullable VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer,
                                     int packedLight, Identifier texture) {
        poseStack.scale(1.35f, 1.35f, 1.35f);
        return super.getRenderType(animatable, partialTick, poseStack, bufferSource, buffer, packedLight, texture);
    }

}

