package net.mat0u5.lifeseries.gui.config.entries.simple;

import net.mat0u5.lifeseries.gui.config.entries.ConfigEntry;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class TextConfigEntry extends ConfigEntry {
    private final boolean clickable;
    public boolean clicked;

    public TextConfigEntry(String fieldName, Text displayName) {
        this(fieldName, displayName, true);
    }

    public TextConfigEntry(String fieldName, Text displayName, boolean clickable) {
        super(fieldName, displayName);
        this.clickable = clickable;
    }

    @Override
    protected void renderEntry(DrawContext context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
    }

    @Override
    public Text getDisplayName() {
        String start = isFocused ? "> " : "⌄ ";
        return Text.literal(start).append(displayName);
    }

    @Override
    protected boolean mouseClickedEntry(double mouseX, double mouseY, int button) {
        if (clickable && button == 0) {
            clicked = !clicked;
        }
        return clickable;
    }

    @Override
    protected boolean keyPressedEntry(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @Override
    protected boolean charTypedEntry(char chr, int modifiers) {
        return false;
    }

    @Override
    protected void resetToDefault() {

    }

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public String getValueAsString() {
        return "";
    }

    @Override
    public Object getDefaultValue() {
        return null;
    }

    @Override
    public String getDefaultValueAsString() {
        return "";
    }

    @Override
    public Object getStartingValue() {
        return null;
    }

    @Override
    public String getValueType() {
        return "text";
    }

    @Override
    public void setValue(Object value) {

    }

    @Override
    public boolean modified() {
        return false;
    }

    @Override
    public boolean canReset() {
        return false;
    }

    @Override
    public boolean hasError() {
        return false;
    }

    public boolean isClickable() {
        return clickable;
    }

    @Override
    protected boolean hasResetButton() {
        return false;
    }
}