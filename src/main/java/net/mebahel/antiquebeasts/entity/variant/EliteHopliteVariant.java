package net.mebahel.antiquebeasts.entity.variant;

import java.util.Arrays;
import java.util.Comparator;

public enum EliteHopliteVariant {
    DEFAULT(0),
    CLOAK(1);

    private static final EliteHopliteVariant[] BY_ID = Arrays.stream(values()).sorted(Comparator.
            comparingInt(EliteHopliteVariant::getId)).toArray(EliteHopliteVariant[]::new);
    private final int id;

    EliteHopliteVariant(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static EliteHopliteVariant byId(int id) {
        return BY_ID[id % BY_ID.length];
    }
}
