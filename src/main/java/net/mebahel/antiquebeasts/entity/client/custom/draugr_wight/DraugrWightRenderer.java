package net.mebahel.antiquebeasts.entity.client.custom.draugr_wight;

import net.mebahel.antiquebeasts.entity.custom.other.DraugrWightEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class DraugrWightRenderer extends GeoEntityRenderer<DraugrWightEntity> {

    public DraugrWightRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new DraugrWightModel());
        this.shadowRadius = 0.45f;

        // Ajout de la couche de rendu émissif
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    @Override
    public RenderLayer getRenderType(DraugrWightEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }
}
