package net.mebahel.antiquebeasts.entity.ai;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.mob.PathAwareEntity;

public class CustomRevengeGoal extends RevengeGoal {
    private final Class<?> classToIgnore;

    public CustomRevengeGoal(PathAwareEntity mob, Class<?> classToIgnore) {
        super(mob);
        this.classToIgnore = classToIgnore;
    }

    @Override
    public boolean canStart() {
        LivingEntity revengeTarget = this.mob.getAttacker();
        if (revengeTarget != null && classToIgnore.isInstance(revengeTarget)) {
            return false;
        }
        return super.canStart();
    }

    @Override
    public void start() {
        LivingEntity revengeTarget = this.mob.getAttacker();
        if (revengeTarget != null && !classToIgnore.isInstance(revengeTarget)) {
            this.mob.setTarget(revengeTarget);
            super.start();
        }
    }
}


