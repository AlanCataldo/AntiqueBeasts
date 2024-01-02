package net.mebahel.antiquebeasts.entity.variant;

import java.util.Arrays;
import java.util.Comparator;

public enum HersirVariant {
    DEFAULT(0),
    CLOAK(1);

    private static final HersirVariant[] BY_ID = Arrays.stream(values()).sorted(Comparator.
            comparingInt(HersirVariant::getId)).toArray(HersirVariant[]::new);
    private final int id;

    HersirVariant(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static HersirVariant byId(int id) {
        return BY_ID[id % BY_ID.length];
    }
}
