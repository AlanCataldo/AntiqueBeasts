package net.mebahel.antiquebeasts.entity.client.custom;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.custom.HeroHopliteEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class HeroHopliteModel extends AnimatedGeoModel<HeroHopliteEntity> {
    float head_x;
    float head_y;
    float max_rotation_x = 35;

    @Override
    public Identifier getModelResource(HeroHopliteEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/hero_hoplite.geo.json");
    }

    @Override
    public Identifier getTextureResource(HeroHopliteEntity object) {
        return HeroHopliteRenderer.LOCATION_BY_VARIANT.get(object.getVariant());
    }

    @Override
    public Identifier getAnimationResource(HeroHopliteEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/champion_hoplite.animation.json");
    }
    @Override
    public void setCustomAnimations(HeroHopliteEntity animatable, int instanceId, AnimationEvent animationEvent) {
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

        head_y = head.getRotationZ() + (extraData.headPitch * ((float) Math.PI / 340F)) * unpausedMultiplier;

        if (head_x > 1.3f) {
            head_x = 1.3f;
        } else if (head_x < -1.3f) {
            head_x = -1.3f;
        }

        head.setRotationY(head_x);
        head.setRotationZ(head_y);
    }
}