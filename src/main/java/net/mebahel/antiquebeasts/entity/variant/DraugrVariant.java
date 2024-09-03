package net.mebahel.antiquebeasts.entity.variant;

import java.util.Arrays;
import java.util.Comparator;

public enum DraugrVariant {
    TEMPERATE(0),
    COLD(1),
    HOT(2),
    TEMPERATE_AXE(3),
    COLD_AXE(4),
    HOT_AXE(5);

    private static final DraugrVariant[] BY_ID = Arrays.stream(values()).sorted(Comparator.
            comparingInt(DraugrVariant::getId)).toArray(DraugrVariant[]::new);
    private final int id;

    DraugrVariant(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static DraugrVariant byId(int id) {
        return BY_ID[id % BY_ID.length];
    }
}
