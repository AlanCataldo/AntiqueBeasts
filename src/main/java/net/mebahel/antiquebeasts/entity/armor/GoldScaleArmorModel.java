package net.mebahel.antiquebeasts.entity.armor;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.item.GoldScaleArmorItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class GoldScaleArmorModel extends GeoModel<GoldScaleArmorItem> {
    @Override
    public Identifier getModelResource(GoldScaleArmorItem object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/item/gold_scale_armor.geo.json");
    }

    @Override
    public Identifier getTextureResource(GoldScaleArmorItem object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "textures/item/gold_scale_armor.png");
    }

    @Override
    public Identifier getAnimationResource(GoldScaleArmorItem animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/item/gold_plate_armor.animation.json");
    }
}
