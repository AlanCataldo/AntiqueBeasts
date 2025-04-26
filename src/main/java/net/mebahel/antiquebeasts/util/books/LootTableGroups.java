package net.mebahel.antiquebeasts.util.books;

import net.minecraft.util.Identifier;

import java.util.List;

public enum LootTableGroups {
    VILLAGE_ALL(List.of(
            id("village_armorer"),
            id("village_butcher"),
            id("village_cartographer"),
            id("village_desert_house"),
            id("village_fisher"),
            id("village_fletcher"),
            id("village_mason"),
            id("village_plains_house"),
            id("village_savanna_house"),
            id("village_shepherd"),
            id("village_snowy_house"),
            id("village_taiga_house"),
            id("village_tannery"),
            id("village_temple"),
            id("village_toolsmith"),
            id("village_weaponsmith")
    )),
    STRONGHOLD_AND_CITY(List.of(
            new Identifier("minecraft", "chests/stronghold_library"),
            new Identifier("minecraft", "chests/ancient_city")
    ));

    private final List<Identifier> tables;

    LootTableGroups(List<Identifier> tables) {
        this.tables = tables;
    }

    public List<Identifier> get() {
        return tables;
    }

    private static Identifier id(String path) {
        return new Identifier("minecraft", "chests/village/" + path);
    }
}
