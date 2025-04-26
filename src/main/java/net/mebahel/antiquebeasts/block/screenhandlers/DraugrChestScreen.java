package net.mebahel.antiquebeasts.block.screenhandlers;

import com.mojang.blaze3d.systems.RenderSystem;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class DraugrChestScreen extends HandledScreen<DraugrChestScreenHandler> {
    // Utilise la texture vanilla ou remplace par ta propre texture personnalisée
    private static final Identifier TEXTURE = new Identifier(AntiqueBeasts.MOD_ID, "textures/gui/draugr_chest.png");

    public DraugrChestScreen(DraugrChestScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 222; // 4 lignes de 18px + barre joueur
    }

    @Override
    protected void init() {
        super.init();
        // Centrer les titres en haut
        this.titleX = 8;
        this.titleY = 6;
        this.playerInventoryTitleX = 8;
        this.playerInventoryTitleY = this.backgroundHeight - 130;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
}
