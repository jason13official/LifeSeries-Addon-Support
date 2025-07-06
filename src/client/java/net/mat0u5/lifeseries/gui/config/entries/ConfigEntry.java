package net.mat0u5.lifeseries.gui.config.entries;

import net.mat0u5.lifeseries.gui.config.ModernConfigScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public abstract class ConfigEntry {
    protected TextRenderer textRenderer;
    protected ModernConfigScreen screen;
    protected final String fieldName;
    protected final Text displayName;
    protected boolean hasError = false;
    protected String errorMessage = "";

    // Enhanced fields for better UX
    protected ButtonWidget resetButton;
    protected float highlightAlpha = 0.0f;
    protected boolean isHovered = false;
    protected static final int RESET_BUTTON_WIDTH = 50;
    protected static final int RESET_BUTTON_HEIGHT = 16;

    public ConfigEntry(String fieldName, Text displayName) {
        this.fieldName = fieldName;
        this.displayName = displayName;
        this.textRenderer = MinecraftClient.getInstance().textRenderer;
        this.initializeResetButton();
    }

    private void initializeResetButton() {
        this.resetButton = ButtonWidget.builder(Text.of("Reset"), this::onResetClicked)
                .dimensions(0, 0, RESET_BUTTON_WIDTH, RESET_BUTTON_HEIGHT)
                .build();
    }

    private void onResetClicked(ButtonWidget button) {
        this.resetToDefault();
        this.markChanged();
    }

    public void setScreen(ModernConfigScreen screen) {
        this.screen = screen;
    }

    public void render(DrawContext context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        this.isHovered = hovered;
        this.updateHighlightAnimation(tickDelta);

        if (this.highlightAlpha > 0.0f) {
            int highlightColor = (int)(this.highlightAlpha * 32) << 24 | 0x808080;
            context.fill(x, y, x + width, y + height, highlightColor);
        }

        context.drawTextWithShadow(textRenderer, this.displayName, x + 5, y + 6, 0xFFFFFF);

        this.resetButton.setX(x + width - RESET_BUTTON_WIDTH - 5);
        this.resetButton.setY(y + 2);
        this.resetButton.active = this.canReset();
        this.resetButton.render(context, mouseX, mouseY, tickDelta);

        if (this.hasError()) {
            int errorX = x + width - RESET_BUTTON_WIDTH - 20;
            context.drawTextWithShadow(textRenderer, "!", errorX, y + 6, 0xFF5555);
        }

        this.renderEntry(context, x, y, width, height, mouseX, mouseY, hovered, tickDelta);
    }

    protected void updateHighlightAnimation(float tickDelta) {
        if (this.isHovered) {
            this.highlightAlpha = 1.0f;
        } else {
            this.highlightAlpha = Math.max(0.0f, this.highlightAlpha - tickDelta * 0.75f);
        }
    }

    protected int getEntryContentWidth(int totalWidth) {
        return totalWidth - RESET_BUTTON_WIDTH - 30;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.resetButton.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return this.mouseClickedEntry(mouseX, mouseY, button);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return this.keyPressedEntry(keyCode, scanCode, modifiers);
    }

    public boolean charTyped(char chr, int modifiers) {
        return this.charTypedEntry(chr, modifiers);
    }

    protected abstract void renderEntry(DrawContext context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta);
    protected abstract boolean mouseClickedEntry(double mouseX, double mouseY, int button);
    protected abstract boolean keyPressedEntry(int keyCode, int scanCode, int modifiers);
    protected abstract boolean charTypedEntry(char chr, int modifiers);
    protected abstract boolean canReset();
    protected abstract void resetToDefault();

    public abstract Object getValue();
    public abstract void setValue(Object value);

    public String getFieldName() {
        return this.fieldName;
    }

    public Text getDisplayName() {
        return this.displayName;
    }

    public boolean hasError() {
        return this.hasError;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    protected void setError(String errorMessage) {
        this.hasError = true;
        this.errorMessage = errorMessage;
    }

    protected void clearError() {
        this.hasError = false;
        this.errorMessage = "";
    }

    protected void markChanged() {
        if (this.screen != null) {
            this.screen.markChanged();
        }
    }
}