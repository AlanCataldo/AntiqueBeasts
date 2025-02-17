package net.mebahel.antiquebeasts.entity.client.custom.draugr_overlord;

import com.google.common.collect.Maps;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.custom.other.DraugrOverlordEntity;
import net.mebahel.antiquebeasts.entity.variant.DraugrOverlordVariant;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import java.util.Map;

public class DraugrOverlordModel extends GeoModel<DraugrOverlordEntity> {
    public static final Map<DraugrOverlordVariant, Identifier> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(DraugrOverlordVariant.class), (map) -> {
                map.put(DraugrOverlordVariant.GREATSWORD,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/draugr/draugr_overlord.png"));
            });
    @Override
    public Identifier getModelResource(DraugrOverlordEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/draugr_overlord.geo.json");
    }

    @Override
    public Identifier getTextureResource(DraugrOverlordEntity animatable) {
        return LOCATION_BY_VARIANT.get(animatable.getOverlordVariant());
    }
    @Override
    public Identifier getAnimationResource(DraugrOverlordEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/draugr_overlord.animation.json");
    }
    @Override
    public void setCustomAnimations(DraugrOverlordEntity entity, long uniqueID, AnimationState<DraugrOverlordEntity> customPredicate) {
        CoreGeoBone head = this.getBone("Head").orElse(null);
        EntityModelData extraData = customPredicate.getData(DataTickets.ENTITY_MODEL_DATA);
        float yawAngle = 0.125F * extraData.netHeadYaw() * 0.017453292F;
        float pitchAngle = 0.125F * extraData.headPitch() * 0.017453292F;
        if (Math.abs(yawAngle) > 0.6F) {
            yawAngle = 0.0F;
        }
        if (head != null) {
            head.setRotY(4.0F * yawAngle);
            head.setRotX(4.0F * pitchAngle);
        }
    }
    public CoreGeoBone getSwordBone(DraugrOverlordEntity entity) {
        return this.getAnimationProcessor().getBone("bone");
    }
}