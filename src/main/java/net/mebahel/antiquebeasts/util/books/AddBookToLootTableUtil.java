package net.mebahel.antiquebeasts.util.books;

import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.function.SetNbtLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.*;

public class AddBookToLootTableUtil {
    private final Map<String, NbtCompound> books = new HashMap<>();

    /**
     * 📖 Ajoute un livre directement à partir d'un texte en String.
     * @param bookId ID unique du livre
     * @param title Titre du livre
     * @param author Auteur du livre
     * @param content Texte complet du livre
     */
    public void addBookFromString(String bookId, String title, String author, String content) {
        List<String> pages = splitIntoPages(content, 256); // Découpe auto en pages
        addBook(bookId, title, author, pages);
    }

    /**
     * 📖 Ajoute un livre personnalisé à la liste.
     */
    public void addBook(String bookId, String title, String author, List<String> pages) {
        NbtCompound bookNbt = new NbtCompound();
        bookNbt.putString("title", title);
        bookNbt.putString("author", author);
        bookNbt.put("pages", createBookPages(pages));
        books.put(bookId, bookNbt);
    }

    /**
     * 📜 Convertit une liste de textes en pages de livre Minecraft.
     */
    private NbtList createBookPages(List<String> pages) {
        NbtList pagesList = new NbtList();
        for (String text : pages) {
            pagesList.add(NbtString.of(Text.Serializer.toJson(Text.of(text)))); // ✅ Correction ici !
        }
        return pagesList;
    }

    /**
     * ✂️ Découpe un texte en plusieurs pages (256 caractères max par page).
     */
    private List<String> splitIntoPages(String text, int maxPageLength) {
        List<String> pages = new ArrayList<>();
        int start = 0;
        while (start < text.length()) {
            int end = Math.min(start + maxPageLength, text.length());
            pages.add(text.substring(start, end));
            start = end;
        }
        return pages;
    }

    /**
     * 🎲 Enregistre les livres dans les loot tables.
     */
    public void registerModifyLootTable() {
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            List<Identifier> lootTables = List.of(
                    new Identifier("minecraft", "chests/village/village_armorer"),
                    new Identifier("minecraft", "chests/village/village_butcher"),
                    new Identifier("minecraft", "chests/village/village_cartographer"),
                    new Identifier("minecraft", "chests/village/village_desert_house"),
                    new Identifier("minecraft", "chests/village/village_fisher"),
                    new Identifier("minecraft", "chests/village/village_fletcher"),
                    new Identifier("minecraft", "chests/village/village_mason"),
                    new Identifier("minecraft", "chests/village/village_plains_house"),
                    new Identifier("minecraft", "chests/village/village_savanna_house"),
                    new Identifier("minecraft", "chests/village/village_shepherd"),
                    new Identifier("minecraft", "chests/village/village_snowy_house"),
                    new Identifier("minecraft", "chests/village/village_taiga_house"),
                    new Identifier("minecraft", "chests/village/village_tannery"),
                    new Identifier("minecraft", "chests/village/village_temple"),
                    new Identifier("minecraft", "chests/village/village_toolsmith"),
                    new Identifier("minecraft", "chests/village/village_weaponsmith"),
                    new Identifier("minecraft", "chests/stronghold_library"),
                    new Identifier("minecraft", "chests/ancient_city")
            );
            int bookCount = books.size();
            int ironWeight = 5 + (bookCount * 3);

            if (lootTables.contains(id)) {
                LootPool.Builder poolBuilder = LootPool.builder();

                // 🎲 Ajoute un item "factice" (lingot de fer, quantité 0) pour réduire la fréquence des livres
                LootPoolEntry emptyEntry = ItemEntry.builder(Items.IRON_INGOT)
                        .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(0))) // Définit la quantité à 0
                        .weight(ironWeight)
                        .build();
                poolBuilder.with(emptyEntry);

                // 📖 Ajoute chaque livre avec un poids ajustable
                for (NbtCompound bookNbt : books.values()) {
                    LootPoolEntry bookEntry = ItemEntry.builder(Items.WRITTEN_BOOK)
                            .apply(SetNbtLootFunction.builder(bookNbt))
                            .weight(5) // Plus petit = plus rare
                            .build();
                    poolBuilder.with(bookEntry);
                }

                tableBuilder.pool(poolBuilder.build());
            }
        });
    }
}
