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
    protected String getDefaultValueAsString() {
        return defaultValue;
    }

    @Override
    public Object getValue() {
        return textField.getText();
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof String stringValue) {
            this.value = stringValue;
            textField.setText(stringValue);
        }
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public int getPreferredHeight() {
        return isHovered ? 40 : super.getPreferredHeight(); //TODO remove
    }
}