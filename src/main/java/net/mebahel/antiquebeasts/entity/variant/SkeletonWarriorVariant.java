package net.mebahel.antiquebeasts.entity.variant;

import java.util.Arrays;
import java.util.Comparator;

public enum SkeletonWarriorVariant {
    ONE(0),

    TWO(1),

    THREE(2);

    private static final SkeletonWarriorVariant[] BY_ID = Arrays.stream(values()).sorted(Comparator.
            comparingInt(SkeletonWarriorVariant::getId)).toArray(SkeletonWarriorVariant[]::new);
    private final int id;

    SkeletonWarriorVariant(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static SkeletonWarriorVariant byId(int id) {
        return BY_ID[id % BY_ID.length];
    }
}
