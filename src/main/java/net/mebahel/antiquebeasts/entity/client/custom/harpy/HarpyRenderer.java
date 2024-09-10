package net.mebahel.antiquebeasts.entity.client.custom.harpy;

import net.mebahel.antiquebeasts.entity.custom.other.HarpyEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HarpyRenderer extends GeoEntityRenderer<HarpyEntity> {
    public HarpyRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new HarpyModel());
        this.shadowRadius = 0.25f;
    }
    @Override
    public RenderLayer getRenderType(HarpyEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }
}
