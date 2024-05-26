package net.mebahel.antiquebeasts.entity.client.projectiles;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.projectiles.ThrowingAxeEntity;
import net.mebahel.antiquebeasts.entity.projectiles.VenomEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class VenomModel extends GeoModel<VenomEntity> {
    @Override
    public Identifier getModelResource(VenomEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/venom_entity.geo.json");
    }

    @Override
    public Identifier getTextureResource(VenomEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/venom_entity_texture.png");
    }

    @Override
    public Identifier getAnimationResource(VenomEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/venom_entity.animation.json");
    }

}