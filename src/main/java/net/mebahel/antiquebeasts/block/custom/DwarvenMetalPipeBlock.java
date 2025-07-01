package net.mebahel.antiquebeasts.block.custom;

import net.mebahel.antiquebeasts.block.entity.DwarvenMetalPipeGearBlockEntity;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class DwarvenMetalPipeBlock extends Block {
    public static final BooleanProperty CONNECTED_SIDE = BooleanProperty.of("connected_side");
    public static final DirectionProperty FACING = Properties.FACING;
    public static final BooleanProperty FRONT_CONNECTED = BooleanProperty.of("front_connected");
    public static final BooleanProperty BACK_CONNECTED = BooleanProperty.of("back_connected");
    public static final BooleanProperty SMOOTH = BooleanProperty.of("smooth");

    public DwarvenMetalPipeBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(FRONT_CONNECTED, false)
                .with(BACK_CONNECTED, false)
                .with(SMOOTH, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, FRONT_CONNECTED, BACK_CONNECTED, SMOOTH, CONNECTED_SIDE);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction dir = ctx.getSide();
        BlockPos pos = ctx.getBlockPos();
        WorldAccess world = ctx.getWorld();
        return this.getDefaultState()
                .with(FACING, dir)
                .with(FRONT_CONNECTED, canConnectFront(world, pos, dir))
                .with(BACK_CONNECTED, canConnectBack(world, pos, dir))
                .with(SMOOTH, isSmooth(world, pos, dir))
                .with(CONNECTED_SIDE, hasSideConnection(world, pos, dir));
    }
    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = pos.offset(dir);
            BlockState neighborState = world.getBlockState(neighborPos);

            if (neighborState.getBlock() instanceof DwarvenMetalPipeBlock) {
                BlockState updated = neighborState
                        .with(FRONT_CONNECTED, canConnectFront(world, neighborPos, neighborState.get(FACING)))
                        .with(BACK_CONNECTED, canConnectBack(world, neighborPos, neighborState.get(FACING)))
                        .with(SMOOTH, isSmooth(world, neighborPos, neighborState.get(FACING)))
                        .with(CONNECTED_SIDE, hasSideConnection(world, neighborPos, neighborState.get(FACING)));

                world.setBlockState(neighborPos, updated);
            }
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction dir, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        Direction facing = state.get(FACING);
        BlockState newState = state
                .with(FRONT_CONNECTED, canConnectFront(world, pos, facing))
                .with(BACK_CONNECTED, canConnectBack(world, pos, facing))
                .with(SMOOTH, isSmooth(world, pos, facing))
                .with(CONNECTED_SIDE, hasSideConnection(world, pos, facing));

        // Force refresh du blockstate du bloc modifié
        if (!world.isClient()) {
            world.scheduleBlockTick(pos, this, 1);
        }

        return newState;
    }


    private boolean canConnectFront(BlockView world, BlockPos pos, Direction dir) {
        BlockState target = world.getBlockState(pos.offset(dir));
        return (target.getBlock() instanceof DwarvenMetalPipeBlock || target.getBlock() instanceof DwarvenMetalPipeGearBlock)
                && target.get(FACING) != dir && target.get(FACING) != dir.getOpposite();
    }

    private boolean canConnectBack(BlockView world, BlockPos pos, Direction dir) {
        BlockState target = world.getBlockState(pos.offset(dir.getOpposite()));
        return (target.getBlock() instanceof DwarvenMetalPipeBlock || target.getBlock() instanceof DwarvenMetalPipeGearBlock)
                && target.get(FACING) != dir && target.get(FACING) != dir.getOpposite();
    }

    private boolean isSmooth(BlockView world, BlockPos pos, Direction dir) {
        BlockState target = world.getBlockState(pos.offset(dir));
        return (target.getBlock() instanceof DwarvenMetalPipeBlock || target.getBlock() instanceof DwarvenMetalPipeGearBlock)
                && target.get(FACING) == dir &&
                !canConnectFront(world, pos, dir);
    }
    private boolean hasSideConnection(WorldAccess world, BlockPos pos, Direction facing) {
        for (Direction dir : Direction.values()) {
            if (dir.getAxis() == facing.getAxis()) continue; // uniquement les directions perpendiculaires

            BlockPos neighborPos = pos.offset(dir);
            BlockState neighborState = world.getBlockState(neighborPos);

            if (neighborState.getBlock() instanceof DwarvenMetalPipeBlock ||
                    neighborState.getBlock() instanceof DwarvenMetalPipeGearBlock) {
                Direction neighborFacing = neighborState.get(FACING);

                // Si les deux tuyaux ne sont pas parallèles, alors ils forment une intersection
                if (neighborFacing.getAxis() != facing.getAxis()) {
                    return true;
                }
            }
        }
        return false;
    }
    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return rotate(state, mirror.getRotation(state.get(FACING)));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction facing = state.get(FACING);
        boolean front = state.get(FRONT_CONNECTED);
        boolean back = state.get(BACK_CONNECTED);
        boolean smooth = state.get(SMOOTH);
        boolean connected_side = state.get(CONNECTED_SIDE);
        if (connected_side && !front && !back && !smooth) {
            return switch (facing) {
                case DOWN -> DOWN_BACK_SMOOTH_SIDE;
                case UP -> UP_BACK_SMOOTH_SIDE;
                case NORTH -> NORTH_BACK_SMOOTH_SIDE;
                case SOUTH -> SOUTH_BACK_SMOOTH_SIDE;
                case EAST -> EAST_BACK_SMOOTH_SIDE;
                case WEST -> WEST_BACK_SMOOTH_SIDE;
            };
        }


        if (connected_side && back && !front && !smooth) {
            return switch (facing) {
                case DOWN -> DOWN_BACK_SMOOTH_TOP;
                case UP -> UP_BACK_SMOOTH_TOP;
                case NORTH -> NORTH_BACK_SMOOTH_TOP;
                case SOUTH -> SOUTH_BACK_SMOOTH_TOP;
                case EAST -> EAST_BACK_SMOOTH_TOP;
                case WEST -> WEST_BACK_SMOOTH_TOP;
            };
        }

        if (smooth && back) {
            return switch (facing) {
                case DOWN -> DOWN_BACK_SMOOTH;
                case UP -> UP_BACK_SMOOTH;
                case NORTH -> NORTH_BACK_SMOOTH;
                case SOUTH -> SOUTH_BACK_SMOOTH;
                case EAST -> EAST_BACK_SMOOTH;
                case WEST -> WEST_BACK_SMOOTH;
            };
        } else if (smooth) {
            return switch (facing) {
                case DOWN, UP -> DOWN_SMOOTH;
                case NORTH, SOUTH -> NORTH_SMOOTH;
                case EAST, WEST -> EAST_SMOOTH;
            };
        } else if (front && back) {
            return switch (facing) {
                case DOWN, UP -> DOWN_DOUBLE;
                case NORTH, SOUTH -> NORTH_DOUBLE;
                case EAST, WEST -> EAST_DOUBLE;
            };
        }
        else if (front) {
            return switch (facing) {
                case DOWN -> DOWN_FRONT;
                case UP -> UP_FRONT;
                case NORTH -> NORTH_FRONT;
                case SOUTH -> SOUTH_FRONT;
                case EAST -> EAST_FRONT;
                case WEST -> WEST_FRONT;
            };
        } else if (back) {
            return switch (facing) {
                case DOWN -> DOWN_BACK;
                case UP -> UP_BACK;
                case NORTH -> NORTH_BACK;
                case SOUTH -> SOUTH_BACK;
                case EAST -> EAST_BACK;
                case WEST -> WEST_BACK;
            };
        } else {
            return switch (facing) {
                case DOWN -> DOWN_SHAPE;
                case UP -> UP_SHAPE;
                case NORTH -> NORTH_SHAPE;
                case SOUTH -> SOUTH_SHAPE;
                case EAST -> EAST_SHAPE;
                case WEST -> WEST_SHAPE;
            };
        }
    }
    private static final VoxelShape NORTH_SHAPE = VoxelShapes.cuboid(0.1250, 0.1250, 0.1875, 0.8750, 0.8750, 1.0000);
    private static final VoxelShape NORTH_SMOOTH = VoxelShapes.cuboid(0.1250, 0.1250, 0.0000, 0.8750, 0.8750, 1.0000);
    private static final VoxelShape NORTH_BACK_SMOOTH_SIDE = VoxelShapes.cuboid(0.1250, 0.1250, 0.1250, 0.8750, 0.8750, 1.0000);
    private static final VoxelShape NORTH_BACK = VoxelShapes.cuboid(0.1250, 0.1250, 0.0000, 0.8750, 0.8750, 1.1250);
    private static final VoxelShape NORTH_BACK_SMOOTH_TOP = VoxelShapes.cuboid(0.1250, 0.1250, 0.1250, 0.8750, 0.8750, 1.1250);
    private static final VoxelShape NORTH_BACK_SMOOTH = VoxelShapes.cuboid(0.1250, 0.1250, 0.0000, 0.8750, 0.8750, 1.1250);
    private static final VoxelShape NORTH_FRONT = VoxelShapes.cuboid(0.1250, 0.1250, -0.1250, 0.8750, 0.8750, 1.0000);

    private static final VoxelShape UP_BACK_SMOOTH_TOP = VoxelShapes.cuboid(0.1250, -0.1250, 0.1250, 0.8750, 0.8750, 0.8750);
    private static final VoxelShape UP_BACK_SMOOTH_SIDE = VoxelShapes.cuboid(0.1250, 0.0000, 0.1250, 0.8750, 0.8750, 0.8750);
    private static final VoxelShape UP_BACK = VoxelShapes.cuboid(0.1250, -0.1250, 0.1250, 0.8750, 0.8750, 0.8750);
    private static final VoxelShape UP_BACK_SMOOTH = VoxelShapes.cuboid(0.1250, -0.1250, 0.1250, 0.8750, 1.0000, 0.8750);
    private static final VoxelShape UP_SHAPE = VoxelShapes.cuboid(0.1250, 0, 0.1250, 0.8750, 0.8750, 0.8750);

    private static final VoxelShape DOWN_DOUBLE = VoxelShapes.cuboid(0.1250, -0.1250, 0.1250, 0.8750, 1.1250, 0.8750);

    private static final VoxelShape SOUTH_SHAPE = VoxelShapes.cuboid(0.1250, 0.1250, 0.0000, 0.8750, 0.8750, 0.8750);
    private static final VoxelShape SOUTH_BACK = VoxelShapes.cuboid(0.1250, 0.1250, -0.1250, 0.8750, 0.8750, 0.8750);
    private static final VoxelShape SOUTH_BACK_SMOOTH = VoxelShapes.cuboid(0.1250, 0.1250, -0.1250, 0.8750, 0.8750, 1);
    private static final VoxelShape SOUTH_BACK_SMOOTH_SIDE = VoxelShapes.cuboid(0.1250, 0.1250, 0.0000, 0.8750, 0.8750, 0.8750);
    private static final VoxelShape SOUTH_BACK_SMOOTH_TOP = VoxelShapes.cuboid(0.1250, 0.1250, -0.1250, 0.8750, 0.8750, 0.8750);

    private static final VoxelShape EAST_SHAPE = VoxelShapes.cuboid(0.0000, 0.1250, 0.1250, 0.8750, 0.8750, 0.8750);
    private static final VoxelShape EAST_BACK = VoxelShapes.cuboid(-0.1250, 0.1250, 0.1250, 0.8750, 0.8750, 0.8750);
    private static final VoxelShape EAST_BACK_SMOOTH = VoxelShapes.cuboid(-0.1250, 0.1250, 0.1250, 1.0000, 0.8750, 0.8750);
    private static final VoxelShape EAST_SMOOTH = VoxelShapes.cuboid(0.0000, 0.1250, 0.1250, 1.0000, 0.8750, 0.8750);
    private static final VoxelShape EAST_BACK_SMOOTH_SIDE = VoxelShapes.cuboid(0.0000, 0.1250, 0.1250, 0.8750, 0.8750, 0.8750);
    private static final VoxelShape EAST_DOUBLE = VoxelShapes.cuboid(-0.1250, 0.1250, 0.1250, 1.1250, 0.8750, 0.8750);
    private static final VoxelShape EAST_FRONT = VoxelShapes.cuboid(0.0000, 0.1250, 0.1250, 1.1250, 0.8750, 0.8750);

    private static final VoxelShape WEST_BACK_SMOOTH_SIDE = VoxelShapes.cuboid(0.1250, 0.1250, 0.1250, 1.0000, 0.8750, 0.8750);
    private static final VoxelShape WEST_BACK_SMOOTH = VoxelShapes.cuboid(0.0000, 0.1250, 0.1250, 1.1250, 0.8750, 0.8750);





    private static final VoxelShape DOWN_SHAPE = VoxelShapes.cuboid(0.1250, 0.0000, 0.1250, 0.8750, 1.0000, 0.8750);
    private static final VoxelShape DOWN_BACK = VoxelShapes.cuboid(0.1250, 0.0000, 0.1250, 0.8750, 1.1250, 0.8750);
    private static final VoxelShape DOWN_BACK_SMOOTH = VoxelShapes.cuboid(0.1250, 0.0000, 0.1250, 0.8750, 1.1250, 0.8750);
    private static final VoxelShape DOWN_BACK_SMOOTH_TOP = VoxelShapes.cuboid(0.1250, 0.0000, 0.1250, 0.8750, 0.8750, 0.8750);
    private static final VoxelShape DOWN_BACK_SMOOTH_SIDE = VoxelShapes.cuboid(0.1250, 0.1250, 0.1250, 0.8750, 1.0000, 0.8750);

    private static final VoxelShape WEST_SHAPE = VoxelShapes.cuboid(0.1250, 0.1250, 0.1250, 1.0000, 0.8750, 0.8750);
    private static final VoxelShape WEST_BACK = VoxelShapes.cuboid(0.1250, 0.1250, 0.1250, 1.1250, 0.8750, 0.8750);

    private static final VoxelShape UP_FRONT = VoxelShapes.cuboid(0.1250, 0.1250, 0.1250, 0.8750, 1.0000, 0.8750);
    private static final VoxelShape DOWN_FRONT = VoxelShapes.cuboid(0.1250, -0.1250, 0.1250, 0.8750, 0.8750, 0.8750);
    private static final VoxelShape SOUTH_FRONT = VoxelShapes.cuboid(0.1250, 0.1250, 0.1250, 0.8750, 0.8750, 1.1250);
    private static final VoxelShape WEST_FRONT = VoxelShapes.cuboid(-0.1250, 0.1250, 0.1250, 0.8750, 0.8750, 0.8750);


    private static final VoxelShape NORTH_DOUBLE = VoxelShapes.cuboid(0.1250, 0.1250, -0.1250, 0.8750, 0.8750, 1.1250);

    private static final VoxelShape WEST_BACK_SMOOTH_TOP = VoxelShapes.cuboid(0.1250, 0.1250, 0.1250, 1.1250, 0.8750, 0.8750);
    private static final VoxelShape DOWN_SMOOTH = VoxelShapes.cuboid(0.1250, 0.0000, 0.1250, 0.8750, 1.0000, 0.8750);
    private static final VoxelShape EAST_BACK_SMOOTH_TOP = VoxelShapes.cuboid(-0.1250, 0.1250, 0.1250, 0.8750, 0.8750, 0.8750);

}
