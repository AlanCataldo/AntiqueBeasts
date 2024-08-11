package net.mebahel.antiquebeasts.entity.ai;

import net.mebahel.antiquebeasts.entity.custom.patrol.ModPatrolEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;

import java.util.EnumSet;
import java.util.List;

public class ModPatrolGoal extends Goal {
    private final ModPatrolEntity entity;
    private final double leaderSpeed;
    private final double followSpeed;
    private ModPatrolEntity assignedLeader;

    public ModPatrolGoal(ModPatrolEntity entity, double leaderSpeed, double followSpeed) {
        this.entity = entity;
        this.leaderSpeed = leaderSpeed;
        this.followSpeed = followSpeed;
        this.setControls(EnumSet.of(Control.MOVE));
        this.assignedLeader = null;
    }

    @Override
    public boolean canStart() {
        if (!this.entity.isPatrolling()) {
            return false;
        }

        // Vérifier si le leader assigné est toujours en vie, sinon désigner un nouveau leader
        if (this.assignedLeader == null || this.assignedLeader.isDead()) {
            this.assignedLeader = findNewLeader();
        }

        // Ne démarre que si un leader valide existe
        return this.assignedLeader != null && !this.assignedLeader.isDead();
    }

    @Override
    public void tick() {
        EntityNavigation entityNavigation = this.entity.getNavigation();

        if (entityNavigation.isIdle()) {
            if (this.entity.isPatrolLeader()) {
                // Le leader se déplace vers la cible de patrouille
                BlockPos leaderTargetPos = this.entity.getPatrolTarget();
                if (leaderTargetPos == null || !entityNavigation.startMovingTo(leaderTargetPos.getX(), leaderTargetPos.getY(), leaderTargetPos.getZ(), this.leaderSpeed)) {
                    this.wander();
                }
            } else if (this.assignedLeader != null && !this.assignedLeader.isDead()) {
                // Les autres membres suivent le leader
                Vec3d leaderPos = this.assignedLeader.getPos();
                Vec3d direction = this.entity.getPos().subtract(leaderPos).normalize();
                Vec3d offsetPosition = leaderPos.add(direction.multiply(2.0)); // Décalage de 2 blocs

                entityNavigation.startMovingTo(offsetPosition.x, offsetPosition.y, offsetPosition.z, this.followSpeed);
            } else {
                this.wander();
            }
        }
    }

    private ModPatrolEntity findNewLeader() {
        // Trouver tous les membres de la patrouille
        List<ModPatrolEntity> patrolMembers = this.entity.getWorld().getEntitiesByClass(ModPatrolEntity.class, this.entity.getBoundingBox().expand(32.0), e -> e.isPartOfSamePatrol(this.entity));

        // Vérifier s'il y a déjà un leader
        for (ModPatrolEntity member : patrolMembers) {
            if (!member.isDead() && member.isPatrolLeader()) {
                return member;
            }
        }

        // Si aucun autre leader n'existe, promouvoir le premier membre non mort qui n'est pas déjà un leader
        for (ModPatrolEntity member : patrolMembers) {
            if (!member.isDead() && !member.isPatrolLeader()) {
                member.setPatrolLeader(true);
                return member;
            }
        }

        return null;
    }

    private void wander() {
        Random random = this.entity.getRandom();
        BlockPos blockPos = this.entity.getWorld().getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, this.entity.getBlockPos().add(-8 + random.nextInt(16), 0, -8 + random.nextInt(16)));

        this.entity.getNavigation().startMovingTo(blockPos.getX(), blockPos.getY(), blockPos.getZ(), this.leaderSpeed);
    }
}
