package net.mat0u5.lifeseries.gui.config.entries.main;

import net.mat0u5.lifeseries.gui.config.entries.NumberConfigEntry;

public class IntegerConfigEntry extends NumberConfigEntry<Integer> {
    private static final String VALUE_TYPE = "integer";

    public IntegerConfigEntry(String fieldName, String displayName, String description, int value, int defaultValue) {
        super(fieldName, displayName, description, value, defaultValue);
    }

    public IntegerConfigEntry(String fieldName, String displayName, String description, int value, int defaultValue, Integer minValue, Integer maxValue) {
        super(fieldName, displayName, description, value, defaultValue, minValue, maxValue);
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
        return VALUE_TYPE;
    }
}