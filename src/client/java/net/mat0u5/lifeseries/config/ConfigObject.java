package net.mat0u5.lifeseries.config;

import net.mat0u5.lifeseries.network.packets.ConfigPayload;

import java.util.List;

public class ConfigObject {
    public String configType;
    private int index;
    public String id;
    public String name;
    public String description;
    private List<String> args;
    public boolean modified = false;
    public ConfigObject(ConfigPayload payload) {
        configType = payload.configType();
        index = payload.index();
        id = payload.id();
        name = payload.name();
        description = payload.description();
        args = payload.args();
    }

    public List<String> getArgs() {
        return args;
    }
}
