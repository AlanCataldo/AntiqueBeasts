package net.mebahel.antiquebeasts.entity.armor;

import net.mebahel.antiquebeasts.item.GoldScaleArmorItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class GoldScaleArmorRenderer extends GeoArmorRenderer<GoldScaleArmorItem> {
    public GoldScaleArmorRenderer() {
        super(new GoldScaleArmorModel());
    }
}