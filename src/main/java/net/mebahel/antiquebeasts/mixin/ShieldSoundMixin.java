package net.mebahel.antiquebeasts.mixin;

import net.mebahel.antiquebeasts.item.CustomShieldItem;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(LivingEntity.class)
public class ShieldSoundMixin {
    @Inject(method = "damage", at = @At("HEAD"))
    public void changeShieldSound(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity thisEntity = (LivingEntity) (Object) this;
        if (!thisEntity.world.isClient && thisEntity.isBlocking() && thisEntity.getActiveItem().getItem() instanceof CustomShieldItem) {
            Entity attacker = source.getAttacker();
            if (attacker != null) {
                Vec3d vec3d = attacker.getPos();
                Vec3d vec3d2 = thisEntity.getPos();
                double d = vec3d2.x - vec3d.x;
                double e = vec3d2.z - vec3d.z;
                if (!(d * d + e * e < 1.0E-7D)) {
                    float f = (float)(MathHelper.atan2(e, d) * 57.2957763671875D) - 90.0F;
                    float g = MathHelper.abs(MathHelper.wrapDegrees(thisEntity.getYaw()) - f);
                    if (g > 90F && g < 270.0F) {
                        thisEntity.world.playSound(null, thisEntity.getX(), thisEntity.getY(), thisEntity.getZ(), ModSounds.SHIELD_BLOCK, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    }
                }
            }
        }
    }
}


