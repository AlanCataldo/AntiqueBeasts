package net.mebahel.antiquebeasts.entity.ai;

import net.mebahel.antiquebeasts.entity.custom.CyclopsEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class CyclopsLookAtTargetGoal extends Goal {
    private final CyclopsEntity cyclops;

    public CyclopsLookAtTargetGoal(CyclopsEntity cyclops) {
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
            Vec3d vec3d = this.cyclops.getVelocity();
            this.cyclops.setYaw(-((float) MathHelper.atan2(vec3d.x, vec3d.z)) * 57.295776F);
            this.cyclops.bodyYaw = this.cyclops.getYaw();
        } else {
            LivingEntity livingEntity = this.cyclops.getTarget();
            double d = 64.0;
            if (livingEntity.squaredDistanceTo(this.cyclops) < 4096.0) {
                double e = livingEntity.getX() - this.cyclops.getX();
                double f = livingEntity.getZ() - this.cyclops.getZ();
                this.cyclops.setYaw(-((float)MathHelper.atan2(e, f)) * 57.295776F);
                this.cyclops.bodyYaw = this.cyclops.getYaw();
            }
        }

    }
}
