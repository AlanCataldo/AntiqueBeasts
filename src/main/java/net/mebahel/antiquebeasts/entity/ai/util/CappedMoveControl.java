package net.mebahel.antiquebeasts.entity.ai.util;

import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.Vec3d;

public class CappedMoveControl extends MoveControl {

    private final MobEntity mob;

    // ✅ dynamiques (plus final)
    private double maxSpeedModifier;
    private double maxHorizontalSpeed;

    public CappedMoveControl(MobEntity mob, double maxSpeedModifier) {
        super(mob);
        this.mob = mob;
        this.maxSpeedModifier = Math.max(0.0, maxSpeedModifier);
        this.maxHorizontalSpeed = Math.max(0.0, 0.5);
    }

    // ✅ à appeler depuis MovementUtil selon le contexte (approche vs combat)
    public void setCaps(double maxSpeedModifier, double maxHorizontalSpeed) {
        this.maxSpeedModifier = Math.max(0.0, maxSpeedModifier);
        this.maxHorizontalSpeed = Math.max(0.0, maxHorizontalSpeed);
    }

    public double getMaxSpeedModifier() { return maxSpeedModifier; }
    public double getMaxHorizontalSpeed() { return maxHorizontalSpeed; }

    @Override
    public void moveTo(double x, double y, double z, double speed) {
        super.moveTo(x, y, z, Math.min(speed, this.maxSpeedModifier));
    }

    @Override
    public void tick() {
        super.tick();
        clampHorizontalVelocity();
    }

    private void clampHorizontalVelocity() {
        // ✅ ne casse pas tes jumps -> clamp QUE au sol
        if (!mob.isOnGround()) return;

        Vec3d v = mob.getVelocity();
        double hx = v.x;
        double hz = v.z;

        double hLen = Math.sqrt(hx * hx + hz * hz);
        if (hLen <= 1.0e-6) return;

        if (hLen > maxHorizontalSpeed) {
            double s = maxHorizontalSpeed / hLen;
            mob.setVelocity(hx * s, v.y, hz * s);
            mob.velocityDirty = true;
        }
    }
}