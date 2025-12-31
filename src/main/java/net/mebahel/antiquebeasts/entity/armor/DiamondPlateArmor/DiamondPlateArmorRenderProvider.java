package net.mebahel.antiquebeasts.entity.armor.DiamondPlateArmor;

import mod.azure.azurelibarmor.animatable.client.RenderProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
public class DiamondPlateArmorRenderProvider implements RenderProvider {

    private final DiamondPlateArmorRenderer renderer = new DiamondPlateArmorRenderer();

    @Override
    public BipedEntityModel<LivingEntity> getHumanoidArmorModel(
            LivingEntity entity,
            ItemStack stack,
            EquipmentSlot slot,
            BipedEntityModel<LivingEntity> baseModel
    ) {
        // IMPORTANT: si ta version a une signature différente, c'est ici que ça casse.
        // Dans beaucoup de builds Yarn, c'est exactement ça :
        renderer.prepForRender(entity, stack, slot, baseModel);

        return renderer;
    }
}
