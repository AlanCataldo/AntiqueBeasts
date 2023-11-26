package net.mebahel.antiquebeasts.entity.client.projectiles;

import net.mebahel.antiquebeasts.entity.projectiles.HopliteSpearEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HopliteSpearRenderer extends GeoEntityRenderer<HopliteSpearEntity> {

    public HopliteSpearRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new HopliteSpearModel());
    }
    @Override
    public RenderLayer getRenderType(HopliteSpearEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }
}

