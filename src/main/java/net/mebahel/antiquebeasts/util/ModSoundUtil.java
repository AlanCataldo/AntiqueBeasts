package net.mebahel.antiquebeasts.util;

import net.mebahel.antiquebeasts.sound.ModSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;

public class ModSoundUtil {
    public static void InfantryPlaySound(SpawnReason spawnReason, LivingEntity entity) {
        if (spawnReason == SpawnReason.SPAWNER && !entity.getWorld().isClient) {
            entity.getWorld().playSound(
                    null,
                    entity.getX(), entity.getY(), entity.getZ(),
                    ModSounds.MILITARY_CREATE,
                    entity.getSoundCategory(),
                    0.45f,
                    1f
            );
        }
    }
}
