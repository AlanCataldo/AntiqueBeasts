package net.mebahel.antiquebeasts.item;

import net.minecraft.server.world.ServerWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TickScheduler {
    private final List<ScheduledTask> tasks = new ArrayList<>();

    public void schedule(ServerWorld world, int delayTicks, Consumer<ServerWorld> action) {
        tasks.add(new ScheduledTask(world, world.getServer().getTicks() + delayTicks, action));
    }

    public void tick() {
        List<ScheduledTask> completedTasks = new ArrayList<>();
        for (ScheduledTask task : tasks) {
            if (task.isDue()) {
                task.run();
                completedTasks.add(task);
            }
        }
        tasks.removeAll(completedTasks);
    }

    private record ScheduledTask(ServerWorld world, long executeAtTick, Consumer<ServerWorld> action) {
        public boolean isDue() {
            long currentTick = world.getServer().getTicks();
            return currentTick >= executeAtTick;
        }
        public void run() {
            action.accept(world);
        }
    }
}
