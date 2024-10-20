package net.mebahel.antiquebeasts.util;

import net.mebahel.antiquebeasts.item.DiamondScaleArmorItem;
import net.mebahel.antiquebeasts.item.GoldScaleArmorItem;
import net.mebahel.antiquebeasts.item.IronScaleArmorItem;
import net.mebahel.antiquebeasts.item.ValkyrieArmorItem;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class ArmorUtils {
    public static boolean isWearingValkyrieArmor(LivingEntity entity) {
        ItemStack head = entity.getEquippedStack(EquipmentSlot.HEAD);
        ItemStack chest = entity.getEquippedStack(EquipmentSlot.CHEST);
        ItemStack legs = entity.getEquippedStack(EquipmentSlot.LEGS);
        ItemStack feet = entity.getEquippedStack(EquipmentSlot.FEET);

        return head.getItem() instanceof ValkyrieArmorItem &&
                chest.getItem() instanceof ValkyrieArmorItem &&
                legs.getItem() instanceof ValkyrieArmorItem &&
                feet.getItem() instanceof ValkyrieArmorItem;
    }

    public static boolean isWearingFullDiamondScaleArmor(LivingEntity entity) {
        ItemStack head = entity.getEquippedStack(EquipmentSlot.HEAD);
        ItemStack chest = entity.getEquippedStack(EquipmentSlot.CHEST);
        ItemStack legs = entity.getEquippedStack(EquipmentSlot.LEGS);
        ItemStack feet = entity.getEquippedStack(EquipmentSlot.FEET);

        return head.getItem() instanceof DiamondScaleArmorItem &&
                chest.getItem() instanceof DiamondScaleArmorItem &&
                legs.getItem() instanceof DiamondScaleArmorItem &&
                feet.getItem() instanceof DiamondScaleArmorItem;
    }

    public static boolean isWearingFullGoldScaleArmor(LivingEntity entity) {
        ItemStack head = entity.getEquippedStack(EquipmentSlot.HEAD);
        ItemStack chest = entity.getEquippedStack(EquipmentSlot.CHEST);
        ItemStack legs = entity.getEquippedStack(EquipmentSlot.LEGS);
        ItemStack feet = entity.getEquippedStack(EquipmentSlot.FEET);

        return head.getItem() instanceof GoldScaleArmorItem &&
                chest.getItem() instanceof GoldScaleArmorItem &&
                legs.getItem() instanceof GoldScaleArmorItem &&
                feet.getItem() instanceof GoldScaleArmorItem;
    }

    public static boolean isWearingFullIronScaleArmor(LivingEntity entity) {
        ItemStack head = entity.getEquippedStack(EquipmentSlot.HEAD);
        ItemStack chest = entity.getEquippedStack(EquipmentSlot.CHEST);
        ItemStack legs = entity.getEquippedStack(EquipmentSlot.LEGS);
        ItemStack feet = entity.getEquippedStack(EquipmentSlot.FEET);

        return head.getItem() instanceof IronScaleArmorItem &&
                chest.getItem() instanceof IronScaleArmorItem &&
                legs.getItem() instanceof IronScaleArmorItem &&
                feet.getItem() instanceof IronScaleArmorItem;
    }
}

