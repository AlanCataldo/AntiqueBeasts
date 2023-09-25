package net.mebahel.antiquebeasts.entity.armor;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.item.IronPlateArmorItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class IronPlateArmorModel extends AnimatedGeoModel<IronPlateArmorItem> {
    @Override
    public Identifier getModelResource(IronPlateArmorItem object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/iron_plate_armor.geo.json");
    }

    @Override
    public Identifier getTextureResource(IronPlateArmorItem object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "textures/armor/iron_plate_armor.png");
    }

    @Override
    public Identifier getAnimationResource(IronPlateArmorItem animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/armor.animation.json");
    }
}
