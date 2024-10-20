package net.mebahel.antiquebeasts.entity.armor;

import net.mebahel.antiquebeasts.item.ValkyrieArmorItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class ValkyrieArmorRenderer extends GeoArmorRenderer<ValkyrieArmorItem> {
    public ValkyrieArmorRenderer() {
        super(new ValkyrieArmorModel());
    }
}