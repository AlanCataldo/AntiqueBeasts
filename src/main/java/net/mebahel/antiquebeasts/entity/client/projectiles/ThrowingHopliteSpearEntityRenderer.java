package net.mebahel.antiquebeasts.entity.client.projectiles;

import net.mebahel.antiquebeasts.entity.projectiles.ThrowingHopliteSpearEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;


public class ThrowingHopliteSpearEntityRenderer extends GeoEntityRenderer<ThrowingHopliteSpearEntity> {

    public ThrowingHopliteSpearEntityRenderer(Object renderManager, String type) {
        super((EntityRendererFactory.Context) renderManager, new ThrowingHopliteSpearEntityModel(type));
    }

    @Override
    public RenderLayer getRenderType(ThrowingHopliteSpearEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }
}