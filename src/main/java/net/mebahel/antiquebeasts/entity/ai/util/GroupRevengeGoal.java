package net.mebahel.antiquebeasts.entity.ai.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.Box;

import java.util.EnumSet;
import java.util.List;

public class GroupRevengeGoal extends RevengeGoal {
    private final Class<?>[] helperTypes;

    public GroupRevengeGoal(PathAwareEntity mob, Class<?>... helperTypes) {
        super(mob);
        this.helperTypes = helperTypes;
        this.setControls(EnumSet.of(Goal.Control.TARGET));
    }

    @Override
    protected void callSameTypeForRevenge() {
        MobEntity mob = this.mob;
        LivingEntity attacker = mob.getAttacker();

        if (attacker == null) {
            return;
        }

        if (isHelperType(attacker)) {
            return;
        }

        double range = 10.0D;
        Box box = Box.from(mob.getPos()).expand(range, range, range);

        List<MobEntity> list = mob.getWorld().getEntitiesByClass(MobEntity.class, box, (mobEntity) -> {
            if (mobEntity == mob) {
                return false;
            }
            if (mobEntity.isAttacking()) {
                return false;
            }
            if (!mobEntity.isAlive()) {
                return false;
            }
            if (!(mobEntity instanceof PathAwareEntity)) {
                return false;
            }
            if (!isHelperType(mobEntity)) {
                return false;
            }

            return true;
        });

        for (MobEntity ally : list) {
            setMobEntityTarget(ally, attacker);
        }
    }

    private boolean isHelperType(LivingEntity entity) {
        for (Class<?> helper : helperTypes) {
            if (helper.isInstance(entity)) {
                return true;
            }
        }
        return false;
    }

    protected void setMobEntityTarget(MobEntity mob, LivingEntity target) {
        mob.setTarget(target);
    }
}
