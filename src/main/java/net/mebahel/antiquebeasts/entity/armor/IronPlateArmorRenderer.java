package net.mebahel.antiquebeasts.entity.armor;

import net.mebahel.antiquebeasts.item.IronPlateArmorItem;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

public class IronPlateArmorRenderer extends GeoArmorRenderer<IronPlateArmorItem> {
    public IronPlateArmorRenderer() {
        super(new IronPlateArmorModel());

        this.headBone = "armorHead";
        this.bodyBone = "armorBody";
        this.rightArmBone = "armorRightArm";
        this.leftArmBone = "armorLeftArm";
        this.rightLegBone = "armorRightLeg";
        this.leftLegBone = "armorLeftLeg";
        this.rightBootBone = "armorRightBoot";
        this.leftBootBone = "armorLeftBoot";
    }
}
