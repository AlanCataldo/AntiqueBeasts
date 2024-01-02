package net.mebahel.antiquebeasts.entity.ai;

import net.mebahel.antiquebeasts.entity.custom.HadesShadeEntity;
import net.mebahel.antiquebeasts.entity.custom.ThrowingAxeManEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class ThrowingAxeManLookAtTargetGoal extends Goal {
    private final ThrowingAxeManEntity shade;
    private double deltaX;
    private double deltaZ;
    private int lookTime;

    public ThrowingAxeManLookAtTargetGoal(ThrowingAxeManEntity shade) {
        this.shade = shade;
        this.setControls(EnumSet.of(Control.LOOK));
    }

    public boolean canStart() {
        return this.shade.getRandom().nextFloat() < 0.02F;
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
        if (this.shade.getTarget() == null) {
            --this.lookTime;
            this.shade.getLookControl().lookAt(this.shade.getX() + this.deltaX, this.shade.getEyeY(), this.shade.getZ() + this.deltaZ);
        } else {
            LivingEntity livingEntity = this.shade.getTarget();
            if (livingEntity.squaredDistanceTo(this.shade) < 4096.0) {
                double e = livingEntity.getX() - this.shade.getX();
                double f = livingEntity.getZ() - this.shade.getZ();
                this.shade.setYaw(-((float)MathHelper.atan2(e, f)) * 57.295776F);
                this.shade.bodyYaw = this.shade.getYaw();
            }
        }
    }
}
