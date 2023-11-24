package net.mebahel.antiquebeasts.mixin;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
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
    public void addIronHopliteSpearModel(ResourceManager resourceManager, BlockColors blockColors, Profiler profiler, int mipmapLevel, CallbackInfo ci) {
        this.addModel(new ModelIdentifier(AntiqueBeasts.MOD_ID, "weapon/iron_hoplite_spear", "inventory"));
        this.addModel(new ModelIdentifier(AntiqueBeasts.MOD_ID, "weapon/gold_hoplite_spear", "inventory"));
        this.addModel(new ModelIdentifier(AntiqueBeasts.MOD_ID, "weapon/diamond_hoplite_spear", "inventory"));
        this.addModel(new ModelIdentifier(AntiqueBeasts.MOD_ID, "weapon/netherite_hoplite_spear", "inventory"));
    }
}