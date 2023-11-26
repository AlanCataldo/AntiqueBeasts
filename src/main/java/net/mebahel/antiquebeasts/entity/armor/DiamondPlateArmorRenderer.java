package net.mebahel.antiquebeasts.entity.armor;

import net.mebahel.antiquebeasts.item.DiamondPlateArmorItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class DiamondPlateArmorRenderer extends GeoArmorRenderer<DiamondPlateArmorItem> {
    public DiamondPlateArmorRenderer() {
        super(new DiamondPlateArmorModel());
    }
}
