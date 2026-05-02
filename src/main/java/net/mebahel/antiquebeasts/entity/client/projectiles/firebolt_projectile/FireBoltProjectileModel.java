package net.mebahel.antiquebeasts.entity.client.projectiles.firebolt_projectile;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.projectiles.FireboltEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class FireBoltProjectileModel extends GeoModel<FireboltEntity> {
    @Override
    public Identifier getModelResource(FireboltEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/firebolt_projectile.geo.json");
    }

    @Override
    public Identifier getTextureResource(FireboltEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/firebolt_projectile.png");
    }

    @Override
    public Identifier getAnimationResource(FireboltEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/firebolt_projectile.animation.json");
    }
}