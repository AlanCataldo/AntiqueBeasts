package net.mebahel.antiquebeasts.entity.armor;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.item.GoldPlateArmorItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class GoldPlateArmorModel extends AnimatedGeoModel<GoldPlateArmorItem> {
    @Override
    public Identifier getModelLocation(GoldPlateArmorItem object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/gold_plate_armor.geo.json");
    }

    @Override
    public Identifier getTextureLocation(GoldPlateArmorItem object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "textures/armor/gold_plate_armor.png");
    }

    @Override
    public Identifier getAnimationFileLocation(GoldPlateArmorItem animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/armor.animation.json");
    }
}
