package net.mebahel.antiquebeasts.entity.ai.util;

import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.mob.PathAwareEntity;

public class ConditionalWanderAroundFarGoal extends WanderAroundFarGoal {
    public ConditionalWanderAroundFarGoal(PathAwareEntity mob, double speed, float probability) {
        super(mob, speed, probability);
    }

    @Override
    public boolean canStart() {
        if (this.mob.getTarget() != null || this.mob.isAttacking()) {
            return false;
        }
        return super.canStart();
    }

    @Override
    public boolean shouldContinue() {
        if (this.mob.getTarget() != null || this.mob.isAttacking()) {
            return false;
        }
        return super.shouldContinue();
    }
}