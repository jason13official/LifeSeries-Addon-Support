package net.mat0u5.lifeseries.gui.config.entries.simple;

import net.mat0u5.lifeseries.gui.config.entries.NumberConfigEntry;
import net.minecraft.text.Text;

public class DoubleConfigEntry extends NumberConfigEntry<Double> {

    public DoubleConfigEntry(String fieldName, Text displayName, double value, double defaultValue) {
        super(fieldName, displayName, value, defaultValue);
    }

    public DoubleConfigEntry(String fieldName, Text displayName, double value, double defaultValue, Double minValue, Double maxValue) {
        super(fieldName, displayName, value, defaultValue, minValue, maxValue);
    }

    @Override
    protected Double parseValue(String text) throws NumberFormatException {
        return Double.parseDouble(text);
    }

    @Override
    protected boolean isValueInRange(Double value) {
        if (minValue == null || maxValue == null) return true;
        return value >= minValue && value <= maxValue;
    }

    @Override
    protected boolean isValidType(Object value) {
        return value instanceof Double;
    }

    @Override
    protected Double castValue(Object value) {
        return (Double) value;
    }

    @Override
    public String getValueType() {
        return "double";
    }
}