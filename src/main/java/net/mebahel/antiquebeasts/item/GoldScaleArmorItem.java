package net.mebahel.antiquebeasts.item;

import net.fabricmc.loader.api.FabricLoader;
import net.mebahel.antiquebeasts.entity.armor.GoldScaleArmorRenderer;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GoldScaleArmorItem extends ArmorItem implements GeoItem {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private Supplier<Object> renderProvider;
    public GoldScaleArmorItem(ArmorMaterial materialIn, Type type, Settings builder) {
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
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.antiquebeasts.scale_armor.tooltip").formatted(Formatting.GRAY, Formatting.ITALIC));
        tooltip.add(Text.translatable("item.antiquebeasts.scale_armor.tooltip2").formatted(Formatting.GRAY, Formatting.ITALIC));
    }
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    // Gestion des animations en fonction du mouvement de l'entité
    private PlayState predicate(AnimationState<GeoItem> animationState) {
        // Récupérer l'entité qui porte l'armure
        Entity entity = animationState.getData(DataTickets.ENTITY);

        // Vérifier que c'est bien une LivingEntity (joueur, créature, etc.)
        if (entity instanceof LivingEntity livingEntity) {
            // Si l'entité est en train de bouger (marche ou course)
            boolean isMoving = livingEntity.forwardSpeed > 0 || livingEntity.sidewaysSpeed > 0;

            // Si l'entité est en mouvement
            if (isMoving) {
                animationState.getController().setAnimation(RawAnimation.begin()
                        .then("transition_walk", Animation.LoopType.PLAY_ONCE)
                        .then("walk", Animation.LoopType.LOOP));
                return PlayState.CONTINUE;
            }

            animationState.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        return PlayState.STOP;
    }

    @Override
    public void createRenderer(Consumer<Object> consumer) {
        if (FabricLoader.getInstance().isModLoaded("fabric")) {
            consumer.accept(new RenderProvider() {
                private GoldScaleArmorRenderer renderer;

                @Override
                public BipedEntityModel<LivingEntity> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack,
                                                                            EquipmentSlot equipmentSlot, BipedEntityModel<LivingEntity> original) {
                    if (this.renderer == null)
                        this.renderer = new GoldScaleArmorRenderer();
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
