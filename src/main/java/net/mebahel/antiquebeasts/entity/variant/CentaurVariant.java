package net.mebahel.antiquebeasts.entity.variant;

import java.util.Arrays;
import java.util.Comparator;

public enum CentaurVariant {
    DEFAULT(0),
    DEFAULT_2(1),
    ARCHER(2),
    ARCHER_2(3);

    private static final CentaurVariant[] BY_ID = Arrays.stream(values()).sorted(Comparator.
            comparingInt(CentaurVariant::getId)).toArray(CentaurVariant[]::new);
    private final int id;

    CentaurVariant(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static CentaurVariant byId(int id) {
        return BY_ID[id % BY_ID.length];
    }
}
