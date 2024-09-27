package net.mebahel.antiquebeasts.entity.armor;

import net.mebahel.antiquebeasts.item.DiamondScaleArmorItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class DiamondScaleArmorRenderer extends GeoArmorRenderer<DiamondScaleArmorItem> {
    public DiamondScaleArmorRenderer() {
        super(new DiamondScaleArmorModel());
    }
}