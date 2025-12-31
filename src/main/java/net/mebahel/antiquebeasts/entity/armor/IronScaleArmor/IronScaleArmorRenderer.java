package net.mebahel.antiquebeasts.entity.armor.IronScaleArmor;

import mod.azure.azurelibarmor.renderer.GeoArmorRenderer;
import net.mebahel.antiquebeasts.item.IronScaleArmorItem;

public class IronScaleArmorRenderer extends GeoArmorRenderer<IronScaleArmorItem> {
    public IronScaleArmorRenderer() {
        super(new IronScaleArmorModel());
    }
}
