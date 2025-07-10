package net.mat0u5.lifeseries.config.entries;

import net.mat0u5.lifeseries.network.packets.ConfigPayload;

import java.util.List;

public class DoubleObject extends ConfigObject {
    public double doubleValue;
    public double defaultValue;
    public double startingValue;
    public DoubleObject(ConfigPayload payload, double doubleValue, double defaultValue) {
        super(payload);
        this.doubleValue = doubleValue;
        this.defaultValue = defaultValue;
        this.startingValue = doubleValue;
    }

    public void updateValue(double newValue) {
        doubleValue = newValue;
        if (!modified && doubleValue != startingValue) {
            modified = true;
        }
    }

    @Override
    public List<String> getArgs() {
        return List.of(String.valueOf(doubleValue));
    }
}
