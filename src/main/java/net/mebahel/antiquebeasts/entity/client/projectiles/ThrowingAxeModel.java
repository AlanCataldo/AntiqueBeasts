package net.mebahel.antiquebeasts.entity.client.projectiles;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.projectiles.ThrowingAxeEntity;
import net.mebahel.antiquebeasts.entity.projectiles.ThrowingRockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class ThrowingAxeModel extends GeoModel<ThrowingAxeEntity> {
    @Override
    public Identifier getModelResource(ThrowingAxeEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/throwing_axe.geo.json");
    }

    @Override
    public Identifier getTextureResource(ThrowingAxeEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "textures/item/weapon/throwing_axe_texture.png");
    }

    @Override
    public Identifier getAnimationResource(ThrowingAxeEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/throwing_axe.animation.json");
    }

}