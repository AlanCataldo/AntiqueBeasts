package net.mebahel.antiquebeasts.entity.armor;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.item.DiamondScaleArmorItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class DiamondScaleArmorModel extends GeoModel<DiamondScaleArmorItem> {
    @Override
    public Identifier getModelResource(DiamondScaleArmorItem object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/item/diamond_scale_armor.geo.json");
    }

    @Override
    public Identifier getTextureResource(DiamondScaleArmorItem object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "textures/item/diamond_scale_armor.png");
    }

    @Override
    public Identifier getAnimationResource(DiamondScaleArmorItem animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/item/gold_plate_armor.animation.json");
    }
}
