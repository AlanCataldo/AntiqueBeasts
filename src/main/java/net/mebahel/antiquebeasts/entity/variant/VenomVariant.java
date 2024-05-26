package net.mebahel.antiquebeasts.entity.variant;

import java.util.Arrays;
import java.util.Comparator;

public enum VenomVariant {
    DEFAULT(0),
    CLOAK(1);

    private static final VenomVariant[] BY_ID = Arrays.stream(values()).sorted(Comparator.
            comparingInt(VenomVariant::getId)).toArray(VenomVariant[]::new);
    private final int id;

    VenomVariant(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static VenomVariant byId(int id) {
        return BY_ID[id % BY_ID.length];
    }
}
