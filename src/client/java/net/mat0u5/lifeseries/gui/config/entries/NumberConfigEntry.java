package net.mat0u5.lifeseries.gui.config.entries;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public abstract class NumberConfigEntry<T extends Number> extends TextFieldConfigEntry {
    protected final T defaultValue;
    protected final T minValue;
    protected final T maxValue;
    protected T value;
    protected T startingValue;

    public NumberConfigEntry(String fieldName, Text displayName, T value, T defaultValue, T minValue, T maxValue) {
        super(fieldName, displayName);
        this.defaultValue = defaultValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.value = value;
        this.startingValue = value;
        initializeTextField();
    }

    @Override
    protected void initializeTextField() {
        setText(value.toString());
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
        if (minValue != null && maxValue != null) {
            String rangeText = "(" + minValue + "-" + maxValue + ")";
            int rangeWidth = textRenderer.getWidth(rangeText);
            int entryWidth = getEntryContentWidth(width);

            context.drawTextWithShadow(textRenderer, rangeText,
                    x + entryWidth - rangeWidth - textField.getWidth() - 15,
                    y + (height - textRenderer.fontHeight) / 2+1, 0xAAAAAA);
        }
    }

    @Override
    public void setValue(Object value) {
        if (isValidType(value)) {
            this.value = castValue(value);
            setText(value.toString());
        }
    }

    protected abstract T parseValue(String text) throws NumberFormatException;

    protected abstract boolean isValueInRange(T value);

    protected abstract boolean isValidType(Object value);

    protected abstract T castValue(Object value);

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public String getValueAsString() {
        return value.toString();
    }

    @Override
    public T getDefaultValue() {
        return defaultValue;
    }

    @Override
    public String getDefaultValueAsString() {
        return defaultValue.toString();
    }

    @Override
    public T getStartingValue() {
        return startingValue;
    }

    public T getMinValue() {
        return minValue;
    }

    public T getMaxValue() {
        return maxValue;
    }
}