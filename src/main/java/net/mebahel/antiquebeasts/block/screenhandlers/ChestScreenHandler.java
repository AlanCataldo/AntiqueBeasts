package net.mebahel.antiquebeasts.block.screenhandlers;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;

public class ChestScreenHandler extends SyncedGuiDescription {
    Inventory inventory;

    public ChestScreenHandler(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, getBlockInventory(context, 36), null); // ✅ Passe à 36 slots
        inventory = blockInventory;
        int rows = 4; // ✅ Maintenant 4 lignes
        int length = 9;

        WPlainPanel root = new WPlainPanel();
        setRootPanel(root);

        WItemSlot itemSlot;
        int counter = 0;

        for (int j = 0; j < rows; j++) { // ✅ Boucle modifiée pour 4 lignes
            for (int i = 0; i < length; i++) {
                itemSlot = WItemSlot.of(blockInventory, counter);
                root.add(itemSlot, (18 * i), 12 + (18 * j));
                counter++;
            }
        }

        // Ajustement de l'affichage
        root.setInsets(Insets.ROOT_PANEL);
        int height = 15 + 18 * rows;
        int width = 0;

        root.add(this.createPlayerInventoryPanel(), width, height);
        root.validate(this);
    }
}
