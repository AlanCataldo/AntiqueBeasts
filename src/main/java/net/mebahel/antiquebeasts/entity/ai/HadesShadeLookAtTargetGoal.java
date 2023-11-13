package net.mebahel.antiquebeasts.entity.ai;

import net.mebahel.antiquebeasts.entity.custom.HadesShadeEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class HadesShadeLookAtTargetGoal extends Goal {
    private final HadesShadeEntity shade;

    public HadesShadeLookAtTargetGoal(HadesShadeEntity shade) {
        this.shade = shade;
        this.setControls(EnumSet.of(Control.LOOK));
    }

    public boolean canStart() {
        return true;
    }

    public boolean shouldRunEveryTick() {
        return true;
    }

    public void tick() {
        if (this.shade.getTarget() == null) {
            Vec3d vec3d = this.shade.getVelocity();
            this.shade.setYaw(-((float) MathHelper.atan2(vec3d.x, vec3d.z)) * 57.295776F);
            this.shade.bodyYaw = this.shade.getYaw();
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
