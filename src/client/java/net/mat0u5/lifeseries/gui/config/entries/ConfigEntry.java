package net.mat0u5.lifeseries.gui.config.entries;

import net.mat0u5.lifeseries.gui.config.ConfigScreen;
import net.mat0u5.lifeseries.utils.TextColors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.Objects;

public abstract class ConfigEntry {
    protected TextRenderer textRenderer;
    protected ConfigScreen screen;
    protected final String fieldName;
    protected final Text displayName;
    protected boolean hasError = false;
    protected String errorMessage = "";

    protected ButtonWidget resetButton;
    public float highlightAlpha = 0.0f;
    protected boolean isHovered = false;
    protected static final int RESET_BUTTON_WIDTH = 50;
    protected static final int RESET_BUTTON_HEIGHT = 16;

    public ConfigEntry(String fieldName, Text displayName) {
        this.fieldName = fieldName;
        this.displayName = displayName;
        this.textRenderer = MinecraftClient.getInstance().textRenderer;
        initializeResetButton();
    }

    private void initializeResetButton() {
        resetButton = ButtonWidget.builder(Text.of("Reset"), this::onResetClicked)
                .dimensions(0, 0, RESET_BUTTON_WIDTH, RESET_BUTTON_HEIGHT)
                .build();
    }

    private void onResetClicked(ButtonWidget button) {
        resetToDefault();
        markChanged();
    }

    public void setScreen(ConfigScreen screen) {
        this.screen = screen;
    }

    public void render(DrawContext context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        isHovered = hovered;
        updateHighlightAnimation(tickDelta);

        if (highlightAlpha > 0.0f) {
            int highlightColor = (int)(highlightAlpha * 128) << 24 | 0x808080;
            context.fill(x, y, x + width, y + height, highlightColor);
        }

        int textColor = hasError() ? TextColors.GUI_RED : TextColors.WHITE;

        context.drawTextWithShadow(textRenderer, displayName, x + 25, y + 6, textColor);

        resetButton.setX(x + width - RESET_BUTTON_WIDTH - 5);
        resetButton.setY(y + 2);
        resetButton.active = canReset();
        resetButton.render(context, mouseX, mouseY, tickDelta);


        renderEntry(context, x, y, width, height, mouseX, mouseY, hovered, tickDelta);
    }

    protected void updateHighlightAnimation(float tickDelta) {
        if (isHovered) {
            highlightAlpha = 1.0f;
        } else {
            highlightAlpha = Math.max(0.0f, highlightAlpha - tickDelta * 0.1f);
        }
    }

    protected int getEntryContentWidth(int totalWidth) {
        return totalWidth - RESET_BUTTON_WIDTH - 15;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (resetButton.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return mouseClickedEntry(mouseX, mouseY, button);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return keyPressedEntry(keyCode, scanCode, modifiers);
    }

    public boolean charTyped(char chr, int modifiers) {
        return charTypedEntry(chr, modifiers);
    }

    public void setFocused(boolean focused) {
        if (focused && screen != null) {
            screen.setFocusedEntry(this);
        }
    }

    public int getPreferredHeight() {
        return 20;
    }

    protected abstract void renderEntry(DrawContext context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta);
    protected abstract boolean mouseClickedEntry(double mouseX, double mouseY, int button);
    protected abstract boolean keyPressedEntry(int keyCode, int scanCode, int modifiers);
    protected abstract boolean charTypedEntry(char chr, int modifiers);
    protected abstract void resetToDefault();

    public abstract Object getValue();
    public abstract String getValueAsString();
    public abstract Object getDefaultValue();
    public abstract String getDefaultValueAsString();
    public abstract Object getStartingValue();
    public abstract String getValueType();
    public abstract void setValue(Object value);

    public boolean modified() {
        return !Objects.equals(getValue(), getStartingValue());
    }

    public boolean canReset() {
        return !Objects.equals(getValue(), getDefaultValue());
    }

    public void onFocused() {
        setFocused(true);
    }

    public String getFieldName() {
        return fieldName;
    }

    public Text getDisplayName() {
        return displayName;
    }

    public boolean hasError() {
        return hasError;
    }

    public String getErrorMessage() {
        return errorMessage;
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
        if (screen != null) {
            screen.onEntryValueChanged();
        }
    }
}