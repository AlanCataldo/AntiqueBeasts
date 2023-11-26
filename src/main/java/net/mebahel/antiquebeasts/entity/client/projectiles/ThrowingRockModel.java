package net.mebahel.antiquebeasts.entity.client.projectiles;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.projectiles.ThrowingRockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ThrowingRockModel extends AnimatedGeoModel<ThrowingRockEntity> {
    @Override
    public Identifier getModelLocation(ThrowingRockEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/throwingrock.geo.json");
    }

    @Override
    public Identifier getTextureLocation(ThrowingRockEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/throwingrock_texture.png");
    }

    @Override
    public Identifier getAnimationFileLocation(ThrowingRockEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/throwingrock.animation.json");
    }

}