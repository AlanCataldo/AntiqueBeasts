package net.mebahel.antiquebeasts.mixin;

import net.mebahel.antiquebeasts.util.ProjectileDataAccessor;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PersistentProjectileEntity.class)
public abstract class PersistentProjectileMixin implements ProjectileDataAccessor {
    private static final TrackedData<Boolean> DRAUGR_BOW_SHOT =
            DataTracker.registerData(PersistentProjectileEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private static final TrackedData<Boolean> FROST_BOW_SHOT =
            DataTracker.registerData(PersistentProjectileEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void initTracker(CallbackInfo ci) {
        ((PersistentProjectileEntity) (Object) this).getDataTracker().startTracking(DRAUGR_BOW_SHOT, false);
        ((PersistentProjectileEntity) (Object) this).getDataTracker().startTracking(FROST_BOW_SHOT, false);
    }

    @Unique
    @Override
    public void setDraugrBowShot(boolean value) {
        ((PersistentProjectileEntity) (Object) this).getDataTracker().set(DRAUGR_BOW_SHOT, value);
    }

    @Unique
    @Override
    public boolean isDraugrBowShot() {
        return ((PersistentProjectileEntity) (Object) this).getDataTracker().get(DRAUGR_BOW_SHOT);
    }

    @Unique
    @Override
    public void setFrostBowShot(boolean value) {
        ((PersistentProjectileEntity) (Object) this).getDataTracker().set(FROST_BOW_SHOT, value);
    }

    @Unique
    @Override
    public boolean isFrostBowShot() {
        return ((PersistentProjectileEntity) (Object) this).getDataTracker().get(FROST_BOW_SHOT);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
    private void writeCustomNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putBoolean("DraugrBowShot", isDraugrBowShot());
        nbt.putBoolean("FrostBowShot", isDraugrBowShot());
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    private void readCustomNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("DraugrBowShot")) {
            setDraugrBowShot(nbt.getBoolean("DraugrBowShot"));

        }
        if (nbt.contains("FrostBowShot")) {
            setDraugrBowShot(nbt.getBoolean("FrostBowShot"));
        }
    }
}
