package net.mebahel.antiquebeasts.entity.armor.GoldPlateArmor;

import mod.azure.azurelibarmor.renderer.GeoArmorRenderer;
import net.mebahel.antiquebeasts.item.GoldPlateArmorItem;

public class GoldPlateArmorRenderer extends GeoArmorRenderer<GoldPlateArmorItem> {
    public GoldPlateArmorRenderer() {
        super(new GoldPlateArmorModel());
    }
}
