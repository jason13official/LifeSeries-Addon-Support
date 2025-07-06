
package net.mat0u5.lifeseries.gui.config.entries;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class BooleanConfigEntry extends ConfigEntry {
    private final ButtonWidget button;
    private final boolean defaultValue;
    private boolean value;

    public BooleanConfigEntry(String fieldName, Text displayName, boolean defaultValue) {
        super(fieldName, displayName);
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.button = ButtonWidget.builder(this.getButtonText(), this::onButtonClick)
                .dimensions(0, 0, 60, 20)
                .build();
    }

    private void onButtonClick(ButtonWidget button) {
        this.value = !this.value;
        this.button.setMessage(this.getButtonText());
        this.markChanged();
    }

    private Text getButtonText() {
        return this.value ? Text.of("§aYes") : Text.of("§cNo");
    }

    @Override
    protected void renderEntry(DrawContext context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        int entryWidth = this.getEntryContentWidth(width);
        this.button.setX(x + entryWidth - 65);
        this.button.setY(y + 3);
        this.button.render(context, mouseX, mouseY, tickDelta);
    }

    @Override
    protected boolean mouseClickedEntry(double mouseX, double mouseY, int button) {
        return this.button.mouseClicked(mouseX, mouseY, button);
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
    protected boolean canReset() {
        return this.value != this.defaultValue;
    }

    @Override
    protected void resetToDefault() {
        this.value = this.defaultValue;
        this.button.setMessage(this.getButtonText());
    }

    @Override
    public Object getValue() {
        return this.value;
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof Boolean) {
            this.value = (Boolean) value;
            this.button.setMessage(this.getButtonText());
        }
    }

    public boolean getDefaultValue() {
        return this.defaultValue;
    }
}