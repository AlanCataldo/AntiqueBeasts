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

    private final Map<String, BookLootData> books = new HashMap<>();

    /**
     * 📖 Ajoute un livre directement à partir d'un texte.
     */
    public void addBookFromString(String bookId, String title, String author, String content, List<Identifier> targetLootTables) {
        List<String> pages = splitIntoPages(content, 256);
        addBook(bookId, title, author, pages, targetLootTables);
    }

    /**
     * 📖 Ajoute un livre à la liste des livres à injecter dans les loot tables.
     */
    public void addBook(String bookId, String title, String author, List<String> pages, List<Identifier> targetLootTables) {
        NbtCompound bookNbt = new NbtCompound();
        bookNbt.putString("title", title);
        bookNbt.putString("author", author);
        bookNbt.put("pages", createBookPages(pages));
        books.put(bookId, new BookLootData(bookNbt, targetLootTables));
    }

    /**
     * 📜 Convertit une liste de pages String en NbtList.
     */
    private NbtList createBookPages(List<String> pages) {
        NbtList pagesList = new NbtList();
        for (String text : pages) {
            pagesList.add(NbtString.of(Text.Serializer.toJson(Text.of(text))));
        }
        return pagesList;
    }

    /**
     * ✂️ Découpe un long texte en pages.
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
     * 📦 Injection dans les loot tables ciblées.
     */
    public void registerModifyLootTable() {
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            List<BookLootData> matchingBooks = books.values().stream()
                    .filter(book -> book.targetLootTables.contains(id))
                    .toList();

            if (matchingBooks.isEmpty()) return;

            LootPool.Builder poolBuilder = LootPool.builder();

            // ⚖️ Entrée factice pour réduire la proba
            poolBuilder.with(
                    ItemEntry.builder(Items.IRON_INGOT)
                            .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(0)))
                            .weight(3 + (matchingBooks.size() * 3))
            );

            // 📚 Ajoute chaque livre
            for (BookLootData book : matchingBooks) {
                poolBuilder.with(
                        ItemEntry.builder(Items.WRITTEN_BOOK)
                                .apply(SetNbtLootFunction.builder(book.nbt))
                                .weight(1)
                );
            }

            // ✅ Nouveau comportement 1.21
            tableBuilder.pool(poolBuilder);
        });
    }

    /**
     * 📚 Représente un livre avec ses loot tables cibles.
     */
    private record BookLootData(NbtCompound nbt, List<Identifier> targetLootTables) {}
}
