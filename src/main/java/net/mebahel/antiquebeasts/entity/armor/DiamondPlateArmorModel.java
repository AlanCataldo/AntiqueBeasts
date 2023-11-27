package net.mebahel.antiquebeasts.entity.armor;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.item.DiamondPlateArmorItem;
import net.mebahel.antiquebeasts.item.GoldPlateArmorItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class DiamondPlateArmorModel extends GeoModel<DiamondPlateArmorItem> {
    @Override
    public Identifier getModelResource(DiamondPlateArmorItem object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/item/diamond_plate_armor.geo.json");
    }

    @Override
    public Identifier getTextureResource(DiamondPlateArmorItem object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "textures/item/diamond_plate_armor.png");
    }

    @Override
    public Identifier getAnimationResource(DiamondPlateArmorItem animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/item/gold_plate_armor.animation.json");
    }
}
