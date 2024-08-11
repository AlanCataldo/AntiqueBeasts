package net.mebahel.antiquebeasts.entity.client.custom;

import com.google.common.collect.Maps;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.custom.norse.EinherjarEntity;
import net.mebahel.antiquebeasts.entity.variant.EinherjarVariant;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import java.util.Map;

public class EinherjarModel extends GeoModel<EinherjarEntity> {
    public static final Map<EinherjarVariant, Identifier> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(EinherjarVariant.class), (map) -> {
                map.put(EinherjarVariant.DEFAULT,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/einherjar_texture.png"));
                map.put(EinherjarVariant.CLOAK,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/einherjar_texture.png"));
            });
    @Override
    public Identifier getModelResource(EinherjarEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/einherjar.geo.json");
    }
    public Identifier getTextureResource(EinherjarEntity animatable) {
        return LOCATION_BY_VARIANT.get(animatable.getVariant());
    }
    @Override
    public Identifier getAnimationResource(EinherjarEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/einherjar.animation.json");
    }
    @Override
    public void setCustomAnimations(EinherjarEntity entity, long uniqueID, AnimationState<EinherjarEntity> customPredicate) {
        CoreGeoBone head = this.getBone("head").orElse(null);
        EntityModelData extraData = customPredicate.getData(DataTickets.ENTITY_MODEL_DATA);
        float yawAngle = 0.125F * extraData.netHeadYaw() * 0.017453292F;
        float pitchAngle = 0.125F * extraData.headPitch() * 0.017453292F;
        if (Math.abs(yawAngle) > 0.6F) {
            yawAngle = 0.0F;
        }
        if (head != null) {
            head.setRotY(1.3F * yawAngle);
            head.setRotX(7.0F * pitchAngle);
        }
    }
}