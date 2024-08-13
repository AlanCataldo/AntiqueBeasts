package net.mebahel.antiquebeasts.entity.client.projectiles;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.projectiles.VenomSlowEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class VenomSlowModel extends AnimatedGeoModel<VenomSlowEntity> {
    @Override
    public Identifier getModelResource(VenomSlowEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/venom_entity.geo.json");
    }

    @Override
    public Identifier getTextureResource(VenomSlowEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/venom_slow_entity_texture.png");
    }

    @Override
    public Identifier getAnimationResource(VenomSlowEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/venom_entity.animation.json");
    }

}