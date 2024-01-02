package net.mebahel.antiquebeasts.entity.variant;

import java.util.Arrays;
import java.util.Comparator;

public enum ThrowingAxeManVariant {
    DEFAULT(0),
    CLOAK(1);

    private static final ThrowingAxeManVariant[] BY_ID = Arrays.stream(values()).sorted(Comparator.
            comparingInt(ThrowingAxeManVariant::getId)).toArray(ThrowingAxeManVariant[]::new);
    private final int id;

    ThrowingAxeManVariant(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static ThrowingAxeManVariant byId(int id) {
        return BY_ID[id % BY_ID.length];
    }
}
