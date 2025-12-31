package net.mebahel.antiquebeasts.entity.armor.ValkyrieArmor;

import mod.azure.azurelibarmor.renderer.GeoArmorRenderer;
import net.mebahel.antiquebeasts.entity.armor.IronPlateArmor.IronPlateArmorModel;
import net.mebahel.antiquebeasts.item.IronScaleArmorItem;
import net.mebahel.antiquebeasts.item.ValkyrieArmorItem;

public class ValkyrieArmorRenderer extends GeoArmorRenderer<ValkyrieArmorItem> {
    public ValkyrieArmorRenderer() {
        super(new ValkyrieArmorModel());
    }
}
