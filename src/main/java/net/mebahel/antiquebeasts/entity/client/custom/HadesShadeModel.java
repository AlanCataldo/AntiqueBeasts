package net.mebahel.antiquebeasts.entity.client.custom;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.custom.HadesShadeEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class HadesShadeModel extends GeoModel<HadesShadeEntity> {
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