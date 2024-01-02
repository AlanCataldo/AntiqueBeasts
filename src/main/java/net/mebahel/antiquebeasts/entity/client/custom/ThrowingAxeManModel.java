package net.mebahel.antiquebeasts.entity.client.custom;

import com.google.common.collect.Maps;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.custom.ThrowingAxeManEntity;
import net.mebahel.antiquebeasts.entity.variant.ThrowingAxeManVariant;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import java.util.Map;

public class ThrowingAxeManModel extends GeoModel<ThrowingAxeManEntity> {
    public static final Map<ThrowingAxeManVariant, Identifier> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(ThrowingAxeManVariant.class), (map) -> {
                map.put(ThrowingAxeManVariant.DEFAULT,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/throwing_axeman1_texture.png"));
                map.put(ThrowingAxeManVariant.CLOAK,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/throwing_axeman2_texture.png"));
            });
    @Override
    public Identifier getModelResource(ThrowingAxeManEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/throwing_axeman.geo.json");
    }

    @Override
    public Identifier getTextureResource(ThrowingAxeManEntity animatable) {
        return LOCATION_BY_VARIANT.get(animatable.getVariant());
    }
    @Override
    public Identifier getAnimationResource(ThrowingAxeManEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/throwing_axeman.animation.json");
    }
    @Override
    public void setCustomAnimations(ThrowingAxeManEntity entity, long uniqueID, AnimationState<ThrowingAxeManEntity> customPredicate) {
        CoreGeoBone head = this.getBone("head").orElse(null);
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