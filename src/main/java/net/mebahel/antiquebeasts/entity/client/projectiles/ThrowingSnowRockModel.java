package net.mebahel.antiquebeasts.entity.client.projectiles;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.projectiles.ThrowingSnowRockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class ThrowingSnowRockModel extends GeoModel<ThrowingSnowRockEntity> {
    @Override
    public Identifier getModelResource(ThrowingSnowRockEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/throwingrock.geo.json");
    }

    @Override
    public Identifier getTextureResource(ThrowingSnowRockEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/throwingsnowrock_texture.png");
    }

    @Override
    public Identifier getAnimationResource(ThrowingSnowRockEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/throwingrock.animation.json");
    }

}