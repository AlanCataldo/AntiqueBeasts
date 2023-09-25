package net.mebahel.antiquebeasts.entity.client.custom;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.custom.FrostCyclopsEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class FrostCyclopsRenderer extends GeoEntityRenderer<FrostCyclopsEntity> {

    public FrostCyclopsRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new FrostCyclopsModel());
        this.shadowRadius = 1f;
    }

    @Override
    public Identifier getTextureResource(FrostCyclopsEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/frost-cyclops_texture.png");
    }

    @Override
    public RenderLayer getRenderType(FrostCyclopsEntity animatable, float partialTick, MatrixStack poseStack,
                                     @Nullable VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer,
                                     int packedLight, Identifier texture) {

        poseStack.scale(1f, 1f, 1f);
        return super.getRenderType(animatable, partialTick, poseStack, bufferSource, buffer, packedLight, texture);
    }

}
