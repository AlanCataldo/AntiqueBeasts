package net.mebahel.antiquebeasts.entity.armor.DiamondPlateArmor;

import mod.azure.azurelibarmor.model.GeoModel;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.item.DiamondPlateArmorItem;
import net.minecraft.util.Identifier;

public class DiamondPlateArmorModel extends GeoModel<DiamondPlateArmorItem> {

    private static final Identifier MODEL = new Identifier(AntiqueBeasts.MOD_ID, "geo/item/diamond_plate_armor.geo.json");
    private static final Identifier TEXTURE = new Identifier(AntiqueBeasts.MOD_ID, "textures/item/diamond_plate_armor.png");
    private static final Identifier ANIM = new Identifier(AntiqueBeasts.MOD_ID, "animations/item/gold_plate_armor.animation.json");

    @Override
    public Identifier getModelResource(DiamondPlateArmorItem animatable) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(DiamondPlateArmorItem animatable) {
        return TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(DiamondPlateArmorItem animatable) {
        return ANIM;
    }
}

