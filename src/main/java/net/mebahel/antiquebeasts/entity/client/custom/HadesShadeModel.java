package net.mebahel.antiquebeasts.entity.client.custom;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.custom.CyclopsEntity;
import net.mebahel.antiquebeasts.entity.custom.HadesShadeEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class HadesShadeModel extends AnimatedGeoModel<HadesShadeEntity> {
    @Override
    public Identifier getModelResource(HadesShadeEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/hades_shade.geo.json");
    }

    @Override
    public Identifier getTextureResource(HadesShadeEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/hades_shade_texture.png");
    }

    @Override
    public Identifier getAnimationResource(HadesShadeEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/hades_shade.animation.json");
    }
}