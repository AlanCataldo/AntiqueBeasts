package net.mebahel.antiquebeasts.entity.client.projectiles;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.projectiles.ThrowingHopliteSpearEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class ThrowingHopliteSpearEntityModel extends GeoModel<ThrowingHopliteSpearEntity> {
    String type = "";
    public ThrowingHopliteSpearEntityModel(String type) {
        this.type = type;
    }

    @Override
    public Identifier getModelResource(ThrowingHopliteSpearEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/hoplite_spear.geo.json");
    }

    @Override
    public Identifier getTextureResource(ThrowingHopliteSpearEntity object) {
        if (this.type == "iron") {
            return new Identifier(AntiqueBeasts.MOD_ID, "textures/item/weapon/iron_hoplite_spear.png");
        } else if (this.type == "gold") {
            return new Identifier(AntiqueBeasts.MOD_ID, "textures/item/weapon/gold_hoplite_spear.png");
        } else if (this.type == "diamond") {
            return new Identifier(AntiqueBeasts.MOD_ID, "textures/item/weapon/diamond_hoplite_spear.png");
        } else {
            return new Identifier(AntiqueBeasts.MOD_ID, "textures/item/weapon/netherite_hoplite_spear.png");
        }
    }

    @Override
    public Identifier getAnimationResource(ThrowingHopliteSpearEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/hoplite_spear.animation.json");
    }

}