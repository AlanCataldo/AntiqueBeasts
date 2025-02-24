package net.mebahel.antiquebeasts.entity.client.custom.draugr_scourge;

import net.mebahel.antiquebeasts.entity.custom.other.DraugrOverlordEntity;
import net.mebahel.antiquebeasts.entity.custom.other.DraugrScourgeEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class DraugrScourgeRenderer extends GeoEntityRenderer<DraugrScourgeEntity> {

    public DraugrScourgeRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new DraugrScourgeModel());
        this.shadowRadius = 0.45f;

        // Ajout de la couche de rendu émissif
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    @Override
    public RenderLayer getRenderType(DraugrScourgeEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }
    @Override
    public float getMotionAnimThreshold(DraugrScourgeEntity animatable) {
        return 0.008F;
    }
}
