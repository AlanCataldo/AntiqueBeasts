package net.mebahel.antiquebeasts.item.custom;

import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import java.util.function.Supplier;

public enum ModToolMaterial implements ToolMaterial {
    FROST_WEAPON(4, 1550, 4.0F, 0.5F, 17, () -> {
        return Ingredient.ofItems(Items.DIAMOND);
    }),
    HERSIR_AXE(4, 680, 6.5F, 0.5F, 19, () -> {
        return Ingredient.ofItems(ModItems.HIGH_IRON_INGOT);
    }),

    IRON_KHOPESH(4, 680, 7F, 1F, 15, () -> {
        return Ingredient.ofItems(ModItems.HIGH_IRON_INGOT);
    }),
    GOLD_KHOPESH(4, 480, 7F, 0.5F, 19, () -> {
        return Ingredient.ofItems(ModItems.HIGH_IRON_INGOT);
    }),
    DIAMOND_KHOPESH(4, 1280, 7F, 1F, 17, () -> {
        return Ingredient.ofItems(ModItems.HIGH_IRON_INGOT);
    }),
    NETHERITE_KHOPESH(4, 1580, 7F, 1F, 20, () -> {
        return Ingredient.ofItems(ModItems.HIGH_IRON_INGOT);
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
