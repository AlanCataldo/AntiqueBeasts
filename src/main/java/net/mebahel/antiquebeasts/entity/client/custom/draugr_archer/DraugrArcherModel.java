package net.mebahel.antiquebeasts.entity.client.custom.draugr_archer;

import com.google.common.collect.Maps;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.custom.other.DraugrArcherEntity;
import net.mebahel.antiquebeasts.entity.variant.DraugrArcherVariant;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import java.util.Map;

public class DraugrArcherModel extends GeoModel<DraugrArcherEntity> {
    public static final Map<DraugrArcherVariant, Identifier> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(DraugrArcherVariant.class), (map) -> {
                map.put(DraugrArcherVariant.TEMPERATE,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/draugr_archer/draugr_archer_temperate.png"));
                map.put(DraugrArcherVariant.COLD,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/draugr_archer/draugr_archer_cold.png"));
                map.put(DraugrArcherVariant.HOT,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/draugr_archer/draugr_archer_hot.png"));
            });
    @Override
    public Identifier getModelResource(DraugrArcherEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/draugr_archer.geo.json");
    }

    @Override
    public Identifier getTextureResource(DraugrArcherEntity animatable) {
        return LOCATION_BY_VARIANT.get(animatable.getArcherVariant());
    }
    @Override
    public Identifier getAnimationResource(DraugrArcherEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/draugr.animation.json");
    }
    @Override
    public void setCustomAnimations(DraugrArcherEntity entity, long uniqueID, AnimationState<DraugrArcherEntity> customPredicate) {
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