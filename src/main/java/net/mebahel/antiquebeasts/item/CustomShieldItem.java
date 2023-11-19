package net.mebahel.antiquebeasts.item;

import com.github.crimsondawn45.fabricshieldlib.lib.object.FabricShieldItem;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class CustomShieldItem extends FabricShieldItem {
    public CustomShieldItem(Settings settings, int cooldownTicks, int enchantability, Item... repairItems) {
        super(settings, cooldownTicks, enchantability, repairItems);
    }
}

