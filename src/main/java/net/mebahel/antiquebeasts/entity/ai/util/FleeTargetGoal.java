package net.mebahel.antiquebeasts.entity.ai.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class FleeTargetGoal extends Goal {
    private final MobEntity entity;
    private final double speed;

    public FleeTargetGoal(MobEntity entity, double speed) {
        this.entity = entity;
        this.speed = speed;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public boolean canStart() {
        // Commence la fuite uniquement si l'entité a une cible
        LivingEntity target = entity.getTarget();
        return target != null; // Si la cible est à moins de 10 blocs
    }

    @Override
    public void start() {
        LivingEntity target = entity.getTarget();
        if (target != null) {
            Vec3d fleeDirection = entity.getPos().subtract(target.getPos()).normalize().multiply(10); // Multiplier par la distance de fuite souhaitée
            Vec3d fleePosition = entity.getPos().add(fleeDirection);

            entity.getNavigation().startMovingTo(fleePosition.x, fleePosition.y, fleePosition.z, speed);
        }
    }

    public void tick() {
        LivingEntity target = entity.getTarget();
        if (target != null) {
            Vec3d fleeDirection = entity.getPos().subtract(target.getPos()).normalize().multiply(10); // Multiplier par la distance de fuite souhaitée
            Vec3d fleePosition = entity.getPos().add(fleeDirection);

            entity.getNavigation().startMovingTo(fleePosition.x, fleePosition.y, fleePosition.z, speed);
        }
    }

    @Override
    public boolean shouldContinue() {
        // Continue de fuir tant que la cible est proche
        LivingEntity target = entity.getTarget();
        return target != null;
    }
}

