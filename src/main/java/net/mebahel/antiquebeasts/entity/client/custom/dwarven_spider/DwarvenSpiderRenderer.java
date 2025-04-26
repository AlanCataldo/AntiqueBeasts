package net.mebahel.antiquebeasts.entity.client.custom.dwarven_spider;

import net.mebahel.antiquebeasts.entity.custom.dwarven.DwarvenSpiderEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DwarvenSpiderRenderer extends GeoEntityRenderer<DwarvenSpiderEntity> {

    public DwarvenSpiderRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new DwarvenSpidersModel());
        this.shadowRadius = 0.32f;
    }

    @Override
    public RenderLayer getRenderType(DwarvenSpiderEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }

    @Override
    public float getMotionAnimThreshold(DwarvenSpiderEntity animatable) {
        return 0.008F;
    }
}
