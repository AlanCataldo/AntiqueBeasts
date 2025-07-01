package net.mebahel.antiquebeasts.entity.armor.GoldScaleArmor;

import mod.azure.azurelib.rewrite.render.armor.AzArmorRenderer;
import mod.azure.azurelib.rewrite.render.armor.AzArmorRendererConfig;
import mod.azure.azurelib.rewrite.render.armor.bone.AzDefaultArmorBoneProvider;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.minecraft.util.Identifier;

public class AzGoldScaleArmorRenderer extends AzArmorRenderer {
    private static final Identifier  MODEL = new Identifier(AntiqueBeasts.MOD_ID, "geo/item/gold_scale_armor.geo.json");
    private static final Identifier  TEXTURE = new Identifier(AntiqueBeasts.MOD_ID, "textures/item/gold_scale_armor.png");

    public AzGoldScaleArmorRenderer() {
        super(
                AzArmorRendererConfig.builder(MODEL, TEXTURE)
                        .setBoneProvider(new AzDefaultArmorBoneProvider())
                        .setAnimatorProvider(AzGoldScaleArmorAnimator::new)
                        .build()
        );
    }
}