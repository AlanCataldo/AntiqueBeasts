package net.mebahel.antiquebeasts.entity.client.custom.draugr_wight;

import com.google.common.collect.Maps;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.custom.other.DraugrWightEntity;
import net.mebahel.antiquebeasts.entity.variant.DraugrVariant;
import net.mebahel.antiquebeasts.entity.variant.DraugrWightVariant;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import java.util.Map;

public class DraugrWightModel extends GeoModel<DraugrWightEntity> {
    public static final Map<DraugrWightVariant, Identifier> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(DraugrWightVariant.class), (map) -> {
                map.put(DraugrWightVariant.TEMPERATE,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/draugr/draugr_wight_temperate.png"));
                map.put(DraugrWightVariant.COLD,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/draugr/draugr_wight_cold.png"));
                map.put(DraugrWightVariant.HOT,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/draugr/draugr_wight_hot.png"));
                map.put(DraugrWightVariant.TEMPERATE_AXE,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/draugr/draugr_wight_temperate_axe.png"));
                map.put(DraugrWightVariant.COLD_AXE,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/draugr/draugr_wight_cold_axe.png"));
                map.put(DraugrWightVariant.HOT_AXE,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/draugr/draugr_wight_hot_axe.png"));
            });
    @Override
    public Identifier getModelResource(DraugrWightEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/draugr.geo.json");
    }

    @Override
    public Identifier getTextureResource(DraugrWightEntity animatable) {
        return LOCATION_BY_VARIANT.get(animatable.getWightVariant());
    }
    @Override
    public Identifier getAnimationResource(DraugrWightEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/draugr.animation.json");
    }
    @Override
    public void setCustomAnimations(DraugrWightEntity entity, long uniqueID, AnimationState<DraugrWightEntity> customPredicate) {
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