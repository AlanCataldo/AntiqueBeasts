package net.mebahel.antiquebeasts.entity.client.custom;

import com.google.common.collect.Maps;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.custom.greek.CentaurEntity;
import net.mebahel.antiquebeasts.entity.variant.CentaurVariant;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import java.util.Map;

public class CentaurModel extends GeoModel<CentaurEntity> {
    private static final Identifier DEFAULT_BLINK = new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/blink_centaur_texture.png");
    private static final Identifier DEFAULT_2_BLINK = new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/blink_centaur_2_texture.png");
    private static final Identifier ARCHER_BLINK = new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/blink_centaur_archer_texture.png");
    private static final Identifier ARCHER_2_BLINK = new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/blink_centaur_archer_2_texture.png");

    public static final Map<CentaurVariant, Identifier> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(CentaurVariant.class), (map) -> {
                map.put(CentaurVariant.DEFAULT,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/centaur_texture.png"));
                map.put(CentaurVariant.DEFAULT_2,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/centaur_2_texture.png"));
                map.put(CentaurVariant.ARCHER,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/centaur_archer_texture.png"));
                map.put(CentaurVariant.ARCHER_2,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/centaur_archer_2_texture.png"));

            });
    @Override
    public Identifier getModelResource(CentaurEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/centaur.geo.json");
    }
    public Identifier getTextureResource(CentaurEntity animatable) {
        if (animatable.isBlinking() && animatable.getVariant() == CentaurVariant.DEFAULT) {
            return DEFAULT_BLINK;
        } else if (!animatable.isBlinking() && animatable.getVariant() == CentaurVariant.DEFAULT){
            return LOCATION_BY_VARIANT.get(animatable.getVariant());
        }

        if (animatable.isBlinking() && animatable.getVariant() == CentaurVariant.DEFAULT_2) {
            return DEFAULT_2_BLINK;
        } else if (!animatable.isBlinking() && animatable.getVariant() == CentaurVariant.DEFAULT_2){
            return LOCATION_BY_VARIANT.get(animatable.getVariant());
        }

        if (animatable.isBlinking() && animatable.getVariant() == CentaurVariant.ARCHER) {
            return ARCHER_BLINK;
        } else if (!animatable.isBlinking() && animatable.getVariant() == CentaurVariant.ARCHER){
            return LOCATION_BY_VARIANT.get(animatable.getVariant());
        }

        if (animatable.isBlinking() && animatable.getVariant() == CentaurVariant.ARCHER_2) {
            return ARCHER_2_BLINK;
        } else if (!animatable.isBlinking() && animatable.getVariant() == CentaurVariant.ARCHER_2){
            return LOCATION_BY_VARIANT.get(animatable.getVariant());
        }

        return LOCATION_BY_VARIANT.get(animatable.getVariant());
    }
    @Override
    public Identifier getAnimationResource(CentaurEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/centaur.animation.json");
    }
    @Override
    public void setCustomAnimations(CentaurEntity entity, long uniqueID, AnimationState<CentaurEntity> customPredicate) {
        CoreGeoBone head = this.getBone("head").orElse(null);
        EntityModelData extraData = customPredicate.getData(DataTickets.ENTITY_MODEL_DATA);
        float yawAngle = 0.125F * extraData.netHeadYaw() * 0.017453292F;
        float pitchAngle = 0.125F * extraData.headPitch() * 0.017453292F;
        if (Math.abs(pitchAngle) > 0.6F) {
            pitchAngle = 0.6F;
        }
        if (head != null) {
            head.setRotY(4.0F * yawAngle);
            head.setRotX(4.0F * pitchAngle);
        }
    }
}