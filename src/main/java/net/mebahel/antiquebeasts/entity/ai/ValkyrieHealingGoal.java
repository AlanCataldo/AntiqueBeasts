package net.mebahel.antiquebeasts.entity.ai;

import net.mebahel.antiquebeasts.entity.custom.NorseEntity;
import net.mebahel.antiquebeasts.entity.custom.ValkyrieEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.predicate.entity.EntityPredicates;

import java.util.Optional;

public class ValkyrieHealingGoal extends Goal {
    private final ValkyrieEntity valkyrie;
    private final double searchRadius;

    public ValkyrieHealingGoal(ValkyrieEntity valkyrie, double searchRadius) {
        this.valkyrie = valkyrie;
        this.searchRadius = searchRadius;
    }

    @Override
    public boolean canStart() {
        LivingEntity target = this.valkyrie.getTarget();
        if (target == null) {
            return true;
        } else {
            double dx = target.getX() - this.valkyrie.getX();
            double dy = target.getY() - this.valkyrie.getY();
            double dz = target.getZ() - this.valkyrie.getZ();
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
            return distance > 8;
        }
    }

    @Override
    public void stop() {
        this.valkyrie.setHealing(false);
    }

    @Override
    public void tick() {
        Optional<NorseEntity> entityToHeal = this.valkyrie.getWorld().getEntitiesByClass(NorseEntity.class, this.valkyrie.getBoundingBox().expand(this.searchRadius), EntityPredicates.VALID_LIVING_ENTITY).stream()
                .filter(entity -> entity.getHealth() < entity.getMaxHealth())
                .findFirst();
        if (entityToHeal.isPresent()) {
            NorseEntity entity = entityToHeal.get();
            this.valkyrie.setHealing(true);
            this.valkyrie.lookAtEntity(entity, 15f, 15f);
            this.valkyrie.getLookControl().lookAt(entity, 15f, 15f);
            float newHealth = Math.min(entity.getHealth() + 0.25F, entity.getMaxHealth());
            entity.setHealth(newHealth);
            double dx = entity.getX() - this.valkyrie.getX();
            double dy = entity.getY() - this.valkyrie.getY();
            double dz = entity.getZ() - this.valkyrie.getZ();
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (distance > 8) {
                this.valkyrie.getNavigation().startMovingTo(entity, 0.35f);
            } else {
                this.valkyrie.getNavigation().stop();
            }
        } else {
            this.valkyrie.setHealing(false);
        }
    }
}
