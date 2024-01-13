package net.mebahel.antiquebeasts.entity.client.custom;

import net.mebahel.antiquebeasts.entity.custom.HersirEntity;
import net.mebahel.antiquebeasts.entity.custom.HuskarlEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HuskarlRenderer extends GeoEntityRenderer<HuskarlEntity> {
    public HuskarlRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new HuskarlModel());
        this.shadowRadius = 0.25f;
    }
    @Override
    public RenderLayer getRenderType(HuskarlEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }
}
