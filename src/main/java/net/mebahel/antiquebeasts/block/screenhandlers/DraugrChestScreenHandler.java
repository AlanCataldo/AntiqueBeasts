package net.mebahel.antiquebeasts.block.screenhandlers;

import net.mebahel.antiquebeasts.block.entity.base.BaseChestBlockEntity;
import net.mebahel.antiquebeasts.block.util.SyncedInventoryWrapper;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DraugrChestScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private DefaultedList<ItemStack> personalInventory;
    public static final int ROWS = 4;
    public static final int COLUMNS = 9;
    private final BlockPos chestPos;
    private final World world;

    // --- constructeur côté client (depuis le buf) ---
    public DraugrChestScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, playerInventory.player.getWorld().getBlockEntity(buf.readBlockPos()));
    }

    // --- constructeur standard (BE existant) ---
    public DraugrChestScreenHandler(int syncId, PlayerInventory playerInventory, BlockEntity blockEntity) {
        super(ModScreenHandlers.DRAUGR_CHEST_SCREEN_HANDLER, syncId);
        this.inventory = (Inventory) blockEntity;
        this.world = playerInventory.player.getWorld();
        this.chestPos = blockEntity.getPos();
        checkSize(inventory, ROWS * COLUMNS);
        inventory.onOpen(playerInventory.player);

        // slots du coffre
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLUMNS; ++col) {
                this.addSlot(new Slot(inventory, col + row * COLUMNS, 8 + col * 18, 18 + row * 18));
            }
        }

        // inventaire joueur
        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
    }

    // --- constructeur avec loot personnel ---
    public DraugrChestScreenHandler(int syncId, PlayerInventory playerInventory, DefaultedList<ItemStack> personalInventory, BlockPos pos) {
        super(ModScreenHandlers.DRAUGR_CHEST_SCREEN_HANDLER, syncId);
        this.inventory = playerInventory;
        this.personalInventory = personalInventory;
        this.chestPos = pos;
        this.world = playerInventory.player.getWorld();

        int slotIndex = 0;
        for (int row = 0; row < 4; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(new SyncedInventoryWrapper(personalInventory), slotIndex++, 8 + col * 18, 18 + row * 18));
            }
        }

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
    }

    // --- gestion fermeture ---
    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);

        if (this.world.getBlockEntity(this.chestPos) instanceof BaseChestBlockEntity chest) {
            chest.onClose(player);
        }
    }


    // --- synchro automatique pour clic droit, drag, etc. ---
    @Override
    public void sendContentUpdates() {
        super.sendContentUpdates();
        if (!this.world.isClient) {
            this.inventory.markDirty();
        }
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        super.onContentChanged(inventory);
        this.sendContentUpdates();
    }

    // --- transfert rapide (shift + clic) ---
    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            int containerSize = ROWS * COLUMNS;

            if (invSlot < containerSize) {
                if (!this.insertItem(originalStack, containerSize, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, containerSize, false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return newStack;
    }

    // --- utilitaires pour slots du joueur ---
    private void addPlayerInventory(PlayerInventory playerInventory) {
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 104 + row * 18));
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory playerInventory) {
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 162));
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
