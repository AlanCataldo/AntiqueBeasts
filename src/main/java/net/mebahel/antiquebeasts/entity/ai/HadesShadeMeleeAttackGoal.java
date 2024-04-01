package net.mebahel.antiquebeasts.entity.ai;

import net.mebahel.antiquebeasts.entity.custom.HadesShadeEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class HadesShadeMeleeAttackGoal extends Goal {
    protected final HadesShadeEntity mob;
    private static final int MAX_COOLDOWN = 21;
    public int cooldown;

    public HadesShadeMeleeAttackGoal(HadesShadeEntity mob) {
        this.mob = mob;
        this.setControls(EnumSet.of(Control.MOVE));
    }
    public boolean canStart() {
        LivingEntity livingEntity = this.mob.getTarget();
        return livingEntity != null;
    }
    public boolean shouldContinue() {
        LivingEntity livingEntity = this.mob.getTarget();
        return livingEntity != null;
    }
    public void start() {
        LivingEntity livingEntity = this.mob.getTarget();
        Vec3d vec3d = livingEntity.getEyePos();
        this.mob.getMoveControl().moveTo(vec3d.x, vec3d.y, vec3d.z, 1.0);
        this.mob.setAttacking(true);
        this.cooldown = MAX_COOLDOWN + 4;
    }
    public void stop() {
        this.mob.setAttacking(false);
        this.mob.setSwinging(false);
    }
    public boolean shouldRunEveryTick() {
        return true;
    }
    public void tick() {
        LivingEntity livingEntity = this.mob.getTarget();
        if (livingEntity != null) {
            Vec3d vec3d = livingEntity.getEyePos();
            Vec3d velo = new Vec3d(livingEntity.getX() - this.mob.getX(), livingEntity.getY() - this.mob.getY(), livingEntity.getZ() - this.mob.getZ());
            if (!this.mob.isSwinging()) {
                this.mob.setVelocity(this.mob.getVelocity().add(velo.multiply(0.0012)));
                this.mob.getMoveControl().moveTo(vec3d.x, vec3d.y - 1, vec3d.z, 1);
            }
            this.mob.getLookControl().lookAt(livingEntity);
            this.attack(livingEntity);
        } else {
            this.stop();
        }
    }
    protected void attack(LivingEntity target) {
        double squaredDistance = this.mob.squaredDistanceTo(target.getX(), target.getY(), target.getZ());
        double d = this.getSquaredMaxAttackDistance(target);
        this.cooldown = Math.max(this.cooldown - 1, 0);

        if (this.cooldown == 0) {
            this.cooldown = MAX_COOLDOWN + 2;
            this.mob.setSwinging(false);
        } else if (squaredDistance <= d && this.cooldown == 20) {
            this.mob.setSwinging(true);
        } else if (squaredDistance <= d + 1 && this.cooldown == 10 && this.mob.isSwinging()) {
            this.mob.tryAttack(target);
        }
    }
    protected double getSquaredMaxAttackDistance(LivingEntity entity) {
        return 8f + entity.getWidth();
    }
}