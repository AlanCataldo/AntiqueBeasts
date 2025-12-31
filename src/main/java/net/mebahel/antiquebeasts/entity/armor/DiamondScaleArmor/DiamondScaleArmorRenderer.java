package net.mebahel.antiquebeasts.entity.armor.DiamondScaleArmor;

import mod.azure.azurelibarmor.renderer.GeoArmorRenderer;
import net.mebahel.antiquebeasts.item.DiamondScaleArmorItem;

public class DiamondScaleArmorRenderer extends GeoArmorRenderer<DiamondScaleArmorItem> {
    public DiamondScaleArmorRenderer() {
        super(new DiamondScaleArmorModel());
    }
}
