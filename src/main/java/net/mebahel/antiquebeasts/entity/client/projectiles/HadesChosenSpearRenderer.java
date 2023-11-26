package net.mebahel.antiquebeasts.entity.client.projectiles;

import net.mebahel.antiquebeasts.entity.projectiles.HadesChosenSpearEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HadesChosenSpearRenderer extends GeoEntityRenderer<HadesChosenSpearEntity> {
    public HadesChosenSpearRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new HadesChosenSpearModel());
    }
    @Override
    public RenderLayer getRenderType(HadesChosenSpearEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }
}

