package net.mebahel.antiquebeasts.entity.client.custom;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.custom.HadesChosenEntity;
import net.mebahel.antiquebeasts.entity.custom.HadesShadeEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class HadesChosenModel extends AnimatedGeoModel<HadesChosenEntity> {
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
}