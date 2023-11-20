package net.mebahel.antiquebeasts.entity.client.custom;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.custom.ChampionHopliteEntity;
import net.mebahel.antiquebeasts.entity.custom.HadesChosenEntity;
import net.mebahel.antiquebeasts.entity.custom.HadesShadeEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class HadesChosenModel extends AnimatedGeoModel<HadesChosenEntity> {
    float head_x;
    float head_y;
    float max_rotation_x = 35;
    @Override
    public Identifier getModelResource(HadesChosenEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/hades_chosen.geo.json");
    }
    @Override
    public Identifier getTextureResource(HadesChosenEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/hades_chosen_texture.png");
    }
    @Override
    public Identifier getAnimationResource(HadesChosenEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/hades_chosen.animation.json");
    }
    @Override
    public void setCustomAnimations(HadesChosenEntity animatable, int instanceId, AnimationEvent animationEvent) {
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

        head_y = head.getRotationZ() + (extraData.headPitch * ((float) Math.PI / 340)) * unpausedMultiplier;

        if (head_x > 1.3f) {
            head_x = 1.3f;
        } else if (head_x < -1.3f) {
            head_x = -1.3f;
        }

        head.setRotationY(head_x);
        head.setRotationZ(head_y);
    }
}