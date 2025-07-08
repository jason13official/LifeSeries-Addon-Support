package net.mat0u5.lifeseries.gui.config.entries.simple;

import net.mat0u5.lifeseries.gui.config.entries.NumberConfigEntry;
import net.minecraft.text.Text;

public class IntegerConfigEntry extends NumberConfigEntry<Integer> {

    public IntegerConfigEntry(String fieldName, Text displayName, int value, int defaultValue, Integer minValue, Integer maxValue) {
        super(fieldName, displayName, value, defaultValue, minValue, maxValue);
    }

    @Override
    protected Integer parseValue(String text) throws NumberFormatException {
        return Integer.parseInt(text);
    }

    @Override
    protected boolean isValueInRange(Integer value) {
        if (minValue == null || maxValue == null) return true;
        return value >= minValue && value <= maxValue;
    }

    @Override
    protected boolean isValidType(Object value) {
        return value instanceof Integer;
    }

    @Override
    protected Integer castValue(Object value) {
        return (Integer) value;
    }

    @Override
    public String getValueType() {
        return "integer";
    }
}