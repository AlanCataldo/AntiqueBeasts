package net.mebahel.antiquebeasts.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class BloodInfusingStationScreen extends HandledScreen<BloodInfusingStationScreenHandler> {
    private static final Identifier TEXTURE =
            new Identifier(AntiqueBeasts.MOD_ID, "textures/gui/blood_infusing_station_gui.png");

    public BloodInfusingStationScreen(BloodInfusingStationScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight - 6) / 2;
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight +20);

        renderProgressArrow(matrices, x, y);
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        // Modify the position of the inventory title
        this.textRenderer.draw(matrices, this.title, titleX, 5, 0x404040);
        this.textRenderer.draw(matrices, this.playerInventoryTitle, 8, 82, 0x404040);

    }

    private void renderProgressArrow(MatrixStack matrices, int x, int y) {
        if(handler.isCrafting()) {
            drawTexture(matrices, x + 105, y + 38, 176, 0, 8, handler.getScaledProgress());
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }
}
