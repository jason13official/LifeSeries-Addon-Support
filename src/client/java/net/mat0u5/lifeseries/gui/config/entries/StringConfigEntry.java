
package net.mat0u5.lifeseries.gui.config.entries;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class StringConfigEntry extends ConfigEntry {
    private final TextFieldWidget textField;
    private final String defaultValue;

    public StringConfigEntry(String fieldName, Text displayName, String defaultValue) {
        super(fieldName, displayName);
        this.defaultValue = defaultValue;
        this.textField = new TextFieldWidget(textRenderer, 0, 0, 150, 18, Text.empty());
        this.textField.setText(defaultValue);
        this.textField.setChangedListener(this::onTextChanged);
    }

    private void onTextChanged(String text) {
        this.clearError();
        this.markChanged();
    }

    @Override
    protected void renderEntry(DrawContext context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        int entryWidth = this.getEntryContentWidth(width);
        this.textField.setX(x + entryWidth - 155);
        this.textField.setY(y + 3);
        this.textField.render(context, mouseX, mouseY, tickDelta);
    }

    @Override
    protected boolean mouseClickedEntry(double mouseX, double mouseY, int button) {
        return this.textField.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected boolean keyPressedEntry(int keyCode, int scanCode, int modifiers) {
        return this.textField.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected boolean charTypedEntry(char chr, int modifiers) {
        return this.textField.charTyped(chr, modifiers);
    }

    @Override
    protected boolean canReset() {
        return !this.textField.getText().equals(this.defaultValue);
    }

    @Override
    protected void resetToDefault() {
        this.textField.setText(this.defaultValue);
    }

    @Override
    public Object getValue() {
        return this.textField.getText();
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof String) {
            this.textField.setText((String) value);
        }
    }

    public String getDefaultValue() {
        return this.defaultValue;
    }
}