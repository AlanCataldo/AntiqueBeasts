package net.mebahel.antiquebeasts.entity.client.projectiles;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.projectiles.MummyProjectileEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class MummyProjectileModel extends GeoModel<MummyProjectileEntity> {
    @Override
    public Identifier getModelResource(MummyProjectileEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/venom_entity.geo.json");
    }

    @Override
    public Identifier getTextureResource(MummyProjectileEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/mummy_projectile_entity_texture.png");
    }

    @Override
    public Identifier getAnimationResource(MummyProjectileEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/venom_entity.animation.json");
    }

}