package net.mebahel.antiquebeasts.entity.ai;

import net.mebahel.antiquebeasts.entity.custom.egyptian.EgyptianCaravanEntity;
import net.mebahel.antiquebeasts.entity.custom.egyptian.EgyptianEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;

import java.util.EnumSet;
import java.util.List;

public class FollowEntityGoal extends Goal {
    private final EgyptianEntity follower;
    private final double speed;
    private final double minimumDistance = 28;
    private final double maximumDistance = 72;

    public FollowEntityGoal(EgyptianEntity follower, double speed) {
        this.follower = follower;
        this.speed = speed;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public boolean canStart() {
        if (this.follower.isInCaravan() && this.follower.getLeadEntity() == null) {
            List<EgyptianCaravanEntity> caravanEntities = this.follower.getWorld().getEntitiesByClass(EgyptianCaravanEntity.class,
                    this.follower.getBoundingBox().expand(32), e -> true);
            double closestDistance = Double.MAX_VALUE;

            for (EgyptianCaravanEntity camelry : caravanEntities) {
                double distance = this.follower.squaredDistanceTo(camelry);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    this.follower.setLeadEntity(camelry);
                }
            }
        }
        if (this.follower.getLeadEntity() == null) return false;
        return !this.follower.isInCaravan() ||
                (this.follower.squaredDistanceTo(this.follower.getLeadEntity()) > maximumDistance);
    }

    @Override
    public boolean shouldContinue() {
        if (this.follower.getLeadEntity() == null || !this.follower.getLeadEntity().isAlive()) return false;
        double distance = this.follower.squaredDistanceTo(this.follower.getLeadEntity());
        return distance > minimumDistance - 12;
    }

    @Override
    public void stop() {
        this.follower.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (this.follower.getLeadEntity() != null) {
            Path path = this.follower.getNavigation().findPathTo(this.follower.getLeadEntity(), 8);
            this.follower.getNavigation().startMovingAlong(path, this.speed);
        }
    }
}
