package net.mebahel.antiquebeasts.entity.client.custom.infernal_draugr;

import com.google.common.collect.Maps;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.custom.other.InfernalDraugrEntity;
import net.mebahel.antiquebeasts.entity.variant.InfernalDraugrVariant;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import java.util.Map;

public class InfernalDraugrModel extends GeoModel<InfernalDraugrEntity> {
    public static final Map<InfernalDraugrVariant, Identifier> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(InfernalDraugrVariant.class), (map) -> {
                map.put(InfernalDraugrVariant.VARIANT_1,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/infernal_draugr/infernal_draugr_1.png"));
                map.put(InfernalDraugrVariant.VARIANT_2,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/infernal_draugr/infernal_draugr_2.png"));
            });
    @Override
    public Identifier getModelResource(InfernalDraugrEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/infernal_draugr.geo.json");
    }

    @Override
    public Identifier getTextureResource(InfernalDraugrEntity animatable) {
        return LOCATION_BY_VARIANT.get(animatable.getWightVariant());
    }
    @Override
    public Identifier getAnimationResource(InfernalDraugrEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/infernal_draugr.animation.json");
    }
    @Override
    public void setCustomAnimations(InfernalDraugrEntity entity, long uniqueID, AnimationState<InfernalDraugrEntity> customPredicate) {
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