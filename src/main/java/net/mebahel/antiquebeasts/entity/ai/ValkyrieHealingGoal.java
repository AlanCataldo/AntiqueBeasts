package net.mebahel.antiquebeasts.entity.ai;

import net.mebahel.antiquebeasts.entity.custom.norse.NorseEntity;
import net.mebahel.antiquebeasts.entity.custom.norse.ValkyrieEntity;
import net.mebahel.antiquebeasts.particle.ModParticles;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;

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

            // Appel de la méthode pour générer les particules de guérison côté serveur et client
            generateHealingParticles(entity);

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

    // Méthode pour générer des particules
    private void generateHealingParticles(NorseEntity entity) {
        // Assurez-vous que la logique s'exécute côté serveur
        if (this.valkyrie.getWorld().isClient()) {
            return; // Ne rien faire côté client directement
        }

        // Générer les particules côté serveur
        ((ServerWorld) this.valkyrie.getWorld()).spawnParticles(
                ModParticles.HEALING_PARTICLE,
                //ParticleTypes.TOTEM_OF_UNDYING,// Type de particule
                entity.getX(), entity.getY() + entity.getHeight() + 0.5, entity.getZ(),  // Position des particules
                7,  // Nombre de particules
                0.45, 0.45, 0.45,  // Taille de la boîte autour de la position centrale
                0.1  // Vitesse des particules (tombent doucement)
        );
    }
}
