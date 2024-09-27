package net.mebahel.antiquebeasts.entity.armor;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.item.GoldScaleArmorItem;
import net.mebahel.antiquebeasts.item.IronScaleArmorItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class IronScaleArmorModel extends GeoModel<IronScaleArmorItem> {
    @Override
    public Identifier getModelResource(IronScaleArmorItem object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/item/iron_scale_armor.geo.json");
    }

    @Override
    public Identifier getTextureResource(IronScaleArmorItem object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "textures/item/iron_scale_armor.png");
    }

    @Override
    public Identifier getAnimationResource(IronScaleArmorItem animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/item/gold_plate_armor.animation.json");
    }
}
