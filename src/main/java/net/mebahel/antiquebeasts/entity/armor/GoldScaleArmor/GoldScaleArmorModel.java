package net.mebahel.antiquebeasts.entity.armor.GoldScaleArmor;

import mod.azure.azurelibarmor.model.GeoModel;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.item.GoldScaleArmorItem;
import net.minecraft.util.Identifier;

public class GoldScaleArmorModel extends GeoModel<GoldScaleArmorItem> {

    private static final Identifier MODEL = new Identifier(AntiqueBeasts.MOD_ID, "geo/item/gold_scale_armor.geo.json");
    private static final Identifier TEXTURE = new Identifier(AntiqueBeasts.MOD_ID, "textures/item/gold_scale_armor.png");
    private static final Identifier ANIM = new Identifier(AntiqueBeasts.MOD_ID, "animations/item/gold_plate_armor.animation.json");

    @Override
    public Identifier getModelResource(GoldScaleArmorItem animatable) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(GoldScaleArmorItem animatable) {
        return TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(GoldScaleArmorItem animatable) {
        return ANIM;
    }
}

