package net.mebahel.antiquebeasts.entity.client.custom;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.custom.ValkyrieEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ValkyrieModel extends AnimatedGeoModel<ValkyrieEntity> {
    @Override
    public Identifier getModelResource(ValkyrieEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/valkyrie.geo.json");
    }

    @Override
    public Identifier getTextureResource(ValkyrieEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/valkyrie_texture.png");
    }

    @Override
    public Identifier getAnimationResource(ValkyrieEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/valkyrie.animation.json");
    }
}