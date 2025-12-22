package net.mebahel.antiquebeasts.entity.armor.IronPlateArmor;

import mod.azure.azurelib.render.armor.AzArmorRenderer;
import mod.azure.azurelib.render.armor.AzArmorRendererConfig;
import mod.azure.azurelib.render.armor.bone.AzDefaultArmorBoneProvider;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.minecraft.util.Identifier;

public class AzIronPlateArmorRenderer extends AzArmorRenderer {
    private static final Identifier  MODEL = new Identifier(AntiqueBeasts.MOD_ID, "geo/item/iron_plate_armor.geo.json");
    private static final Identifier  TEXTURE = new Identifier(AntiqueBeasts.MOD_ID, "textures/item/iron_plate_armor.png");

    public AzIronPlateArmorRenderer() {
        super(
                AzArmorRendererConfig.builder(MODEL, TEXTURE)
                        .setBoneProvider(new AzDefaultArmorBoneProvider())
                        .setAnimatorProvider(AzIronPlateArmorAnimator::new)
                        .build()
        );
    }
}