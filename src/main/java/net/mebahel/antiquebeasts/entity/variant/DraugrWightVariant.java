package net.mebahel.antiquebeasts.entity.variant;

import java.util.Arrays;
import java.util.Comparator;

public enum DraugrWightVariant {
    TEMPERATE(0),

    COLD(1),

    HOT(2),
    TEMPERATE_AXE(3),
    COLD_AXE(4),
    HOT_AXE(5);

    private static final DraugrWightVariant[] BY_ID = Arrays.stream(values()).sorted(Comparator.
            comparingInt(DraugrWightVariant::getId)).toArray(DraugrWightVariant[]::new);
    private final int id;

    DraugrWightVariant(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static DraugrWightVariant byId(int id) {
        return BY_ID[id % BY_ID.length];
    }
}
