package net.mebahel.antiquebeasts.block.custom;

import net.mebahel.antiquebeasts.block.ModBlockEntities;
import net.mebahel.antiquebeasts.block.entity.DraugrChestBlockEntity;
import net.mebahel.antiquebeasts.util.packet.ChestOpenSync;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DraugrChestBlock extends ChestBlock {
    public DraugrChestBlock(Settings settings) {
        super(settings, () -> ModBlockEntities.DRAUGR_CHEST_ENTITY);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DraugrChestBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction direction = ctx.getHorizontalPlayerFacing().getOpposite();
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        return this.getDefaultState()
                .with(FACING, direction)
                .with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (type != ModBlockEntities.DRAUGR_CHEST_ENTITY) return null;
        if (world.isClient) return (w, p, s, be) -> ((DraugrChestBlockEntity) be).clientTick();
        return (w, p, s, be) -> ((DraugrChestBlockEntity) be).serverTick();
    }

    @Override
    @Nullable
    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        return null;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos,
                              PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }

        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof DraugrChestBlockEntity chestEntity) {
            // 1) prépare / génère le loot perso
            chestEntity.handlePlayerLoot(player);

            // 2) ouvre l’UI
            player.openHandledScreen(chestEntity.createPersonalScreenHandlerFactory(player));

            // ⚠️ TRÈS IMPORTANT : n’appeler onOpen(player) QUE pour les coffres à loot perso
            if (!chestEntity.isSharedChest()) {
                chestEntity.onOpen(player);
            }

            // stats + piglin
            player.incrementStat(this.getOpenStat());
            PiglinBrain.onGuardedBlockInteracted(player, true);

            return ActionResult.CONSUME;
        }

        return ActionResult.FAIL;
    }


    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof DraugrChestBlockEntity chestEntity)) {
            super.onBreak(world, pos, state, player);
            return;
        }

        DefaultedList<ItemStack> itemsToDrop = DefaultedList.ofSize(chestEntity.size(), ItemStack.EMPTY);

        try {
            if (chestEntity.isSharedChest()) {
                // 🟩 Coffre placé par joueur : inventaire commun
                itemsToDrop = chestEntity.getInternalInventory();

            } else if (player != null && chestEntity.personalLoots.containsKey(player.getUuid())) {
                // 🟨 Coffre à loot perso cassé par le joueur : son inventaire à lui
                itemsToDrop = chestEntity.personalLoots.get(player.getUuid());

            } else if (!chestEntity.personalLoots.isEmpty()) {
                // 🟦 Cassé par mob, explosion... → loot d’un joueur aléatoire
                List<DefaultedList<ItemStack>> allLoots = new ArrayList<>(chestEntity.personalLoots.values());
                itemsToDrop = allLoots.get(world.random.nextInt(allLoots.size()));

            } else if (chestEntity.hasLootTable()) {
                // 🟥 Coffre structurel jamais ouvert → on génère un loot temporaire
                if (world instanceof ServerWorld serverWorld) {
                    LootTable table = serverWorld.getServer().getLootManager().getLootTable(chestEntity.getBaseLootTableId());
                    LootContextParameterSet lootContext = new LootContextParameterSet.Builder(serverWorld)
                            .add(LootContextParameters.ORIGIN, Vec3d.ofCenter(pos))
                            .build(LootContextTypes.CHEST);

                    net.minecraft.inventory.SimpleInventory tempInv = new net.minecraft.inventory.SimpleInventory(chestEntity.size());
                    table.supplyInventory(tempInv, lootContext, world.random.nextLong());

                    for (int i = 0; i < chestEntity.size(); i++) {
                        itemsToDrop.set(i, tempInv.getStack(i).copy());
                    }
                }

            } else {
                // 🟫 Par défaut : inventaire vide
                itemsToDrop = chestEntity.getInternalInventory();
            }

        } catch (Exception e) {
            System.err.println("[DraugrChest] ⚠️ Error choosing or generating drop inventory: " + e.getMessage());
            e.printStackTrace();
            itemsToDrop = DefaultedList.ofSize(chestEntity.size(), ItemStack.EMPTY);
        }

        // ✅ Drop des items dans le monde
        if (itemsToDrop != null) {
            for (ItemStack stack : itemsToDrop) {
                if (!stack.isEmpty()) {
                    dropStack(world, pos, stack);
                }
            }
        }

        super.onBreak(world, pos, state, player);
    }

}