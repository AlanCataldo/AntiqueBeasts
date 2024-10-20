package net.mebahel.antiquebeasts.entity.variant;

import java.util.Arrays;
import java.util.Comparator;

public enum DraugrScourgeVariant {
    TEMPERATE(0),

    COLD(1),

    HOT(2);

    private static final DraugrScourgeVariant[] BY_ID = Arrays.stream(values()).sorted(Comparator.
            comparingInt(DraugrScourgeVariant::getId)).toArray(DraugrScourgeVariant[]::new);
    private final int id;

    DraugrScourgeVariant(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static DraugrScourgeVariant byId(int id) {
        return BY_ID[id % BY_ID.length];
    }
}
