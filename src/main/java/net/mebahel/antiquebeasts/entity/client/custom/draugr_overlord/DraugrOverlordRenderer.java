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
    public Vec3d getSwordPosition(DraugrOverlordEntity entity) {
        DraugrOverlordModel model = (DraugrOverlordModel) this.getGeoModel();
        CoreGeoBone swordBone = model.getSwordBone(entity);

        if (swordBone == null) {
            System.out.println("[ERROR] Sword bone not found!");
            return entity.getPos();
        }

        // 🔹 Utiliser getPivotX(), getPivotY(), getPivotZ() pour la position fixe de l'épée
        double swordX = swordBone.getPivotX() / 16.0;
        double swordY = swordBone.getPivotY() / 16.0;
        double swordZ = swordBone.getPivotZ() / 16.0;

        // 🔹 Convertir en position globale en ajoutant la position de l'entité
        return new Vec3d(entity.getX() + swordX, entity.getY() + swordY, entity.getZ() + swordZ);
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
