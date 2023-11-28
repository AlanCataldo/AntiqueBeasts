package net.mebahel.antiquebeasts.mixin;

import com.mojang.datafixers.types.templates.Tag;
import net.fabricmc.loader.api.FabricLoader;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.item.custom.ModItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ModelPredicateProvider;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;
import net.minecraft.util.UseAction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.function.Predicate;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @ModifyVariable(method = "renderItem", at = @At(value = "HEAD"), argsOnly = true)
    public BakedModel useIronHopliteSpearModel(BakedModel value, ItemStack stack, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (stack.isOf(ModItems.IRON_HOPLITE_SPEAR) && renderMode != ModelTransformation.Mode.GUI && renderMode != ModelTransformation.Mode.FIXED) {
            return ((ItemRendererAccessor) this).antiquebeasts$getModels().getModelManager().getModel(new ModelIdentifier(AntiqueBeasts.MOD_ID, "weapon/iron_hoplite_spear_2d", "inventory"));
        }
        if (stack.isOf(ModItems.GOLD_HOPLITE_SPEAR) && renderMode != ModelTransformation.Mode.GUI && renderMode != ModelTransformation.Mode.FIXED) {
            return ((ItemRendererAccessor) this).antiquebeasts$getModels().getModelManager().getModel(new ModelIdentifier(AntiqueBeasts.MOD_ID, "weapon/gold_hoplite_spear_2d", "inventory"));
        }
        if (stack.isOf(ModItems.DIAMOND_HOPLITE_SPEAR) && renderMode != ModelTransformation.Mode.GUI && renderMode != ModelTransformation.Mode.FIXED) {
            return ((ItemRendererAccessor) this).antiquebeasts$getModels().getModelManager().getModel(new ModelIdentifier(AntiqueBeasts.MOD_ID, "weapon/diamond_hoplite_spear_2d", "inventory"));
        }
        if (stack.isOf(ModItems.NETHERITE_HOPLITE_SPEAR) && renderMode != ModelTransformation.Mode.GUI && renderMode != ModelTransformation.Mode.FIXED) {
            return ((ItemRendererAccessor) this).antiquebeasts$getModels().getModelManager().getModel(new ModelIdentifier(AntiqueBeasts.MOD_ID, "weapon/netherite_hoplite_spear_2d", "inventory"));
        }
        return value;
    }
}