package net.mebahel.antiquebeasts.entity.client.projectiles;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.projectiles.HopliteSpearEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class HopliteSpearModel extends GeoModel<HopliteSpearEntity> {
    @Override
    public Identifier getModelResource(HopliteSpearEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/hoplite_spear.geo.json");
    }

    @Override
    public Identifier getTextureResource(HopliteSpearEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "textures/item/weapon/diamond_hoplite_spear.png");
    }

    @Override
    public Identifier getAnimationResource(HopliteSpearEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/hoplite_spear.animation.json");
    }

}