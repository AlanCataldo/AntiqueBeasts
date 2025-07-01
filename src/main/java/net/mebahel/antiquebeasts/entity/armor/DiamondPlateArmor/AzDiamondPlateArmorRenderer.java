package net.mebahel.antiquebeasts.entity.armor.DiamondPlateArmor;

import mod.azure.azurelib.rewrite.animation.controller.AzAnimationController;
import mod.azure.azurelib.rewrite.render.armor.AzArmorRenderer;
import mod.azure.azurelib.rewrite.render.armor.AzArmorRendererConfig;
import mod.azure.azurelib.rewrite.render.armor.bone.AzDefaultArmorBoneProvider;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class AzDiamondPlateArmorRenderer extends AzArmorRenderer {
    private static final Identifier  MODEL = new Identifier(AntiqueBeasts.MOD_ID, "geo/item/diamond_plate_armor.geo.json");
    private static final Identifier  TEXTURE = new Identifier(AntiqueBeasts.MOD_ID, "textures/item/diamond_plate_armor.png");

    public AzDiamondPlateArmorRenderer() {
        super(
                AzArmorRendererConfig.builder(MODEL, TEXTURE)
                        .setBoneProvider(new AzDefaultArmorBoneProvider())
                        .setAnimatorProvider(AzDiamondPlateArmorAnimator::new)
                        .build()
        );
    }

}