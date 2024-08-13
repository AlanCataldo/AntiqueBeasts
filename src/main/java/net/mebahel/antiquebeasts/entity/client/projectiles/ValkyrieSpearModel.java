package net.mebahel.antiquebeasts.entity.client.projectiles;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.projectiles.ValkyrieSpearEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ValkyrieSpearModel extends AnimatedGeoModel<ValkyrieSpearEntity> {
    @Override
    public Identifier getModelResource(ValkyrieSpearEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/valkyrie_spear.geo.json");
    }

    @Override
    public Identifier getTextureResource(ValkyrieSpearEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "textures/item/weapon/valkyrie_spear_texture.png");
    }

    @Override
    public Identifier getAnimationResource(ValkyrieSpearEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/hoplite_spear.animation.json");
    }

}