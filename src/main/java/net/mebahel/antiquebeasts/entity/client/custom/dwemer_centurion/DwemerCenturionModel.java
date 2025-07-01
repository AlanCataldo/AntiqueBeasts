package net.mebahel.antiquebeasts.entity.client.custom.dwemer_centurion;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.custom.dwemer.DwemerCenturionEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class DwemerCenturionModel extends GeoModel<DwemerCenturionEntity> {
    private static final Identifier TEXTURE = new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/dwemer_centurion/dwemer_centurion_new.png");
    @Override
    public Identifier getModelResource(DwemerCenturionEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/dwemer_centurion.geo.json");
    }
    public Identifier getTextureResource(DwemerCenturionEntity animatable) {
        return TEXTURE;
    }
    @Override
    public Identifier getAnimationResource(DwemerCenturionEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/dwemer_centurion.animation.json");
    }
    @Override
    public void setCustomAnimations(DwemerCenturionEntity entity, long uniqueID, AnimationState<DwemerCenturionEntity> customPredicate) {
        CoreGeoBone head = this.getBone("head").orElse(null);
        EntityModelData extraData = customPredicate.getData(DataTickets.ENTITY_MODEL_DATA);
        if (head != null) {
            float yawAngle = 0.125F * extraData.netHeadYaw() * 0.017453292F;
            float pitchAngle = 0.125F * extraData.headPitch() * 0.017453292F;

            if (Math.abs(yawAngle) > 0.4F) {
                yawAngle = 0.0F;
            }

            head.setRotY(7.0F * yawAngle);
            head.setRotX(7.0F * pitchAngle);
        }
    }
}