package net.mebahel.antiquebeasts.util.raid;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.PersistentState;

public class AntiquebeastsDifficultyState extends PersistentState {
    private int difficultyLevel;

    public AntiquebeastsDifficultyState(int difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public int getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(int difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
        this.markDirty();
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putInt("difficultyLevel", difficultyLevel);
        return nbt;
    }

    public static AntiquebeastsDifficultyState fromNbt(NbtCompound nbt) {
        return new AntiquebeastsDifficultyState(nbt.getInt("difficultyLevel"));
    }
}
