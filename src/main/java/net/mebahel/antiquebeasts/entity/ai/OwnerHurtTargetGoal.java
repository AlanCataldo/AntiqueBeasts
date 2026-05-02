package net.mebahel.antiquebeasts.entity.ai;

import net.mebahel.antiquebeasts.entity.custom.other.FlameAtronachEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;

public class OwnerHurtTargetGoal extends Goal {
    private final FlameAtronachEntity mob;
    private LivingEntity cachedTarget;
    private int lastAttackTime;

    public OwnerHurtTargetGoal(FlameAtronachEntity mob) {
        this.mob = mob;
        this.setControls(EnumSet.noneOf(Control.class));
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

        LivingEntity target = owner.getAttacking();
        int time = owner.getLastAttackTime();

        if (target == null) return false;
        if (time == lastAttackTime) return false;
        if (!target.isAlive()) return false;

        if (mob.isOwner(target)) return false;

        cachedTarget = target;
        return true;
    }

    @Override
    public void start() {
        mob.setTarget(cachedTarget);
        LivingEntity owner = getOwner();
        if (owner != null) {
            lastAttackTime = owner.getLastAttackTime();
        }
        cachedTarget = null;
    }
}
