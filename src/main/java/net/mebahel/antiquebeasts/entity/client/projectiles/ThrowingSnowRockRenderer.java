package net.mebahel.antiquebeasts.entity.client.projectiles;

import net.mebahel.antiquebeasts.entity.projectiles.ThrowingSnowRockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ThrowingSnowRockRenderer extends GeoEntityRenderer<ThrowingSnowRockEntity> {

    public ThrowingSnowRockRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new ThrowingSnowRockModel());
    }
    @Override
    public RenderLayer getRenderType(ThrowingSnowRockEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }
}

