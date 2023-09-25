package net.mebahel.antiquebeasts.entity.variant;

import java.util.Arrays;
import java.util.Comparator;

public enum HeroHopliteVariant {
    DEFAULT(0),
    CLOAK(1);

    private static final HeroHopliteVariant[] BY_ID = Arrays.stream(values()).sorted(Comparator.
            comparingInt(HeroHopliteVariant::getId)).toArray(HeroHopliteVariant[]::new);
    private final int id;

    HeroHopliteVariant(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static HeroHopliteVariant byId(int id) {
        return BY_ID[id % BY_ID.length];
    }
}
