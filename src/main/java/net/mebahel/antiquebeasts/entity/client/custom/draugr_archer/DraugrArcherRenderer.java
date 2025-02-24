package net.mebahel.antiquebeasts.entity.client.custom.draugr_archer;

import net.mebahel.antiquebeasts.entity.custom.other.DraugrArcherEntity;
import net.mebahel.antiquebeasts.entity.custom.other.DraugrEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class DraugrArcherRenderer extends GeoEntityRenderer<DraugrArcherEntity> {

    public DraugrArcherRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new DraugrArcherModel());
        this.shadowRadius = 0.45f;
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    @Override
    public RenderLayer getRenderType(DraugrArcherEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }
    @Override
    public float getMotionAnimThreshold(DraugrArcherEntity animatable) {
        return 0.008F;
    }
}
