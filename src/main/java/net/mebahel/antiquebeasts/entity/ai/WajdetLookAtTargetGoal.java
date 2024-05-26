package net.mebahel.antiquebeasts.entity.ai;

import net.mebahel.antiquebeasts.entity.custom.WadjetEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.MathHelper;

import java.util.EnumSet;

public class WajdetLookAtTargetGoal extends Goal {
    private final WadjetEntity shade;
    private double deltaX;
    private double deltaZ;
    private int lookTime;

    public WajdetLookAtTargetGoal(WadjetEntity shade) {
        this.shade = shade;
        this.setControls(EnumSet.of(Control.LOOK));
    }

    public boolean canStart() {
        return this.shade.getTarget() != null;
    }
    public boolean shouldContinue() {
        return this.lookTime >= 0;
    }

    public boolean shouldRunEveryTick() {
        return true;
    }

    public void start() {
        double d = 6.283185307179586 * this.shade.getRandom().nextDouble();
        this.deltaX = Math.cos(d);
        this.deltaZ = Math.sin(d);
        this.lookTime = 20 + this.shade.getRandom().nextInt(20);
    }

    public void tick() {
        LivingEntity livingEntity = this.shade.getTarget();
        if (livingEntity != null) {
            if (livingEntity.squaredDistanceTo(this.shade) < 4096.0) {
                double e = livingEntity.getX() - this.shade.getX();
                double f = livingEntity.getZ() - this.shade.getZ();
                this.shade.setYaw(-((float)MathHelper.atan2(e, f)) * 57.295776F);
                this.shade.bodyYaw = this.shade.getYaw();
            }
        } else {
            --this.lookTime;
            this.shade.getLookControl().lookAt(this.shade.getX() + this.deltaX, this.shade.getEyeY(), this.shade.getZ() + this.deltaZ);
        }
    }
}
