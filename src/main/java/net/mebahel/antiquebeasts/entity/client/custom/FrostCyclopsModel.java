package net.mebahel.antiquebeasts.entity.client.custom;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.custom.FrostCyclopsEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class FrostCyclopsModel extends GeoModel<FrostCyclopsEntity> {
    @Override
    public Identifier getModelResource(FrostCyclopsEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/frost-cyclops.geo.json");
    }

    @Override
    public Identifier getTextureResource(FrostCyclopsEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/frost-cyclops_texture.png");
    }

    @Override
    public Identifier getAnimationResource(FrostCyclopsEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/cyclops.animation.json");
    }
}
