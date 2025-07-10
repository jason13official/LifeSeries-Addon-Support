package net.mat0u5.lifeseries.config.entries;

import net.mat0u5.lifeseries.network.packets.ConfigPayload;

import java.util.List;

public class BooleanObject extends ConfigObject {
    public boolean booleanValue;
    public boolean defaultValue;
    public boolean startingValue;
    public BooleanObject(ConfigPayload payload, boolean booleanValue, boolean defaultValue) {
        super(payload);
        this.booleanValue = booleanValue;
        this.defaultValue = defaultValue;
        this.startingValue = booleanValue;
    }

    public void updateValue(boolean newValue) {
        booleanValue = newValue;
        if (!modified && booleanValue != startingValue) {
            modified = true;
        }
    }

    @Override
    public List<String> getArgs() {
        return List.of(String.valueOf(booleanValue));
    }
}
