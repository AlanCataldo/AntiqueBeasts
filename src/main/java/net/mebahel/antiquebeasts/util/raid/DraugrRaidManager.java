package net.mebahel.antiquebeasts.util.raid;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;

import net.mebahel.antiquebeasts.entity.custom.other.DraugrEntity;
import net.mebahel.antiquebeasts.item.soul_gem.DraugrAwakeningSoulGem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;

public class DraugrRaidManager {
    public static void registerEvents() {
        ServerLivingEntityEvents.AFTER_DEATH.register((LivingEntity entity, DamageSource source) -> {
            if (entity instanceof DraugrEntity && source.getAttacker() instanceof PlayerEntity player) {
                DraugrAwakeningSoulGem.onDraugrKilled(player, entity);
            }
        });
    }
}