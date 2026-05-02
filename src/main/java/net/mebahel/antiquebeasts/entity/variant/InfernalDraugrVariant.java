package net.mebahel.antiquebeasts.entity.variant;

import java.util.Arrays;
import java.util.Comparator;

public enum InfernalDraugrVariant {
    VARIANT_1(0),
    VARIANT_2(1);

    private static final InfernalDraugrVariant[] BY_ID = Arrays.stream(values()).sorted(Comparator.
            comparingInt(InfernalDraugrVariant::getId)).toArray(InfernalDraugrVariant[]::new);
    private final int id;

    InfernalDraugrVariant(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static InfernalDraugrVariant byId(int id) {
        return BY_ID[id % BY_ID.length];
    }
}
