package net.mebahel.antiquebeasts.entity.custom.patrol;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class ModPatrolEntity extends AnimalEntity {
    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }

    public boolean shouldDespawnInPeaceful() {
        return this.getWorld().getDifficulty() == Difficulty.PEACEFUL;
    }

    @Nullable
    private BlockPos patrolTarget;
    private boolean patrolLeader;
    private boolean patrolling;

    protected ModPatrolEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    public static final TrackedData<String> PATROL_UUID = DataTracker.registerData(ModPatrolEntity.class,
            TrackedDataHandlerRegistry.STRING);
    

    public void setPatrolTarget(BlockPos targetPos) {
        this.patrolTarget = targetPos;
        this.patrolling = true;
    }

    public BlockPos getPatrolTarget() {
        return this.patrolTarget;
    }

    public void setPatrolLeader(boolean patrolLeader) {
        this.patrolLeader = patrolLeader;
        this.patrolling = true;
    }

    public boolean isPatrolLeader() {
        return this.patrolLeader;
    }

    public boolean isPatrolling() {
        return this.patrolling && this.getTarget() == null;
    }

    private boolean wasInitiallyInPatrol = false;

    public boolean wasInitiallyInPatrol() {
        return this.wasInitiallyInPatrol;
    }

    public void setWasInitiallyInPatrol(boolean wasInPatrol) {
        this.wasInitiallyInPatrol = wasInPatrol;
    }

    public boolean isAnyMemberAttacking() {
        List<ModPatrolEntity> patrolMembers = this.getWorld().getEntitiesByClass(ModPatrolEntity.class, this.getBoundingBox().expand(16.0), e -> e.isPartOf(this));
        for (ModPatrolEntity member : patrolMembers) {
            if (member.getTarget() != null) {
                return true;
            }
        }
        return false;
    }

    public void setPatrolling(boolean b) {
        this.patrolling = b;
    }

    public void checkAndResumePatrolling() {
        if (!this.isAnyMemberAttacking()) {
            this.setPatrolling(true);
        }
    }

    public String getPatrolId() {
        return this.dataTracker.get(PATROL_UUID);
    }

    public void setPatrolId(String patrolId) {
        this.dataTracker.set(PATROL_UUID, patrolId);
    }

    public boolean isPartOfSamePatrol(ModPatrolEntity other) {
        return Objects.equals(this.dataTracker.get(PATROL_UUID), other.getPatrolId());
    }
}
