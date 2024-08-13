package net.mebahel.antiquebeasts.entity.client.custom;

import com.google.common.collect.Maps;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.custom.ChimeraEntity;
import net.mebahel.antiquebeasts.entity.variant.ChimeraVariant;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

import java.util.Map;

public class ChimeraModel extends AnimatedGeoModel<ChimeraEntity> {
    float head_x;
    float head_y;
    float max_rotation_x = 35;

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
    public void setCustomAnimations(ChimeraEntity animatable, int instanceId, AnimationEvent animationEvent) {
        super.setCustomAnimations(animatable, instanceId, animationEvent);
        IBone head = this.getAnimationProcessor().getBone("lion_head");
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