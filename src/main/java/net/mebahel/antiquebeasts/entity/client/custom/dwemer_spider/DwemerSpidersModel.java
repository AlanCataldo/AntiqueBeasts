package net.mebahel.antiquebeasts.entity.client.custom.dwemer_spider;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.custom.dwemer.DwemerSpiderEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class DwemerSpidersModel extends GeoModel<DwemerSpiderEntity> {
    private static final Identifier TEXTURE = new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/dwemer_spider/dwemer_spider_texture.png");
    @Override
    public Identifier getModelResource(DwemerSpiderEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/dwemer_spider.geo.json");
    }
    public Identifier getTextureResource(DwemerSpiderEntity animatable) {
        return TEXTURE;
    }
    @Override
    public Identifier getAnimationResource(DwemerSpiderEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/dwemer_spider.animation.json");
    }
    @Override
    public void setCustomAnimations(DwemerSpiderEntity entity, long uniqueID, AnimationState<DwemerSpiderEntity> customPredicate) {
        String currentAnimation = entity.getCurrentAnimation();

        if (!"walk".equals(currentAnimation) && !"mine".equals(currentAnimation)) {
            CoreGeoBone head = this.getBone("body").orElse(null);
            EntityModelData extraData = customPredicate.getData(DataTickets.ENTITY_MODEL_DATA);
            if (head != null) {
                // ✅ Appliquer la rotation du joueur SEULEMENT si ce n'est PAS idle3
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
}