package net.mebahel.antiquebeasts.entity.client.custom;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.custom.HadesShadeEntity;
import net.mebahel.antiquebeasts.entity.custom.HersirEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HersirRenderer extends GeoEntityRenderer<HersirEntity> {
    public HersirRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new HersirModel());
        this.shadowRadius = 0.25f;
    }
    @Override
    public RenderLayer getRenderType(HersirEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }
}
