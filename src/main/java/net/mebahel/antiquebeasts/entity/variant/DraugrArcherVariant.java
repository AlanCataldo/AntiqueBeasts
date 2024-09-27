package net.mebahel.antiquebeasts.entity.variant;

import java.util.Arrays;
import java.util.Comparator;

public enum DraugrArcherVariant {
    TEMPERATE(0),
    COLD(1),
    HOT(2);

    private static final DraugrArcherVariant[] BY_ID = Arrays.stream(values()).sorted(Comparator.
            comparingInt(DraugrArcherVariant::getId)).toArray(DraugrArcherVariant[]::new);
    private final int id;

    DraugrArcherVariant(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static DraugrArcherVariant byId(int id) {
        return BY_ID[id % BY_ID.length];
    }
}
