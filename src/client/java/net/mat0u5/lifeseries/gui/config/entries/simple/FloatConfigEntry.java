package net.mat0u5.lifeseries.gui.config.entries.simple;

import net.mat0u5.lifeseries.gui.config.entries.NumberConfigEntry;
import net.minecraft.text.Text;

public class FloatConfigEntry extends NumberConfigEntry<Float> {

    public FloatConfigEntry(String fieldName, Text displayName, float value, float defaultValue, float minValue, float maxValue) {
        super(fieldName, displayName, value, defaultValue, minValue, maxValue);
    }

    @Override
    protected Float parseValue(String text) throws NumberFormatException {
        return Float.parseFloat(text);
    }

    @Override
    protected boolean isValueInRange(Float value) {
        return value >= minValue && value <= maxValue;
    }

    @Override
    protected boolean isValidType(Object value) {
        return value instanceof Float;
    }

    @Override
    protected Float castValue(Object value) {
        return (Float) value;
    }
}