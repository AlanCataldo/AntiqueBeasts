package net.mebahel.antiquebeasts.entity.armor;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.item.ValkyrieArmorItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class ValkyrieArmorModel extends GeoModel<ValkyrieArmorItem> {
    @Override
    public Identifier getModelResource(ValkyrieArmorItem object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/item/valkyrie_armor.geo.json");
    }

    @Override
    public Identifier getTextureResource(ValkyrieArmorItem object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "textures/item/valkyrie_armor.png");
    }

    @Override
    public Identifier getAnimationResource(ValkyrieArmorItem animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/item/gold_plate_armor.animation.json");
    }
}
