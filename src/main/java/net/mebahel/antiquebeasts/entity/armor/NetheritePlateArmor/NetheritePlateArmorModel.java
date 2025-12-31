package net.mebahel.antiquebeasts.entity.armor.NetheritePlateArmor;

import mod.azure.azurelibarmor.model.GeoModel;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.item.IronScaleArmorItem;
import net.mebahel.antiquebeasts.item.NetheritePlateArmorItem;
import net.minecraft.util.Identifier;

public class NetheritePlateArmorModel extends GeoModel<NetheritePlateArmorItem> {

    private static final Identifier MODEL = new Identifier(AntiqueBeasts.MOD_ID, "geo/item/netherite_plate_armor.geo.json");
    private static final Identifier TEXTURE = new Identifier(AntiqueBeasts.MOD_ID, "textures/item/netherite_plate_armor.png");
    private static final Identifier ANIM = new Identifier(AntiqueBeasts.MOD_ID, "animations/item/gold_plate_armor.animation.json");

    @Override
    public Identifier getModelResource(NetheritePlateArmorItem animatable) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(NetheritePlateArmorItem animatable) {
        return TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(NetheritePlateArmorItem animatable) {
        return ANIM;
    }
}

