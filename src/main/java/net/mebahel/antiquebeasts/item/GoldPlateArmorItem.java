package net.mebahel.antiquebeasts.item;

import net.fabricmc.loader.api.FabricLoader;
import net.mebahel.antiquebeasts.entity.armor.GoldPlateArmorRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class GoldPlateArmorItem extends ArmorItem implements GeoItem {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private Supplier<Object> renderProvider;
    public GoldPlateArmorItem(ArmorMaterial materialIn, ArmorItem.Type type, Settings builder) {
        super(materialIn, type, builder);
        // Prevent initialization of Fabric-only methods in Forge/Syntra environments
        if (FabricLoader.getInstance().isModLoaded("fabric")) {
            try {
                this.renderProvider = GeoItem.makeRenderer(this);
            } catch (NoSuchMethodError e) {
                this.renderProvider = () -> null;  // Fallback in case method is missing
            }
        } else {
            this.renderProvider = () -> null;  // No renderer for non-Fabric environments
        }
    }
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {return this.cache;}
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController(this, "controller", 20, this::predicate));
    }
    private PlayState predicate(AnimationState animationState) {
        animationState.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }
    @Override
    public void createRenderer(Consumer<Object> consumer) {
        if (FabricLoader.getInstance().isModLoaded("fabric")) {
            consumer.accept(new RenderProvider() {
                private GoldPlateArmorRenderer renderer;

                @Override
                public BipedEntityModel<LivingEntity> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack,
                                                                            EquipmentSlot equipmentSlot, BipedEntityModel<LivingEntity> original) {
                    if (this.renderer == null)
                        this.renderer = new GoldPlateArmorRenderer();
                    this.renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);
                    return this.renderer;
                }
            });
        }
    }
    @Override
    public Supplier<Object> getRenderProvider() {
        return this.renderProvider;
    }

}
