package net.mebahel.antiquebeasts.entity.client.projectiles;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.projectiles.HadesChosenSpearEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class HadesChosenSpearModel extends AnimatedGeoModel<HadesChosenSpearEntity> {
    @Override
    public Identifier getModelResource(HadesChosenSpearEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/hoplite_spear.geo.json");
    }

    @Override
    public Identifier getTextureResource(HadesChosenSpearEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "textures/item/weapon/netherite_hoplite_spear.png");
    }

    @Override
    public Identifier getAnimationResource(HadesChosenSpearEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/hoplite_spear.animation.json");
    }

}