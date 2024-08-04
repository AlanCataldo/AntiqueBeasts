package net.mebahel.antiquebeasts.entity.client.custom;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.custom.FrostCyclopsEntity;
import net.mebahel.antiquebeasts.entity.custom.HadesChosenEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class FrostCyclopsModel extends GeoModel<FrostCyclopsEntity> {
    private static final Identifier TEXTURE = new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/frost_cyclops_texture.png");
    private static final Identifier BLINK_TEXTURE = new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/blink_frost_cyclops_texture.png");
    @Override
    public Identifier getModelResource(FrostCyclopsEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/frost_cyclops.geo.json");
    }
    @Override
    public Identifier getTextureResource(FrostCyclopsEntity object) {
        if (object.isBlinking()) {
            return BLINK_TEXTURE;
        } else {
            return TEXTURE;
        }
    }

    @Override
    public Identifier getAnimationResource(FrostCyclopsEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/cyclops.animation.json");
    }
    @Override
    public void setCustomAnimations(FrostCyclopsEntity entity, long uniqueID, AnimationState<FrostCyclopsEntity> customPredicate) {
        CoreGeoBone head = this.getBone("head").orElse(null);
        EntityModelData extraData = customPredicate.getData(DataTickets.ENTITY_MODEL_DATA);
        float yawAngle = 0.125F * extraData.netHeadYaw() * 0.017453292F;
        float pitchAngle = 0.125F * extraData.headPitch() * 0.017453292F;
        if (Math.abs(pitchAngle) > 0.6F) {
            pitchAngle = 0.6F;
        }
        if (head != null) {
            head.setRotY(4.0F * yawAngle);
            head.setRotX(4.0F * pitchAngle);
        }
    }
}
