package net.mebahel.antiquebeasts.item.custom;

import mod.azure.azurelib.rewrite.animation.cache.AzIdentityRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.block.ModBlocks;
import net.mebahel.antiquebeasts.entity.ModEntities;
import net.mebahel.antiquebeasts.item.*;
import net.mebahel.antiquebeasts.item.staff.FrostBiteStaff;
import net.mebahel.antiquebeasts.item.staff.IceSpikeStaff;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

import static net.minecraft.registry.tag.InstrumentTags.SCREAMING_GOAT_HORNS;

public class ModArmors {
    public static void registerModArmors() {
        AzIdentityRegistry.register(ModItems.GOLD_SCALE_HELMET,
                ModItems.GOLD_SCALE_CHESTPLATE,
                ModItems.GOLD_SCALE_LEGGINGS,
                ModItems.GOLD_SCALE_BOOTS);

        AzIdentityRegistry.register(ModItems.DIAMOND_SCALE_HELMET,
                ModItems.DIAMOND_SCALE_CHESTPLATE,
                ModItems.DIAMOND_SCALE_LEGGINGS,
                ModItems.DIAMOND_SCALE_BOOTS);

        AzIdentityRegistry.register(ModItems.IRON_SCALE_HELMET,
                ModItems.IRON_SCALE_CHESTPLATE,
                ModItems.IRON_SCALE_LEGGINGS,
                ModItems.IRON_SCALE_BOOTS);

        AzIdentityRegistry.register(ModItems.IRON_PLATE_HELMET,
                ModItems.IRON_PLATE_CHESTPLATE,
                ModItems.IRON_PLATE_LEGGINGS,
                ModItems.IRON_PLATE_BOOTS);

        AzIdentityRegistry.register(ModItems.GOLD_PLATE_HELMET,
                ModItems.GOLD_PLATE_CHESTPLATE,
                ModItems.GOLD_PLATE_LEGGINGS,
                ModItems.GOLD_PLATE_BOOTS);

        AzIdentityRegistry.register(ModItems.DIAMOND_PLATE_HELMET,
                ModItems.DIAMOND_PLATE_CHESTPLATE,
                ModItems.DIAMOND_PLATE_LEGGINGS,
                ModItems.DIAMOND_PLATE_BOOTS);

        AzIdentityRegistry.register(ModItems.NETHERITE_PLATE_HELMET,
                ModItems.NETHERITE_PLATE_CHESTPLATE,
                ModItems.NETHERITE_PLATE_LEGGINGS,
                ModItems.NETHERITE_PLATE_BOOTS);

        AzIdentityRegistry.register(ModItems.VALKYRIE_HELMET,
                ModItems.VALKYRIE_CHESTPLATE,
                ModItems.VALKYRIE_LEGGINGS,
                ModItems.VALKYRIE_BOOTS);

        AntiqueBeasts.LOGGER.info("[AntiqueBeasts] Registering items for " + AntiqueBeasts.MOD_ID + ".");
    }
}

