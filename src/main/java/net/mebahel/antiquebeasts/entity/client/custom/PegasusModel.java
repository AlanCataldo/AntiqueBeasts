package net.mebahel.antiquebeasts.entity.client.custom;

import com.google.common.collect.Maps;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.custom.PegasusEntity;
import net.mebahel.antiquebeasts.entity.variant.PegasusVariant;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

import java.util.Map;

public class PegasusModel extends AnimatedGeoModel<PegasusEntity> {
    float head_x;
    float head_y;
    float max_rotation_x = 35;

    public static final Map<PegasusVariant, Identifier> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(PegasusVariant.class), (map) -> {
                map.put(PegasusVariant.DEFAULT,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/pegasus_texture.png"));
                map.put(PegasusVariant.CLOAK,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/pegasus_saddle_texture.png"));
                map.put(PegasusVariant.UNMOUNT,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/pegasus_saddle_unmount_texture.png"));
                map.put(PegasusVariant.LEATHER_UNMOUNT,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/pegasus_leather_armor_texture.png"));
                map.put(PegasusVariant.IRON_UNMOUNT,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/pegasus_iron_armor_texture.png"));
                map.put(PegasusVariant.GOLD_UNMOUNT,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/pegasus_gold_armor_texture.png"));
                map.put(PegasusVariant.DIAMOND_UNMOUNT,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/pegasus_diamond_armor_texture.png"));
                map.put(PegasusVariant.LEATHER_SADDLED_UNMOUNTED,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/pegasus_leather_armor_saddled_unmounted_texture.png"));
                map.put(PegasusVariant.IRON_SADDLED_UNMOUNTED,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/pegasus_iron_armor_saddled_unmounted_texture.png"));
                map.put(PegasusVariant.GOLD_SADDLED_UNMOUNTED,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/pegasus_gold_armor_saddled_unmounted_texture.png"));
                map.put(PegasusVariant.DIAMOND_SADDLED_UNMOUNTED,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/pegasus_diamond_armor_saddled_unmounted_texture.png"));
                map.put(PegasusVariant.LEATHER_SADDLED_MOUNTED,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/pegasus_leather_armor_saddled_mounted_texture.png"));
                map.put(PegasusVariant.IRON_SADDLED_MOUNTED,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/pegasus_iron_armor_saddled_mounted_texture.png"));
                map.put(PegasusVariant.GOLD_SADDLED_MOUNTED,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/pegasus_gold_armor_saddled_mounted_texture.png"));
                map.put(PegasusVariant.DIAMOND_SADDLED_MOUNTED,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/pegasus_diamond_armor_saddled_mounted_texture.png"));
            });
    @Override
    public Identifier getModelResource(PegasusEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/pegasus.geo.json");
    }
    public Identifier getTextureResource(PegasusEntity animatable) {
        return LOCATION_BY_VARIANT.get(animatable.getPegasusVariant());
    }
    @Override
    public Identifier getAnimationResource(PegasusEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/pegasus.animation.json");
    }
    @Override
    public void setCustomAnimations(PegasusEntity animatable, int instanceId, AnimationEvent animationEvent) {
        super.setCustomAnimations(animatable, instanceId, animationEvent);
        IBone head = this.getAnimationProcessor().getBone("horse_head");
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