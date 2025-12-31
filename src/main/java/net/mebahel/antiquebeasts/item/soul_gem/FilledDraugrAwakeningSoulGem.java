package net.mebahel.antiquebeasts.item.soul_gem;

import net.mebahel.antiquebeasts.util.raid.DraugrRaid;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FilledDraugrAwakeningSoulGem extends Item {

    // Clés i18n (centralisées pour éviter les fautes de frappe)
    private static final String TT_LINE1 = "item.antiquebeasts.filled_draugr_awakening_soul_gem.tooltip1";
    private static final String TT_LINE2 = "item.antiquebeasts.filled_draugr_awakening_soul_gem.tooltip2";
    private static final String MSG_SURFACE_REQUIRED = "item.antiquebeasts.filled_draugr_awakening_soul_gem.msg.surface_required";
    private static final String MSG_AWAKENED = "item.antiquebeasts.filled_draugr_awakening_soul_gem.msg.awakened"; // déjà existant chez toi

    public FilledDraugrAwakeningSoulGem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world,
                              List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable(TT_LINE1).formatted(Formatting.DARK_PURPLE));
        tooltip.add(Text.translatable(TT_LINE2).formatted(Formatting.GRAY));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        // Doit être à la surface
        if (!world.isSkyVisible(player.getBlockPos())) {
            if (!world.isClient) {
                player.sendMessage(
                        Text.translatable(MSG_SURFACE_REQUIRED).formatted(Formatting.RED),
                        true
                );
            }
            return TypedActionResult.fail(stack);
        }

        if (!world.isClient) {
            if (!startRaid(player)) {
                return TypedActionResult.fail(stack);
            }

            // Au choix : consommer ou endommager
            // stack.decrement(1);
            stack.damage(1, player, (entity) -> entity.sendToolBreakStatus(hand));
        }

        return TypedActionResult.success(stack);
    }

    private boolean startRaid(PlayerEntity player) {
        if (!(player.getWorld() instanceof ServerWorld serverWorld)) return false;

        DraugrRaid raid = new DraugrRaid(player, serverWorld);

        player.sendMessage(
                Text.translatable(MSG_AWAKENED).formatted(Formatting.RED),
                true
        );

        raid.startRaid();
        raid.saveRaid();
        return true;
    }
}