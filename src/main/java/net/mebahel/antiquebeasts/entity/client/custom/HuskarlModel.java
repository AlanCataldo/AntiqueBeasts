package net.mebahel.antiquebeasts.entity.client.custom;

import com.google.common.collect.Maps;
import net.mebahel.antiquebeasts.AntiqueBeasts;

import net.mebahel.antiquebeasts.entity.custom.norse.HuskarlEntity;
import net.mebahel.antiquebeasts.entity.variant.HersirVariant;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

import java.util.Map;

public class HuskarlModel extends AnimatedGeoModel<HuskarlEntity> {
    float head_x;
    float head_y;
    float max_rotation_x = 35;

    public static final Map<HersirVariant, Identifier> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(HersirVariant.class), (map) -> {
                map.put(HersirVariant.DEFAULT,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/huskarl1_texture.png"));
                map.put(HersirVariant.CLOAK,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/huskarl2_texture.png"));
            });
    @Override
    public Identifier getModelResource(HuskarlEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/huskarl.geo.json");
    }
    public Identifier getTextureResource(HuskarlEntity animatable) {
        return LOCATION_BY_VARIANT.get(animatable.getVariant());
    }
    @Override
    public Identifier getAnimationResource(HuskarlEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/huskarl.animation.json");
    }

    @Override
    public void setCustomAnimations(HuskarlEntity animatable, int instanceId, AnimationEvent animationEvent) {
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