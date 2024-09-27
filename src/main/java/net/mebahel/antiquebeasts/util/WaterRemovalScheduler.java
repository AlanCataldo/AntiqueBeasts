package net.mebahel.antiquebeasts.util;

import net.mebahel.antiquebeasts.block.MummyBossAltarBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class WaterRemovalScheduler {
    private final List<ScheduledTask> tasks = new CopyOnWriteArrayList<>();
    public final List<ServerWorld> worlds = new CopyOnWriteArrayList<>();

    public void addWorld(ServerWorld world) {
        if (world.getRegistryKey().equals(World.OVERWORLD)) {
            worlds.clear();
            worlds.add(world);
        }
    }

    public void removeWorld(ServerWorld world) {
        worlds.remove(world);
        tasks.clear();
    }

    public void tick() {
        for (ScheduledTask task : tasks) {
            if (task.isDue()) {
                task.run();
                tasks.remove(task);
            }
        }
    }

    public void schedule(BlockPos startPos, BlockPos endPos, int delayTicks) {
        tasks.add(new ScheduledTask(worlds, new Box(startPos, endPos), delayTicks));
    }

    private static class ScheduledTask {
        private final List<ServerWorld> worlds;
        private final Box structureBox;
        private final int delayTicks;
        private int currentTick;

        public ScheduledTask(List<ServerWorld> worlds, Box structureBox, int delayTicks) {
            this.worlds = worlds;
            this.structureBox = structureBox;
            this.delayTicks = delayTicks;
        }
        public boolean isDue() {
            return currentTick++ >= delayTicks;
        }

        public void run() {
            for (ServerWorld world : worlds) {
                BlockPos.stream(structureBox).forEach(pos -> {

                    BlockState state = world.getBlockState(pos);
                    if (state.getBlock() instanceof MummyBossAltarBlock) {
                        ((MummyBossAltarBlock) state.getBlock()).scheduleNextEffectTick(world, pos);
                    }
                    if (state.contains(Properties.WATERLOGGED) && state.get(Properties.WATERLOGGED)) {
                        world.setBlockState(pos, state.with(Properties.WATERLOGGED, false), 3);
                        for (Direction direction : Direction.values()) {
                            BlockPos neighborPos = pos.offset(direction);
                            BlockState neighborState = world.getBlockState(neighborPos);
                            if (neighborState.isOf(Blocks.WATER)) {
                                world.setBlockState(neighborPos, Blocks.AIR.getDefaultState(), 3);
                            }
                        }
                    }
                });
            }
        }
    }
}
