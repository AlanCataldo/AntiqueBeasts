package net.mebahel.antiquebeasts.entity.ai.other;

import net.mebahel.antiquebeasts.entity.custom.other.HarpyEntity;
import net.mebahel.antiquebeasts.entity.projectiles.HarpyFeatherEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.EnumSet;

public class HarpyRangedAttackGoal extends Goal {
    private final HarpyEntity harpy;
    private final double speed;
    private final double circleRadius;
    private final World world;
    private double circlingAngle; // Angle pour tourner autour de la cible
    private final int minAltitude; // Hauteur minimale configurable
    private final int maxAltitude; // Hauteur maximale configurable
    private Vec3d circlingPosition; // Dernière position cible circulaire

    public HarpyRangedAttackGoal(HarpyEntity harpy, double speed, double circleRadius, int attackInterval, int minAltitude, int maxAltitude) {
        this.harpy = harpy;
        this.speed = speed;
        this.circleRadius = circleRadius;
        this.minAltitude = minAltitude;
        this.maxAltitude = maxAltitude;
        this.world = harpy.getWorld();
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        this.circlingAngle = 0.0; // Angle initial
    }

    @Override
    public boolean canStart() {
        LivingEntity potentialTarget = this.harpy.getTarget();
        return potentialTarget != null && potentialTarget.isAlive();
    }

    @Override
    public void start() {
        this.harpy.setFireBreathingCooldown(51);
        this.circlingPosition = null;  // Réinitialiser la position cible
        this.circlingAngle = 0.0; // Réinitialiser l'angle circulaire
    }

    @Override
    public boolean shouldContinue() {
        LivingEntity livingEntity = this.harpy.getTarget();

        if (livingEntity instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity) livingEntity;
            if (playerEntity.isCreative() || playerEntity.isSpectator()) {
                return false;
            }
        }
        return livingEntity != null && livingEntity.isAlive();
    }

    @Override
    public void stop() {
        this.harpy.setFireBreathing(false);
        this.harpy.setFireBreathingCooldown(51);
        this.circlingPosition = null;  // Réinitialiser la position circulaire
    }

    @Override
    public void tick() {
        LivingEntity target = this.harpy.getTarget();
        if (target != null) {
            double distanceToTarget = this.harpy.squaredDistanceTo(target.getX(), target.getY(), target.getZ());

            // Mise à jour continue de la position circulaire, que la harpie ait atteint la cible ou non
            circlingAngle += 0.05;  // Incrémenter l'angle pour le mouvement circulaire en continu

            double offsetX = Math.cos(circlingAngle) * circleRadius;
            double offsetZ = Math.sin(circlingAngle) * circleRadius;
            double heightOffset = calculateHeightOffset(target); // Calculer la hauteur de vol

            // Calculer la position circulaire autour de la cible
            circlingPosition = new Vec3d(target.getX() + offsetX, target.getY() + heightOffset, target.getZ() + offsetZ);

            // S'assurer que la harpie se déplace vers la position cible circulaire
            Vec3d direction = circlingPosition.subtract(this.harpy.getPos()).normalize();
            this.harpy.setVelocity(direction.multiply(this.speed));

            // Ajustement du comportement en cas de FireBreathing
            if (this.harpy.isFireBreathing()) {
                this.harpy.setVelocity(Vec3d.ZERO);  // Stopper tout mouvement
            }

            rotateTowardsTarget(target);
            avoidObstacles();

            // Gestion du cooldown et des attaques
            if (this.harpy.canSee(target)) {
                this.harpy.setFireBreathingCooldown(Math.max(this.harpy.getFireBreathingCooldown() - 1, 0));
                if (this.harpy.getFireBreathingCooldown() == 5) {
                    shootArrow(target);
                } else if (this.harpy.getFireBreathingCooldown() == 10) {
                    this.harpy.setFireBreathing(true);
                } else if (this.harpy.getFireBreathingCooldown() == 0) {
                    this.harpy.setFireBreathingCooldown(50);
                    this.harpy.setFireBreathing(false);
                }
            } else {
                this.harpy.setFireBreathing(false);
                this.harpy.setFireBreathingCooldown(51);
            }
        }
    }

    // Méthode pour ajuster la rotation (yaw) de la harpie vers la cible
    private void rotateTowardsTarget(LivingEntity target) {
        Vec3d targetPos = target.getPos();
        Vec3d harpyPos = this.harpy.getPos();

        double dx = targetPos.x - harpyPos.x;
        double dz = targetPos.z - harpyPos.z;

        // Calculer l'angle de rotation en radians
        double targetYaw = Math.toDegrees(Math.atan2(dz, dx)) - 90.0;

        // Ajuster le yaw progressivement vers la cible
        this.harpy.setYaw(this.lerpRotation(this.harpy.getYaw(), (float) targetYaw, 30.0F));
    }

    // Méthode pour interpoler la rotation de manière fluide
    private float lerpRotation(float currentYaw, float targetYaw, float maxTurnSpeed) {
        float deltaYaw = MathHelper.wrapDegrees(targetYaw - currentYaw);
        return currentYaw + MathHelper.clamp(deltaYaw, -maxTurnSpeed, maxTurnSpeed);
    }

    // Méthode pour éviter les obstacles en vol
    private void avoidObstacles() {
        Vec3d currentPos = this.harpy.getPos();
        BlockPos blockPosAhead = BlockPos.ofFloored(currentPos.add(this.harpy.getVelocity().normalize().multiply(2.0))); // Vérifie 2 blocs en avant

        if (!this.world.isAir(blockPosAhead)) {
            // Si un bloc se trouve devant, ajuster la trajectoire vers le haut
            this.harpy.setVelocity(this.harpy.getVelocity().add(0, 0.5, 0));  // Monter pour éviter l'obstacle
        }
    }

    // Méthode pour calculer une hauteur de vol entre minAltitude et maxAltitude
    private double calculateHeightOffset(LivingEntity target) {
        double currentAltitude = this.harpy.getY();
        double targetAltitude = target.getY();

        double targetHeight = targetAltitude + minAltitude + (Math.random() * (maxAltitude - minAltitude));
        return targetHeight - currentAltitude;
    }

    // Méthode pour tirer une flèche
    private void shootArrow(LivingEntity target) {
        Vec3d harpyPos = this.harpy.getPos();
        Vec3d targetPos = target.getPos();

        // Tirer la plume principale
        shootSingleArrow(target, harpyPos, targetPos, 0.0);  // 0.0 signifie sans déviation

        // Tirer entre 1 et 2 plumes supplémentaires
        int extraPlumes = 1 + this.world.random.nextInt(2); // Tire 1 ou 2 plumes supplémentaires

        for (int i = 0; i < extraPlumes; i++) {
            // Ajouter un léger décalage à gauche ou à droite
            double angleOffset = (i == 0 ? -10 : 10); // Premier à gauche (-10°) et second à droite (+10°)
            shootSingleArrow(target, harpyPos, targetPos, angleOffset);
        }
    }

    // Méthode pour tirer une seule plume avec un angle de déviation
    private void shootSingleArrow(LivingEntity target, Vec3d harpyPos, Vec3d targetPos, double angleOffset) {
        HarpyFeatherEntity arrow = new HarpyFeatherEntity(this.world, this.harpy, 6);

        double dx = targetPos.x - harpyPos.x;
        double dy = (targetPos.y + target.getStandingEyeHeight()) - (harpyPos.y + 1.5); // Compte la hauteur des yeux
        double dz = targetPos.z - harpyPos.z;
        double distanceXZ = Math.sqrt(dx * dx + dz * dz);

        // Ajuster la direction avec l'angle de déviation
        float velocity = 1.35F;
        float gravity = 0.05F;

        // Calculer l'angle de déviation horizontalement
        double angleInRadians = Math.toRadians(angleOffset);
        double adjustedDx = Math.cos(angleInRadians) * dx - Math.sin(angleInRadians) * dz;
        double adjustedDz = Math.sin(angleInRadians) * dx + Math.cos(angleInRadians) * dz;

        arrow.setVelocity(adjustedDx, dy + gravity * distanceXZ / velocity, adjustedDz, velocity, 1.0F);
        arrow.setPosition(harpyPos.getX(), harpyPos.getY() + 1.5D, harpyPos.getZ());

        this.world.spawnEntity(arrow);
    }
}
