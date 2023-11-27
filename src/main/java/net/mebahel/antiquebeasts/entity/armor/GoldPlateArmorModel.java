package net.mebahel.antiquebeasts.entity.armor;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.item.GoldPlateArmorItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class GoldPlateArmorModel extends GeoModel<GoldPlateArmorItem> {
    @Override
    public Identifier getModelResource(GoldPlateArmorItem object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/item/gold_plate_armor.geo.json");
    }

    @Override
    public Identifier getTextureResource(GoldPlateArmorItem object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "textures/item/gold_plate_armor.png");
    }

    @Override
    public Identifier getAnimationResource(GoldPlateArmorItem animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/item/gold_plate_armor.animation.json");
    }
}
