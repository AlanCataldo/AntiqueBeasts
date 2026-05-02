package net.mebahel.antiquebeasts.entity.ai.util;

import net.mebahel.antiquebeasts.entity.custom.other.FlameAtronachEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.mob.PathAwareEntity;

public class CustomRevengeGoal extends RevengeGoal {
    private final Class<?> classToIgnore;

    public CustomRevengeGoal(PathAwareEntity mob, Class<?> classToIgnore) {
        super(mob);
        this.classToIgnore = classToIgnore;
    }

    private boolean shouldIgnore(LivingEntity attacker) {
        if (attacker == null) return true;

        // Ignore un type (ton comportement actuel)
        if (classToIgnore != null && classToIgnore.isInstance(attacker)) return true;

        // Ignore le owner si c'est un summon (FlameAtronach)
        if (this.mob instanceof FlameAtronachEntity fa && fa.isSummoned()) {
            if (fa.isOwner(attacker)) return true;
        }

        return false;
    }

    @Override
    public boolean canStart() {
        LivingEntity attacker = this.mob.getAttacker();
        if (shouldIgnore(attacker)) {
            return false;
        }
        return super.canStart();
    }

    @Override
    public void start() {
        LivingEntity attacker = this.mob.getAttacker();
        if (!shouldIgnore(attacker)) {
            this.mob.setTarget(attacker);
            super.start();
        }
    }
}
