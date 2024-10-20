package net.mebahel.antiquebeasts.entity.client.projectiles.draugr;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.projectiles.FrostSpikeEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class FrostSpikeProjectileModel extends GeoModel<FrostSpikeEntity> {
    @Override
    public Identifier getModelResource(FrostSpikeEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/frost_spike.geo.json");
    }

    @Override
    public Identifier getTextureResource(FrostSpikeEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/frost_spike.png");
    }

    @Override
    public Identifier getAnimationResource(FrostSpikeEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/venom_entity.animation.json");
    }

}