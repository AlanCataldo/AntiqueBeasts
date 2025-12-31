package net.mebahel.antiquebeasts.entity.ai.other;

import net.mebahel.antiquebeasts.entity.custom.other.DraugrEntity;
import net.mebahel.antiquebeasts.entity.custom.other.DraugrOverlordEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;
import java.util.Objects;

public class DraugrOverlordSpinGoal extends Goal {

    private final DraugrOverlordEntity mob;

    private static final int SPIN_TOTAL_TICKS = 60;    // 3s à 20 tps
    private static final int SPIN_DASH_TICK   = 15;    // 0.75s après le début
    private static final int DASH_END_TICK    = 35;    // jusqu'à 1.75s ≈ durée du rush
    private static final double DASH_POWER    = 2.8D;  // puissance du premier dash

    // Ticks d'AOE (en ticks depuis le début)
    private static final int AOE_T1 = 18;  // 0.9s
    private static final int AOE_T2 = 22;  // 1.1s
    private static final int AOE_T3 = 28;  // 1.4s
    private static final int AOE_T4 = 34;  // 1.7s
    private static final int AOE_T5 = 38;  // 1.9s

    private int spinTicks = 0;
    private final double originalSpeed = 0.3D;
    private boolean hasDashed = false;

    // direction du rush, pour le maintenir plusieurs ticks
    private Vec3d dashDirection = Vec3d.ZERO;

    public DraugrOverlordSpinGoal(DraugrOverlordEntity mob) {
        this.mob = mob;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        if (!mob.wantsSpin()) return false;
        if (mob.isSpinning()) return false;

        LivingEntity target = mob.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public boolean shouldContinue() {
        LivingEntity target = mob.getTarget();
        if (target == null || !target.isAlive()) return false;

        return spinTicks > 0 && spinTicks < SPIN_TOTAL_TICKS && mob.isSpinning();
    }

    @Override
    public void start() {
        spinTicks = 0;
        hasDashed = false;
        dashDirection = Vec3d.ZERO;

        mob.setAttacking(true);
        mob.setSwinging(false);
        mob.setBlocking(false);
        mob.clearBlockRequest();

        mob.setSpinning(true);
        mob.clearSpinRequest(); // on consomme la demande

        // Immobilise le boss pour le wind-up (le dash utilisera addVelocity)
        Objects.requireNonNull(mob.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED))
                .setBaseValue(0.0D);
        mob.getNavigation().stop();
        mob.getMoveControl().strafeTo(0, 0);

        // Déclenche l’anim de spin côté Geckolib
        mob.triggerAnim("attacking", "sl_block_spin2");
    }

    @Override
    public void stop() {
        mob.setSpinning(false);
        mob.setAttacking(false);
        mob.setSwinging(false);

        Objects.requireNonNull(mob.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED))
                .setBaseValue(originalSpeed);

        mob.getNavigation().stop();
        mob.getMoveControl().strafeTo(0, 0);
        mob.setVelocity(0, mob.getVelocity().y, 0);

        spinTicks = 0;
        hasDashed = false;
        dashDirection = Vec3d.ZERO;
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity target = mob.getTarget();
        if (target == null || !target.isAlive()) {
            stop();
            return;
        }

        spinTicks++;

        // Phase 1 : wind-up, il reste sur place et regarde la cible
        if (spinTicks < SPIN_DASH_TICK) {
            mob.getLookControl().lookAt(target, 30.0F, 30.0F);
            mob.getNavigation().stop();
            mob.setVelocity(0, mob.getVelocity().y, 0);
        }
        // Tick exact où on lance la direction + gros dash initial
        else if (spinTicks == SPIN_DASH_TICK && !hasDashed) {
            dashTowardLastKnownPosition(target, DASH_POWER);
            hasDashed = true;
        }
        // Phase 2 : rush prolongé, il continue de se propulser tout droit
        else {
            mob.getNavigation().stop();

            if (hasDashed && spinTicks <= DASH_END_TICK && !dashDirection.equals(Vec3d.ZERO)) {
                // petite poussée continue pour allonger le rush
                Vec3d extra = dashDirection.multiply(0.25); // à ajuster
                mob.addVelocity(extra.x, 0.0, extra.z);
                mob.velocityDirty = true;
            }
        }

        // AOE aux timings voulus
        if (spinTicks == AOE_T1 || spinTicks == AOE_T2 || spinTicks == AOE_T3
                || spinTicks == AOE_T4 || spinTicks == AOE_T5) {
            performSpinAoe(4.0, 0.8F); // rayon 3 blocs, 80% des dégâts de base
        }

        if (spinTicks >= SPIN_TOTAL_TICKS) {
            stop();
        }
    }

    private void dashTowardLastKnownPosition(LivingEntity target, double power) {
        // Direction basée sur la position de la cible à ce tick précis
        Vec3d dir = target.getPos().subtract(mob.getPos()).normalize();
        dashDirection = dir; // on mémorise pour prolonger le rush

        float yaw = (float) (MathHelper.atan2(dir.z, dir.x) * (180.0 / Math.PI)) - 90.0F;
        mob.setYaw(yaw);
        mob.setHeadYaw(yaw);
        mob.setBodyYaw(yaw);
        mob.prevYaw = yaw;
        mob.prevBodyYaw = yaw;

        // Dash tout droit dans cette direction
        Vec3d dash = new Vec3d(
                -MathHelper.sin(mob.getYaw() * ((float) Math.PI / 180F)) * power,
                0.05,
                MathHelper.cos(mob.getYaw() * ((float) Math.PI / 180F)) * power
        );

        mob.addVelocity(dash.x, dash.y, dash.z);
        mob.velocityDirty = true;
    }

    // AOE autour du boss, dégâts réduits, sans toucher les autres Draugr
    private void performSpinAoe(double radius, float damageMultiplier) {
        var world = mob.getWorld();
        if (world.isClient()) return;

        double baseDamage = mob.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        float aoeDamage = (float) (baseDamage * damageMultiplier);

        world.getOtherEntities(mob, mob.getBoundingBox().expand(radius), e ->
                e instanceof LivingEntity le
                        && le.isAlive()
                        && le != mob
                        && !(e instanceof DraugrEntity) // pas de friendly fire
        ).forEach(entity -> {
            LivingEntity nearby = (LivingEntity) entity;

            nearby.damage(world.getDamageSources().mobAttack(mob), aoeDamage);

            // petit knockback radial
            Vec3d push = nearby.getPos().subtract(mob.getPos()).normalize().multiply(0.35);
            nearby.addVelocity(push.x, 0.15, push.z);

            // feedback visuel
            world.addParticle(ParticleTypes.SWEEP_ATTACK,
                    nearby.getX(), nearby.getBodyY(0.5), nearby.getZ(),
                    0, 0.05, 0);
        });
    }
}
