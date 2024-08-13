package net.mebahel.antiquebeasts.entity.variant;

import java.util.Arrays;
import java.util.Comparator;

public enum WadjetVariant {
    DEFAULT(0),
    CLOAK(1);

    private static final WadjetVariant[] BY_ID = Arrays.stream(values()).sorted(Comparator.
            comparingInt(WadjetVariant::getId)).toArray(WadjetVariant[]::new);
    private final int id;

    WadjetVariant(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static WadjetVariant byId(int id) {
        return BY_ID[id % BY_ID.length];
    }
}
