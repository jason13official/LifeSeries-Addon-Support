package com.cursee.ls_addon_support.gui.config.entries.main;

import com.cursee.ls_addon_support.gui.config.entries.NumberConfigEntry;
import com.cursee.ls_addon_support.utils.enums.ConfigTypes;

public class DoubleConfigEntry extends NumberConfigEntry<Double> {

  public DoubleConfigEntry(String fieldName, String displayName, String description, double value,
      double defaultValue) {
    super(fieldName, displayName, description, value, defaultValue);
  }

  public DoubleConfigEntry(String fieldName, String displayName, String description, double value,
      double defaultValue, Double minValue, Double maxValue) {
    super(fieldName, displayName, description, value, defaultValue, minValue, maxValue);
  }

  @Override
  protected Double parseValue(String text) throws NumberFormatException {
    return Double.parseDouble(text);
  }

  @Override
  protected boolean isValueInRange(Double value) {
      if (minValue == null || maxValue == null) {
          return true;
      }
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
  public ConfigTypes getValueType() {
    return ConfigTypes.DOUBLE;
  }
}