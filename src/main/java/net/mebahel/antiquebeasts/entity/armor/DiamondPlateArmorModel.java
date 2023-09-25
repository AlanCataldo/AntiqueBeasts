package net.mebahel.antiquebeasts.entity.armor;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.item.DiamondPlateArmorItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class DiamondPlateArmorModel extends AnimatedGeoModel<DiamondPlateArmorItem> {
    @Override
    public Identifier getModelResource(DiamondPlateArmorItem object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/diamond_plate_armor.geo.json");
    }

    @Override
    public Identifier getTextureResource(DiamondPlateArmorItem object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "textures/armor/diamond_plate_armor.png");
    }

    @Override
    public Identifier getAnimationResource(DiamondPlateArmorItem animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/armor.animation.json");
    }
}
