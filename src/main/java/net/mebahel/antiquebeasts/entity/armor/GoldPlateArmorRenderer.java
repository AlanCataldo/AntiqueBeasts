package net.mebahel.antiquebeasts.entity.armor;

import net.mebahel.antiquebeasts.item.GoldPlateArmorItem;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

public class GoldPlateArmorRenderer extends GeoArmorRenderer<GoldPlateArmorItem> {
    public GoldPlateArmorRenderer() {
        super(new GoldPlateArmorModel());

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
