package net.mebahel.antiquebeasts.entity.client.custom.dwarven_spider_guardian;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.custom.dwarven.DwarvenSpiderGuardianEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class DwarvenSpidersGuardianModel extends GeoModel<DwarvenSpiderGuardianEntity> {
    private static final Identifier TEXTURE = new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/dwarven_spider/dwarven_spider_guardian_texture.png");
    @Override
    public Identifier getModelResource(DwarvenSpiderGuardianEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/dwarven_spider_guardian.geo.json");
    }
    public Identifier getTextureResource(DwarvenSpiderGuardianEntity animatable) {
        return TEXTURE;
    }
    @Override
    public Identifier getAnimationResource(DwarvenSpiderGuardianEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/dwarven_spider_guardian.animation.json");
    }
    @Override
    public void setCustomAnimations(DwarvenSpiderGuardianEntity entity, long uniqueID, AnimationState<DwarvenSpiderGuardianEntity> customPredicate) {

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