package net.mebahel.antiquebeasts.mixin;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ModelLoader.class)
public abstract class ModelLoaderMixin {
    @Shadow
    protected abstract void addModel(ModelIdentifier modelId);

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/ModelLoader;addModel(Lnet/minecraft/client/util/ModelIdentifier;)V", ordinal = 3, shift = At.Shift.AFTER))
    public void addHornModel(BlockColors blockColors, Profiler profiler, Map jsonUnbakedModels, Map blockStates, CallbackInfo ci) {
        this.addModel(new ModelIdentifier(AntiqueBeasts.MOD_ID, "weapon/egyptian_halberd/inv_iron_egyptian_halberd", "inventory"));
        this.addModel(new ModelIdentifier(AntiqueBeasts.MOD_ID, "weapon/egyptian_halberd/inv_gold_egyptian_halberd", "inventory"));
        this.addModel(new ModelIdentifier(AntiqueBeasts.MOD_ID, "weapon/egyptian_halberd/inv_diamond_egyptian_halberd", "inventory"));
        this.addModel(new ModelIdentifier(AntiqueBeasts.MOD_ID, "weapon/egyptian_halberd/inv_netherite_egyptian_halberd", "inventory"));
        this.addModel(new ModelIdentifier(AntiqueBeasts.MOD_ID, "weapon/spear/inv_iron_hoplite_spear", "inventory"));
        this.addModel(new ModelIdentifier(AntiqueBeasts.MOD_ID, "weapon/spear/inv_gold_hoplite_spear", "inventory"));
        this.addModel(new ModelIdentifier(AntiqueBeasts.MOD_ID, "weapon/spear/inv_diamond_hoplite_spear", "inventory"));
        this.addModel(new ModelIdentifier(AntiqueBeasts.MOD_ID, "weapon/spear/inv_netherite_hoplite_spear", "inventory"));
        this.addModel(new ModelIdentifier(AntiqueBeasts.MOD_ID, "weapon/inv_hersir_axe", "inventory"));
        this.addModel(new ModelIdentifier(AntiqueBeasts.MOD_ID, "weapon/inv_pharaoh_scepter", "inventory"));
    }
}