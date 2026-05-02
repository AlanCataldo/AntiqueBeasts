package net.mebahel.antiquebeasts.mixin;

import net.mebahel.antiquebeasts.entity.custom.other.SkeletonWarriorEntity;
import net.mebahel.antiquebeasts.entity.custom.other.SkeletonWarriorHeadEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.passive.WolfEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WolfEntity.class)
public abstract class SkeletonWarriorWolfMixin {

    @Inject(method = "initGoals", at = @At("TAIL"))
    private void mebahel$addCustomTargets(CallbackInfo ci) {
        WolfEntity wolf = (WolfEntity) (Object) this;

        if (wolf.isTamed()) {
            return;
        }

        GoalSelector targetSelector = ((MobEntityAccessor) wolf).getTargetSelector();

        targetSelector.add(3, new ActiveTargetGoal<>(wolf, SkeletonWarriorEntity.class, true));
        targetSelector.add(3, new ActiveTargetGoal<>(wolf, SkeletonWarriorHeadEntity.class, true));
    }
}