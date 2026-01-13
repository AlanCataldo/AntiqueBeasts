package net.mebahel.antiquebeasts.mixin;

import net.mebahel.antiquebeasts.entity.ModEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VillagerEntity.class)
public abstract class VillagerAvoidAntiqueBeastsMixin extends MerchantEntity {
    protected VillagerAvoidAntiqueBeastsMixin(EntityType<? extends MerchantEntity> type, World world) {
        super(type, world);
    }

    @Inject(
            method = "<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/World;)V",
            at = @At("TAIL")
    )
    private void mebahel$addAvoidGoals(EntityType<? extends VillagerEntity> type, World world, CallbackInfo ci) {
        this.goalSelector.add(1, new FleeEntityGoal<>(
                (VillagerEntity) (Object) this,
                LivingEntity.class,
                10.0F,
                0.65D,
                0.75D,
                e -> isVillagerThreat(e.getType())
        ));
    }

    @Unique
    private static boolean isVillagerThreat(EntityType<?> type) {
        return type == ModEntities.DWEMER_CENTURION
                || type == ModEntities.DWEMER_SPIDER_GUARDIAN
                || type == ModEntities.DWEMER_SPIDER

                || type == ModEntities.DRAUGR_OVERLORD
                || type == ModEntities.DRAUGR
                || type == ModEntities.DRAUGR_ARCHER
                || type == ModEntities.DRAUGR_WIGHT
                || type == ModEntities.DRAUGR_SCOURGE
                || type == ModEntities.SKELETON_WARRIOR
                || type == ModEntities.SKELETON_WARRIOR_HEAD

                || type == ModEntities.HARPY
                || type == ModEntities.CENTAUR
                || type == ModEntities.CHIMERA
                || type == ModEntities.ELEPHANT_RIDER
                || type == ModEntities.SERVANT
                || type == ModEntities.MUMMY
                || type == ModEntities.MUMMY_BOSS
                || type == ModEntities.CAMELRY
                || type == ModEntities.AXEMAN
                || type == ModEntities.WADJET
                || type == ModEntities.CYCLOPS
                || type == ModEntities.FROST_CYCLOPS
                || type == ModEntities.HERSIR
                || type == ModEntities.HUSKARL
                || type == ModEntities.THROWING_AXEMAN
                || type == ModEntities.EINHERJAR
                || type == ModEntities.VALKYRIE
                || type == ModEntities.CHAMPION_HOPLITE
                || type == ModEntities.ELITE_HOPLITE
                || type == ModEntities.HERO_HOPLITE
                || type == ModEntities.HADES_CHOSEN
                || type == ModEntities.HADES_SHADE;
    }
}
