package net.mebahel.antiquebeasts.entity.armor;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.item.DiamondPlateArmorItem;
import net.mebahel.antiquebeasts.item.NetheritePlateArmorItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class NetheritePlateArmorModel extends AnimatedGeoModel<NetheritePlateArmorItem> {
    @Override
    public Identifier getModelLocation(NetheritePlateArmorItem object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/netherite_plate_armor.geo.json");
    }

    @Override
    public Identifier getTextureLocation(NetheritePlateArmorItem object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "textures/armor/netherite_plate_armor.png");
    }

    @Override
    public Identifier getAnimationFileLocation(NetheritePlateArmorItem animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/armor.animation.json");
    }
}
