package net.mat0u5.lifeseries.gui.config.entries.simple;

import net.mat0u5.lifeseries.gui.config.entries.TextFieldConfigEntry;
import net.minecraft.text.Text;

public class StringConfigEntry extends TextFieldConfigEntry {
    protected final String defaultValue;
    protected String value;

    public StringConfigEntry(String fieldName, Text displayName, String value, String defaultValue) {
        super(fieldName, displayName, 150);
        this.defaultValue = defaultValue;
        this.value = value;
        initializeTextField();
    }

    @Override
    protected void initializeTextField() {
        textField.setText(value);
    }

    @Override
    protected void onTextChanged(String text) {
        clearError();
        markChanged();
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof String stringValue) {
            this.value = stringValue;
            textField.setText(stringValue);
        }
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public String getValueAsString() {
        return getValue();
    }

    @Override
    public String getDefaultValueAsString() {
        return getDefaultValue();
    }

    @Override
    public String getValueType() {
        return "string";
    }
}