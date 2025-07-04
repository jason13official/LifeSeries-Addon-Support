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
        this.textField = new TextFieldWidget(textRenderer, 0, 0, 80, 20, Text.empty());
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
    public void render(DrawContext context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        context.drawTextWithShadow(textRenderer, this.displayName, x + 5, y + 6, 0xFFFFFF);

        String rangeText = "(" + this.minValue + "-" + this.maxValue + ")";
        context.drawTextWithShadow(textRenderer, rangeText, x + width - 170, y + 6, 0xAAAAAA);

        this.textField.setX(x + width - 85);
        this.textField.setY(y + 2);
        this.textField.render(context, mouseX, mouseY, tickDelta);

        if (this.hasError()) {
            context.drawTextWithShadow(textRenderer, "!", x + width - 100, y + 6, 0xFF5555);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.textField.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return this.textField.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return this.textField.charTyped(chr, modifiers);
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