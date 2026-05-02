package net.mebahel.antiquebeasts.entity.ai;


import net.mebahel.antiquebeasts.entity.custom.other.FlameAtronachEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class FollowOwnerGoal extends Goal {
    private final FlameAtronachEntity mob;

    private final double speed;
    private final float startDist;
    private final float stopDist;
    private final float teleportDist;

    public FollowOwnerGoal(FlameAtronachEntity mob, double speed, float startDist, float stopDist, float teleportDist) {
        this.mob = mob;
        this.speed = speed;
        this.startDist = startDist;
        this.stopDist = stopDist;
        this.teleportDist = teleportDist;

        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    private LivingEntity getOwner() {
        return mob.getOwnerEntity();
    }

    @Override
    public boolean canStart() {
        if (!mob.isSummoned()) return false;
        if (!mob.getHasSpawned()) return false;

        LivingEntity owner = getOwner();
        if (owner == null || !owner.isAlive()) return false;

        LivingEntity t = mob.getTarget();
        if (t != null && t.isAlive() && !t.isRemoved() && t != owner) return false;

        double distSq = mob.squaredDistanceTo(owner);
        return distSq > (double)(startDist * startDist);
    }

    @Override
    public boolean shouldContinue() {
        if (!mob.isSummoned()) return false;
        if (!mob.getHasSpawned()) return false;

        LivingEntity owner = getOwner();
        if (owner == null || !owner.isAlive()) return false;

        LivingEntity t = mob.getTarget();
        if (t != null && t.isAlive() && !t.isRemoved() && t != owner) return false;

        double distSq = mob.squaredDistanceTo(owner);
        return distSq > (double)(stopDist * stopDist);
    }

    @Override
    public void start() {
        // rien
    }

    @Override
    public void stop() {
        mob.getNavigation().stop();
    }

    @Override
    public void tick() {
        LivingEntity owner = getOwner();
        if (owner == null) return;

        mob.getLookControl().lookAt(owner, 30.0f, 30.0f);

        double distSq = mob.squaredDistanceTo(owner);

        // Téléport si trop loin (évite qu’il reste bloqué après reconnexion / chunk issues)
        if (distSq > (double)(teleportDist * teleportDist)) {
            Vec3d p = owner.getPos();
            mob.refreshPositionAndAngles(p.x, p.y, p.z, mob.getYaw(), mob.getPitch());
            mob.getNavigation().stop();
            return;
        }

        // Sinon pathfinding normal
        mob.getNavigation().startMovingTo(owner, speed);
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }
}
