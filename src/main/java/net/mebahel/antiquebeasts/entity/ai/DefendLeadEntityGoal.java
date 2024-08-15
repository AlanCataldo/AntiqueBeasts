package net.mebahel.antiquebeasts.entity.ai;

import net.mebahel.antiquebeasts.entity.custom.egyptian.EgyptianEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;

import java.util.EnumSet;

public class DefendLeadEntityGoal extends Goal {
    private final EgyptianEntity defender;
    private LivingEntity lastAttacker;

    public DefendLeadEntityGoal(EgyptianEntity defender) {
        this.defender = defender;
        this.setControls(EnumSet.of(Control.TARGET));
    }

    @Override
    public boolean canStart() {
        if (this.defender.getLeadEntity() != null && this.defender.isInCaravan()) {
            LivingEntity leadEntity = this.defender.getLeadEntity();
            DamageSource lastDamageSource = leadEntity.getRecentDamageSource();
            if (lastDamageSource != null && lastDamageSource.getAttacker() instanceof LivingEntity) {
                LivingEntity attacker = (LivingEntity) lastDamageSource.getAttacker();
                if (attacker instanceof PlayerEntity && ((PlayerEntity) attacker).isCreative()) {
                    return false;
                }
                lastAttacker = attacker;
                return true;
            }
        }
        return false;
    }

    @Override
    public void start() {
        this.defender.setTarget(this.lastAttacker);
        super.start();
    }

    public boolean shouldContinue() {
        LivingEntity livingEntity = this.defender.getTarget();

        if (livingEntity instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity) livingEntity;
            if (playerEntity.isCreative() || playerEntity.isSpectator()) {
                return false;
            }
        }
        return livingEntity != null && livingEntity.isAlive() && this.defender.getTarget() != null;
    }
}
