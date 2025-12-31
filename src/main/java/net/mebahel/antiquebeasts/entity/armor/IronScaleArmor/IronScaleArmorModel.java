package net.mebahel.antiquebeasts.entity.armor.IronScaleArmor;

import mod.azure.azurelibarmor.model.GeoModel;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.item.IronScaleArmorItem;
import net.minecraft.util.Identifier;

public class IronScaleArmorModel extends GeoModel<IronScaleArmorItem> {

    private static final Identifier MODEL = new Identifier(AntiqueBeasts.MOD_ID, "geo/item/gold_scale_armor.geo.json");
    private static final Identifier TEXTURE = new Identifier(AntiqueBeasts.MOD_ID, "textures/item/iron_scale_armor.png");
    private static final Identifier ANIM = new Identifier(AntiqueBeasts.MOD_ID, "animations/item/gold_plate_armor.animation.json");

    @Override
    public Identifier getModelResource(IronScaleArmorItem animatable) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(IronScaleArmorItem animatable) {
        return TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(IronScaleArmorItem animatable) {
        return ANIM;
    }
}

