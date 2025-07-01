package net.mebahel.antiquebeasts.entity.armor.DiamondScaleArmor;

import mod.azure.azurelib.rewrite.render.armor.AzArmorRenderer;
import mod.azure.azurelib.rewrite.render.armor.AzArmorRendererConfig;
import mod.azure.azurelib.rewrite.render.armor.bone.AzDefaultArmorBoneProvider;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.minecraft.util.Identifier;

public class AzDiamondScaleArmorRenderer extends AzArmorRenderer {
    private static final Identifier  MODEL = new Identifier(AntiqueBeasts.MOD_ID, "geo/item/diamond_scale_armor.geo.json");
    private static final Identifier  TEXTURE = new Identifier(AntiqueBeasts.MOD_ID, "textures/item/diamond_scale_armor.png");

    public AzDiamondScaleArmorRenderer() {
        super(
                AzArmorRendererConfig.builder(MODEL, TEXTURE)
                        .setBoneProvider(new AzDefaultArmorBoneProvider())
                        .setAnimatorProvider(AzDiamondScaleArmorAnimator::new)
                        .build()
        );
    }
}