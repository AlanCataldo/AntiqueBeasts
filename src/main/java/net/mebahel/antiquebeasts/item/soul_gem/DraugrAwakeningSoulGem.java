package net.mebahel.antiquebeasts.item.soul_gem;


import net.mebahel.antiquebeasts.entity.custom.other.DraugrEntity;
import net.mebahel.antiquebeasts.item.custom.ModItems;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DraugrAwakeningSoulGem extends Item {
    private static final String KILL_COUNT_KEY = "DraugrKills";
    private static final int MAX_KILL = 12;

    public DraugrAwakeningSoulGem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world,
                              List<Text> tooltip, TooltipContext context) {
        int killCount = getKillCount(stack);

        tooltip.add(
                Text.translatable("item.antiquebeasts.filled_draugr_awakening_soul_gem.tooltip.kills")
                        .formatted(Formatting.GOLD)
                        .append(Text.literal(killCount + " / " + MAX_KILL).formatted(Formatting.YELLOW))
        );
        tooltip.add(Text.translatable("item.antiquebeasts.filled_draugr_awakening_soul_gem.msg.absorbing")
                .formatted(Formatting.GRAY));
    }

    // 🔹 Lecture du compteur
    public static int getKillCount(ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateNbt();
        return nbt.contains(KILL_COUNT_KEY) ? nbt.getInt(KILL_COUNT_KEY) : 0;
    }

    // 🔹 Incrément + éventuelle transformation en gemme remplie
    public static void incrementKillCount(ItemStack stack, PlayerEntity player) {
        NbtCompound nbt = stack.getOrCreateNbt();
        int current = getKillCount(stack);

        if (current >= MAX_KILL) return;

        current++;
        nbt.putInt(KILL_COUNT_KEY, current);
        stack.setNbt(nbt);

        // Sync client
        player.getInventory().markDirty();
        player.currentScreenHandler.sendContentUpdates();

        // 💥 FULL → transformer en Filled gem
        if (current >= MAX_KILL) {
            transformToFilledGem(player, stack);
        }
    }

    // 🔹 Remplace cette gemme par la version "filled"
    private static void transformToFilledGem(PlayerEntity player, ItemStack originalStack) {
        if (player.getWorld().isClient) return;

        ItemStack filled = new ItemStack(ModItems.FILLED_DRAUGR_AWAKENING_SOUL_GEM);

        // Copie le nom custom si tu en as un
        if (originalStack.hasCustomName()) {
            filled.setCustomName(originalStack.getName());
        }

        // Optionnel : tu peux copier certains NBT si tu veux garder une trace
        // filled.setNbt(originalStack.getOrCreateNbt().copy());

        // Remplacement dans main / offhand / inventaire
        boolean replaced = false;

        // Main hand
        if (player.getMainHandStack() == originalStack) {
            player.setStackInHand(Hand.MAIN_HAND, filled);
            replaced = true;
        }

        // Offhand
        if (!replaced && player.getOffHandStack() == originalStack) {
            player.setStackInHand(Hand.OFF_HAND, filled);
            replaced = true;
        }

        // Inventaire principal
        if (!replaced) {
            var inv = player.getInventory();
            for (int i = 0; i < inv.size(); i++) {
                if (inv.getStack(i) == originalStack) {
                    inv.setStack(i, filled);
                    replaced = true;
                    break;
                }
            }
        }

        if (!replaced) {
            // Sécurité : si on ne trouve pas la stack (rare), on give et on retire une
            originalStack.decrement(1);
            player.giveItemStack(filled);
        }
    }

    /**
     * ✅ Retourne juste PASS : la version de base ne fait rien quand on clique.
     */
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        return TypedActionResult.pass(player.getStackInHand(hand));
    }

    /**
     * 🔥 Appelée quand un Draugr meurt pour donner de l’XP "d’âme" à une gemme.
     */
    public static void onDraugrKilled(PlayerEntity player, LivingEntity killedEntity) {
        if (!(killedEntity instanceof DraugrEntity)) return;

        List<ItemStack> validGems = new ArrayList<>();

        // Main hand
        ItemStack main = player.getMainHandStack();
        if (isValidBaseGem(main)) validGems.add(main);

        // Offhand
        ItemStack off = player.getOffHandStack();
        if (isValidBaseGem(off)) validGems.add(off);

        // Inventaire
        for (ItemStack stack : player.getInventory().main) {
            if (isValidBaseGem(stack)) {
                validGems.add(stack);
            }
        }

        if (!validGems.isEmpty()) {
            Random random = new Random();
            ItemStack selected = validGems.get(random.nextInt(validGems.size()));
            incrementKillCount(selected, player);
        }
    }

    private static boolean isValidBaseGem(ItemStack stack) {
        return !stack.isEmpty()
                && stack.getItem() == ModItems.DRAUGR_AWAKENING_SOUL_GEM
                && getKillCount(stack) < MAX_KILL;
    }
}
