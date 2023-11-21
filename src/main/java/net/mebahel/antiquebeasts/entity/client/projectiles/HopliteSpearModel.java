package net.mebahel.antiquebeasts.entity.client.projectiles;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.projectiles.HopliteSpearEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class HopliteSpearModel extends AnimatedGeoModel<HopliteSpearEntity> {
    @Override
    public Identifier getModelResource(HopliteSpearEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/hoplite_spear.geo.json");
    }

    @Override
    public Identifier getTextureResource(HopliteSpearEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "item/weapon/iron_hoplite_spear");
    }

    @Override
    public Identifier getAnimationResource(HopliteSpearEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/champion_hoplite.animation.json");
    }

}