package net.mebahel.antiquebeasts.item;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.custom.other.DraugrEntity;
import net.mebahel.antiquebeasts.item.custom.ModItems;
import net.mebahel.antiquebeasts.util.raid.DraugrRaidTest;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Formatting;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ThurisazRune extends Item {
    private static final String KILL_COUNT_KEY = "DraugrKills"; // Clé NBT
    private static final int MAX_KILL = 20;
    private static final String ACTIVATABLE_KEY = "Activatable"; // Clé pour la rune activable

    public ThurisazRune(Settings settings) {
        super(settings);
    }

    /**
     * ✅ Affiche le compteur de kills dans la description de l'item.
     */
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        int killCount = getKillCount(stack);
        boolean activatable = isActivatable(stack);

        // 🟢 Met à jour le tooltip avec le bon nombre de kills
        tooltip.add(Text.translatable("§6Draugrs killed :")
                .append(Text.literal(" " + killCount + " / " + MAX_KILL)));

        // Si la rune est activable, ajoute une ligne supplémentaire
        if (activatable) {
            tooltip.add(Text.literal("§aReady to activate!").formatted(Formatting.GREEN));
        }
    }

    /**
     * 🔄 Récupère le nombre de Draugrs tués stocké dans le NBT.
     */
    public static int getKillCount(ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateNbt();
        return nbt.contains(KILL_COUNT_KEY) ? nbt.getInt(KILL_COUNT_KEY) : 0;
    }

    /**
     * 📈 Incrémente le compteur et vérifie si la rune devient activable.
     */
    public static void incrementKillCount(ItemStack stack, PlayerEntity player) {
        NbtCompound nbt = stack.getOrCreateNbt();
        int currentCount = getKillCount(stack);
        //System.out.println("Kill count actuel : " + currentCount);

        if (currentCount < MAX_KILL) { // Cap max à 30 kills
            currentCount++;
            nbt.putInt(KILL_COUNT_KEY, currentCount); // ✅ Incrémente le compteur

            // Vérifie si la rune atteint 30/30
            if (currentCount == MAX_KILL) {
                nbt.putBoolean(ACTIVATABLE_KEY, true); // ✅ Marque la rune comme activable
                //System.out.println("Rune activable !");
            }

            stack.setNbt(nbt); // ✅ Met à jour l'item avec les nouveaux NBT

            // 🔄 Force le rafraîchissement du client
            player.getInventory().markDirty();
            player.currentScreenHandler.sendContentUpdates();
        }
    }

    /**
     * ✅ Vérifie si la rune est activable.
     */
    public static boolean isActivatable(ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateNbt();
        return nbt.contains(ACTIVATABLE_KEY) && nbt.getBoolean(ACTIVATABLE_KEY);
    }

    /**
     * ⚔️ Événement : Si un Draugr est tué, on met à jour l'item.
     */
    public static void onDraugrKilled(PlayerEntity player, LivingEntity killedEntity) {
        if (killedEntity instanceof DraugrEntity) {

            // ✅ Liste pour stocker les runes éligibles
            List<ItemStack> validRunes = new ArrayList<>();

            // 🔍 Vérifie la main principale
            ItemStack mainHandItem = player.getMainHandStack();
            if (isValidRune(mainHandItem)) {
                validRunes.add(mainHandItem);
            }

            // 🔍 Vérifie la main secondaire
            ItemStack offHandItem = player.getOffHandStack();
            if (isValidRune(offHandItem)) {
                validRunes.add(offHandItem);
            }

            // 🔍 Vérifie l'inventaire (évite d'ajouter celles déjà au max)
            for (ItemStack stack : player.getInventory().main) {
                if (isValidRune(stack)) {
                    validRunes.add(stack);
                }
            }

            // ✅ Sélectionne une rune au hasard parmi celles disponibles
            if (!validRunes.isEmpty()) {
                Random random = new Random();
                ItemStack selectedRune = validRunes.get(random.nextInt(validRunes.size()));
                incrementKillCount(selectedRune, player); // 📈 Incrémente la rune choisie
            }
        }
    }

    /**
     * ✅ Vérifie si une rune est valide (n'est pas au max).
     */
    private static boolean isValidRune(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() == ModItems.THURISAZ_RUNE && getKillCount(stack) < MAX_KILL;
    }


    /**
     * 🖱️ Déclenche l'activation de la rune si elle est activable.
     */
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        if (isActivatable(stack)) {
            // Vérifie si le joueur est à la surface
            if (world.isSkyVisible(player.getBlockPos())) {
                startRaid(player);
                stack.damage(1, player, (entity) -> entity.sendToolBreakStatus(hand)); // Casse l'item
                return TypedActionResult.success(stack); // Retourne un succès avec l'ItemStack
            } else {
                // Si le joueur n'est pas à la surface, affiche un message
                if (!world.isClient) { // Côté serveur pour éviter les doublons
                    player.sendMessage(Text.literal("You must be on the surface to activate this rune!")
                            .formatted(Formatting.RED), true);
                }
                return TypedActionResult.fail(stack); // Retourne un échec
            }
        }

        return TypedActionResult.pass(stack); // Si la rune n'est pas activable, retourne un PASS
    }

    /**
     * 🌟 Méthode appelée lors de l'activation de la rune.
     */
    private void startRaid(PlayerEntity player) {
        if (player.getWorld() instanceof ServerWorld serverWorld) {
            if (AntiqueBeasts.ongoingRaids.isEmpty()) {
                System.out.println("No ongoing raids. Proceeding to create a new raid.");
            } else {
                // Retirez les raids complétés ou inactifs
                AntiqueBeasts.ongoingRaids.removeIf(raid -> !raid.isRaidInProgress() || raid.isRaidCompleted());
                System.out.println("Cleaned up completed or inactive raids from ongoingRaids.");
            }

            // Vérifiez si un raid est actif pour un autre joueur
            boolean anyRaidInProgress = AntiqueBeasts.ongoingRaids.stream()
                    .anyMatch(DraugrRaidTest::isRaidInProgress);

            if (anyRaidInProgress) {
                player.sendMessage(Text.literal("A raid is already in progress! Please wait for it to finish.")
                        .styled(style -> style.withColor(Formatting.RED)), false);
                return;
            }

            DraugrRaidTest raid = new DraugrRaidTest(player, serverWorld);
            player.sendMessage(Text.literal("You've awoken an ancient evil...")
                    .formatted(Formatting.RED), true);
            raid.startRaid();
            raid.saveRaid();
        }
    }
}
