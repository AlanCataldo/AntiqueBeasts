package net.mebahel.antiquebeasts.entity.armor;

import net.mebahel.antiquebeasts.item.IronPlateArmorItem;
import net.mebahel.antiquebeasts.item.NetheritePlateArmorItem;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

public class NetheritePlateArmorRenderer extends GeoArmorRenderer<NetheritePlateArmorItem> {
    public NetheritePlateArmorRenderer() {
        super(new NetheritePlateArmorModel());

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
