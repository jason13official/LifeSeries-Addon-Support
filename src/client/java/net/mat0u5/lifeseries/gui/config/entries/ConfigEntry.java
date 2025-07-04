package net.mat0u5.lifeseries.gui.config.entries;

import net.mat0u5.lifeseries.gui.config.ModernConfigScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public abstract class ConfigEntry {
    protected TextRenderer textRenderer;
    protected ModernConfigScreen screen;
    protected final String fieldName;
    protected final Text displayName;
    protected boolean hasError = false;
    protected String errorMessage = "";

    public ConfigEntry(String fieldName, Text displayName) {
        this.fieldName = fieldName;
        this.displayName = displayName;
        this.textRenderer = MinecraftClient.getInstance().textRenderer;
    }

    public void setScreen(ModernConfigScreen screen) {
        this.screen = screen;
    }

    public abstract void render(DrawContext context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta);

    public abstract boolean mouseClicked(double mouseX, double mouseY, int button);

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    public boolean charTyped(char chr, int modifiers) {
        return false;
    }

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