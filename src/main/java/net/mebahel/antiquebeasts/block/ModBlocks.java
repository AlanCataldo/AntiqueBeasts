package net.mebahel.antiquebeasts.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.Instrument;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static final Block AMPHORA = registerBlock("amphora",
            new Block(FabricBlockSettings.copyOf(Blocks.DECORATED_POT).sounds(BlockSoundGroup.DECORATED_POT)));
    public static final Block CURSED_GOLDEN_BLOCK = registerBlock("cursed_gold_block",
            new CursedGoldBlock(FabricBlockSettings.copyOf(Blocks.GOLD_BLOCK)));
    public static final Block MUMMY_BOSS_ALTAR = registerBlock("mummy_boss_altar",
            new MummyBossAltarBlock(FabricBlockSettings.copyOf(Blocks.GOLD_BLOCK)));
    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, new Identifier(AntiqueBeasts.MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, new Identifier(AntiqueBeasts.MOD_ID, name), new BlockItem(block, new FabricItemSettings()));
    }

    public static void registerModBlocks() {
        AntiqueBeasts.LOGGER.info("[AntiqueBeasts] Registering blocks for " + AntiqueBeasts.MOD_ID + ".");
    }
}
