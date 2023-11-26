package net.mebahel.antiquebeasts.item;

import net.mebahel.antiquebeasts.item.custom.ModArmorMaterials;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class DiamondPlateArmorItem extends ArmorItem implements GeoItem {
    public DiamondPlateArmorItem(ArmorMaterial materialIn, EquipmentSlot slot, Settings builder) {
        super(materialIn, Type.CHESTPLATE, builder);
    }
    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }
    private PlayState predicate(AnimationState animationState) {
        animationState.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController(this, "controller",0, this::predicate));
    }
    @Override
    public void createRenderer(Consumer<Object> consumer) {

    }
    @Override
    public Supplier<Object> getRenderProvider() {
        return null;
    }
}
