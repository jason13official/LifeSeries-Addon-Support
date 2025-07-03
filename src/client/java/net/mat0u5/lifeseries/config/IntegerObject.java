package net.mat0u5.lifeseries.config;

import net.mat0u5.lifeseries.network.packets.ConfigPayload;

import java.util.List;

public class IntegerObject extends ConfigObject {
    public int integerValue;
    public int defaultValue;
    public int startingValue;
    public IntegerObject(ConfigPayload payload, int integerValue, int defaultValue) {
        super(payload);
        this.integerValue = integerValue;
        this.defaultValue = defaultValue;
        this.startingValue = integerValue;
    }

    public void updateValue(int newValue) {
        integerValue = newValue;
        if (!modified && integerValue != startingValue) {
            modified = true;
        }
    }

    @Override
    public List<String> getArgs() {
        return List.of(String.valueOf(integerValue));
    }
}