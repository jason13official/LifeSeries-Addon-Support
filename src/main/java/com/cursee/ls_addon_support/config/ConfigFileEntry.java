package com.cursee.ls_addon_support.config;

import com.cursee.ls_addon_support.LSAddonSupport;
import com.cursee.ls_addon_support.utils.enums.ConfigTypes;
import com.cursee.ls_addon_support.utils.other.TextUtils;
import java.util.List;

public class ConfigFileEntry<T> {

  public final String key;
  public final ConfigTypes type;
  public final String displayName;
  public final String description;
  public final String groupInfo;
  public final List<String> args;
  public T defaultValue;

  public ConfigFileEntry(String key, T defaultValue, String groupInfo, String displayName,
      String description) {
    this(key, defaultValue, getTypeFromValue(defaultValue), groupInfo, displayName, description);
  }

  public ConfigFileEntry(String key, T defaultValue, ConfigTypes type, String groupInfo,
      String displayName, String description) {
    this(key, defaultValue, type, groupInfo, displayName, description, null);
  }

  public ConfigFileEntry(String key, T defaultValue, ConfigTypes type, String groupInfo,
      String displayName, String description, List<String> args) {
    this.key = key;
    this.defaultValue = defaultValue;
    this.type = type;
    this.displayName = displayName;
    this.description = description;
    this.groupInfo = groupInfo;
    this.args = args;
  }

  public static ConfigTypes getTypeFromValue(Object defaultValue) {
    if (defaultValue instanceof Integer) {
      return ConfigTypes.INTEGER;
    } else if (defaultValue instanceof Boolean) {
      return ConfigTypes.BOOLEAN;
    } else if (defaultValue instanceof Double) {
      return ConfigTypes.DOUBLE;
    } else if (defaultValue instanceof String) {
      return ConfigTypes.STRING;
    }
    return ConfigTypes.NULL;
  }

  @SuppressWarnings("unchecked")
  public T get(ConfigManager config) {
    try {
      if (defaultValue instanceof Integer i) {
        return (T) Integer.valueOf(config.getOrCreateInt(key, i));
      } else if (defaultValue instanceof Boolean b) {
        return (T) Boolean.valueOf(config.getOrCreateBoolean(key, b));
      } else if (defaultValue instanceof Double d) {
        return (T) Double.valueOf(config.getOrCreateDouble(key, d));
      } else if (defaultValue instanceof String s) {
        return (T) config.getOrCreateProperty(key, s);
      }
    } catch (Exception e) {
    }

    LSAddonSupport.LOGGER.error(
        TextUtils.formatString("Config value {} was null, returning default value - {}", key,
            defaultValue));
    return defaultValue;
  }
}