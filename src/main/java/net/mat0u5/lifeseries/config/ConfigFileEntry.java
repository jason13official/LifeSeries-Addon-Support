package net.mat0u5.lifeseries.config;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.utils.enums.ConfigTypes;

public class ConfigFileEntry<T> {
    public final String key;
    public T defaultValue;
    public final ConfigTypes type;
    public final String displayName;
    public final String description;
    public final String groupInfo;

    public ConfigFileEntry(String key, T defaultValue, String groupInfo, String displayName, String description) {
        this(key, defaultValue, getTypeFromValue(defaultValue), groupInfo, displayName, description);
    }
    public ConfigFileEntry(String key, T defaultValue, ConfigTypes type, String groupInfo, String displayName, String description) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.type = type;
        this.displayName = displayName;
        this.description = description;
        this.groupInfo = groupInfo;
    }

    public static ConfigTypes getTypeFromValue(Object defaultValue) {
        if (defaultValue instanceof Integer) {
            return ConfigTypes.INTEGER;
        }
        else if (defaultValue instanceof Boolean) {
            return ConfigTypes.BOOLEAN;
        }
        else if (defaultValue instanceof Double) {
            return ConfigTypes.DOUBLE;
        }
        else if (defaultValue instanceof String) {
            return ConfigTypes.STRING;
        }
        return ConfigTypes.NULL;
    }

    @SuppressWarnings("unchecked")
    public T get(ConfigManager config) {
        try {
        if (defaultValue instanceof Integer i) {
            return (T) Integer.valueOf(config.getOrCreateInt(key, i));
        }
        else if (defaultValue instanceof Boolean b) {
            return (T) Boolean.valueOf(config.getOrCreateBoolean(key, b));
        }
        else if (defaultValue instanceof Double d) {
            return (T) Double.valueOf(config.getOrCreateDouble(key, d));
        }
        else if (defaultValue instanceof String s) {
            return (T) config.getOrCreateProperty(key, s);
        }
        }catch(Exception e) {}

        Main.LOGGER.error("Config value "+ key +" was null, returning default value - "+defaultValue);
        return defaultValue;
    }
}