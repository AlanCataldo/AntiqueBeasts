package net.mebahel.antiquebeasts.entity.client.projectiles;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.projectiles.HopliteSpearEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class HopliteSpearModel extends AnimatedGeoModel<HopliteSpearEntity> {
    @Override
    public Identifier getModelLocation(HopliteSpearEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/hoplite_spear.geo.json");
    }

    @Override
    public Identifier getTextureLocation(HopliteSpearEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "textures/item/weapon/netherite_hoplite_spear.png");
    }

    @Override
    public Identifier getAnimationFileLocation(HopliteSpearEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/champion_hoplite.animation.json");
    }

}