package net.mebahel.antiquebeasts.entity.client.custom.draugr_scourge;

import com.google.common.collect.Maps;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.custom.other.DraugrScourgeEntity;
import net.mebahel.antiquebeasts.entity.variant.DraugrScourgeVariant;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import java.util.Map;

public class DraugrScourgeModel extends GeoModel<DraugrScourgeEntity> {
    public static final Map<DraugrScourgeVariant, Identifier> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(DraugrScourgeVariant.class), (map) -> {
                map.put(DraugrScourgeVariant.TEMPERATE,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/draugr_scourge/draugr_scourge_temperate.png"));
                map.put(DraugrScourgeVariant.COLD,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/draugr_scourge/draugr_scourge_cold.png"));
                map.put(DraugrScourgeVariant.HOT,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/draugr_scourge/draugr_scourge_hot.png"));
            });
    @Override
    public Identifier getModelResource(DraugrScourgeEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/draugr_scourge.geo.json");
    }

    @Override
    public Identifier getTextureResource(DraugrScourgeEntity animatable) {
        return LOCATION_BY_VARIANT.get(animatable.getScourgeVariant());
    }
    @Override
    public Identifier getAnimationResource(DraugrScourgeEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/draugr.animation.json");
    }
    @Override
    public void setCustomAnimations(DraugrScourgeEntity entity, long uniqueID, AnimationState<DraugrScourgeEntity> customPredicate) {
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