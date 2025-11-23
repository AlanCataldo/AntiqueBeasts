package net.mebahel.antiquebeasts.entity.ai.other;

import net.mebahel.antiquebeasts.entity.custom.other.DraugrWightEntity;
import net.mebahel.antiquebeasts.entity.projectiles.DraugrWightProjectileEntity;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.mebahel.antiquebeasts.util.entity.ProjectileUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class DraugrSpellGoal extends Goal {
    private float damage;
    private final DraugrWightEntity draugr;
    private static final int COOLDOWN_TICKS = 220;
    private static final int SHOOT_START = 60;
    private static final int SHOOT_END = 10;
    private static final int SHOOT_INTERVAL = 5;
    private final ProjectileUtil projectileUtil;

    public DraugrSpellGoal(DraugrWightEntity draugr, float damage) {
        this.draugr = draugr;
        this.damage = damage;
        this.projectileUtil = new ProjectileUtil();
    }

    public boolean canStart() {
        LivingEntity livingEntity = this.draugr.getTarget();

        if (livingEntity instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity) livingEntity;
            if (playerEntity.isCreative() || playerEntity.isSpectator()) {
                return false;
            }
        }
        return livingEntity != null && livingEntity.isAlive();
    }

    public void start() {
        this.draugr.setCooldown(COOLDOWN_TICKS); // Initial cooldown set to 180 ticks
    }

    public boolean shouldContinue() {
        LivingEntity livingEntity = this.draugr.getTarget();

        if (livingEntity instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity) livingEntity;
            if (playerEntity.isCreative() || playerEntity.isSpectator()) {
                return false;
            }
        }
        return livingEntity != null && livingEntity.isAlive();
    }

    public void stop() {
        this.draugr.setShooting(false);
        this.draugr.setCooldown(COOLDOWN_TICKS); // Reset cooldown after spell
    }

    public boolean shouldRunEveryTick() {
        return true;
    }

    public void tick() {
        LivingEntity target = this.draugr.getTarget();
        if (target == null || !target.isAlive() || !this.draugr.canSee(target)) {
            this.stop();
            return;
        }

        // Réduire le cooldown chaque tick
        if (this.draugr.getCooldown() > 0) {
            this.draugr.setCooldown(this.draugr.getCooldown() - 1);
        }

        // Reculer lorsque le mob est en mode "shooting"
        if (this.draugr.isShooting()) {
            this.draugr.getNavigation().stop();
            moveBackwardFromTarget(target);
        }

        float cooldown = this.draugr.getCooldown();

        // Le tir commence à partir du tick 60 et continue jusqu'à 10
        if (cooldown <= SHOOT_START - 10 && cooldown >= SHOOT_END && cooldown % SHOOT_INTERVAL == 0) {
            projectileUtil.shootFrostBiteProjectile(target, this.draugr, this.damage, new Vec3d(0.3, 0.3, -0.6));
        }

        // Activation de l'état "isShooting" à 60 ticks
        if (cooldown == SHOOT_START) {
            this.draugr.setShooting(true);
        }

        // Désactivation de l'état "isShooting" et réinitialisation du cooldown à 0
        if (cooldown == 0) {
            this.draugr.setShooting(false);
            this.draugr.setCooldown(COOLDOWN_TICKS); // Reset to 180 ticks cooldown
        }
    }
    private void moveBackwardFromTarget(LivingEntity target) {
        // Calculer le vecteur de direction opposé à la cible
        Vec3d directionToTarget = this.draugr.getPos().subtract(target.getPos()).normalize();

        // Appliquer un mouvement en recul (vers l'opposé de la cible)
        Vec3d backwardMovement = directionToTarget.multiply(0.065); // Modifier la valeur pour ajuster la vitesse
        this.draugr.setVelocity(backwardMovement.x, this.draugr.getVelocity().y, backwardMovement.z);

        // Ajuster l'orientation pour rester face au joueur
        this.draugr.lookAtEntity(target, 30.0F, 30.0F);
    }
}
