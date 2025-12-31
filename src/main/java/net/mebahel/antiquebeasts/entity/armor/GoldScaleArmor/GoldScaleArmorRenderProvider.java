package net.mebahel.antiquebeasts.entity.armor.GoldScaleArmor;

import mod.azure.azurelibarmor.animatable.client.RenderProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.mebahel.antiquebeasts.entity.armor.IronPlateArmor.IronPlateArmorRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
public class GoldScaleArmorRenderProvider implements RenderProvider {

    private final GoldScaleArmorRenderer renderer = new GoldScaleArmorRenderer();

    @Override
    public BipedEntityModel<LivingEntity> getHumanoidArmorModel(
            LivingEntity entity,
            ItemStack stack,
            EquipmentSlot slot,
            BipedEntityModel<LivingEntity> baseModel
    ) {
        renderer.prepForRender(entity, stack, slot, baseModel);

        return renderer;
    }
}
