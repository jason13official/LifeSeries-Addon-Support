package net.mat0u5.lifeseries.gui.config.entries.simple;

import net.mat0u5.lifeseries.gui.config.entries.TextFieldConfigEntry;
import net.minecraft.text.Text;

public class StringConfigEntry extends TextFieldConfigEntry {
    protected final String defaultValue;
    protected String value;

    public StringConfigEntry(String fieldName, Text displayName, String value, String defaultValue) {
        super(fieldName, displayName, 150, 18);
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
    protected String getDefaultValueAsString() {
        return defaultValue;
    }

    @Override
    public Object getValue() {
        return textField.getText();
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof String) {
            textField.setText((String) value);
        }
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}