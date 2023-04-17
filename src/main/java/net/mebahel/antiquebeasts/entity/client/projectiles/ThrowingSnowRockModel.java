package net.mebahel.antiquebeasts.entity.client.projectiles;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.projectiles.ThrowingRockEntity;
import net.mebahel.antiquebeasts.entity.projectiles.ThrowingSnowRockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ThrowingSnowRockModel extends AnimatedGeoModel<ThrowingSnowRockEntity> {
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