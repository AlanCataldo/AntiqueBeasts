package net.mebahel.antiquebeasts.entity.ai.other;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

import java.util.EnumSet;

public class HarpyFlyGoal extends Goal {
    private final PathAwareEntity harpy;
    private final double speed;
    private final int minAltitude;
    private final int maxAltitude;
    private Vec3d targetPos;
    private final double targetThreshold = 1.5D;

    private int idleTime = 0;        // Temps pendant lequel la harpie reste idle
    private final int minIdleTime = 20;  // Temps minimum de l'état idle (en ticks, 20 ticks = 1 seconde)
    private final int maxIdleTime = 60;  // Temps maximum de l'état idle
    private boolean isIdle = false;  // Indique si la harpie est actuellement en mode idle

    public HarpyFlyGoal(PathAwareEntity harpy, double speed, int minAltitude, int maxAltitude) {
        this.harpy = harpy;
        this.speed = speed;
        this.minAltitude = minAltitude;
        this.maxAltitude = maxAltitude;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public boolean canStart() {
        // La harpie peut démarrer si elle est au sol ou si elle a fini de naviguer, et si elle n'est pas idle
        return (this.harpy.getNavigation().isIdle()) && !isIdle;
    }

    @Override
    public void start() {
        // Si la harpie est en mode idle, ne pas chercher de nouvelle cible
        if (isIdle) {
            return;
        }

        // Calcul d'une nouvelle cible uniquement au début ou si la harpie a atteint sa destination précédente
        if (this.targetPos == null || this.harpy.getPos().distanceTo(this.targetPos) < this.targetThreshold) {
            this.targetPos = this.getRandomAirPosition();

            // Après avoir atteint une cible, déterminer si la harpie doit entrer en mode idle
            if (this.targetPos == null || this.harpy.getRandom().nextFloat() < 0.4) {  // 30% de chance d'entrer en idle
                enterIdleMode();
                return;
            }
        }

        // Appliquer le mouvement vers la nouvelle cible
        if (this.targetPos != null) {
            Vec3d direction = this.targetPos.subtract(this.harpy.getPos()).normalize().multiply(this.speed);
            this.harpy.setVelocity(direction);

            // Faire tourner la harpie vers la cible
            this.harpy.getLookControl().lookAt(this.targetPos.x, this.targetPos.y, this.targetPos.z);
            rotateTowardsTarget();
        }
    }

    @Override
    public boolean shouldContinue() {
        // Si la harpie est en mode idle, elle ne doit pas continuer à chercher une cible
        if (isIdle) {
            return idleTime > 0;
        }

        // Continuer si elle n'a pas encore atteint la cible
        return !this.harpy.getNavigation().isIdle() && (this.targetPos == null || this.harpy.getPos().distanceTo(this.targetPos) >= this.targetThreshold);
    }

    @Override
    public void tick() {
        super.tick();

        if (isIdle) {
            // Réduire le temps d'idle
            idleTime--;
            // Empêcher tout mouvement et rotation pendant l'idle
            this.harpy.setVelocity(Vec3d.ZERO);  // Immobile
            this.harpy.getLookControl().lookAt(this.harpy.getX(), this.harpy.getY(), this.harpy.getZ());  // Ne tourne pas

            if (idleTime <= 0) {
                // Sortir du mode idle
                isIdle = false;
            }
        } else {
            // Vérifier si la harpie est bloquée contre un obstacle
            if (this.harpy.horizontalCollision) {
                // Si une collision est détectée, choisir une nouvelle cible
                this.targetPos = this.getRandomAirPosition();
            }

            // Si la harpie a atteint sa cible ou n'a pas de cible, en trouver une nouvelle
            if (this.targetPos == null || this.harpy.getPos().distanceTo(this.targetPos) < this.targetThreshold) {
                this.targetPos = this.getRandomAirPosition();
            }

            // Appliquer le mouvement vers la nouvelle cible
            if (this.targetPos != null) {
                Vec3d direction = this.targetPos.subtract(this.harpy.getPos()).normalize().multiply(this.speed);
                this.harpy.setVelocity(direction);

                // Faire tourner la harpie vers la cible
                this.harpy.getLookControl().lookAt(this.targetPos.x, this.targetPos.y, this.targetPos.z);
                rotateTowardsTarget();
            }
        }
    }

    // Méthode pour entrer en mode idle
    private void enterIdleMode() {
        Random random = this.harpy.getRandom();
        idleTime = random.nextInt(maxIdleTime - minIdleTime + 1) + minIdleTime;
        isIdle = true;
        this.harpy.setVelocity(Vec3d.ZERO);  // Arrêter le mouvement
    }

    private Vec3d getRandomAirPosition() {
        Random random = this.harpy.getRandom();
        World world = this.harpy.getWorld();

        BlockPos currentPos = this.harpy.getBlockPos();
        int currentGroundHeight = world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, currentPos.getX(), currentPos.getZ());

        int altitudeRange = this.maxAltitude - this.minAltitude;

        // Boucle pour s'assurer que la position soit valide (dans l'air et sans bloc de feuilles)
        for (int i = 0; i < 10; i++) { // Limite à 10 essais pour éviter une boucle infinie
            double newY = currentGroundHeight + this.minAltitude + random.nextDouble() * altitudeRange;

            double x = this.harpy.getX() + (random.nextDouble() * 20 - 10);
            double z = this.harpy.getZ() + (random.nextDouble() * 20 - 10);

            Vec3d targetPos = new Vec3d(x, newY, z);
            BlockPos targetBlockPos = BlockPos.ofFloored(targetPos);

            // Vérification que la position est dans l'air et qu'il n'y a pas de bloc de feuilles ou d'obstacle
            if (isValidFlyPosition(world, targetBlockPos)) {
                return targetPos;
            }
        }

        // Si après 10 essais aucune position valide n'a été trouvée, on retourne null
        return null;
    }

    // Vérifie si la position est dans l'air et qu'il n'y a pas de bloc de feuilles autour
    private boolean isValidFlyPosition(World world, BlockPos pos) {
        // Vérifie que la position est dans l'air
        if (!world.isAir(pos) || !world.isAir(pos.up())) {
            return false;
        }

        // Vérifie que le bloc sous la harpie n'est pas un bloc de feuilles
        BlockPos belowPos = pos.down();
        if (world.getBlockState(belowPos).getBlock().getTranslationKey().contains("leaves")) {
            return false;
        }

        // Vérifie qu'il n'y a pas de feuilles autour de la position cible
        for (BlockPos adjacentPos : BlockPos.iterate(pos.add(-1, -1, -1), pos.add(1, 1, 1))) {
            if (world.getBlockState(adjacentPos).getBlock().getTranslationKey().contains("leaves")) {
                return false;
            }
        }

        // Si la position passe toutes les vérifications, elle est valide
        return true;
    }

    private void rotateTowardsTarget() {
        Vec3d currentPosition = this.harpy.getPos();
        Vec3d directionToTarget = this.targetPos.subtract(currentPosition).normalize();

        double yaw = Math.toDegrees(Math.atan2(directionToTarget.z, directionToTarget.x)) - 90.0F;
        double pitch = -Math.toDegrees(Math.atan2(directionToTarget.y, Math.sqrt(directionToTarget.x * directionToTarget.x + directionToTarget.z * directionToTarget.z)));

        this.harpy.setYaw((float) yaw);
        this.harpy.setPitch((float) pitch);
    }
}
