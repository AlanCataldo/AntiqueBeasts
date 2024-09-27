package net.mebahel.antiquebeasts.entity.client.projectiles;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.projectiles.PharaohScepterProjectileEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class PharaohScepterProjectileModel extends GeoModel<PharaohScepterProjectileEntity> {
    @Override
    public Identifier getModelResource(PharaohScepterProjectileEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/venom_entity.geo.json");
    }

    @Override
    public Identifier getTextureResource(PharaohScepterProjectileEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/mummy_projectile_entity_texture.png");
    }

    @Override
    public Identifier getAnimationResource(PharaohScepterProjectileEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/venom_entity.animation.json");
    }

}