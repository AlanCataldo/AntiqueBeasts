package net.mebahel.antiquebeasts.entity.ai.mummy_boss;

import net.mebahel.antiquebeasts.entity.custom.egyptian.EgyptianCaravanEntity;
import net.mebahel.antiquebeasts.entity.custom.egyptian.EgyptianEntity;
import net.mebahel.antiquebeasts.entity.custom.egyptian.MummyBossEntity;
import net.mebahel.antiquebeasts.entity.custom.patrol.ModPatrolEntity;
import net.mebahel.antiquebeasts.item.CustomShieldItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;
import java.util.List;

public class MummyBossMeleeAttackGoal extends Goal {

    protected final MummyBossEntity mob;
    private final double speed;
    private final double attackRange;
    private final int attackDistance;
    private final int attackMoment;
    private static final int MAX_COOLDOWN = 21;

    public int cooldown;
    private long lastUpdateTime;
    double rand;
    public MummyBossMeleeAttackGoal(MummyBossEntity mob, double speed, double attackRange, int attackDistance, int attackMoment) {
        this.mob = mob;
        this.speed = speed;
        this.attackRange = attackRange;
        this.attackDistance = attackDistance;
        this.attackMoment = attackMoment;
        this.cooldown = MAX_COOLDOWN + 8;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    public boolean canStart() {
        long l = this.mob.getWorld().getTime();
        if (this.mob.getSpawn())
            return false;
        if (l - this.lastUpdateTime < 20L) {
            return false;
        } else {
            this.lastUpdateTime = l;
            LivingEntity livingEntity = this.mob.getTarget();
            if (livingEntity == null) {
                return false;
            } else if (!livingEntity.isAlive()) {
                return false;
            } else {
                Path path = this.mob.getNavigation().findPathTo(livingEntity, 0);
                if (path != null) {
                    return true;
                } else {
                    return this.getSquaredMaxAttackDistance() >= this.mob.squaredDistanceTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
                }
            }
        }
    }

    public boolean shouldContinue() {
        LivingEntity livingEntity = this.mob.getTarget();

        if (livingEntity instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity) livingEntity;
            if (playerEntity.isCreative() || playerEntity.isSpectator()) {
                return false;
            }
        }
        return livingEntity != null && livingEntity.isAlive() || this.mob.getSpawn() ||
                this.mob.getSpawnCooldown() < 40 || this.mob.getCooldown() < 40 || !this.mob.inTransitionPhase;
    }

    public void start() {
        this.mob.setAttacking(true);

        ModPatrolEntity patrolEntity = this.mob;
        LivingEntity target = this.mob.getTarget();

        List<ModPatrolEntity> patrolMembers = patrolEntity.getWorld().getEntitiesByClass(ModPatrolEntity.class, patrolEntity.getBoundingBox().expand(32.0), e -> e.isPartOfSamePatrol(patrolEntity)
                && e.isPatrolling());
        for (ModPatrolEntity member : patrolMembers) {
            if (!(member instanceof EgyptianCaravanEntity) && member.isPatrolling()) {
                member.setPatrolling(false);
                member.setTarget(target);
            }
        }
    }
    @Override
    public void stop() {
        this.mob.setAttacking(false);
        this.mob.setSwinging(false);

        ModPatrolEntity patrolEntity = this.mob;
        if (patrolEntity.wasInitiallyInPatrol()) {
            patrolEntity.checkAndResumePatrolling();
        }
    }
    public boolean shouldRunEveryTick() {
        return true;
    }
    public void tick() {
        LivingEntity livingEntity = this.mob.getTarget();
        if (livingEntity != null && livingEntity.isAlive()) {
            this.mob.getLookControl().lookAt(livingEntity, 15.0F, 15.0F);
            this.attack(livingEntity);
        } else {
            this.stop();
        }
    }
    protected void attack(LivingEntity target) {
        double squaredDistance = this.mob.squaredDistanceTo(target.getX(), target.getY(), target.getZ());
        double d = this.getSquaredMaxAttackDistance();
        this.cooldown = Math.max(this.cooldown - 1, 0);

        if (this.mob.secondPhase) {
            this.mob.getNavigation().startMovingTo(target, this.speed / 1.5);
        } else {
            this.mob.getNavigation().startMovingTo(target, this.speed);
        }

        if (this.cooldown == 0) {
            this.cooldown = MAX_COOLDOWN + 2;
            this.mob.setSwinging(false);
        } else if (squaredDistance <= d && this.cooldown == 20) {
            this.mob.setSwinging(true);
        } else if (squaredDistance <= d + 1 && this.cooldown == 10 && this.mob.isSwinging()) {
            if (this.mob.secondPhase || this.mob.thirdPhase) {
                performAreaAttack();
            } else {
                this.mob.tryAttack(target);
            }

        }
    }
    private void performAreaAttack() {
        double attackRange = 4.0; // Define the range of the area attack
        double attackAngle = Math.toRadians(90); // Define the angle of the attack cone (90 degrees)

        // Get the boss's facing direction
        Vec3d bossPos = this.mob.getPos();
        Vec3d bossLookVec = this.mob.getRotationVec(1.0F);

        // Define a box in front of the Mummy Boss for the area attack
        Box attackBox = this.mob.getBoundingBox().expand(attackRange, 1.0, attackRange);

        // Get all entities within the box
        List<LivingEntity> entities = this.mob.getWorld().getEntitiesByClass(LivingEntity.class, attackBox, entity -> entity != this.mob);

        for (LivingEntity entity : entities) {
            // Check if the entity is within the attack cone (angle check)
            Vec3d toEntityVec = entity.getPos().subtract(bossPos).normalize();
            double angle = bossLookVec.dotProduct(toEntityVec);

            // If the entity is in front of the Mummy Boss and within the attack angle, apply damage
            if (angle > Math.cos(attackAngle / 2)) {
                breakShield(entity);
                entity.damage(this.mob.getWorld().getDamageSources().mobAttack(this.mob), 10.0F); // Adjust damage as necessary
            }
        }
    }
    private void breakShield(LivingEntity target) {
        if (target instanceof PlayerEntity player) {
            if (player.isBlocking()) {
                ItemStack activeItem = player.getActiveItem();

                // Vérifier que l'objet utilisé est bien un bouclier
                if (activeItem.getItem() instanceof ShieldItem) {
                    // Retirer 1 point de durabilité au bouclier
                    activeItem.damage(1, player, (p) -> p.sendToolBreakStatus(player.getActiveHand()));

                    // Si le bouclier n'est pas un bouclier personnalisé, désactiver le blocage
                    if (!(activeItem.getItem() instanceof CustomShieldItem)) {
                        player.disableShield(true);
                    }
                }
            }
        }
    }
    protected double getSquaredMaxAttackDistance() {
        return 10;
    }
}