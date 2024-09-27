package net.mebahel.antiquebeasts.entity.armor;

import net.mebahel.antiquebeasts.item.GoldScaleArmorItem;
import net.mebahel.antiquebeasts.item.IronScaleArmorItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class IronScaleArmorRenderer extends GeoArmorRenderer<IronScaleArmorItem> {
    public IronScaleArmorRenderer() {
        super(new IronScaleArmorModel());
    }
}