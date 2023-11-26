package net.mebahel.antiquebeasts.mixin;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.item.custom.ModItems;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @ModifyVariable(method = "renderItem", at = @At(value = "HEAD"), argsOnly = true)
    public BakedModel useIronHopliteSpearModel(BakedModel value, ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (stack.isOf(ModItems.IRON_HOPLITE_SPEAR) && renderMode != ModelTransformationMode.GUI && renderMode != ModelTransformationMode.FIXED) {
            return ((ItemRendererAccessor) this).antiquebeasts$getModels().getModelManager().getModel(new ModelIdentifier(AntiqueBeasts.MOD_ID, "weapon/iron_hoplite_spear", "inventory"));
        }
        if (stack.isOf(ModItems.GOLD_HOPLITE_SPEAR) && renderMode != ModelTransformationMode.GUI && renderMode != ModelTransformationMode.FIXED) {
            return ((ItemRendererAccessor) this).antiquebeasts$getModels().getModelManager().getModel(new ModelIdentifier(AntiqueBeasts.MOD_ID, "weapon/gold_hoplite_spear", "inventory"));
        }
        if (stack.isOf(ModItems.DIAMOND_HOPLITE_SPEAR) && renderMode != ModelTransformationMode.GUI && renderMode != ModelTransformationMode.FIXED) {
            return ((ItemRendererAccessor) this).antiquebeasts$getModels().getModelManager().getModel(new ModelIdentifier(AntiqueBeasts.MOD_ID, "weapon/diamond_hoplite_spear", "inventory"));
        }
        if (stack.isOf(ModItems.NETHERITE_HOPLITE_SPEAR) && renderMode != ModelTransformationMode.GUI && renderMode != ModelTransformationMode.FIXED) {
            return ((ItemRendererAccessor) this).antiquebeasts$getModels().getModelManager().getModel(new ModelIdentifier(AntiqueBeasts.MOD_ID, "weapon/netherite_hoplite_spear", "inventory"));
        }
        return value;
    }
}