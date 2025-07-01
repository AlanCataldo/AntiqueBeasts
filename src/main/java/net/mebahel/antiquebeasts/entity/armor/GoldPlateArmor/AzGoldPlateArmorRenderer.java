package net.mebahel.antiquebeasts.entity.armor.GoldPlateArmor;

import mod.azure.azurelib.rewrite.render.armor.AzArmorRenderer;
import mod.azure.azurelib.rewrite.render.armor.AzArmorRendererConfig;
import mod.azure.azurelib.rewrite.render.armor.bone.AzDefaultArmorBoneProvider;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.minecraft.util.Identifier;

public class AzGoldPlateArmorRenderer extends AzArmorRenderer {
    private static final Identifier  MODEL = new Identifier(AntiqueBeasts.MOD_ID, "geo/item/gold_plate_armor.geo.json");
    private static final Identifier  TEXTURE = new Identifier(AntiqueBeasts.MOD_ID, "textures/item/gold_plate_armor.png");

    public AzGoldPlateArmorRenderer() {
        super(
                AzArmorRendererConfig.builder(MODEL, TEXTURE)
                        .setBoneProvider(new AzDefaultArmorBoneProvider())
                        .setAnimatorProvider(AzGoldPlateArmorAnimator::new)
                        .build()
        );
    }
}