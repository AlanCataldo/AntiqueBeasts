package net.mebahel.antiquebeasts.entity.ai;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.util.math.MathHelper;

import java.util.EnumSet;

public class LookAtTargetGoal extends Goal {
    private final HostileEntity cyclops;

    public LookAtTargetGoal(HostileEntity cyclops) {
        this.cyclops = cyclops;
        this.setControls(EnumSet.of(Control.LOOK));
    }

    public boolean canStart() {
        return true;
    }

    public boolean shouldRunEveryTick() {
        return true;
    }

    public void tick() {
        if (this.cyclops.getTarget() == null) {
            if (this.cyclops.getRandom().nextFloat() < 0.01F) { // Randomize direction occasionally
                float randomYaw = this.cyclops.getRandom().nextFloat() * 360.0F;
                this.cyclops.setYaw(randomYaw);
                this.cyclops.bodyYaw = randomYaw;
            }
        } else {
            LivingEntity livingEntity = this.cyclops.getTarget();
            double d = 64.0;
            if (livingEntity.squaredDistanceTo(this.cyclops) < 4096.0) {
                double e = livingEntity.getX() - this.cyclops.getX();
                double f = livingEntity.getZ() - this.cyclops.getZ();
                this.cyclops.setYaw(-((float) MathHelper.atan2(e, f)) * 57.295776F);
                this.cyclops.bodyYaw = this.cyclops.getYaw();
            }
        }
    }
}
