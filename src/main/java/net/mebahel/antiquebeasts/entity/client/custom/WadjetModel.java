package net.mebahel.antiquebeasts.entity.client.custom;

import com.google.common.collect.Maps;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.custom.egyptian.WadjetEntity;
import net.mebahel.antiquebeasts.entity.variant.WadjetVariant;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import java.util.Map;

public class WadjetModel extends GeoModel<WadjetEntity> {
    public static final Map<WadjetVariant, Identifier> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(WadjetVariant.class), (map) -> {
                map.put(WadjetVariant.DEFAULT,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/wajdet_texture.png"));
                map.put(WadjetVariant.CLOAK,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/wajdet_texture_2.png"));
            });
    @Override
    public Identifier getModelResource(WadjetEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/wajdet.geo.json");
    }

    @Override
    public Identifier getTextureResource(WadjetEntity animatable) {
        return LOCATION_BY_VARIANT.get(animatable.getVariant());
    }
    @Override
    public Identifier getAnimationResource(WadjetEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/wajdet.animation.json");
    }
    @Override
    public void setCustomAnimations(WadjetEntity entity, long uniqueID, AnimationState<WadjetEntity> customPredicate) {
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