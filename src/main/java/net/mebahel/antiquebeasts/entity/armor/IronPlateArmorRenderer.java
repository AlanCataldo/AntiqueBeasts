package net.mebahel.antiquebeasts.entity.armor;

import net.mebahel.antiquebeasts.item.IronPlateArmorItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class IronPlateArmorRenderer extends GeoArmorRenderer<IronPlateArmorItem> {
    public IronPlateArmorRenderer() {
        super(new IronPlateArmorModel());
    }
}
