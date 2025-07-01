package net.mebahel.antiquebeasts.entity.client.projectiles.dwemer_centurion;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.projectiles.SteamProjectileEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class SteamProjectileModel extends GeoModel<SteamProjectileEntity> {
    @Override
    public Identifier getModelResource(SteamProjectileEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/venom_entity.geo.json");
    }

    @Override
    public Identifier getTextureResource(SteamProjectileEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/mummy_projectile_entity_texture.png");
    }

    @Override
    public Identifier getAnimationResource(SteamProjectileEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/venom_entity.animation.json");
    }

}