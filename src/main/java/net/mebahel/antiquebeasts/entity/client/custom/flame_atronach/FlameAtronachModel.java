package net.mebahel.antiquebeasts.entity.client.custom.flame_atronach;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.custom.other.FlameAtronachEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class FlameAtronachModel extends GeoModel<FlameAtronachEntity> {

    @Override
    public Identifier getModelResource(FlameAtronachEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/flame_atronach.geo.json");
    }

    @Override
    public Identifier getTextureResource(FlameAtronachEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/flame_atronach/flame_atronach_texture.png");
    }
    @Override
    public Identifier getAnimationResource(FlameAtronachEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/flame_atronach.animation.json");
    }
    @Override
    public void setCustomAnimations(FlameAtronachEntity entity, long uniqueID, AnimationState<FlameAtronachEntity> customPredicate) {
        CoreGeoBone head = this.getBone("Head").orElse(null);
        EntityModelData extraData = customPredicate.getData(DataTickets.ENTITY_MODEL_DATA);
        float yawAngle = 0.125F * extraData.netHeadYaw() * 0.017453292F;
        float pitchAngle = 0.125F * extraData.headPitch() * 0.017453292F;
        if (Math.abs(yawAngle) > 0.6F) {
            yawAngle = 0.0F;
        }
        if (head != null) {
            head.setRotY(7.0F * yawAngle);
            head.setRotX(7.0F * pitchAngle);
        }
    }
}