package net.mebahel.antiquebeasts.entity.ai;

import net.mebahel.antiquebeasts.entity.custom.HadesShadeEntity;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class HadesShadeMoveControl extends MoveControl {
    private final HadesShadeEntity ghast;
    private int collisionCheckCooldown;

    public HadesShadeMoveControl(HadesShadeEntity ghast) {
        super(ghast);
        this.ghast = ghast;
    }

    public void tick() {
        if (this.state == State.MOVE_TO) {
            if (this.collisionCheckCooldown-- <= 0) {
                this.collisionCheckCooldown += this.ghast.getRandom().nextInt(5) + 2;
                Vec3d vec3d = new Vec3d(this.targetX - this.ghast.getX(), this.targetY - this.ghast.getY(), this.targetZ - this.ghast.getZ());
                double d = vec3d.length();
                vec3d = vec3d.normalize();
                if (this.willCollide(vec3d, MathHelper.ceil(d))) {
                    this.ghast.setVelocity(this.ghast.getVelocity().add(vec3d.multiply(0.03)));
                } else {
                    this.state = State.WAIT;
                }
            }
        }
    }

    private boolean willCollide(Vec3d direction, int steps) {
        Box box = this.ghast.getBoundingBox();

        for(int i = 1; i < steps; ++i) {
            box = box.offset(direction);
            if (!this.ghast.world.isSpaceEmpty(this.ghast, box)) {
                return false;
            }
        }

        return true;
    }
}
