package com.cursee.ls_addon_support.config;

import static com.cursee.ls_addon_support.LSAddonSupportClient.clientConfig;

import com.cursee.ls_addon_support.config.entries.BooleanObject;
import com.cursee.ls_addon_support.config.entries.ConfigObject;
import com.cursee.ls_addon_support.config.entries.DoubleObject;
import com.cursee.ls_addon_support.config.entries.IntegerObject;
import com.cursee.ls_addon_support.config.entries.StringObject;
import com.cursee.ls_addon_support.config.entries.TextObject;
import com.cursee.ls_addon_support.gui.config.entries.ConfigEntry;
import com.cursee.ls_addon_support.network.NetworkHandlerClient;
import com.cursee.ls_addon_support.network.packets.ConfigPayload;
import com.cursee.ls_addon_support.utils.ClientResourcePacks;
import com.cursee.ls_addon_support.utils.enums.ConfigTypes;
import com.cursee.ls_addon_support.utils.enums.PacketNames;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ClientConfigNetwork {

  public static Map<Integer, ConfigObject> configObjects = new TreeMap<>();
  public static Map<Integer, ConfigObject> clientConfigObjects = new TreeMap<>();

  public static void load() {
    configObjects.clear();
    clientConfigObjects.clear();
    NetworkHandlerClient.sendStringPacket(PacketNames.REQUEST_CONFIG, "");

    int index = 0;
    for (ConfigFileEntry<?> entry : clientConfig.getAllConfigEntries()) {
      handleConfigPacket(clientConfig.getConfigPayload(entry, index), true);
      index++;
    }
  }

  public static void handleConfigPacket(ConfigPayload payload, boolean client) {
    int index = payload.index();
    ConfigObject configObject = getConfigEntry(payload);
      if (configObject == null) {
          return;
      }
    if (!client) {
      configObjects.put(index, configObject);
    } else {
      clientConfigObjects.put(index, configObject);
    }
  }

  public static ConfigObject getConfigEntry(ConfigPayload payload) {
    ConfigTypes configType = ConfigTypes.getFromString(payload.configType());
    String id = payload.id();
    String name = payload.name();
    String description = payload.description();
    List<String> args = payload.args();
      if (args.size() < 3) {
          return null;
      }
    String argValue = args.get(0);
    String argDefaultValue = args.get(1);
    String argGroupInfo = args.get(2);

    if (configType.parentText()) {
      boolean clickable = !argDefaultValue.equalsIgnoreCase("false");
      return new TextObject(payload, name, clickable);
    }
    if (configType.parentString()) {
      return new StringObject(payload, argValue, argDefaultValue);
    }
    if (configType.parentBoolean()) {
      boolean value = argValue.equalsIgnoreCase("true");
      boolean defaultValue = argDefaultValue.equalsIgnoreCase("true");
      return new BooleanObject(payload, value, defaultValue);
    }
    if (configType.parentDouble()) {
      try {
        double value = Double.parseDouble(argValue);
        double defaultValue = Double.parseDouble(argDefaultValue);
        return new DoubleObject(payload, value, defaultValue);
      } catch (Exception e) {
      }
    }
    if (configType.parentInteger()) {
      try {
        int value = Integer.parseInt(argValue);
        int defaultValue = Integer.parseInt(argDefaultValue);
        return new IntegerObject(payload, value, defaultValue);
      } catch (Exception e) {
      }
    }

    return null;
  }

  public static void onConfigSave(ConfigEntry entry) {
    String id = entry.getFieldName();
    String valueStr = entry.getValueAsString();
    clientConfig.setProperty(id, valueStr);

    // Actions
    if (id.equals(ClientConfig.MINIMAL_ARMOR.key)) {
      ClientResourcePacks.checkClientPacks();
    }
  }
}
