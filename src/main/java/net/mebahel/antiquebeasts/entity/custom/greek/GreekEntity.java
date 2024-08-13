package net.mebahel.antiquebeasts.entity.custom.greek;

import net.mebahel.antiquebeasts.entity.custom.patrol.ModPatrolEntity;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
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
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static java.lang.Math.random;

public class GreekEntity extends ModPatrolEntity {
    protected GreekEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }
    double rand;

    public static final TrackedData<Integer> DATA_ID_TYPE_VARIANT =
            DataTracker.registerData(GreekEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public static final TrackedData<String> ATTACK_NAME = DataTracker.registerData(GreekEntity.class,
            TrackedDataHandlerRegistry.STRING);

    public static final TrackedData<Boolean> SHOOTING = DataTracker.registerData(GreekEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);

    public static final TrackedData<Float> COOLDOWN = DataTracker.registerData(GreekEntity.class,
            TrackedDataHandlerRegistry.FLOAT);

    public static final TrackedData<Boolean> SWINGING = DataTracker.registerData(GreekEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);
    public void setShooting(boolean shooting) {
        this.dataTracker.set(SHOOTING, shooting);
    }
    public boolean isShooting() {
        return this.dataTracker.get(SHOOTING);
    }
    public void setSwinging(boolean swinging) { this.dataTracker.set(SWINGING, swinging); }
    public boolean isSwinging() { return this.dataTracker.get(SWINGING); }
    public void setAttackName(String attackName) { this.dataTracker.set(ATTACK_NAME, attackName); }
    public String getAttackName() { return this.dataTracker.get(ATTACK_NAME); }
    public float getCooldown() { return this.dataTracker.get(COOLDOWN);}
    public void setCooldown(float cooldown) {
        this.dataTracker.set(COOLDOWN, cooldown);
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        rand = random();
        if (rand < 0.5)
            return ModSounds.HOPLITE_HURT1;
        else
            return ModSounds.HOPLITE_HURT2;
    }
    @Override
    protected SoundEvent getDeathSound() {
        rand = random();
        if (rand < 0.5)
            return ModSounds.HOPLITE_DEATH1;
        else
            return ModSounds.HOPLITE_DEATH2;
    }
    @Override
    public int getMinAmbientSoundDelay() {
        return 180;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        LivingEntity target = this.getTarget();
        rand = random();
        if (target != null) {
            if (rand < 0.5)
                return ModSounds.HOPLITE_ATTACKING1;
            else
                return ModSounds.HOPLITE_ATTACKING2;
        } else {
            if (rand < 0.3)
                return ModSounds.HOPLITE_AMBIENT1;
            else if (rand > 0.3 && rand < 0.6)
                return ModSounds.HOPLITE_AMBIENT2;
            else
                return ModSounds.HOPLITE_AMBIENT3;
        }
    }
    @Override
    public void playAmbientSound() {
        SoundEvent soundEvent = this.getAmbientSound();
        if (soundEvent != null) {
            this.playSound(soundEvent, 0.35f, 0.93f);
        }
    }
    public int getTypeVariant() {
        return this.dataTracker.get(DATA_ID_TYPE_VARIANT);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("Variant", this.getTypeVariant());
        nbt.putString("PatrolUUID", this.getPatrolId().toString());
        nbt.putBoolean("PatrolLeader", this.isPatrolLeader());
        nbt.putBoolean("Patrolling", this.isPatrolling());
        nbt.putBoolean("WasPatrolling", this.wasInitiallyInPatrol());

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

        if (nbt.contains("PatrolTarget")) {
            this.setPatrolTarget(NbtHelper.toBlockPos(nbt.getCompound("PatrolTarget")));
        }
    }
}
