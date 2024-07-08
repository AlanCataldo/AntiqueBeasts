package net.mebahel.antiquebeasts.entity.client.projectiles;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.projectiles.ChimeraProjectileEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class ChimeraProjectileModel extends GeoModel<ChimeraProjectileEntity> {
    @Override
    public Identifier getModelResource(ChimeraProjectileEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/venom_entity.geo.json");
    }

    @Override
    public Identifier getTextureResource(ChimeraProjectileEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/mummy_projectile_entity_texture.png");
    }

    @Override
    public Identifier getAnimationResource(ChimeraProjectileEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/venom_entity.animation.json");
    }

}