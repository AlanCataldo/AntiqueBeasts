package net.mebahel.antiquebeasts.item;

import net.mebahel.antiquebeasts.entity.armor.DiamondPlateArmor.AzDiamondPlateArmorDispatcher;
import net.mebahel.antiquebeasts.item.custom.ModItems;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DiamondPlateArmorItem extends ArmorItem {
    public final AzDiamondPlateArmorDispatcher dispatcher;

    public DiamondPlateArmorItem(ArmorMaterial material, Type type, Settings settings) {
        super(material, type, settings);
        this.dispatcher = new AzDiamondPlateArmorDispatcher();
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.antiquebeasts.scale_armor.tooltip").formatted(Formatting.GRAY, Formatting.ITALIC));
        tooltip.add(Text.translatable("item.antiquebeasts.scale_armor.tooltip2").formatted(Formatting.GRAY, Formatting.ITALIC));
    }
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient && entity instanceof PlayerEntity player) {
            for (ItemStack wornArmor : player.getArmorItems()) {
                if (!wornArmor.isEmpty() && wornArmor.getItem() == ModItems.DIAMOND_PLATE_CHESTPLATE) {
                    dispatcher.updateAnimations(player, wornArmor);
                }
            }
        }
    }
}
