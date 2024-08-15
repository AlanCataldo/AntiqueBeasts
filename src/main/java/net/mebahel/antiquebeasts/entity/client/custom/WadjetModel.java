package net.mebahel.antiquebeasts.entity.client.custom;

import com.google.common.collect.Maps;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.custom.egyptian.EgyptianCaravanEntity;
import net.mebahel.antiquebeasts.entity.custom.egyptian.WadjetEntity;
import net.mebahel.antiquebeasts.entity.variant.WadjetVariant;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

import java.util.Map;

public class WadjetModel extends AnimatedGeoModel<WadjetEntity> {
    float head_x;
    float head_y;
    float max_rotation_x = 35;
    public static final Map<WadjetVariant, Identifier> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(WadjetVariant.class), (map) -> {
                map.put(WadjetVariant.DEFAULT,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/wajdet_texture.png"));
                map.put(WadjetVariant.CLOAK,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/wajdet_texture_2.png"));
            });
    @Override
    public Identifier getModelResource(WadjetEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/wajdet.geo.json");
    }

    @Override
    public Identifier getTextureResource(WadjetEntity animatable) {
        return LOCATION_BY_VARIANT.get(animatable.getVariant());
    }
    @Override
    public Identifier getAnimationResource(WadjetEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/wajdet.animation.json");
    }
}