package net.mebahel.antiquebeasts.entity.variant;

import java.util.Arrays;
import java.util.Comparator;

public enum DraugrOverlordVariant {
    GREATSWORD(0),
    BOW(1);

    private static final DraugrOverlordVariant[] BY_ID = Arrays.stream(values()).sorted(Comparator.
            comparingInt(DraugrOverlordVariant::getId)).toArray(DraugrOverlordVariant[]::new);
    private final int id;

    DraugrOverlordVariant(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static DraugrOverlordVariant byId(int id) {
        return BY_ID[id % BY_ID.length];
    }
}
