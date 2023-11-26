package net.mebahel.antiquebeasts.entity.ai;

import net.mebahel.antiquebeasts.entity.custom.HadesShadeEntity;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;
import java.util.Random;

public class HadesShadeFlyGoal extends Goal {
    private final HadesShadeEntity shade;

    public HadesShadeFlyGoal(HadesShadeEntity shade) {
        this.shade = shade;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    public boolean canStart() {
        MoveControl moveControl = this.shade.getMoveControl();
        if (!moveControl.isMoving()) {
            return true;
        } else {
            double d = moveControl.getTargetX() - this.shade.getX();
            double e = moveControl.getTargetY() - this.shade.getY();
            double f = moveControl.getTargetZ() - this.shade.getZ();
            double g = d * d + e * e + f * f;
            return g < 1.0 || g > 3600.0;
        }
    }

    public boolean shouldContinue() {
        return false;
    }

    public void start() {
        if (!this.shade.isAttacking()) {
            Random random = this.shade.getRandom();
            double d = this.shade.getX() + (double)((random.nextFloat() * 2.0F - 1.0F) * 6.0F);
            double e = this.shade.getY() + (double)((random.nextFloat() * 2.0F - 1.0F) * 6.0F);
            double f = this.shade.getZ() + (double)((random.nextFloat() * 2.0F - 1.0F) * 6.0F);
            this.shade.getMoveControl().moveTo(d, e, f, 0.25f);
        }
    }
}
