package net.mebahel.antiquebeasts.entity.client.projectiles.draugr;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.projectiles.DraugrWightProjectileEntity;
import net.mebahel.antiquebeasts.entity.projectiles.PharaohScepterProjectileEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class DraugrWightProjectileModel extends GeoModel<DraugrWightProjectileEntity> {
    @Override
    public Identifier getModelResource(DraugrWightProjectileEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/venom_entity.geo.json");
    }

    @Override
    public Identifier getTextureResource(DraugrWightProjectileEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/mummy_projectile_entity_texture.png");
    }

    @Override
    public Identifier getAnimationResource(DraugrWightProjectileEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/venom_entity.animation.json");
    }

}