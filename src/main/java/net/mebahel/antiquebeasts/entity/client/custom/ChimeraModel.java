package net.mebahel.antiquebeasts.entity.client.custom;

import com.google.common.collect.Maps;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.custom.ChimeraEntity;
import net.mebahel.antiquebeasts.entity.variant.ChimeraVariant;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import java.util.Map;

public class ChimeraModel extends GeoModel<ChimeraEntity> {
    public static final Map<ChimeraVariant, Identifier> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(ChimeraVariant.class), (map) -> {
                map.put(ChimeraVariant.DEFAULT,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/archimera_texture_1.png"));
                map.put(ChimeraVariant.CLOAK,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/archimera_texture_1.png"));
            });
    @Override
    public Identifier getModelResource(ChimeraEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/chimera.geo.json");
    }
    public Identifier getTextureResource(ChimeraEntity animatable) {
        return LOCATION_BY_VARIANT.get(animatable.getVariant());
    }
    @Override
    public Identifier getAnimationResource(ChimeraEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/chimera.animation.json");
    }
    @Override
    public void setCustomAnimations(ChimeraEntity entity, long uniqueID, AnimationState<ChimeraEntity> customPredicate) {
        if (!entity.isAttacking()) {
            CoreGeoBone head = this.getBone("lion_head").orElse(null);
            EntityModelData extraData = customPredicate.getData(DataTickets.ENTITY_MODEL_DATA);
            float yawAngle = 0.125F * extraData.netHeadYaw() * 0.017453292F;
            float pitchAngle = 0.125F * extraData.headPitch() * 0.017453292F;
            if (Math.abs(yawAngle) > 0.6F) {
                yawAngle = 0.0F;
            }
            if (head != null) {
                head.setRotY(3.0F * yawAngle);
                head.setRotX(3.0F * pitchAngle);
            }
        }
    }
}