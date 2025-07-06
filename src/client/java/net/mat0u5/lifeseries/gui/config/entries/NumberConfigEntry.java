package net.mat0u5.lifeseries.gui.config.entries;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public abstract class NumberConfigEntry<T extends Number> extends TextFieldConfigEntry {
    protected final T defaultValue;
    protected final T minValue;
    protected final T maxValue;
    protected T value;

    public NumberConfigEntry(String fieldName, Text displayName, T value, T defaultValue, T minValue, T maxValue) {
        super(fieldName, displayName, 80, 18);
        this.defaultValue = defaultValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.value = value;
        initializeTextField();
    }

    @Override
    protected void initializeTextField() {
        textField.setText(value.toString());
    }

    @Override
    protected void onTextChanged(String text) {
        try {
            T newValue = parseValue(text);
            if (isValueInRange(newValue)) {
                value = newValue;
                clearError();
            } else {
                setError("Value must be between " + minValue + " and " + maxValue);
            }
        } catch (NumberFormatException e) {
            setError("Invalid number format");
        }
        markChanged();
    }

    @Override
    protected void renderAdditionalContent(DrawContext context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        String rangeText = "(" + minValue + "-" + maxValue + ")";
        int rangeWidth = textRenderer.getWidth(rangeText);
        int entryWidth = getEntryContentWidth(width);

        context.drawTextWithShadow(textRenderer, rangeText,
                x + entryWidth - rangeWidth - textField.getWidth() - 15,
                y + (height - textRenderer.fontHeight) / 2, 0xAAAAAA);
    }

    @Override
    protected String getDefaultValueAsString() {
        return defaultValue.toString();
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(Object value) {
        if (isValidType(value)) {
            value = castValue(value);
            textField.setText(value.toString());
        }
    }

    protected abstract T parseValue(String text) throws NumberFormatException;

    protected abstract boolean isValueInRange(T value);

    protected abstract boolean isValidType(Object value);

    protected abstract T castValue(Object value);

    public T getDefaultValue() {
        return defaultValue;
    }

    public T getMinValue() {
        return minValue;
    }

    public T getMaxValue() {
        return maxValue;
    }
}