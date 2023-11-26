package net.mebahel.antiquebeasts.entity.armor;

import net.mebahel.antiquebeasts.item.GoldPlateArmorItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class GoldPlateArmorRenderer extends GeoArmorRenderer<GoldPlateArmorItem> {
    public GoldPlateArmorRenderer() {
        super(new GoldPlateArmorModel());
    }
}
