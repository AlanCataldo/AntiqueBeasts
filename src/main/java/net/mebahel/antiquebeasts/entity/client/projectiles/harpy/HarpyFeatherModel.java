package net.mebahel.antiquebeasts.entity.client.projectiles.harpy;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.projectiles.HarpyFeatherEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class HarpyFeatherModel extends GeoModel<HarpyFeatherEntity> {
    @Override
    public Identifier getModelResource(HarpyFeatherEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/harpy_feather.geo.json");
    }

    @Override
    public Identifier getTextureResource(HarpyFeatherEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/harpy_feather.png");
    }

    @Override
    public Identifier getAnimationResource(HarpyFeatherEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/harpy_feather.animation.json");
    }

}