package net.mebahel.antiquebeasts.entity.client.projectiles;

import net.mebahel.antiquebeasts.entity.projectiles.HadesChosenSpearEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer;

public class HadesChosenSpearRenderer extends GeoProjectilesRenderer<HadesChosenSpearEntity> {

    public HadesChosenSpearRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new HadesChosenSpearModel());
    }

    @Override
    public RenderLayer getRenderType(HadesChosenSpearEntity animatable, float partialTick, MatrixStack poseStack,
                                     @Nullable VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer,
                                     int packedLight, Identifier texture) {
        poseStack.scale(1.35f, 1.35f, 1.35f);
        return super.getRenderType(animatable, partialTick, poseStack, bufferSource, buffer, packedLight, texture);
    }

}

