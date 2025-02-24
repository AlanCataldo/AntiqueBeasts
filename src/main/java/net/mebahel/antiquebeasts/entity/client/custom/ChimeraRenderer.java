package net.mebahel.antiquebeasts.entity.client.custom;

import net.mebahel.antiquebeasts.entity.custom.ChimeraEntity;
import net.mebahel.antiquebeasts.entity.custom.greek.ChampionHopliteEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ChimeraRenderer extends GeoEntityRenderer<ChimeraEntity> {
    public ChimeraRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new ChimeraModel());
        this.shadowRadius = 0.85f;
    }
    @Override
    public RenderLayer getRenderType(ChimeraEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }

    @Override
    public float getMotionAnimThreshold(ChimeraEntity animatable) {
        return 0.008F;
    }
}
