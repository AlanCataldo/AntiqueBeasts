package net.mebahel.antiquebeasts.block.custom;

import net.mebahel.antiquebeasts.block.entity.DwemerSpiderBlockEntity;
import net.mebahel.antiquebeasts.entity.ModEntities;
import net.mebahel.antiquebeasts.entity.custom.dwemer.DwemerSpiderEntity;
import net.mebahel.antiquebeasts.item.custom.ModItems;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DwemerSpiderBlock extends FacingBlock implements BlockEntityProvider  {
    public static final IntProperty STATE = IntProperty.of("state", 0, 2);
    public static final DirectionProperty FACING = Properties.FACING;
    public DwemerSpiderBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
        this.setDefaultState(this.stateManager.getDefaultState().with(STATE, 0));
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, STATE);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos,
                              PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            world.playSound(player, pos.getX(), pos.getY(), pos.getZ(),
                    SoundEvents.BLOCK_COPPER_BREAK, SoundCategory.BLOCKS, 0.5f, 1f);
            return ActionResult.SUCCESS;
        }

        int currentState = state.get(STATE);
        Random random = world.getRandom();

        if (currentState == 0 && random.nextInt(3) == 0) {
            // 1/2 chance de se casser et de spawn un DWEMER_SPIDER
            world.breakBlock(pos, false);

            DwemerSpiderEntity spider = ModEntities.DWEMER_SPIDER.create(world);
            if (spider != null) {
                Direction facing = state.get(FACING);
                float yaw = switch (facing) {
                    case NORTH -> 180f;
                    case SOUTH -> 0f;
                    case WEST  -> 90f;
                    case EAST  -> -90f;
                    default    -> 0f;
                };

                // Position centrale du bloc
                double x = pos.getX() + 0.5;
                double y = pos.getY();
                double z = pos.getZ() + 0.5;

                // Position + orientation
                spider.refreshPositionAndAngles(x, y, z, yaw, 0.0f);
                spider.setYaw(yaw);
                spider.setHeadYaw(yaw);
                spider.setBodyYaw(yaw);
                spider.prevYaw = yaw;
                spider.prevBodyYaw = yaw;
                spider.prevHeadYaw = yaw;

                world.spawnEntity(spider);
            }

            return ActionResult.CONSUME;
        }

        // Si ce n'était pas cassé à l'état 0, on drop quelque chose
        int roll = random.nextInt(9);
        ItemStack drop = switch (roll) {
            case 0 -> new ItemStack(Items.RAW_IRON);
            case 1 -> new ItemStack(Items.RAW_GOLD);
            case 2 -> new ItemStack(Items.DIAMOND);
            case 3 -> new ItemStack(Items.EMERALD);
            default -> new ItemStack(ModItems.DWEMER_METAL_SCRAP);
        };

        dropStack(world, pos, drop);

        if (currentState < 2) {
            world.setBlockState(pos, state.with(STATE, currentState + 1), 3);
        } else {
            world.breakBlock(pos, false);
        }

        return ActionResult.CONSUME;
    }



    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DwemerSpiderBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction direction = ctx.getHorizontalPlayerFacing().getOpposite();
        return this.getDefaultState().with(FACING, direction);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return rotate(state, mirror.getRotation(state.get(FACING)));
    }
}
