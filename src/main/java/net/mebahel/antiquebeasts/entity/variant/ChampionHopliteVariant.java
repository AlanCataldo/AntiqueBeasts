package net.mebahel.antiquebeasts.entity.variant;

import java.util.Arrays;
import java.util.Comparator;

public enum ChampionHopliteVariant {
    DEFAULT(0),
    CLOAK(1);

    private static final ChampionHopliteVariant[] BY_ID = Arrays.stream(values()).sorted(Comparator.
            comparingInt(ChampionHopliteVariant::getId)).toArray(ChampionHopliteVariant[]::new);
    private final int id;

    ChampionHopliteVariant(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static ChampionHopliteVariant byId(int id) {
        return BY_ID[id % BY_ID.length];
    }
}
