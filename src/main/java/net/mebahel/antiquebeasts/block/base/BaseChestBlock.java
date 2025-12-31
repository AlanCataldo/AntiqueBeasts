package net.mebahel.antiquebeasts.block.base;

import net.mebahel.antiquebeasts.block.entity.base.BaseChestBlockEntity;
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
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class BaseChestBlock extends ChestBlock {

    private final Supplier<BlockEntityType<? extends BaseChestBlockEntity>> beType;
    private final BiFunction<BlockPos, BlockState, ? extends BaseChestBlockEntity> factory;

    public BaseChestBlock(Settings settings,
                              Supplier<BlockEntityType<? extends BaseChestBlockEntity>> beType,
                              BiFunction<BlockPos, BlockState, ? extends BaseChestBlockEntity> factory) {
        // upcast implicite vers ? extends ChestBlockEntity
        super(settings, () -> beType.get());
        this.beType = beType;
        this.factory = factory;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        // modèle géré par Tile/Geo (comme tes coffres actuels)
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

    // ---------- BlockEntity ----------

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return factory.apply(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world,
                                                                  BlockState state,
                                                                  BlockEntityType<T> type) {
        if (type != beType.get()) return null;
        if (world.isClient) {
            return (w, p, s, be) -> ((BaseChestBlockEntity) be).clientTick();
        }
        return (w, p, s, be) -> ((BaseChestBlockEntity) be).serverTick();
    }

    @Override
    @Nullable
    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state,
                                                                World world,
                                                                BlockPos pos) {
        // on gère l’UI nous-mêmes dans onUse()
        return null;
    }

    // ---------- Interaction ----------

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos,
                              PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }

        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof BaseChestBlockEntity chestEntity) {
            // 1) prépare / génère le loot perso
            chestEntity.handlePlayerLoot(player);

            // 2) ouvre l’UI (shared ou perso)
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

    // ---------- Drop à la casse ----------

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof BaseChestBlockEntity chestEntity)) {
            super.onBreak(world, pos, state, player);
            return;
        }

        DefaultedList<ItemStack> itemsToDrop = DefaultedList.ofSize(chestEntity.size(), ItemStack.EMPTY);

        try {
            if (chestEntity.isSharedChest()) {
                // Coffre placé par joueur : inventaire commun
                itemsToDrop = chestEntity.getInternalInventory();

            } else if (player != null && chestEntity.personalLoots.containsKey(player.getUuid())) {
                // Coffre à loot perso cassé par le joueur : son inventaire à lui
                itemsToDrop = chestEntity.personalLoots.get(player.getUuid());

            } else if (!chestEntity.personalLoots.isEmpty()) {
                // Cassé par mob, explosion... → loot d’un joueur aléatoire
                List<DefaultedList<ItemStack>> allLoots = new ArrayList<>(chestEntity.personalLoots.values());
                itemsToDrop = allLoots.get(world.random.nextInt(allLoots.size()));

            } else if (chestEntity.hasLootTable() && player != null) {
                // ✅ Coffre structurel jamais ouvert → on génère le loot perso POUR CE JOUEUR (robuste, pas de LootManager)
                chestEntity.handlePlayerLoot(player);

                DefaultedList<ItemStack> pLoot = chestEntity.personalLoots.get(player.getUuid());
                if (pLoot != null) {
                    itemsToDrop = pLoot;
                } else {
                    // fallback safe : inventaire interne (souvent vide)
                    itemsToDrop = chestEntity.getInternalInventory();
                }

            } else {
                itemsToDrop = chestEntity.getInternalInventory();
            }

        } catch (Exception e) {
            System.err.println("[LootChest] ⚠️ Error choosing or generating drop inventory: " + e.getMessage());
            e.printStackTrace();
            itemsToDrop = DefaultedList.ofSize(chestEntity.size(), ItemStack.EMPTY);
        }

        // Drop des items
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
