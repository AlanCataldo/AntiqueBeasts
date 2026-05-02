package net.mebahel.antiquebeasts.entity.ai.other;

import net.mebahel.antiquebeasts.entity.custom.other.DraugrEntity;
import net.mebahel.antiquebeasts.util.entity.MovementUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;

public class DraugrBlockGoal extends Goal {

    private final DraugrEntity mob;
    private final MovementUtil movementUtil;

    private static final int BLOCK_DURATION_TICKS = 120;
    private static final int STRAFE_DISTANCE = 2;

    private int blockTicks = 0;

    public DraugrBlockGoal(DraugrEntity mob) {
        this.mob = mob;
        this.movementUtil = new MovementUtil(this.mob, 4);

        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    private void log(String msg) {
        System.out.println("[DRAUGR_BLOCK_GOAL] " + msg);
    }

    @Override
    public boolean canStart() {
        LivingEntity target = mob.getTarget();
        if (target == null || !target.isAlive()) return false;

        // On ne bloque pas pendant la potion
        if (mob.isUsingPotion()) return false;

        // Le melee goal a demandé un block ?
        return mob.wantsToBlock();
    }

    @Override
    public boolean shouldContinue() {
        LivingEntity target = mob.getTarget();
        if (target == null || !target.isAlive()) return false;

        // Si pendant le block il commence un heal, on arrête
        if (mob.isUsingPotion()) return false;

        return blockTicks > 0;
    }

    @Override
    public void start() {
        blockTicks = BLOCK_DURATION_TICKS;
        mob.setBlocking(true);
        mob.clearBlockRequest();

        // 🔹 on s'assure qu'il n'est plus en swing d'attaque
        mob.setSwinging(false);
        mob.setAttacking(false);

        // 🔥 TRIGGER de l'animation de block
        mob.setAttackName("block_attack");                // nom de ton anim Geckolib
        mob.triggerAnim("attacking", mob.getAttackName()); // même controller que le melee

        // On efface tout mouvement / path en cours
        mob.getNavigation().stop();
        mob.getMoveControl().strafeTo(0, 0);

        log("BLOCK START");
    }

    @Override
    public void stop() {
        mob.setBlocking(false);
        mob.clearBlockRequest();
        blockTicks = 0;

        // On coupe net la navigation et le strafe
        mob.getNavigation().stop();
        mob.getMoveControl().strafeTo(0, 0);

        log("BLOCK END");
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

        // --- on décrémente d'abord ---
        blockTicks--;

        // Si la durée est finie, on ne bouge plus dans ce goal
        if (blockTicks <= 0) {
            mob.getNavigation().stop();
            mob.getMoveControl().strafeTo(0, 0);
            var vel = mob.getVelocity();
            mob.setVelocity(vel.x * 0.2, vel.y, vel.z * 0.2);
            return;
        }

        // Sinon, logique de mouvement de blocage
        this.movementUtil.maintainRangedPosition(target);
    }
}