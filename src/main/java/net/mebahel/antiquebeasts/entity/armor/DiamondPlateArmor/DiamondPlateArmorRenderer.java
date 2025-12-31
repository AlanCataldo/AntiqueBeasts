package net.mebahel.antiquebeasts.entity.armor.DiamondPlateArmor;

import mod.azure.azurelibarmor.renderer.GeoArmorRenderer;
import net.mebahel.antiquebeasts.entity.armor.IronPlateArmor.IronPlateArmorModel;
import net.mebahel.antiquebeasts.item.DiamondPlateArmorItem;

public class DiamondPlateArmorRenderer extends GeoArmorRenderer<DiamondPlateArmorItem> {
    public DiamondPlateArmorRenderer() {
        super(new DiamondPlateArmorModel());
    }
}
