package net.mebahel.antiquebeasts.entity.ai;

import net.mebahel.antiquebeasts.entity.custom.CyclopsEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import java.util.List;
import java.util.Objects;

public class CyclopsSocializeGoal extends Goal {
    private final CyclopsEntity cyclops;
    private CyclopsEntity mate;
    private int socializeTimer = 0;
    private final double SOCIALIZE_DISTANCE = 2.8;

    private final StatusEffect potionEffect;

    public CyclopsSocializeGoal(CyclopsEntity cyclops, StatusEffect effect) {
        this.cyclops = cyclops;
        this.potionEffect = effect;
    }

    public boolean canStart() {
        if (this.cyclops.hasStatusEffect(this.potionEffect)) {
            return false;
        }

        List<CyclopsEntity> cyclopses = this.cyclops.world.getEntitiesByClass(CyclopsEntity.class, this.cyclops.getBoundingBox().expand(20),
                cyclopsEntity -> cyclopsEntity != this.cyclops && !cyclopsEntity.hasStatusEffect(this.potionEffect) && this.cyclops.canSee(cyclopsEntity));

        for (CyclopsEntity otherCyclops : cyclopses) {
            if (otherCyclops != this.cyclops && !otherCyclops.hasStatusEffect(this.potionEffect) && this.cyclops.canSee(otherCyclops)) {
                this.mate = otherCyclops;
                return true;
            }
        }
        return false;
    }

    public void start() {
        this.socializeTimer = 0;
        Objects.requireNonNull(this.cyclops.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(0.32f);
    }

    public void stop() {
        this.socializeTimer = 0;
        this.mate = null;
        Objects.requireNonNull(this.cyclops.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(0.72f);
    }

    public boolean shouldContinue() {
        return this.socializeTimer > 0 || (this.mate != null && this.cyclops.squaredDistanceTo(this.mate) > SOCIALIZE_DISTANCE);
    }

    public void tick() {
        if (this.cyclops.hasStatusEffect(this.potionEffect)) {
            this.stop();
        }
        if (this.mate != null) {

            double dx = this.mate.getX() - this.cyclops.getX();
            double dy = this.mate.getY() - this.cyclops.getY();
            double dz = this.mate.getZ() - this.cyclops.getZ();

            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
            dx /= distance;
            dy /= distance;
            dz /= distance;

            double targetX = this.mate.getX() - dx * SOCIALIZE_DISTANCE;
            double targetY = this.mate.getY() - dy * SOCIALIZE_DISTANCE;
            double targetZ = this.mate.getZ() - dz * SOCIALIZE_DISTANCE;

            this.cyclops.getLookControl().lookAt(targetX, targetY + 5f, targetZ);
            this.cyclops.getNavigation().startMovingTo(targetX, targetY, targetZ, 0.72f);

            if (this.cyclops.squaredDistanceTo(this.mate) > 20) {
                this.socializeTimer = 85;
            } else {
                this.socializeTimer--;
                if (this.socializeTimer == 0) {
                    this.cyclops.addStatusEffect(new StatusEffectInstance(this.potionEffect, 2400, 1));
                    this.mate.addStatusEffect(new StatusEffectInstance(this.potionEffect, 2400, 1));
                }
            }
        }
    }
}
