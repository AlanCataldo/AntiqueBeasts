package net.mebahel.antiquebeasts.entity.ai.other;

import net.mebahel.antiquebeasts.entity.custom.other.DraugrScourgeEntity;
import net.mebahel.antiquebeasts.util.entity.MovementUtil;
import net.mebahel.antiquebeasts.util.entity.ProjectileUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class DraugrIceSpikeSpellGoal extends Goal {
    private final DraugrScourgeEntity actor;
    private final float iceSpikeDamage;
    private final float frostBiteDamage;
    private final ProjectileUtil projectileUtil;
    private boolean castIceSpike = true;
    private static final int COOLDOWN_TICKS = 140;
    private static final int SHOOT_START = 60;
    private static final int SHOOT_END = 10;
    private static final int SHOOT_INTERVAL = 5;
    private final MovementUtil movementUtil;

    public DraugrIceSpikeSpellGoal(DraugrScourgeEntity actor, float frostbiteDamage, float iceSpikeDamage) {
        this.actor = actor;
        this.frostBiteDamage = frostbiteDamage;
        this.iceSpikeDamage = iceSpikeDamage;
        this.movementUtil = new MovementUtil(this.actor);
        this.projectileUtil = new ProjectileUtil();
    }

    public boolean canStart() {
        LivingEntity livingEntity = this.actor.getTarget();

        if (livingEntity instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity) livingEntity;
            if (playerEntity.isCreative() || playerEntity.isSpectator()) {
                return false;
            }
        }
        return livingEntity != null && livingEntity.isAlive();
    }

    public void start() {
        this.actor.setCooldown(COOLDOWN_TICKS);
    }

    public void stop() {
        this.actor.setCooldown(141);
        this.actor.setShooting(false);
        this.actor.getMoveControl().strafeTo(0, 0);
    }

    public boolean shouldRunEveryTick() {
        return true;
    }

    public boolean shouldContinue() {
        LivingEntity livingEntity = this.actor.getTarget();

        if (livingEntity instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity) livingEntity;
            if (playerEntity.isCreative() || playerEntity.isSpectator()) {
                return false;
            }
        }
        return livingEntity != null && livingEntity.isAlive();
    }

    public void tick() {
        LivingEntity target = this.actor.getTarget();
        if (target == null || !target.isAlive()) {
            this.stop();
            return;
        }
        double distanceToTarget = this.actor.distanceTo(target);
        this.movementUtil.lookAtTarget(target, this.actor);
        this.movementUtil.checkIfStuck(target, this.actor);

        if (!this.movementUtil.isSkyVisibleAbove(this.actor)) {
            this.movementUtil.strafeUnderground(target, this.actor);
        } else if (distanceToTarget <= 8) {
            this.movementUtil.moveBackward(target, this.actor);
        } else {
            this.movementUtil.strafeAroundTarget(target, this.actor);
        }

        if (this.actor.getVisibilityCache().canSee(target)) {
            this.actor.setCooldown(Math.max(this.actor.getCooldown() - 1, 0));
            if (this.castIceSpike) {
                if (this.actor.getCooldown() == 30) {
                    projectileUtil.shootProjectile(target, this.actor, this.iceSpikeDamage, new Vec3d(0, 0, 0));
                } else if (this.actor.getCooldown() == 60) {
                    this.actor.setShooting(true);
                } else if (this.actor.getCooldown() == 0) {
                    castIceSpike = Math.random() < 0.65;
                    this.actor.setCooldown(COOLDOWN_TICKS);
                    this.actor.setShooting(false);
                } else if (this.actor.getCooldown() > 60 && this.actor.getCooldown() < COOLDOWN_TICKS) {
                    this.actor.setAttackName("ice_spike");
                    this.actor.setShooting(false);
                }
            } else {
                float cooldown = this.actor.getCooldown();
                if (cooldown <= SHOOT_START - 10 && cooldown >= SHOOT_END && cooldown % SHOOT_INTERVAL == 0) {
                    projectileUtil.shootFrostBiteProjectile(target, this.actor, this.frostBiteDamage, new Vec3d(0, 0, 0));
                }
                if (cooldown == SHOOT_START) {
                    this.actor.setAttackName("frostbite");
                    this.actor.setShooting(true);
                }
                if (cooldown == 0) {
                    castIceSpike = Math.random() < 0.65;
                    this.actor.setShooting(false);
                    this.actor.setCooldown(COOLDOWN_TICKS); // Reset to 180 ticks cooldown
                }
            }
        } else {
            this.actor.setShooting(false);
            this.actor.setCooldown(COOLDOWN_TICKS);
        }
    }
}
