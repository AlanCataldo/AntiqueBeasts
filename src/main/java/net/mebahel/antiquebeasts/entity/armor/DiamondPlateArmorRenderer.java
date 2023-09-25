package net.mebahel.antiquebeasts.entity.armor;

import net.mebahel.antiquebeasts.item.DiamondPlateArmorItem;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

public class DiamondPlateArmorRenderer extends GeoArmorRenderer<DiamondPlateArmorItem> {
    public DiamondPlateArmorRenderer() {
        super(new DiamondPlateArmorModel());

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
