package net.mebahel.antiquebeasts.entity.client.custom;

import com.google.common.collect.Maps;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.custom.norse.HersirEntity;
import net.mebahel.antiquebeasts.entity.variant.HersirVariant;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import java.util.Map;

public class HersirModel extends GeoModel<HersirEntity> {
    public static final Map<HersirVariant, Identifier> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(HersirVariant.class), (map) -> {
                map.put(HersirVariant.DEFAULT,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/hersir1_texture.png"));
                map.put(HersirVariant.CLOAK,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/hersir2_texture.png"));
            });
    @Override
    public Identifier getModelResource(HersirEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/hersir.geo.json");
    }
    public Identifier getTextureResource(HersirEntity animatable) {
        return LOCATION_BY_VARIANT.get(animatable.getVariant());
    }
    @Override
    public Identifier getAnimationResource(HersirEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/hersir.animation.json");
    }
    @Override
    public void setCustomAnimations(HersirEntity entity, long uniqueID, AnimationState<HersirEntity> customPredicate) {
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