package net.mat0u5.lifeseries.gui.config.entries.extra;


import net.mat0u5.lifeseries.gui.config.entries.main.DoubleConfigEntry;

public class PercentageConfigEntry extends DoubleConfigEntry {
    private static final String VALUE_TYPE = "percentage";

    public PercentageConfigEntry(String fieldName, String displayName, String description, double value, double defaultValue) {
        super(fieldName, displayName, description, value, defaultValue, 0.0, 1.0);
    }

    public PercentageConfigEntry(String fieldName, String displayName, String description, double value, double defaultValue, Double minValue, Double maxValue) {
        super(fieldName, displayName, description, value, defaultValue, minValue, maxValue);
    }
}