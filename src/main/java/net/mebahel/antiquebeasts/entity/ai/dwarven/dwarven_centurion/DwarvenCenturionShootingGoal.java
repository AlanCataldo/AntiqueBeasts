package net.mebahel.antiquebeasts.entity.ai.dwarven.dwarven_centurion;

import net.mebahel.antiquebeasts.entity.custom.dwemer.DwemerCenturionEntity;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.mebahel.antiquebeasts.util.entity.ProjectileUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import software.bernie.geckolib.util.ClientUtils;

public class DwarvenCenturionShootingGoal extends Goal {
    private final DwemerCenturionEntity centurion;
    private static final int COOLDOWN_TICKS = 300;
    private static final int SHOOT_START = 60;
    private static final int SHOOT_END = 26;
    private static final int SHOOT_INTERVAL = 2;
    private final ProjectileUtil projectileUtil;
    private Vec3d lockedTargetPos;
    private int shotsFired = 0;

    public float damage;

    public DwarvenCenturionShootingGoal(DwemerCenturionEntity centurion, float damage) {
        this.centurion = centurion;
        this.projectileUtil = new ProjectileUtil();
        this.damage = damage;
    }

    @Override
    public boolean canStart() {
        LivingEntity target = this.centurion.getTarget();
        return target != null && target.isAlive() && !(target instanceof PlayerEntity player && (player.isCreative() || player.isSpectator()));
    }

    @Override
    public void start() {
        this.centurion.setCooldown(COOLDOWN_TICKS);
        this.lockedTargetPos = null;
        this.shotsFired = 0;
    }

    @Override
    public boolean shouldContinue() {
        LivingEntity target = this.centurion.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public void stop() {
        this.centurion.setShooting(false);
        this.centurion.setCooldown(COOLDOWN_TICKS);
        this.lockedTargetPos = null;
        this.shotsFired = 0;
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity target = this.centurion.getTarget();
        if (target == null || !target.isAlive() || !this.centurion.canSee(target)) {
            this.stop();
            return;
        }

        float cooldown = this.centurion.getCooldown();
        this.centurion.setCooldown(Math.max(cooldown - 1, 0));

        if (this.centurion.isShooting()) {
            this.centurion.getNavigation().stop();
        }

        if (cooldown == SHOOT_START) {
            this.centurion.setShooting(true);
            Vec3d toTarget = target.getPos().add(0, target.getHeight() * 0.75, 0).subtract(this.centurion.getPos()).normalize();
            Vec3d leftOffset = toTarget.crossProduct(new Vec3d(0, 1, 0)).normalize().multiply(1.5); // Décalage vers la gauche
            this.lockedTargetPos = target.getPos().add(leftOffset).add(0, target.getHeight() * 0.7, 0);
            this.shotsFired = 0;

            // Calcule l'angle pour tourner le corps
            Vec3d diff = this.lockedTargetPos.subtract(this.centurion.getPos());
            double yaw = Math.toDegrees(Math.atan2(diff.z, diff.x)) - 90.0;

            this.centurion.setYaw((float) yaw);
            this.centurion.prevYaw = (float) yaw;
            this.centurion.headYaw = (float) yaw;
            this.centurion.setBodyYaw((float) yaw);

            PlayerEntity player = ClientUtils.getClientPlayer();
            if (player != null) {
                this.centurion.getWorld().playSound(player, this.centurion.getX(), this.centurion.getY(),
                        this.centurion.getZ(), ModSounds.DWARVEN_CENTURION_SHOOTING, this.centurion.getSoundCategory(), 1f, 1f);
            }
        }


        if (this.centurion.isShooting() && this.lockedTargetPos != null) {
            Vec3d diff = this.lockedTargetPos.subtract(this.centurion.getPos());
            double yaw = Math.toDegrees(Math.atan2(diff.z, diff.x)) - 90.0;

            this.centurion.setYaw((float) yaw);
            this.centurion.prevYaw = (float) yaw;
            this.centurion.headYaw = (float) yaw;
            this.centurion.setBodyYaw((float) yaw);
        }

        if (cooldown <= SHOOT_START - 12 && cooldown >= SHOOT_END && cooldown % SHOOT_INTERVAL == 0 && lockedTargetPos != null) {
            Vec3d start = centurion.getPos().add(0, 4, 0); // position plus haute (sortie vapeur)
            Vec3d dirToTarget = lockedTargetPos.add(0, target.getHeight() * 0.6, 0).subtract(start).normalize();

            Vec3d perpendicular = dirToTarget.crossProduct(new Vec3d(0, 1, 0)).normalize().multiply(0.8);

            float progress = (SHOOT_START - cooldown) / (float)(SHOOT_START - SHOOT_END); // [0 → 1]
            float angle = -(progress - 0.5f) * 2f; // de +1 → -1 pour droite → gauche

            Vec3d shootDir = dirToTarget.add(perpendicular.multiply(angle)).normalize();

            projectileUtil.shootSteamProjectile(centurion.getWorld(), centurion, shootDir, this.damage);
            shotsFired++;
        }


        if (cooldown == 0) {
            this.centurion.setShooting(false);
            this.centurion.setCooldown(COOLDOWN_TICKS);
            this.shotsFired = 0;
            this.lockedTargetPos = null;
        }
    }
}

