package net.mebahel.antiquebeasts.entity.armor.GoldScaleArmor;

import mod.azure.azurelibarmor.renderer.GeoArmorRenderer;
import net.mebahel.antiquebeasts.item.GoldScaleArmorItem;

public class GoldScaleArmorRenderer extends GeoArmorRenderer<GoldScaleArmorItem> {
    public GoldScaleArmorRenderer() {
        super(new GoldScaleArmorModel());
    }
}
