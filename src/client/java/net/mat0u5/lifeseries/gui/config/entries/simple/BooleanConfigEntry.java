package net.mat0u5.lifeseries.gui.config.entries.simple;

import net.mat0u5.lifeseries.gui.config.entries.ButtonConfigEntry;
import net.mat0u5.lifeseries.utils.interfaces.IEntryGroupHeader;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class BooleanConfigEntry extends ButtonConfigEntry implements IEntryGroupHeader {
    private final boolean defaultValue;
    private boolean value;
    private boolean startingValue;

    public BooleanConfigEntry(String fieldName, Text displayName, boolean value, boolean defaultValue) {
        super(fieldName, displayName, 60, 20);
        this.defaultValue = defaultValue;
        this.value = value;
        this.startingValue = value;
        updateButtonText();
    }

    @Override
    protected void onButtonClick(ButtonWidget button) {
        value = !value;
        updateButtonText();
        markChanged();
    }

    @Override
    public Text getButtonText() {
        return value ? Text.of("§aYes") : Text.of("§cNo");
    }

    @Override
    protected void resetToDefault() {
        value = defaultValue;
        updateButtonText();
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof Boolean booleanValue) {
            this.value = booleanValue;
            updateButtonText();
        }
    }

    @Override
    public Boolean getValue() {
        return value;
    }

    @Override
    public String getValueAsString() {
        return String.valueOf(value);
    }

    @Override
    public Boolean getDefaultValue() {
        return defaultValue;
    }

    @Override
    public String getDefaultValueAsString() {
        return String.valueOf(defaultValue);
    }

    @Override
    public Boolean getStartingValue() {
        return startingValue;
    }

    @Override
    public String getValueType() {
        return "boolean";
    }

    @Override
    public void expand() {
    }

    @Override
    public boolean shouldExpand() {
        return getValue();
    }
}