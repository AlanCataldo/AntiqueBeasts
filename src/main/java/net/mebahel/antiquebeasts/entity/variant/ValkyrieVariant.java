package net.mebahel.antiquebeasts.entity.variant;

import java.util.Arrays;
import java.util.Comparator;

public enum ValkyrieVariant {
    DEFAULT(0),
    CLOAK(1);

    private static final ValkyrieVariant[] BY_ID = Arrays.stream(values()).sorted(Comparator.
            comparingInt(ValkyrieVariant::getId)).toArray(ValkyrieVariant[]::new);
    private final int id;

    ValkyrieVariant(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static ValkyrieVariant byId(int id) {
        return BY_ID[id % BY_ID.length];
    }
}
