package net.mebahel.antiquebeasts.entity.armor;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.item.GoldPlateArmorItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class GoldPlateArmorRenderer extends GeoArmorRenderer<GoldPlateArmorItem> {
    public GoldPlateArmorRenderer() {
        super(new GoldPlateArmorModel());
    }
}
