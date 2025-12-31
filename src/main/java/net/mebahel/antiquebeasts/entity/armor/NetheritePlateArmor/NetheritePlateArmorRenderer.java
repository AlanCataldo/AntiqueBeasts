package net.mebahel.antiquebeasts.entity.armor.NetheritePlateArmor;

import mod.azure.azurelibarmor.renderer.GeoArmorRenderer;
import net.mebahel.antiquebeasts.entity.armor.IronPlateArmor.IronPlateArmorModel;
import net.mebahel.antiquebeasts.item.IronScaleArmorItem;
import net.mebahel.antiquebeasts.item.NetheritePlateArmorItem;

public class NetheritePlateArmorRenderer extends GeoArmorRenderer<NetheritePlateArmorItem> {
    public NetheritePlateArmorRenderer() {
        super(new NetheritePlateArmorModel());
    }
}
