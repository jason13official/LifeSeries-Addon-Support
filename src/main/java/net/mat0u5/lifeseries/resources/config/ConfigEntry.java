package net.mat0u5.lifeseries.resources.config;

import net.mat0u5.lifeseries.Main;

public class ConfigEntry<T> {
    public final String key;
    public T defaultValue;
    public final String type;
    public final String displayName;
    public final String description;

    public ConfigEntry(String key, T defaultValue, String type, String displayName, String description) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.type = type;
        this.displayName = displayName;
        this.description = description;
    }

    public T get(ConfigManager config) {
        /*
        try {
            //TODO
        }catch(Exception e) {

        }
        */

        if (defaultValue instanceof Integer i) {
            return (T) Integer.valueOf(config.getOrCreateInt(key, i));
        } else if (defaultValue instanceof Boolean b) {
            return (T) Boolean.valueOf(config.getOrCreateBoolean(key, b));
        } else if (defaultValue instanceof Double d) {
            return (T) Double.valueOf(config.getOrCreateDouble(key, d));
        } else if (defaultValue instanceof String s) {
            return (T) config.getOrCreateProperty(key, s);
        }

        Main.LOGGER.error("Config value "+ key +" was null, returning default value - "+defaultValue);
        return defaultValue;
    }
}