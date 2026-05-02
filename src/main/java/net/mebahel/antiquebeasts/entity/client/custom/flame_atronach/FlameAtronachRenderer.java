package net.mebahel.antiquebeasts.entity.client.custom.flame_atronach;

import net.mebahel.antiquebeasts.entity.custom.other.FlameAtronachEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class FlameAtronachRenderer extends GeoEntityRenderer<FlameAtronachEntity> {

    public FlameAtronachRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new FlameAtronachModel());
        this.shadowRadius = 0.25f;

        this.addRenderLayer(new FlameAtronachFlameParticleLayer(this));
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }


    @Override
    public RenderLayer getRenderType(FlameAtronachEntity animatable, Identifier texture,
                                     @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }

    @Override
    public void preRender(MatrixStack poseStack,
                          FlameAtronachEntity animatable,
                          BakedGeoModel model,
                          VertexConsumerProvider bufferSource,
                          VertexConsumer buffer,
                          boolean isReRender,
                          float partialTick,
                          int packedLight,
                          int packedOverlay,
                          float red,
                          float green,
                          float blue,
                          float alpha) {

        float s = animatable.getScale();

        this.scaleWidth = s;
        this.scaleHeight = s;

        super.preRender(poseStack, animatable, model, bufferSource, buffer,
                isReRender, partialTick, packedLight, packedOverlay,
                red, green, blue, alpha);
    }

    @Override
    public float getMotionAnimThreshold(FlameAtronachEntity animatable) {
        return 0.008F;
    }
}
