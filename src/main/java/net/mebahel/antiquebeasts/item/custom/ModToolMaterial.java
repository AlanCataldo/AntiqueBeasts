package net.mebahel.antiquebeasts.item.custom;

import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import java.util.function.Supplier;

public enum ModToolMaterial implements ToolMaterial {

    IRON_CENTAUR(2, 480, 6.0F, 2.5F, 17, () -> {
        return Ingredient.ofItems(Items.IRON_INGOT);
    }),
    FROST_WEAPON(4, 1550, 4.0F, 0.5F, 17, () -> {
        return Ingredient.ofItems(ModItems.FROST_SHARD);
    }),

    PHARAOH_SCEPTER(4, 1550, 4.0F, 0.5F, 25, () -> {
        return Ingredient.ofItems(ModItems.GOLD_SCARAB);
    }),
    HERSIR_AXE(4, 680, 6.5F, 0.5F, 19, () -> {
        return Ingredient.ofItems(ModItems.HIGH_IRON_INGOT);
    }),

    IRON_KHOPESH(4, 680, 7F, 1F, 15, () -> {
        return Ingredient.ofItems(ModItems.IRON_SCARAB);
    }),
    GOLD_KHOPESH(4, 480, 7F, 0.5F, 19, () -> {
        return Ingredient.ofItems(ModItems.GOLD_SCARAB);
    }),
    DIAMOND_KHOPESH(4, 1280, 7F, 1F, 17, () -> {
        return Ingredient.ofItems(ModItems.DIAMOND_SCARAB);
    }),
    NETHERITE_KHOPESH(4, 1580, 7F, 1F, 20, () -> {
        return Ingredient.ofItems(Items.NETHERITE_INGOT);
    }),

    EBONY(4, 2048, 7F, 0.5F, 22, () -> {
        return Ingredient.ofItems(ModItems.EBONY_INGOT);
    }),
    DWARVEN_1(4, 1380, 7F, 0F, 20, () -> {
        return Ingredient.ofItems(ModItems.EBONY_INGOT);
    }),
    DWARVEN_2(4, 1380, 7F, 0.5F, 20, () -> {
        return Ingredient.ofItems(ModItems.EBONY_INGOT);
    }),

    HIGH_IRON(4, 680, 7.0F, 1.5F, 19, () -> {
        return Ingredient.ofItems(ModItems.HIGH_IRON_INGOT);
    }),

    THROWING_AXE(2, 680, 6.0F, 0.5F, 15, () -> {
        return Ingredient.ofItems(ModItems.HIGH_IRON_INGOT);
    });

    private final int miningLevel;
    private final int itemDurability;
    private final float miningSpeed;
    private final float attackDamage;
    private final int enchantability;
    private final Supplier<Ingredient> repairIngredient;

    private ModToolMaterial(int miningLevel, int itemDurability, float miningSpeed, float attackDamage, int enchantability, Supplier repairIngredient) {
        this.miningLevel = miningLevel;
        this.itemDurability = itemDurability;
        this.miningSpeed = miningSpeed;
        this.attackDamage = attackDamage;
        this.enchantability = enchantability;
        this.repairIngredient = repairIngredient;
    }

    public int getDurability() {
        return this.itemDurability;
    }

    public float getMiningSpeedMultiplier() {
        return this.miningSpeed;
    }

    public float getAttackDamage() {
        return this.attackDamage;
    }

    public int getMiningLevel() {
        return this.miningLevel;
    }

    public int getEnchantability() {
        return this.enchantability;
    }

    public Ingredient getRepairIngredient() {
        return (Ingredient)this.repairIngredient.get();
    }
}
