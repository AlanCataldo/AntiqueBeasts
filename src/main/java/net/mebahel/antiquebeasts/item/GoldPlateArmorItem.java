package net.mebahel.antiquebeasts.item;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.mebahel.antiquebeasts.entity.armor.GoldPlateArmorRenderer;
import net.mebahel.antiquebeasts.item.custom.ModItems;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import software.bernie.example.client.renderer.armor.WolfArmorRenderer;
import software.bernie.example.registry.ItemRegistry;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GoldPlateArmorItem extends ArmorItem implements GeoItem {
    public GoldPlateArmorItem(ArmorMaterial materialIn, ArmorItem.Type type, Settings builder) {
        super(materialIn, type, builder);
    }
    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, 0, state -> {
            state.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
            Entity entity = state.getData(DataTickets.ENTITY);
            if (entity instanceof ArmorStandEntity)
                return PlayState.CONTINUE;
            Set<Item> wornArmor = new ObjectOpenHashSet<>();
            for (ItemStack stack : entity.getArmorItems()) {
                if (stack.isEmpty())
                    return PlayState.STOP;
                wornArmor.add(stack.getItem());
            }
            boolean isFullSet = wornArmor.containsAll(ObjectArrayList.of(
                    ModItems.GOLD_PLATE_HELMET,
                    ModItems.GOLD_PLATE_CHESTPLATE,
                    ModItems.GOLD_PLATE_LEGGINGS,
                    ModItems.GOLD_PLATE_BOOTS));
            return isFullSet ? PlayState.CONTINUE : PlayState.STOP;
        }));
    }
    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new RenderProvider() {
            private GeoArmorRenderer<?> renderer;
            @Override
            public @NotNull BipedEntityModel<LivingEntity> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, BipedEntityModel<LivingEntity> original) {
                if (this.renderer == null)
                    this.renderer = new GoldPlateArmorRenderer();
                this.renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);
                return this.renderer;
            }
        });
    }
    @Override
    public Supplier<Object> getRenderProvider() {
        return this.renderProvider;
    }
}
