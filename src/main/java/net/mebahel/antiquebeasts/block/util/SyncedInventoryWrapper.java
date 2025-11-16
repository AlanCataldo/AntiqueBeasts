package net.mebahel.antiquebeasts.block.util;

import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.entity.player.PlayerEntity;

public class SyncedInventoryWrapper implements Inventory {
    private final DefaultedList<ItemStack> backingList;

    public SyncedInventoryWrapper(DefaultedList<ItemStack> list) {
        this.backingList = list;
    }

    @Override
    public int size() {
        return backingList.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : backingList) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        return backingList.get(slot);
    }

    // ✅ Utilise splitStack pour retirer une partie du stack
    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack result = Inventories.splitStack(backingList, slot, amount);
        if (!result.isEmpty()) markDirty();
        return result;
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack result = backingList.get(slot);
        backingList.set(slot, ItemStack.EMPTY);
        markDirty();
        return result;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        backingList.set(slot, stack);
        if (stack.getCount() > getMaxCountPerStack()) {
            stack.setCount(getMaxCountPerStack());
        }
        markDirty();
    }

    @Override
    public void markDirty() {}

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        backingList.clear();
        markDirty();
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return true;
    }
}
