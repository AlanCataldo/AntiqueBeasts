package net.mebahel.antiquebeasts.entity.client.custom;

import com.google.common.collect.Maps;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.custom.ChampionHopliteEntity;
import net.mebahel.antiquebeasts.entity.custom.CyclopsEntity;
import net.mebahel.antiquebeasts.entity.custom.EinherjarEntity;
import net.mebahel.antiquebeasts.entity.variant.CyclopsVariant;
import net.mebahel.antiquebeasts.entity.variant.EinherjarVariant;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import java.util.Map;

public class CyclopsModel extends GeoModel<CyclopsEntity> {
    public static final Map<CyclopsVariant, Identifier> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(CyclopsVariant.class), (map) -> {
                map.put(CyclopsVariant.DEFAULT,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/cyclops_texture.png"));
                map.put(CyclopsVariant.CLOAK,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/cyclops_texture1.png"));
            });
    @Override
    public Identifier getModelResource(CyclopsEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/cyclops.geo.json");
    }
    public Identifier getTextureResource(CyclopsEntity animatable) {
        return LOCATION_BY_VARIANT.get(animatable.getVariant());
    }
    @Override
    public Identifier getAnimationResource(CyclopsEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/cyclops.animation.json");
    }
    @Override
    public void setCustomAnimations(CyclopsEntity entity, long uniqueID, AnimationState<CyclopsEntity> customPredicate) {
        CoreGeoBone head = this.getBone("head").orElse(null);
        EntityModelData extraData = customPredicate.getData(DataTickets.ENTITY_MODEL_DATA);
        float yawAngle = 0.125F * extraData.netHeadYaw() * 0.017453292F;
        float pitchAngle = 0.125F * extraData.headPitch() * 0.017453292F;
        if (Math.abs(yawAngle) > 0.6F) {
            yawAngle = 0.0F;
        }
        if (head != null) {
            head.setRotY(7.0F * yawAngle);
            head.setRotZ(7.0F * pitchAngle);
        }
    }
}