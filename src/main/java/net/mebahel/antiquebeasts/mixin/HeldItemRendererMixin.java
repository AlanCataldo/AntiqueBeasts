package net.mebahel.antiquebeasts.mixin;

import net.mebahel.antiquebeasts.item.staff.FlameAtronachSummoningStaff;
import net.mebahel.antiquebeasts.item.staff.FrostBiteStaff;
import net.mebahel.antiquebeasts.item.staff.IceSpikeStaff;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {
    private static final Random random = new Random();

    @Inject(method = "renderFirstPersonItem", at = @At("HEAD"))
    private void modifyItemPosition(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (item.getItem() instanceof FlameAtronachSummoningStaff && player.isUsingItem()) {
            int chargeTicks = player.getItemUseTime();

            // ✅ Interpolation fluide avec tickDelta
            float chargeProgress = Math.min(1.0F, (chargeTicks + tickDelta) / 20);

            // ✅ Lissage avec une courbe plus naturelle
            float smoothFactor = 1 - (1 - chargeProgress) * (1 - chargeProgress);
            float offsetY = -smoothFactor * 0.12F;

            // ✅ Appliquer la translation de base (descente du bâton)
            matrices.translate(0.0F, offsetY, 0.0F);

            // ✅ Ajout du tremblement uniquement si la charge est complète
            if (chargeProgress >= 1.0F) {
                float shakeIntensity = 0.008F; // Intensité du tremblement (peut être ajustée)
                float shakeX = (random.nextFloat() - 0.5F) * shakeIntensity; // Oscillation sur X
                float shakeZ = (random.nextFloat() - 0.5F) * shakeIntensity; // Oscillation sur Z

                matrices.translate(shakeX, 0.0F, shakeZ);
            }
        }

        if (item.getItem() instanceof IceSpikeStaff && player.isUsingItem()) {
            int chargeTicks = player.getItemUseTime();

            // ✅ Interpolation fluide avec tickDelta
            float chargeProgress = Math.min(1.0F, (chargeTicks + tickDelta) / 18);

            // ✅ Lissage avec une courbe plus naturelle
            float smoothFactor = 1 - (1 - chargeProgress) * (1 - chargeProgress);
            float offsetY = -smoothFactor * 0.12F;

            // ✅ Appliquer la translation de base (descente du bâton)
            matrices.translate(0.0F, offsetY, 0.0F);

            // ✅ Ajout du tremblement uniquement si la charge est complète
            if (chargeProgress >= 1.0F) {
                float shakeIntensity = 0.008F; // Intensité du tremblement (peut être ajustée)
                float shakeX = (random.nextFloat() - 0.5F) * shakeIntensity; // Oscillation sur X
                float shakeZ = (random.nextFloat() - 0.5F) * shakeIntensity; // Oscillation sur Z

                matrices.translate(shakeX, 0.0F, shakeZ);
            }
        }
        if (item.getItem() instanceof FrostBiteStaff && player.isUsingItem()) {
            int chargeTicks = player.getItemUseTime();
            float chargeProgress = Math.min(1.0F, (chargeTicks + tickDelta) / 5); // Charge rapide

            // ✅ Translation vers l'avant (+1.0) et vers le haut
            float forwardOffset = chargeProgress * -0.65F; // Déplace de 1 devant
            float upwardOffset = chargeProgress * 0.45F;  // Monte légèrement le bâton
            matrices.translate(0.0F, upwardOffset, forwardOffset);

            // ✅ Rotation pour pencher encore plus l'arme
            float rotationAngle = chargeProgress * -60.0F; // Penché jusqu'à 60°
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotationAngle));

            // ✅ Ajout d'un léger tremblement si la charge est complète
            if (chargeProgress >= 1.0F) {
                float shakeIntensity = 0.008F;
                float shakeX = (random.nextFloat() - 0.55F) * shakeIntensity;
                float shakeZ = (random.nextFloat() - 0.55F) * shakeIntensity;
                matrices.translate(shakeX, 0.0F, shakeZ);
            }
        }

    }
}
