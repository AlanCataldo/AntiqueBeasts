package net.mebahel.antiquebeasts.entity.ai.other;


import net.mebahel.antiquebeasts.entity.custom.other.DraugrOverlordEntity;
import net.mebahel.antiquebeasts.util.entity.MovementUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.VillagerEntity;

import java.util.EnumSet;

public class DraugrOverlordBlockGoal extends Goal {
    private final DraugrOverlordEntity mob;
    private final MovementUtil movementUtil;

    private static final int BLOCK_DURATION_TICKS = 120;
    private static final int STRAFE_DISTANCE = 2;

    // Probabilité par tick, dans la fenêtre, de partir sur un spin
    private static final float SPIN_TRIGGER_CHANCE = 0.04f; // 2%

    private int blockTicks = 0;

    public DraugrOverlordBlockGoal(DraugrOverlordEntity mob) {
        this.mob = mob;
        this.movementUtil = new MovementUtil(this.mob, 3);

        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        LivingEntity target = mob.getTarget();
        if (target == null || !target.isAlive()) return false;
        if (target instanceof VillagerEntity) return false;
        if (mob.isUsingPotion()) return false;

        return mob.wantsToBlock();
    }

    @Override
    public boolean shouldContinue() {
        LivingEntity target = mob.getTarget();
        if (target == null || !target.isAlive()) return false;
        if (target instanceof VillagerEntity) return false;
        if (mob.isUsingPotion()) return false;

        return blockTicks > 0 && !mob.isSpinning();
    }

    @Override
    public void start() {
        blockTicks = BLOCK_DURATION_TICKS;
        mob.setBlocking(true);
        mob.clearBlockRequest();

        mob.setSwinging(false);
        mob.setAttacking(false);

        mob.getNavigation().stop();
        mob.getMoveControl().strafeTo(0, 0);

        // L’anim de block est gérée côté animation controller avec isBlocking()
        mob.triggerAnim("attacking", "block_attack");
    }

    @Override
    public void stop() {
        mob.setBlocking(false);
        mob.clearBlockRequest();
        blockTicks = 0;

        mob.getNavigation().stop();
        mob.getMoveControl().strafeTo(0, 0);
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity target = mob.getTarget();
        if (target == null || !target.isAlive()) {
            blockTicks = 0;
            return;
        }

        blockTicks--;

        int elapsed = BLOCK_DURATION_TICKS - blockTicks; // temps passé depuis le début du block

        // --------------------------------------------------------------------
        // 🔥 1) CHANCE DE SPIN APRÈS 1 SECONDE (tous les 2 ticks après 20 ticks)
        // --------------------------------------------------------------------
        if (elapsed >= 30 && blockTicks > 0) { // on ne tente que si le block n'est pas fini
            if (elapsed % 5 == 0) {            // toutes les 2 ticks
                float roll = mob.getRandom().nextFloat();
                if (roll < 0.04f) {            // 5% chance
                    mob.requestSpin();

                    // On arrête proprement le block pour laisser SpinGoal prendre le relais
                    mob.setBlocking(false);
                    mob.clearBlockRequest();
                    mob.getNavigation().stop();
                    mob.getMoveControl().strafeTo(0, 0);
                    mob.setVelocity(0, mob.getVelocity().y, 0);

                    return;
                }
            }
        }

        // --------------------------------------------------------------------
        // 🔥 2) FIN DU BLOCK → 50% CHANCE DE SPIN si pas encore déclenché
        // --------------------------------------------------------------------
        if (blockTicks <= 0) {

            if (!mob.wantsSpin()) { // seulement si pas déjà déclenché avant
                float roll = mob.getRandom().nextFloat();
                if (roll < 0.5f) {  // 50% chance
                    mob.requestSpin();
                }
            }

            // Fin propre du block
            mob.setBlocking(false);
            mob.clearBlockRequest();
            mob.getNavigation().stop();
            mob.getMoveControl().strafeTo(0, 0);
            mob.setVelocity(0, mob.getVelocity().y, 0);

            return;
        }
        this.movementUtil.maintainRangedPosition(target);
    }
}
