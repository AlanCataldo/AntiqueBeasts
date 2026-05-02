package net.mebahel.antiquebeasts.entity.ai;

import net.mebahel.antiquebeasts.entity.custom.other.FlameAtronachEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;

public class OwnerHurtByTargetGoal extends Goal {
    private final FlameAtronachEntity mob;
    private LivingEntity cachedAttacker;
    private int lastAttackedTime;

    public OwnerHurtByTargetGoal(FlameAtronachEntity mob) {
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

        LivingEntity attacker = owner.getAttacker();
        int time = owner.getLastAttackedTime();

        if (attacker == null) return false;
        if (time == lastAttackedTime) return false;
        if (!attacker.isAlive()) return false;

        // Ne jamais target le owner
        if (mob.isOwner(attacker)) return false;

        cachedAttacker = attacker;
        return true;
    }

    @Override
    public void start() {
        mob.setTarget(cachedAttacker);
        LivingEntity owner = getOwner();
        if (owner != null) {
            lastAttackedTime = owner.getLastAttackedTime();
        }
        cachedAttacker = null;
    }
}
