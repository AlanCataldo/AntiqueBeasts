package net.mebahel.antiquebeasts.entity.client.custom;

import com.google.common.collect.Maps;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.custom.norse.ValkyrieEntity;
import net.mebahel.antiquebeasts.entity.variant.ValkyrieVariant;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import java.util.Map;

public class ValkyrieModel extends GeoModel<ValkyrieEntity> {
    public static final Map<ValkyrieVariant, Identifier> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(ValkyrieVariant.class), (map) -> {
                map.put(ValkyrieVariant.DEFAULT,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/valkyrie_texture.png"));
                map.put(ValkyrieVariant.CLOAK,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/valkyrie_texture.png"));
            });
    @Override
    public Identifier getModelResource(ValkyrieEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/valkyrie.geo.json");
    }
    public Identifier getTextureResource(ValkyrieEntity animatable) {
        return LOCATION_BY_VARIANT.get(animatable.getVariant());
    }
    @Override
    public Identifier getAnimationResource(ValkyrieEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/valkyrie.animation.json");
    }
    @Override
    public void setCustomAnimations(ValkyrieEntity entity, long uniqueID, AnimationState<ValkyrieEntity> customPredicate) {
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