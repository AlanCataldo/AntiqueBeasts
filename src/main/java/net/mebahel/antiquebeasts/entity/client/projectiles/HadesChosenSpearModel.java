package net.mebahel.antiquebeasts.entity.client.projectiles;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.projectiles.HadesChosenSpearEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class HadesChosenSpearModel extends GeoModel<HadesChosenSpearEntity> {
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
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/champion_hoplite.animation.json");
    }

}