package net.mebahel.antiquebeasts.entity.client.custom;

import com.google.common.collect.Maps;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.custom.AxemanEntity;
import net.mebahel.antiquebeasts.entity.custom.CamelryEntity;
import net.mebahel.antiquebeasts.entity.variant.EgyptiantVariant;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import java.util.Map;

public class CamelryModel extends GeoModel<CamelryEntity> {
    public static final Map<EgyptiantVariant, Identifier> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(EgyptiantVariant.class), (map) -> {
                map.put(EgyptiantVariant.DEFAULT,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/camelry_texture.png"));
                map.put(EgyptiantVariant.CLOAK,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/camelry_texture.png"));
            });
    @Override
    public Identifier getModelResource(CamelryEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/camelry.geo.json");
    }

    @Override
    public Identifier getTextureResource(CamelryEntity animatable) {
        return LOCATION_BY_VARIANT.get(animatable.getVariant());
    }
    @Override
    public Identifier getAnimationResource(CamelryEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/camelry.animation.json");
    }
    @Override
    public void setCustomAnimations(CamelryEntity entity, long uniqueID, AnimationState<CamelryEntity> customPredicate) {
        CoreGeoBone head2 = this.getBone("head2").orElse(null);
        EntityModelData extraData = customPredicate.getData(DataTickets.ENTITY_MODEL_DATA);
        float yawAngle = 0.125F * extraData.netHeadYaw() * 0.017453292F;
        float pitchAngle = 0.125F * extraData.headPitch() * 0.017453292F;
        if (Math.abs(yawAngle) > 0.6F) {
            yawAngle = 0.0F;
        }
        if (head2 != null) {
            head2.setRotY(4.0F * yawAngle);
            head2.setRotX(4.0F * pitchAngle);
        }
    }

}