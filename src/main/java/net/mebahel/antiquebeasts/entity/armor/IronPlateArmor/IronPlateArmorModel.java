package net.mebahel.antiquebeasts.entity.armor.IronPlateArmor;

import mod.azure.azurelibarmor.model.GeoModel;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.item.IronPlateArmorItem;
import net.mebahel.antiquebeasts.item.IronScaleArmorItem;
import net.minecraft.util.Identifier;

public class IronPlateArmorModel extends GeoModel<IronPlateArmorItem> {

    private static final Identifier MODEL = new Identifier(AntiqueBeasts.MOD_ID, "geo/item/iron_plate_armor.geo.json");
    private static final Identifier TEXTURE = new Identifier(AntiqueBeasts.MOD_ID, "textures/item/iron_plate_armor.png");
    private static final Identifier ANIM = new Identifier(AntiqueBeasts.MOD_ID, "animations/item/gold_plate_armor.animation.json");

    @Override
    public Identifier getModelResource(IronPlateArmorItem animatable) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(IronPlateArmorItem animatable) {
        return TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(IronPlateArmorItem animatable) {
        return ANIM;
    }
}

