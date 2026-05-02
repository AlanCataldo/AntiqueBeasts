package net.mebahel.antiquebeasts.entity.ai;


import net.mebahel.antiquebeasts.entity.ai.util.ProjectileUtil;
import net.mebahel.antiquebeasts.entity.custom.other.FlameAtronachEntity;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.mebahel.antiquebeasts.util.entity.MovementUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;

import java.util.Objects;

public class FlameAtronachShootingGoal extends Goal {
    private final ProjectileUtil projectileUtil;
    private final FlameAtronachEntity actor;
    private final MovementUtil movementUtil;
    private final int STRAFE_DISTANCE = 2;
    private String chosenAnim = null;
    private int ATTACK_COOLDOWN = 20;
    private float damage;

    public FlameAtronachShootingGoal(FlameAtronachEntity actor, float damage) {
        this.actor = actor;
        this.damage = damage;
        this.movementUtil = new MovementUtil(this.actor, 6);
        this.projectileUtil = new ProjectileUtil();
    }
    private boolean isValidTarget(LivingEntity target) {
        if (target == null || !target.isAlive()) return false;

        // Ne jamais se target soi-même
        if (target == this.actor) return false;

        // Si c'est un summon: ne jamais target l'owner
        if (this.actor.isSummoned() && this.actor.isOwner(target)) return false;

        // Joueur créatif / spectateur => jamais
        if (target instanceof PlayerEntity p && (p.isCreative() || p.isSpectator())) return false;

        return true;
    }


    @Override
    public boolean canStart() {
        return isValidTarget(this.actor.getTarget());
    }

    @Override
    public boolean shouldContinue() {
        return isValidTarget(this.actor.getTarget());
    }

    @Override
    public void start() {
        this.actor.setCooldown(61);
        this.actor.setShooting(false);
    }

    @Override
    public void stop() {
        this.actor.setCooldown(61);
        this.actor.setShooting(false);
        this.actor.getNavigation().stop();
        this.actor.getMoveControl().strafeTo(0, 0);
        this.actor.setVelocity(0, actor.getVelocity().y, 0);
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }


    @Override
    public void tick() {
        LivingEntity target = this.actor.getTarget();
        if (target == null || !target.isAlive()) {
            this.stop();
            return;
        }

        this.movementUtil.maintainRangedPosition(target);

        if (this.actor.getVisibilityCache().canSee(target)) {
            this.actor.setCooldown(Math.max(this.actor.getCooldown() - 1, 0));
            
            if (this.actor.getCooldown() == 40) {
                this.actor.setShooting(true);

                this.chosenAnim = this.pickRandomAttackAnim();
                this.actor.triggerAnim("main", chosenAnim);
            }

            if (this.actor.getCooldown() == 0) {
                this.actor.setCooldown(61);
                this.actor.setShooting(false);
            }

            if (Objects.equals(chosenAnim, ATTACK_ANIMS[3]))
                this.ATTACK_COOLDOWN = 12;
            else if (Objects.equals(chosenAnim, ATTACK_ANIMS[2]))
                this.ATTACK_COOLDOWN = 18;
            else
                this.ATTACK_COOLDOWN = 16;

            if (this.actor.getCooldown() ==  this.ATTACK_COOLDOWN + 6) {
                playShootingSound(ModSounds.FLAME_ATRONACH_ATTACK_1);
            } else if (this.actor.getCooldown() ==  this.ATTACK_COOLDOWN) {
                projectileUtil.shootFirebolt(target, this.actor, damage);
                playShootingSound(ModSounds.FIREBOLT_FIRE);
            } else if (this.actor.getCooldown() == 0) {
                this.actor.setCooldown(61);
                this.actor.setShooting(false);

            } else if (this.actor.getCooldown() > 25 && this.actor.getCooldown() < 61) {
                this.actor.setShooting(false);
            }

        } else {
            this.actor.setShooting(false);
            this.actor.setCooldown(61);
        }
    }

    private static final String[] ATTACK_ANIMS = {
            "attack1", "attack2", "attackrotate", "attackdual"
    };

    private String pickRandomAttackAnim() {
        return ATTACK_ANIMS[this.actor.getRandom().nextInt(ATTACK_ANIMS.length)];
    }

    private void playShootingSound(SoundEvent sound) {
        this.actor.getWorld().playSound(
                null,
                this.actor.getX(),
                this.actor.getY(),
                this.actor.getZ(),
                sound,
                this.actor.getSoundCategory(),
                0.85F + this.actor.getRandom().nextFloat() * 0.2F,
                0.8F + this.actor.getRandom().nextFloat() * 0.4F
        );
    }
}
