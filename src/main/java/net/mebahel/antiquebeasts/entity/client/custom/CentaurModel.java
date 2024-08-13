package net.mebahel.antiquebeasts.entity.client.custom;

import com.google.common.collect.Maps;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.custom.greek.CentaurEntity;
import net.mebahel.antiquebeasts.entity.variant.CentaurVariant;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

import java.util.Map;

public class CentaurModel extends AnimatedGeoModel<CentaurEntity> {
    float head_x;
    float head_y;
    float max_rotation_x = 35;
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
    public void setCustomAnimations(CentaurEntity animatable, int instanceId, AnimationEvent animationEvent) {
        super.setCustomAnimations(animatable, instanceId, animationEvent);
        IBone head = this.getAnimationProcessor().getBone("head");
        EntityModelData extraData = (EntityModelData) animationEvent.getExtraDataOfType(EntityModelData.class).get(0);
        AnimationData manager = animatable.getFactory().getOrCreateAnimationData(instanceId);
        int unpausedMultiplier = !MinecraftClient.getInstance().isPaused() || manager.shouldPlayWhilePaused ? 1 : 0;


        if (head.getRotationY() > max_rotation_x) {
            head_x = max_rotation_x;
        } else if (head.getRotationY() < -max_rotation_x) {
            head_x = -max_rotation_x;
        } else {
            head_x = head.getRotationY() + (extraData.netHeadYaw * ((float) Math.PI / 340F)) * unpausedMultiplier;
        }

        head_y = head.getRotationZ() + (extraData.headPitch * ((float) Math.PI / 170F)) * unpausedMultiplier;

        if (head_x > 1.3f) {
            head_x = 1.3f;
        } else if (head_x < -1.3f) {
            head_x = -1.3f;
        }

        head.setRotationY(head_x);
        head.setRotationX(head_y);
    }
}