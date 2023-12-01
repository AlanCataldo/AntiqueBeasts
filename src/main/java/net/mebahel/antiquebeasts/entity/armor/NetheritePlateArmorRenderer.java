package net.mebahel.antiquebeasts.entity.armor;

import net.mebahel.antiquebeasts.item.DiamondPlateArmorItem;
import net.mebahel.antiquebeasts.item.NetheritePlateArmorItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class NetheritePlateArmorRenderer extends GeoArmorRenderer<NetheritePlateArmorItem> {
    public NetheritePlateArmorRenderer() {
        super(new NetheritePlateArmorModel());
    }
}