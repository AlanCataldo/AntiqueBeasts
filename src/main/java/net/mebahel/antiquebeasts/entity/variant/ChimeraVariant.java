package net.mebahel.antiquebeasts.entity.variant;

import java.util.Arrays;
import java.util.Comparator;

public enum ChimeraVariant {
    DEFAULT(0),
    CLOAK(1);

    private static final ChimeraVariant[] BY_ID = Arrays.stream(values()).sorted(Comparator.
            comparingInt(ChimeraVariant::getId)).toArray(ChimeraVariant[]::new);
    private final int id;

    ChimeraVariant(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static ChimeraVariant byId(int id) {
        return BY_ID[id % BY_ID.length];
    }
}
