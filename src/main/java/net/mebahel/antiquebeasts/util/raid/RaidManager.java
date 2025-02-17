package net.mebahel.antiquebeasts.util.raid;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.mebahel.antiquebeasts.entity.custom.other.DraugrEntity;
import net.mebahel.antiquebeasts.item.ThurisazRune;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;

public class RaidManager {
    public static void registerEvents() {
        ServerLivingEntityEvents.AFTER_DEATH.register((LivingEntity entity, DamageSource source) -> {
            if (entity instanceof DraugrEntity && source.getAttacker() instanceof PlayerEntity player) {
                ThurisazRune.onDraugrKilled(player, entity);
            }

            /*if (entity instanceof DraugrEntity) {
                if (source.getAttacker() instanceof PlayerEntity player) {
                    DraugrKillTracker.incrementKillCount(player, entity);
                }
            }*/
        });
    }
}