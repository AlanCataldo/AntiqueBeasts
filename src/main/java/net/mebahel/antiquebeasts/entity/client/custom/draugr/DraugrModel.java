package net.mebahel.antiquebeasts.entity.client.custom.draugr;

import com.google.common.collect.Maps;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.custom.other.DraugrEntity;
import net.mebahel.antiquebeasts.entity.variant.DraugrVariant;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import java.util.Map;

public class DraugrModel extends GeoModel<DraugrEntity> {
    public static final Map<DraugrVariant, Identifier> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(DraugrVariant.class), (map) -> {
                map.put(DraugrVariant.TEMPERATE,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/draugr/draugr_temperate.png"));
                map.put(DraugrVariant.COLD,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/draugr/draugr_cold.png"));
                map.put(DraugrVariant.HOT,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/draugr/draugr_hot.png"));
                map.put(DraugrVariant.TEMPERATE_AXE,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/draugr/draugr_temperate_axe.png"));
                map.put(DraugrVariant.COLD_AXE,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/draugr/draugr_cold_axe.png"));
                map.put(DraugrVariant.HOT_AXE,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/draugr/draugr_hot_axe.png"));
            });
    @Override
    public Identifier getModelResource(DraugrEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/draugr.geo.json");
    }

    @Override
    public Identifier getTextureResource(DraugrEntity animatable) {
        return LOCATION_BY_VARIANT.get(animatable.getVariant());
    }
    @Override
    public Identifier getAnimationResource(DraugrEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/draugr.animation.json");
    }
    @Override
    public void setCustomAnimations(DraugrEntity entity, long uniqueID, AnimationState<DraugrEntity> customPredicate) {
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