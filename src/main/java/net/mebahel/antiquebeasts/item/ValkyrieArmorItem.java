package net.mebahel.antiquebeasts.item;

import net.mebahel.antiquebeasts.entity.armor.GoldScaleArmor.AzGoldScaleArmorDispatcher;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;
import java.util.List;

public class ValkyrieArmorItem extends ArmorItem {
    public final AzGoldScaleArmorDispatcher dispatcher;

    public ValkyrieArmorItem(ArmorMaterial material, Type type, Settings settings) {
        super(material, type, settings);
        this.dispatcher = new AzGoldScaleArmorDispatcher();
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.antiquebeasts.valkyrie.tooltip").formatted(Formatting.GRAY, Formatting.ITALIC));
        tooltip.add(Text.translatable("item.antiquebeasts.valkyrie.tooltip2").formatted(Formatting.GRAY, Formatting.ITALIC));
    }
}
