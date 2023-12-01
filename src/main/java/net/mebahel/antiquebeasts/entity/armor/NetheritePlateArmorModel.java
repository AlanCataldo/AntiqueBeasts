package net.mebahel.antiquebeasts.entity.armor;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.item.DiamondPlateArmorItem;
import net.mebahel.antiquebeasts.item.NetheritePlateArmorItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class NetheritePlateArmorModel extends GeoModel<NetheritePlateArmorItem> {
    @Override
    public Identifier getModelResource(NetheritePlateArmorItem object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/item/netherite_plate_armor.geo.json");
    }

    @Override
    public Identifier getTextureResource(NetheritePlateArmorItem object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "textures/item/netherite_plate_armor.png");
    }

    @Override
    public Identifier getAnimationResource(NetheritePlateArmorItem animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/item/gold_plate_armor.animation.json");
    }
}
