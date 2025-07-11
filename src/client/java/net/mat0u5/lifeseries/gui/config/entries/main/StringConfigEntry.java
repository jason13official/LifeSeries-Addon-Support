package net.mat0u5.lifeseries.gui.config.entries.main;

import net.mat0u5.lifeseries.gui.config.entries.TextFieldConfigEntry;

public class StringConfigEntry extends TextFieldConfigEntry {
    private static final String VALUE_TYPE = "string";
    private static final int FIELD_WIDTH = 150;

    protected final String defaultValue;
    protected String value;
    protected String startingValue;

    public StringConfigEntry(String fieldName, String displayName, String description, String value, String defaultValue) {
        super(fieldName, displayName, description, FIELD_WIDTH);
        this.defaultValue = defaultValue;
        this.value = value;
        this.startingValue = value;
        initializeTextField();
    }

    @Override
    protected void initializeTextField() {
        setText(value);
    }

    @Override
    protected void onTextChanged(String text) {
        super.onTextChanged(text);
        this.value = text;
        clearError();
        markChanged();
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof String stringValue) {
            this.value = stringValue;
            setText(stringValue);
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
    public String getStartingValue() {
        return startingValue;
    }

    @Override
    public String getValueType() {
        return VALUE_TYPE;
    }
}