package net.mebahel.antiquebeasts.entity.ai.other;

import net.mebahel.antiquebeasts.entity.ai.util.ProjectileUtil;
import net.mebahel.antiquebeasts.entity.custom.other.DraugrScourgeEntity;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.mebahel.antiquebeasts.util.entity.MovementUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class DraugrIceSpikeSpellGoal extends Goal {
    private final DraugrScourgeEntity actor;
    private final float iceSpikeDamage;
    private final float frostBiteDamage;
    private final ProjectileUtil projectileUtil;
    private final MovementUtil movementUtil;

    private boolean castIceSpike = true;

    private static final int COOLDOWN_TICKS = 140;
    private static final int SHOOT_START = 60;     // début anim frostbite
    private static final int SHOOT_END = 10;       // fin fenêtre tirs frostbite
    private static final int SHOOT_INTERVAL = 5;   // cadence frostbite

    public DraugrIceSpikeSpellGoal(DraugrScourgeEntity actor, float frostbiteDamage, float iceSpikeDamage) {
        this.actor = actor;
        this.frostBiteDamage = frostbiteDamage;
        this.iceSpikeDamage = iceSpikeDamage;
        this.movementUtil = new MovementUtil(this.actor, 6);
        this.projectileUtil = new ProjectileUtil();
    }

    @Override
    public boolean canStart() {
        LivingEntity livingEntity = this.actor.getTarget();

        if (livingEntity instanceof PlayerEntity playerEntity) {
            if (playerEntity.isCreative() || playerEntity.isSpectator()) {
                return false;
            }
        }
        return livingEntity != null && livingEntity.isAlive() && this.actor.getHasSpawned();
    }

    @Override
    public void start() {
        this.actor.setCooldown(COOLDOWN_TICKS);
        this.actor.setShooting(false);
    }

    @Override
    public void stop() {
        this.actor.setCooldown(COOLDOWN_TICKS + 1);
        this.actor.setShooting(false);
        this.actor.getMoveControl().strafeTo(0, 0);
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public boolean shouldContinue() {
        LivingEntity livingEntity = this.actor.getTarget();

        if (livingEntity instanceof PlayerEntity playerEntity) {
            if (playerEntity.isCreative() || playerEntity.isSpectator()) {
                return false;
            }
        }
        return livingEntity != null && livingEntity.isAlive() && this.actor.getHasSpawned();
    }

    @Override
    public void tick() {
        if (actor.isUsingPotion() || actor.getHealTicks() > 0) {
            actor.setShooting(false);
            actor.getNavigation().stop();
            return;
        }

        LivingEntity target = this.actor.getTarget();
        if (target == null || !target.isAlive()) {
            this.stop();
            return;
        }

        this.movementUtil.maintainRangedPosition(target);

        if (!this.actor.getVisibilityCache().canSee(target)) {
            this.actor.setShooting(false);
            this.actor.setCooldown(COOLDOWN_TICKS);
            return;
        }

        // --- logique de cooldown commune ---
        this.actor.setCooldown(Math.max(this.actor.getCooldown() - 1, 0));
        int cd = this.actor.getCooldown();

        if (this.castIceSpike) {
            handleIceSpike(target, cd);
        } else {
            handleFrostbite(target, cd);
        }
    }

    // =========================
    //      ICE SPIKE SPELL
    // =========================
    private void handleIceSpike(LivingEntity target, int cd) {
        // Début de cast : anim + flag shooting
        if (cd == 60) {
            this.actor.setAttackName("ice_spike");
            this.actor.setShooting(true);
            this.actor.triggerAnim("attacking", this.actor.getAttackName());
        }

        // Tir principal
        if (cd == 30) {
            projectileUtil.shootProjectile(target, this.actor, this.iceSpikeDamage, new Vec3d(0, 0, 0));
            this.actor.getWorld().playSound(null, this.actor.getX(), this.actor.getY(), this.actor.getZ(),
                    ModSounds.DRAUGR_ICE_SPIKE,
                    this.actor.getSoundCategory(),
                    0.75F + this.actor.getRandom().nextFloat() * 0.2F,
                    0.8F + this.actor.getRandom().nextFloat() * 0.4F
            );
        }

        // Fin de cycle
        if (cd == 0) {
            castIceSpike = Math.random() < 0.65;  // choix du prochain sort
            this.actor.setCooldown(COOLDOWN_TICKS);
            this.actor.setShooting(false);
        }
    }

    // =========================
    //      FROSTBITE SPELL
    // =========================
    private void handleFrostbite(LivingEntity target, int cd) {
        // Début du cast frostbite
        if (cd == SHOOT_START) { // 60
            this.actor.setAttackName("frostbite");
            this.actor.setShooting(true);
            this.actor.triggerAnim("attacking", this.actor.getAttackName());
        }

        if (cd == SHOOT_START - 10)
            this.actor.getWorld().playSound(null, this.actor.getX(), this.actor.getY(), this.actor.getZ(),
                    ModSounds.DRAUGR_FROST_SPELL,
                    this.actor.getSoundCategory(),
                    0.75F + this.actor.getRandom().nextFloat() * 0.2F,
                    0.8F + this.actor.getRandom().nextFloat() * 0.4F
            );
        // Multi-tirs pendant la fenêtre
        if (cd <= SHOOT_START - 10 && cd >= SHOOT_END && cd % SHOOT_INTERVAL == 0) {
            projectileUtil.shootFrostBiteProjectile(target, this.actor, this.frostBiteDamage, new Vec3d(0, 0, 0));
        }

        // Fin de cycle
        if (cd == 0) {
            castIceSpike = Math.random() < 0.65;
            this.actor.setShooting(false);
            this.actor.setCooldown(COOLDOWN_TICKS);
        }
    }
}
