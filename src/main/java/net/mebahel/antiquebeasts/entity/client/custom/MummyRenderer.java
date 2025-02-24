package net.mebahel.antiquebeasts.entity.client.custom;

import net.mebahel.antiquebeasts.entity.custom.egyptian.MummyEntity;
import net.mebahel.antiquebeasts.entity.custom.norse.HuskarlEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MummyRenderer extends GeoEntityRenderer<MummyEntity> {
    public MummyRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new MummyModel());
        this.shadowRadius = 0.5f;
    }
    @Override
    public RenderLayer getRenderType(MummyEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }
    @Override
    public float getMotionAnimThreshold(MummyEntity animatable) {
        return 0.008F;
    }
}
