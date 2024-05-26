package net.mebahel.antiquebeasts.entity.variant;

import java.util.Arrays;
import java.util.Comparator;

public enum EgyptiantVariant {
    DEFAULT(0),
    CLOAK(1);

    private static final EgyptiantVariant[] BY_ID = Arrays.stream(values()).sorted(Comparator.
            comparingInt(EgyptiantVariant::getId)).toArray(EgyptiantVariant[]::new);
    private final int id;

    EgyptiantVariant(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static EgyptiantVariant byId(int id) {
        return BY_ID[id % BY_ID.length];
    }
}
