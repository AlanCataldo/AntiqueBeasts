package net.mebahel.antiquebeasts.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(BossBarHud.class)
public abstract class CustomBossBarHudMixin {

    // Add the new texture for the 4th phase
    private static final Identifier MUMMY_BOSS_OVERLAY_PHASE1 = new Identifier("antiquebeasts", "textures/gui/mummy_boss_overlay_phase1.png");
    private static final Identifier MUMMY_BOSS_OVERLAY_PHASE2 = new Identifier("antiquebeasts", "textures/gui/mummy_boss_overlay_phase2.png");
    private static final Identifier MUMMY_BOSS_OVERLAY_PHASE3 = new Identifier("antiquebeasts", "textures/gui/mummy_boss_overlay_phase3.png");
    private static final Identifier MUMMY_BOSS_OVERLAY_PHASE4 = new Identifier("antiquebeasts", "textures/gui/mummy_boss_overlay_phase4.png");  // New phase
    private static final Set<String> BOSS_NAMES = Set.of(
            "Khendjer", "Djedkare", "Seti", "Raneferef", "Hatshepsut", "Taharka", "Sheshonq", "Sobekhotep", "Hakor", "Menes", "Thoutmosis"
    );
    @Inject(method = "renderBossBar", at = @At("TAIL"))
    private void renderCustomBossBarOverlay(DrawContext context, int x, int y, BossBar bossBar, CallbackInfo ci) {
        String bossName = bossBar.getName().getString();

        if (isMummyBoss(bossName)) {
            float healthPercent = bossBar.getPercent();
            Identifier overlayTexture;

            if (healthPercent <= 0.31) {
                overlayTexture = MUMMY_BOSS_OVERLAY_PHASE3; // New phase for below 33%
            } else if (healthPercent <= 0.66f) {
                overlayTexture = MUMMY_BOSS_OVERLAY_PHASE2; // 50% to 66%
            } else {
                overlayTexture = MUMMY_BOSS_OVERLAY_PHASE1; // Above 66%
            }

            // Render the chosen overlay texture
            renderBossBarOverlay(context, overlayTexture, x, y);
        }
    }
    private boolean isMummyBoss(String bossName) {
        // Check if any of the names in the set is contained in the boss name
        for (String validName : BOSS_NAMES) {
            if (bossName.contains(validName)) {
                return true;
            }
        }
        return false;
    }

    private void renderBossBarOverlay(DrawContext context, Identifier overlayTexture, int x, int y) {
        // Define the size of the overlay (adjust based on how large you want it to be relative to the boss bar)
        int overlayWidth = 210;  // You may need to fine-tune this width
        int overlayHeight = 15;  // You may need to fine-tune this height

        // Recalculate X and Y to align the overlay perfectly above the boss bar
        int centeredX = x - (overlayWidth - 204) / 2;  // Centers the overlay on the X-axis
        int overlayY = y - 1;  // Slightly offset upwards; tweak this to position it vertically

        // Bind the overlay texture and render it over the boss bar
        MinecraftClient.getInstance().getTextureManager().bindTexture(overlayTexture);
        context.drawTexture(overlayTexture, centeredX, overlayY, 0, 0, overlayWidth, overlayHeight);
    }
}
