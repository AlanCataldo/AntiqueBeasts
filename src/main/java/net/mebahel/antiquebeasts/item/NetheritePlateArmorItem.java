package net.mebahel.antiquebeasts.item;

import mod.azure.azurelibarmor.animatable.GeoItem;
import mod.azure.azurelibarmor.animatable.SingletonGeoAnimatable;
import mod.azure.azurelibarmor.constant.DataTickets;
import mod.azure.azurelibarmor.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelibarmor.core.animatable.instance.SingletonAnimatableInstanceCache;
import mod.azure.azurelibarmor.core.animation.AnimatableManager;
import mod.azure.azurelibarmor.core.animation.AnimationController;
import mod.azure.azurelibarmor.core.animation.RawAnimation;
import mod.azure.azurelibarmor.core.object.PlayState;
import net.mebahel.antiquebeasts.entity.armor.NetheritePlateArmor.NetheritePlateArmorRenderProvider;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class NetheritePlateArmorItem extends ArmorItem implements GeoItem {

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    private static final String WAS_MOVING_KEY = "ab_was_moving";

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    public NetheritePlateArmorItem(ArmorMaterial material, Type type, Settings settings) {
        super(material, type, settings);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(
                this,
                "controller",
                1,
                state -> {
                    var entity = state.getData(DataTickets.ENTITY);
                    ItemStack stack = state.getData(DataTickets.ITEMSTACK);

                    if (!(entity instanceof LivingEntity living) || stack == null) {
                        return state.setAndContinue(IDLE);
                    }

                    boolean moving = Math.abs(living.forwardSpeed) > 0.001f
                            || Math.abs(living.sidewaysSpeed) > 0.001f;

                    // état persistant côté client par stack
                    boolean wasMoving = stack.getOrCreateNbt().getBoolean(WAS_MOVING_KEY);

                    if (moving) {
                        if (!wasMoving) {
                            stack.getOrCreateNbt().putBoolean(WAS_MOVING_KEY, true);

                            // transition une seule fois, ensuite walk
                            return state.setAndContinue(RawAnimation.begin()
                                    .thenPlay("transition_walk")
                                    .thenLoop("walk"));
                        }

                        // déjà en mouvement : walk direct
                        return state.setAndContinue(WALK);
                    }

                    // immobile : reset + idle
                    if (wasMoving) {
                        stack.getOrCreateNbt().putBoolean(WAS_MOVING_KEY, false);
                    }
                    return state.setAndContinue(IDLE);
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
        consumer.accept(new NetheritePlateArmorRenderProvider());
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.antiquebeasts.scale_armor.tooltip").formatted(Formatting.GRAY, Formatting.ITALIC));
        tooltip.add(Text.translatable("item.antiquebeasts.scale_armor.tooltip2").formatted(Formatting.GRAY, Formatting.ITALIC));
    }
}
