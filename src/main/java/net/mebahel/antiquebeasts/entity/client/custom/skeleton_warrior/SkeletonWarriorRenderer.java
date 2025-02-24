package net.mebahel.antiquebeasts.entity.client.custom.skeleton_warrior;

import net.mebahel.antiquebeasts.entity.custom.egyptian.MummyBossEntity;
import net.mebahel.antiquebeasts.entity.custom.other.SkeletonWarriorEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class SkeletonWarriorRenderer extends GeoEntityRenderer<SkeletonWarriorEntity> {
    public SkeletonWarriorRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new SkeletonWarriorModel());
        this.shadowRadius = 0.45f;
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }
    @Override
    public RenderLayer getRenderType(SkeletonWarriorEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }
    @Override
    public float getMotionAnimThreshold(SkeletonWarriorEntity animatable) {
        return 0.008F;
    }
}
