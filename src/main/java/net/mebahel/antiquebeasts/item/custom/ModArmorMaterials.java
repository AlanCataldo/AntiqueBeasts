package net.mebahel.antiquebeasts.item.custom;

import net.mebahel.antiquebeasts.util.config.ModArmorValueConfig;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Lazy;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;

import java.util.EnumMap;
import java.util.function.Supplier;

public enum ModArmorMaterials implements StringIdentifiable, ArmorMaterial {
    HIGH_IRON("diamond", 32, (EnumMap)Util.make(new EnumMap(ArmorItem.Type.class), (map) -> {
        map.put(ArmorItem.Type.BOOTS, ModArmorValueConfig.valkyrieArmor[0]);
        map.put(ArmorItem.Type.LEGGINGS, ModArmorValueConfig.valkyrieArmor[1]);
        map.put(ArmorItem.Type.CHESTPLATE, ModArmorValueConfig.valkyrieArmor[2]);
        map.put(ArmorItem.Type.HELMET, ModArmorValueConfig.valkyrieArmor[3]);
    }), 24, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 2.0F, 0.2F, () -> {
        return Ingredient.ofItems(new ItemConvertible[]{ModItems.HIGH_IRON_INGOT});
    }),
    IRON_PLATE("iron", 17, (EnumMap)Util.make(new EnumMap(ArmorItem.Type.class), (map) -> {
        map.put(ArmorItem.Type.BOOTS, ModArmorValueConfig.ironPlateArmor[0]);
        map.put(ArmorItem.Type.LEGGINGS, ModArmorValueConfig.ironPlateArmor[1]);
        map.put(ArmorItem.Type.CHESTPLATE, ModArmorValueConfig.ironPlateArmor[2]);
        map.put(ArmorItem.Type.HELMET, ModArmorValueConfig.ironPlateArmor[3]);
    }), 15, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0F, 0.0F, () -> {
        return Ingredient.ofItems(new ItemConvertible[]{ModItems.IRON_PLATE});
    }),
    GOLD_PLATE("gold", 20, (EnumMap)Util.make(new EnumMap(ArmorItem.Type.class), (map) -> {
        map.put(ArmorItem.Type.BOOTS, ModArmorValueConfig.goldPlateArmor[0]);
        map.put(ArmorItem.Type.LEGGINGS, ModArmorValueConfig.goldPlateArmor[1]);
        map.put(ArmorItem.Type.CHESTPLATE, ModArmorValueConfig.goldPlateArmor[2]);
        map.put(ArmorItem.Type.HELMET, ModArmorValueConfig.goldPlateArmor[3]);
    }), 12, SoundEvents.ITEM_ARMOR_EQUIP_GOLD, 0.0F, 0.0F, () -> {
        return Ingredient.ofItems(new ItemConvertible[]{ModItems.GOLD_PLATE});
    }),
    DIAMOND_PLATE("diamond", 38, (EnumMap)Util.make(new EnumMap(ArmorItem.Type.class), (map) -> {
        map.put(ArmorItem.Type.BOOTS, ModArmorValueConfig.diamondPlateArmor[0]);
        map.put(ArmorItem.Type.LEGGINGS, ModArmorValueConfig.diamondPlateArmor[1]);
        map.put(ArmorItem.Type.CHESTPLATE, ModArmorValueConfig.diamondPlateArmor[2]);
        map.put(ArmorItem.Type.HELMET, ModArmorValueConfig.diamondPlateArmor[3]);
    }), 18, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 2.0F, 0.1F, () -> {
        return Ingredient.ofItems(new ItemConvertible[]{ModItems.DIAMOND_PLATE});
    }),

    NETHERITE_PLATE("netherite", 41, (EnumMap)Util.make(new EnumMap(ArmorItem.Type.class), (map) -> {
        map.put(ArmorItem.Type.BOOTS, ModArmorValueConfig.netheritePlateArmor[0]);
        map.put(ArmorItem.Type.LEGGINGS, ModArmorValueConfig.netheritePlateArmor[1]);
        map.put(ArmorItem.Type.CHESTPLATE, ModArmorValueConfig.netheritePlateArmor[2]);
        map.put(ArmorItem.Type.HELMET, ModArmorValueConfig.netheritePlateArmor[3]);
    }), 22, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 3.0F, 0.2F, () -> {
        return Ingredient.ofItems(new ItemConvertible[]{Items.NETHERITE_INGOT});
    }),

    IRON_SCALE("iron", 17, (EnumMap)Util.make(new EnumMap(ArmorItem.Type.class), (map) -> {
        map.put(ArmorItem.Type.BOOTS, ModArmorValueConfig.ironScaleArmor[0]);
        map.put(ArmorItem.Type.LEGGINGS, ModArmorValueConfig.ironScaleArmor[1]);
        map.put(ArmorItem.Type.CHESTPLATE, ModArmorValueConfig.ironScaleArmor[2]);
        map.put(ArmorItem.Type.HELMET, ModArmorValueConfig.ironScaleArmor[3]);
    }), 15, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0F, 0.0F, () -> {
        return Ingredient.ofItems(new ItemConvertible[]{ModItems.IRON_SCARAB});
    }),
    GOLD_SCALE("gold", 20, (EnumMap)Util.make(new EnumMap(ArmorItem.Type.class), (map) -> {
        map.put(ArmorItem.Type.BOOTS, ModArmorValueConfig.goldScaleArmor[0]);
        map.put(ArmorItem.Type.LEGGINGS, ModArmorValueConfig.goldScaleArmor[1]);
        map.put(ArmorItem.Type.CHESTPLATE, ModArmorValueConfig.goldScaleArmor[2]);
        map.put(ArmorItem.Type.HELMET, ModArmorValueConfig.goldScaleArmor[3]);
    }), 12, SoundEvents.ITEM_ARMOR_EQUIP_GOLD, 0.0F, 0.0F, () -> {
        return Ingredient.ofItems(new ItemConvertible[]{ModItems.GOLD_SCARAB});
    }),

    DIAMOND_SCALE("diamond", 38, (EnumMap)Util.make(new EnumMap(ArmorItem.Type.class), (map) -> {
        map.put(ArmorItem.Type.BOOTS, ModArmorValueConfig.diamondScaleArmor[0]);
        map.put(ArmorItem.Type.LEGGINGS, ModArmorValueConfig.diamondScaleArmor[1]);
        map.put(ArmorItem.Type.CHESTPLATE, ModArmorValueConfig.diamondScaleArmor[2]);
        map.put(ArmorItem.Type.HELMET, ModArmorValueConfig.diamondScaleArmor[3]);
    }), 18, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 2.0F, 0.1F, () -> {
        return Ingredient.ofItems(new ItemConvertible[]{ModItems.DIAMOND_PLATE});
    });

    public static final StringIdentifiable.Codec<net.minecraft.item.ArmorMaterials> CODEC = StringIdentifiable.createCodec(net.minecraft.item.ArmorMaterials::values);
    private static final EnumMap<ArmorItem.Type, Integer> BASE_DURABILITY = (EnumMap)Util.make(new EnumMap(ArmorItem.Type.class), (map) -> {
        map.put(ArmorItem.Type.BOOTS, 13);
        map.put(ArmorItem.Type.LEGGINGS, 15);
        map.put(ArmorItem.Type.CHESTPLATE, 16);
        map.put(ArmorItem.Type.HELMET, 11);
    });
    private final String name;
    private final int durabilityMultiplier;
    private final EnumMap<ArmorItem.Type, Integer> protectionAmounts;
    private final int enchantability;
    private final SoundEvent equipSound;
    private final float toughness;
    private final float knockbackResistance;
    private final Lazy<Ingredient> repairIngredientSupplier;

    private ModArmorMaterials(String name, int durabilityMultiplier, EnumMap protectionAmounts, int enchantability, SoundEvent equipSound, float toughness, float knockbackResistance, Supplier repairIngredientSupplier) {
        this.name = name;
        this.durabilityMultiplier = durabilityMultiplier;
        this.protectionAmounts = protectionAmounts;
        this.enchantability = enchantability;
        this.equipSound = equipSound;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.repairIngredientSupplier = new Lazy(repairIngredientSupplier);
    }

    public int getDurability(ArmorItem.Type type) {
        return (Integer)BASE_DURABILITY.get(type) * this.durabilityMultiplier;
    }

    public int getProtection(ArmorItem.Type type) {
        return (Integer)this.protectionAmounts.get(type);
    }

    public int getEnchantability() {
        return this.enchantability;
    }

    public SoundEvent getEquipSound() {
        return this.equipSound;
    }

    public Ingredient getRepairIngredient() {
        return (Ingredient)this.repairIngredientSupplier.get();
    }

    public String getName() {
        return this.name;
    }

    public float getToughness() {
        return this.toughness;
    }

    public float getKnockbackResistance() {
        return this.knockbackResistance;
    }

    public String asString() {
        return this.name;
    }
}
