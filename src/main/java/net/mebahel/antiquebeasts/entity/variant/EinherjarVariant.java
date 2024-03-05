package net.mebahel.antiquebeasts.entity.variant;

import java.util.Arrays;
import java.util.Comparator;

public enum EinherjarVariant {
    DEFAULT(0),
    CLOAK(1);

    private static final EinherjarVariant[] BY_ID = Arrays.stream(values()).sorted(Comparator.
            comparingInt(EinherjarVariant::getId)).toArray(EinherjarVariant[]::new);
    private final int id;

    EinherjarVariant(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static EinherjarVariant byId(int id) {
        return BY_ID[id % BY_ID.length];
    }
}
