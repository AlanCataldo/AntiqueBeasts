package net.mebahel.antiquebeasts.util;

import net.mebahel.antiquebeasts.block.custom.MummyBossAltarBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class WaterRemovalScheduler {
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static final int MAX_TASKS = 120; // 🚀 Limite de tâches pour éviter une surcharge
    private static final int MAX_TASKS_PER_TICK = 10; // ✅ Nombre max de tâches par tick

    private final Queue<ScheduledTask> tasks = new ConcurrentLinkedQueue<>();
    private final Set<Integer> processedStructures = new HashSet<>(); // 🔥 Empêche les duplications
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
        processedStructures.clear(); // 🔄 Nettoie aussi les structures traitées
    }

    public void tick() {
        int processedCount = 0;
        Iterator<ScheduledTask> iterator = tasks.iterator();

        while (iterator.hasNext() && processedCount < MAX_TASKS_PER_TICK) {
            ScheduledTask task = iterator.next();

            if (task.isDue() && !processedStructures.contains(task.getHash())) {
                task.run();
                processedStructures.add(task.getHash());
                iterator.remove();
                processedCount++;
            }
        }

        if (processedStructures.size() > MAX_TASKS) {
            processedStructures.clear();
            tasks.clear();
        }
        if (tasks.size() > 1000) {
            System.out.println("Forcing cleanup of WaterRemovalScheduler - Too many tasks!");
            tasks.clear();
            processedStructures.clear();
        }
        System.out.println("WaterRemovalScheduler - Tasks in queue: " + tasks.size() + ", Processed structures: " + processedStructures.size());
    }

    public void schedule(BlockPos startPos, BlockPos endPos, int delayTicks) {
        int hash = Objects.hash(startPos, endPos);

        if (tasks.size() < MAX_TASKS && !processedStructures.contains(hash)) {
            tasks.add(new ScheduledTask(worlds, new Box(startPos, endPos), delayTicks, hash));
        }
    }

    private static class ScheduledTask {
        private final List<ServerWorld> worlds;
        private final Box structureBox;
        private final int delayTicks;
        private final int hash;
        private int currentTick;

        public ScheduledTask(List<ServerWorld> worlds, Box structureBox, int delayTicks, int hash) {
            this.worlds = worlds;
            this.structureBox = structureBox;
            this.delayTicks = delayTicks;
            this.hash = hash;
        }

        public int getHash() {
            return hash;
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
                        world.setBlockState(pos, state.with(Properties.WATERLOGGED, false), 2);
                        for (Direction direction : Direction.values()) {
                            BlockPos neighborPos = pos.offset(direction);
                            BlockState neighborState = world.getBlockState(neighborPos);
                            if (neighborState.isOf(Blocks.WATER)) {
                                world.setBlockState(neighborPos, Blocks.AIR.getDefaultState(), 2);
                            }
                        }
                    }
                });
            }
        }
    }
}
