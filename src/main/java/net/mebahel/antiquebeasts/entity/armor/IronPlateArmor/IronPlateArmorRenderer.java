package net.mebahel.antiquebeasts.entity.armor.IronPlateArmor;

import mod.azure.azurelibarmor.renderer.GeoArmorRenderer;
import net.mebahel.antiquebeasts.item.IronPlateArmorItem;
import net.mebahel.antiquebeasts.item.IronScaleArmorItem;

public class IronPlateArmorRenderer extends GeoArmorRenderer<IronPlateArmorItem> {
    public IronPlateArmorRenderer() {
        super(new IronPlateArmorModel());
    }
}
