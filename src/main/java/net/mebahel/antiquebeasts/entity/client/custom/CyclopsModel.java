package net.mebahel.antiquebeasts.entity.client.custom;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.custom.CyclopsEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class CyclopsModel extends GeoModel<CyclopsEntity> {
    float head_x;
    float head_y;
    float max_rotation_x = 35;

    @Override
    public Identifier getModelResource(CyclopsEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/cyclops.geo.json");
    }

    @Override
    public Identifier getTextureResource(CyclopsEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/cyclops_texture.png");
    }

    @Override
    public Identifier getAnimationResource(CyclopsEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/cyclops.animation.json");
    }
}