package net.mebahel.antiquebeasts.entity.client.projectiles;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.projectiles.ThrowingRockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class ThrowingRockModel extends GeoModel<ThrowingRockEntity> {
    @Override
    public Identifier getModelResource(ThrowingRockEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/throwingrock.geo.json");
    }

    @Override
    public Identifier getTextureResource(ThrowingRockEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/throwingrock_texture.png");
    }

    @Override
    public Identifier getAnimationResource(ThrowingRockEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/throwingrock.animation.json");
    }

}