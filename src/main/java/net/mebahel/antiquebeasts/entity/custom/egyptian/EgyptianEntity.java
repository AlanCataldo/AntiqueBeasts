package net.mebahel.antiquebeasts.entity.custom.egyptian;

import net.mebahel.antiquebeasts.entity.custom.patrol.ModPatrolEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class EgyptianEntity extends ModPatrolEntity {

    public double rand;
    public boolean shouldDespawn;

    public SpawnReason spawnReason;
    protected EgyptianEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }
    public static final TrackedData<Boolean> SWINGING = DataTracker.registerData(EgyptianEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);

    public static final TrackedData<String> ATTACK_NAME = DataTracker.registerData(EgyptianEntity.class,
            TrackedDataHandlerRegistry.STRING);
    public static final TrackedData<Boolean> IS_IN_CARAVAN = DataTracker.registerData(EgyptianEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);

    public static final TrackedData<Integer> DATA_ID_TYPE_VARIANT =
            DataTracker.registerData(EgyptianEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public void setAttackName(String attackName) {
        this.dataTracker.set(ATTACK_NAME, attackName);
    }
    public String getAttackName() {
        return this.dataTracker.get(ATTACK_NAME);
    }
    public void setSwinging(boolean swinging) {
        this.dataTracker.set(SWINGING, swinging);
    }
    public boolean isSwinging() {
        return this.dataTracker.get(SWINGING);
    }
    private LivingEntity leadEntity;
    public void setInCaravan(boolean swinging) {
        this.dataTracker.set(IS_IN_CARAVAN, swinging);
    }
    public boolean isInCaravan() {
        return this.dataTracker.get(IS_IN_CARAVAN);
    }
    public LivingEntity getLeadEntity() {
        return leadEntity;
    }

    public void setLeadEntity(LivingEntity leadEntity) {
        this.leadEntity = leadEntity;
    }
    @Override
    public int getMinAmbientSoundDelay() {
        return 240;
    }
    @Override
    public boolean damage(DamageSource source, float amount) {
        return super.damage(source, amount);
    }
    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }
    @Override
    public void playAmbientSound() {
        SoundEvent soundEvent = this.getAmbientSound();
        if (soundEvent != null) {
            this.playSound(soundEvent, 0.35f, 1f);
        }
    }
    public int getTypeVariant() {
        return this.dataTracker.get(DATA_ID_TYPE_VARIANT);
    }
    public boolean shouldDespawnInPeaceful() {
        return this.getWorld().getDifficulty() == Difficulty.PEACEFUL;
    }
    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("Variant", this.getTypeVariant());
        nbt.putString("PatrolUUID", this.getPatrolId().toString());
        nbt.putBoolean("PatrolLeader", this.isPatrolLeader());
        nbt.putBoolean("Patrolling", this.isPatrolling());
        nbt.putBoolean("WasPatrolling", this.wasInitiallyInPatrol());
        nbt.putBoolean("IsInCaravan", this.isInCaravan());

        if (this.getPatrolTarget() != null) {
            nbt.put("PatrolTarget", NbtHelper.fromBlockPos(this.getPatrolTarget()));
        }
    }
    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.dataTracker.set(DATA_ID_TYPE_VARIANT, nbt.getInt("Variant"));
        this.dataTracker.set(PATROL_UUID, nbt.getString("PatrolUUID"));
        this.setPatrolLeader(nbt.getBoolean("PatrolLeader"));
        this.setPatrolling(nbt.getBoolean("Patrolling"));
        this.setWasInitiallyInPatrol(nbt.getBoolean("WasPatrolling"));
        this.dataTracker.set(IS_IN_CARAVAN, nbt.getBoolean("IsInCaravan"));

        if (nbt.contains("PatrolTarget")) {
            this.setPatrolTarget(NbtHelper.toBlockPos(nbt.getCompound("PatrolTarget")));
        }
    }
    public void tick() {
        super.tick();
        if (shouldDespawnInPeaceful()) {
            remove(RemovalReason.DISCARDED);
        }
    }
}
