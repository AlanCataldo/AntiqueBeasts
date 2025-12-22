package net.mebahel.antiquebeasts.entity.armor.DiamondPlateArmor;

import mod.azure.azurelib.animation.dispatch.command.AzCommand;
import mod.azure.azurelib.animation.play_behavior.AzPlayBehaviors;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class AzDiamondPlateArmorDispatcher {

    private static final AzCommand IDLE_COMMAND = AzCommand.create(
            "base_controller",
            "idle",
            AzPlayBehaviors.LOOP
    );

    private static final AzCommand WALK_COMMAND = AzCommand.create(
            "base_controller",
            "walk",
            AzPlayBehaviors.LOOP
    );

    private static final AzCommand TRANSITION_WALK = AzCommand.create(
            "base_controller",
            "transition_walk",
            AzPlayBehaviors.PLAY_ONCE
    );

    public boolean isMoving(double dx, double dz) {
        return (dx * dx + dz * dz) > 0.0004;
    }
    public void updateAnimations(LivingEntity entity, ItemStack stack) {
        if (entity.isSprinting()) {
            WALK_COMMAND.sendForItem(entity, stack);
        } else {
            IDLE_COMMAND.sendForItem(entity, stack);
        }
    }
}
