package net.mebahel.antiquebeasts.entity.ai.dwarven.dwarven_spider;

import net.mebahel.antiquebeasts.entity.custom.dwarven.DwarvenEntity;
import net.mebahel.antiquebeasts.entity.custom.dwarven.DwarvenSpiderEntity;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class DwarvenSpiderWanderFarGoal extends WanderAroundGoal {
    protected final float probability;

    public DwarvenEntity mob;

    public DwarvenSpiderWanderFarGoal(DwarvenEntity mob, double speed, float probability) {
        super(mob, speed);
        this.mob = mob;
        this.probability = probability;
    }
    @Override
    public boolean shouldContinue() {
        return !this.mob.getNavigation().isIdle() && !this.mob.hasPassengers() && !this.mob.getDataTracker().get(DwarvenSpiderEntity.IS_MINING);
    }

    @Nullable
    protected Vec3d getWanderTarget() {
        if (this.mob.isInsideWaterOrBubbleColumn()) {
            Vec3d vec3d = FuzzyTargeting.find(this.mob, 15, 7);
            return vec3d == null ? super.getWanderTarget() : vec3d;
        } else {
            return this.mob.getRandom().nextFloat() >= this.probability ? FuzzyTargeting.find(this.mob, 10, 7) : super.getWanderTarget();
        }
    }

    @Override
    public boolean canStart() {
        if (this.mob.hasPassengers() || this.mob.getDataTracker().get(DwarvenSpiderEntity.IS_MINING)) {
            return false;
        } else {
            if (!this.ignoringChance) {
                if (this.mob.getRandom().nextInt(toGoalTicks(this.chance)) != 0) {
                    return false;
                }
            }

            Vec3d vec3d = this.getWanderTarget();
            if (vec3d == null) {
                return false;
            } else {
                this.targetX = vec3d.x;
                this.targetY = vec3d.y;
                this.targetZ = vec3d.z;
                this.ignoringChance = false;
                return true;
            }
        }
    }
}
