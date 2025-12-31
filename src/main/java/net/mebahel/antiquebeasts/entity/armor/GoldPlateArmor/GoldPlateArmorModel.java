package net.mebahel.antiquebeasts.entity.armor.GoldPlateArmor;

import mod.azure.azurelibarmor.model.GeoModel;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.item.GoldPlateArmorItem;
import net.minecraft.util.Identifier;

public class GoldPlateArmorModel extends GeoModel<GoldPlateArmorItem> {

    private static final Identifier MODEL = new Identifier(AntiqueBeasts.MOD_ID, "geo/item/gold_plate_armor.geo.json");
    private static final Identifier TEXTURE = new Identifier(AntiqueBeasts.MOD_ID, "textures/item/gold_plate_armor.png");
    private static final Identifier ANIM = new Identifier(AntiqueBeasts.MOD_ID, "animations/item/gold_plate_armor.animation.json");

    @Override
    public Identifier getModelResource(GoldPlateArmorItem animatable) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(GoldPlateArmorItem animatable) {
        return TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(GoldPlateArmorItem animatable) {
        return ANIM;
    }
}

