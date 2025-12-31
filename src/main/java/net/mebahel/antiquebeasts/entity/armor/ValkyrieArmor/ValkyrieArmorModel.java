package net.mebahel.antiquebeasts.entity.armor.ValkyrieArmor;

import mod.azure.azurelibarmor.model.GeoModel;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.item.ValkyrieArmorItem;
import net.minecraft.util.Identifier;

public class ValkyrieArmorModel extends GeoModel<ValkyrieArmorItem> {

    private static final Identifier MODEL = new Identifier(AntiqueBeasts.MOD_ID, "geo/item/valkyrie_armor.geo.json");
    private static final Identifier TEXTURE = new Identifier(AntiqueBeasts.MOD_ID, "textures/item/valkyrie_armor.png");
    private static final Identifier ANIM = new Identifier(AntiqueBeasts.MOD_ID, "animations/item/gold_plate_armor.animation.json");

    @Override
    public Identifier getModelResource(ValkyrieArmorItem animatable) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(ValkyrieArmorItem animatable) {
        return TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(ValkyrieArmorItem animatable) {
        return ANIM;
    }
}

