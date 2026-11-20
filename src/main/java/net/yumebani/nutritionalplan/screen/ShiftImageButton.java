package net.yumebani.nutritionalplan.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ShiftImageButton extends ImageButton {
    private final ResourceLocation texture;
    private final ResourceLocation shifttexture;
    private final ResourceLocation disabled;

    private float uiScale;
    private int panelWidth;
    private int panelHeight;

    public ShiftImageButton(
            int x,
            int y,
            int width,
            int height,
            float uiScale,
            int panelWidth,
            int panelHeight,
            ResourceLocation texture,
            ResourceLocation shifttexture,
            ResourceLocation disabled,
            WidgetSprites spritesBase,
            OnPress onPress,
            Component message
    ) {
        super(x, y, width, height, spritesBase, onPress, message);
        this.texture = texture;
        this.shifttexture = shifttexture;
        this.disabled = disabled;

        this.uiScale = uiScale;
        this.panelWidth = panelWidth;
        this.panelHeight = panelHeight;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (!this.isHoveredOrFocused() && this.isActive()) return;
        ResourceLocation resourceLocation = !this.isActive() ? disabled : Screen.hasShiftDown() ? shifttexture : texture;
        guiGraphics.blit(resourceLocation, this.getX(), this.getY(), 0, 0, this.width, this.height, this.width, this.height);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Screen currentScreen = Minecraft.getInstance().screen;

        float scaledLeft = ((currentScreen.width - (this.panelWidth * uiScale)) / 2.0F);
        float scaledTop  = ((currentScreen.height - (this.panelHeight * uiScale)) / 2.0F);

        int realMouseX = (int)((mouseX - scaledLeft) / uiScale);
        int realMouseY = (int)((mouseY - scaledTop) / uiScale);

        return super.mouseClicked(realMouseX, realMouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        Screen currentScreen = Minecraft.getInstance().screen;

        float scaledLeft = ((currentScreen.width - (this.panelWidth * uiScale)) / 2.0F);
        float scaledTop  = ((currentScreen.height - (this.panelHeight * uiScale)) / 2.0F);

        int realMouseX = (int)((mouseX - scaledLeft) / uiScale);
        int realMouseY = (int)((mouseY - scaledTop) / uiScale);

        return super.mouseReleased(realMouseX, realMouseY, button);
    }
}