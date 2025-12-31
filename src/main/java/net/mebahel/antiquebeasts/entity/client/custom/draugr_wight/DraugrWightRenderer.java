package net.mebahel.antiquebeasts.entity.client.custom.draugr_wight;

import net.mebahel.antiquebeasts.entity.custom.other.DraugrWightEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class DraugrWightRenderer extends GeoEntityRenderer<DraugrWightEntity> {

    public DraugrWightRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new DraugrWightModel());
        this.shadowRadius = 0.45f;

        // Ajout de la couche de rendu émissif
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
        this.addRenderLayer(new DraugrWightPotionLayer(this));
    }

    @Override
    public RenderLayer getRenderType(DraugrWightEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }

    @Override
    public void preRender(MatrixStack poseStack,
                          DraugrWightEntity animatable,
                          BakedGeoModel model,
                          VertexConsumerProvider bufferSource,
                          VertexConsumer buffer,
                          boolean isReRender,
                          float partialTick,
                          int packedLight,
                          int packedOverlay,
                          float red,
                          float green,
                          float blue,
                          float alpha) {

        float s = animatable.getDraugrScale(); // 1.0–1.1, ce que tu as mis côté entity

        // 👉 On donne la scale au système interne de GeoEntityRenderer
        this.scaleWidth = s;
        this.scaleHeight = s;

        // ❌ surtout pas de poseStack.scale(...) ici
        super.preRender(poseStack, animatable, model, bufferSource, buffer,
                isReRender, partialTick, packedLight, packedOverlay,
                red, green, blue, alpha);
    }

    @Override
    public float getMotionAnimThreshold(DraugrWightEntity animatable) {
        return 0.008F;
    }
}
