package net.mebahel.antiquebeasts.entity.client.custom.harpy;

import com.google.common.collect.Maps;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.custom.other.HarpyEntity;
import net.mebahel.antiquebeasts.entity.variant.HarpyVariant;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import java.util.Map;

public class HarpyModel extends GeoModel<HarpyEntity> {
    public static final Map<HarpyVariant, Identifier> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(HarpyVariant.class), (map) -> {
                map.put(HarpyVariant.DEFAULT,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/harpy.png"));
                map.put(HarpyVariant.CLOAK,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/harpy.png"));
            });
    @Override
    public Identifier getModelResource(HarpyEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/harpy.geo.json");
    }

    @Override
    public Identifier getTextureResource(HarpyEntity animatable) {
        return LOCATION_BY_VARIANT.get(animatable.getVariant());
    }
    @Override
    public Identifier getAnimationResource(HarpyEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/harpy.animation.json");
    }
    @Override
    public void setCustomAnimations(HarpyEntity entity, long uniqueID, AnimationState<HarpyEntity> customPredicate) {
        CoreGeoBone head = this.getBone("Head").orElse(null);
        EntityModelData extraData = customPredicate.getData(DataTickets.ENTITY_MODEL_DATA);
        float yawAngle = 0.125F * extraData.netHeadYaw() * 0.017453292F;
        float pitchAngle = 0.125F * extraData.headPitch() * 0.017453292F;

        if (head != null) {
            //head.setRotX(7.0F * pitchAngle); de haut en bas
            head.setRotY(7.0F * yawAngle);
            //head.setRotX(7.0F * pitchAngle);
        }
    }

}