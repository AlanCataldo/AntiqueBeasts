package net.mebahel.antiquebeasts.item;

import mod.azure.azurelibarmor.animatable.GeoItem;
import mod.azure.azurelibarmor.animatable.SingletonGeoAnimatable;
import mod.azure.azurelibarmor.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelibarmor.core.animatable.instance.SingletonAnimatableInstanceCache;
import mod.azure.azurelibarmor.core.animation.AnimatableManager;
import mod.azure.azurelibarmor.core.animation.AnimationController;
import mod.azure.azurelibarmor.core.animation.RawAnimation;
import mod.azure.azurelibarmor.core.object.PlayState;
import net.mebahel.antiquebeasts.entity.armor.IronPlateArmor.IronPlateArmorRenderProvider;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class IronPlateArmorItem extends ArmorItem implements GeoItem {

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    public IronPlateArmorItem(ArmorMaterial material, Type type, Settings settings) {
        super(material, type, settings);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(
                this,
                "base_controller",
                0,
                state -> {
                    state.setAnimation(IDLE);
                    return PlayState.CONTINUE;
                }
        ));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public Supplier<Object> getRenderProvider() {
        return this.renderProvider;
    }

    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new IronPlateArmorRenderProvider());
    }
}
