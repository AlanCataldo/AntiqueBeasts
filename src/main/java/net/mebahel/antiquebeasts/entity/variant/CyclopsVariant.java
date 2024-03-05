package net.mebahel.antiquebeasts.entity.variant;

import java.util.Arrays;
import java.util.Comparator;

public enum CyclopsVariant {
    DEFAULT(0),
    CLOAK(1);

    private static final CyclopsVariant[] BY_ID = Arrays.stream(values()).sorted(Comparator.
            comparingInt(CyclopsVariant::getId)).toArray(CyclopsVariant[]::new);
    private final int id;

    CyclopsVariant(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static CyclopsVariant byId(int id) {
        return BY_ID[id % BY_ID.length];
    }
}
