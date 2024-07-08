package net.mebahel.antiquebeasts.entity.variant;

import java.util.Arrays;
import java.util.Comparator;

public enum PegasusVariant {
    DEFAULT(0),

    CLOAK(1),
    UNMOUNT(2),
    LEATHER_UNMOUNT(3),
    IRON_UNMOUNT(4),
    GOLD_UNMOUNT(5),
    DIAMOND_UNMOUNT(6),
    LEATHER_SADDLED_UNMOUNTED(7),
    IRON_SADDLED_UNMOUNTED(8),
    GOLD_SADDLED_UNMOUNTED(9),
    DIAMOND_SADDLED_UNMOUNTED(10),
    LEATHER_SADDLED_MOUNTED(11),
    IRON_SADDLED_MOUNTED(12),
    GOLD_SADDLED_MOUNTED(13),
    DIAMOND_SADDLED_MOUNTED(14);


    private static final PegasusVariant[] BY_ID = Arrays.stream(values()).sorted(Comparator.
            comparingInt(PegasusVariant::getId)).toArray(PegasusVariant[]::new);
    private final int id;

    PegasusVariant(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static PegasusVariant byId(int id) {
        return BY_ID[id % BY_ID.length];
    }
}
