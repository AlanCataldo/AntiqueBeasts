package net.mebahel.antiquebeasts.entity.client.custom.draugr_overlord;

import net.mebahel.antiquebeasts.entity.custom.other.DraugrArcherEntity;
import net.mebahel.antiquebeasts.entity.custom.other.DraugrOverlordEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class DraugrOverlordRenderer extends GeoEntityRenderer<DraugrOverlordEntity> {

    public DraugrOverlordRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new DraugrOverlordModel());
        this.shadowRadius = 0.65f;

        // Ajout de la couche de rendu émissif
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    @Override
    public RenderLayer getRenderType(DraugrOverlordEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }
    @Override
    public float getMotionAnimThreshold(DraugrOverlordEntity animatable) {
        return 0.008F;
    }
}
