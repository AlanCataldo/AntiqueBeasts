package net.mebahel.antiquebeasts.entity.armor.DiamondScaleArmor;

import mod.azure.azurelibarmor.model.GeoModel;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.item.DiamondScaleArmorItem;
import net.minecraft.util.Identifier;

public class DiamondScaleArmorModel extends GeoModel<DiamondScaleArmorItem> {

    private static final Identifier MODEL = new Identifier(AntiqueBeasts.MOD_ID, "geo/item/gold_scale_armor.geo.json");
    private static final Identifier TEXTURE = new Identifier(AntiqueBeasts.MOD_ID, "textures/item/diamond_scale_armor.png");
    private static final Identifier ANIM = new Identifier(AntiqueBeasts.MOD_ID, "animations/item/gold_plate_armor.animation.json");

    @Override
    public Identifier getModelResource(DiamondScaleArmorItem animatable) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(DiamondScaleArmorItem animatable) {
        return TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(DiamondScaleArmorItem animatable) {
        return ANIM;
    }
}

