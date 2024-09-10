package net.mebahel.antiquebeasts.entity.variant;

import java.util.Arrays;
import java.util.Comparator;

public enum HarpyVariant {
    DEFAULT(0),
    CLOAK(1);

    private static final HarpyVariant[] BY_ID = Arrays.stream(values()).sorted(Comparator.
            comparingInt(HarpyVariant::getId)).toArray(HarpyVariant[]::new);
    private final int id;

    HarpyVariant(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static HarpyVariant byId(int id) {
        return BY_ID[id % BY_ID.length];
    }
}
