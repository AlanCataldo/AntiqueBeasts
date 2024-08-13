package net.mebahel.antiquebeasts.entity.client.custom;

import com.google.common.collect.Maps;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.custom.norse.ThrowingAxeManEntity;
import net.mebahel.antiquebeasts.entity.variant.ThrowingAxeManVariant;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

import java.util.Map;

public class ThrowingAxeManModel extends AnimatedGeoModel<ThrowingAxeManEntity> {
    public static final Map<ThrowingAxeManVariant, Identifier> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(ThrowingAxeManVariant.class), (map) -> {
                map.put(ThrowingAxeManVariant.DEFAULT,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/throwing_axeman1_texture.png"));
                map.put(ThrowingAxeManVariant.CLOAK,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/throwing_axeman2_texture.png"));
            });
    float head_x;
    float head_y;
    float max_rotation_x = 35;

    @Override
    public Identifier getModelResource(ThrowingAxeManEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/throwing_axeman.geo.json");
    }

    @Override
    public Identifier getTextureResource(ThrowingAxeManEntity animatable) {
        return LOCATION_BY_VARIANT.get(animatable.getVariant());
    }
    @Override
    public Identifier getAnimationResource(ThrowingAxeManEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/throwing_axeman.animation.json");
    }

    @Override
    public void setCustomAnimations(ThrowingAxeManEntity animatable, int instanceId, AnimationEvent animationEvent) {
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