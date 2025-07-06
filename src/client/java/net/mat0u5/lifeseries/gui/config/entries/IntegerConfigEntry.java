
package net.mat0u5.lifeseries.gui.config.entries;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class IntegerConfigEntry extends ConfigEntry {
    private final TextFieldWidget textField;
    private final int defaultValue;
    private final int minValue;
    private final int maxValue;
    private int value;

    public IntegerConfigEntry(String fieldName, Text displayName, int defaultValue, int minValue, int maxValue) {
        super(fieldName, displayName);
        this.defaultValue = defaultValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.value = defaultValue;
        this.textField = new TextFieldWidget(textRenderer, 0, 0, 80, 18, Text.empty());
        this.textField.setText(String.valueOf(defaultValue));
        this.textField.setChangedListener(this::onTextChanged);
    }

    private void onTextChanged(String text) {
        try {
            int newValue = Integer.parseInt(text);
            if (newValue < this.minValue || newValue > this.maxValue) {
                this.setError("Value must be between " + this.minValue + " and " + this.maxValue);
            } else {
                this.value = newValue;
                this.clearError();
            }
        } catch (NumberFormatException e) {
            this.setError("Invalid number format");
        }
        this.markChanged();
    }

    @Override
    protected void renderEntry(DrawContext context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        String rangeText = "(" + this.minValue + "-" + this.maxValue + ")";
        int rangeWidth = textRenderer.getWidth(rangeText);
        int entryWidth = this.getEntryContentWidth(width);

        context.drawTextWithShadow(textRenderer, rangeText, x + entryWidth - rangeWidth - 90, y + 6, 0xAAAAAA);

        this.textField.setX(x + entryWidth - 85);
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
        return this.value != this.defaultValue;
    }

    @Override
    protected void resetToDefault() {
        this.value = this.defaultValue;
        this.textField.setText(String.valueOf(this.defaultValue));
        this.clearError();
    }

    @Override
    public Object getValue() {
        return this.value;
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof Integer) {
            this.value = (Integer) value;
            this.textField.setText(String.valueOf(this.value));
        }
    }

    public int getDefaultValue() {
        return this.defaultValue;
    }

    public int getMinValue() {
        return this.minValue;
    }

    public int getMaxValue() {
        return this.maxValue;
    }
}