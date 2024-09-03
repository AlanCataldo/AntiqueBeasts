package net.mebahel.antiquebeasts.mixin;

import net.mebahel.antiquebeasts.entity.custom.ChimeraEntity;
import net.mebahel.antiquebeasts.entity.custom.CyclopsEntity;
import net.mebahel.antiquebeasts.entity.custom.FrostCyclopsEntity;
import net.mebahel.antiquebeasts.entity.custom.greek.GreekEntity;
import net.mebahel.antiquebeasts.entity.custom.egyptian.EgyptianEntity;
import net.mebahel.antiquebeasts.entity.custom.norse.NorseEntity;
import net.mebahel.antiquebeasts.entity.custom.other.DraugrEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.ai.goal.GoalSelector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.mebahel.antiquebeasts.mixin.MobEntityAccessor;

@Mixin(IronGolemEntity.class)
public abstract class IronGolemMixin extends MobEntity {

    public IronGolemMixin() {
        super(null, null);
    }

    @Inject(method = "initGoals", at = @At("TAIL"))
    private void addCustomTargets(CallbackInfo info) {
        GoalSelector targetSelector = ((MobEntityAccessor) this).getTargetSelector();

        targetSelector.add(3, new ActiveTargetGoal<>(this, GreekEntity.class, true));
        targetSelector.add(3, new ActiveTargetGoal<>(this, EgyptianEntity.class, true));
        targetSelector.add(3, new ActiveTargetGoal<>(this, NorseEntity.class, true));
        targetSelector.add(3, new ActiveTargetGoal<>(this, ChimeraEntity.class, true));
        targetSelector.add(3, new ActiveTargetGoal<>(this, CyclopsEntity.class, true));
        targetSelector.add(3, new ActiveTargetGoal<>(this, FrostCyclopsEntity.class, true));
        targetSelector.add(3, new ActiveTargetGoal<>(this, DraugrEntity.class, true));
    }
}
