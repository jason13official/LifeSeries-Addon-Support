package net.mat0u5.lifeseries.config;

import net.mat0u5.lifeseries.config.entries.*;
import net.mat0u5.lifeseries.gui.config.ConfigScreen;
import net.mat0u5.lifeseries.gui.config.entries.ConfigEntry;
import net.mat0u5.lifeseries.gui.config.entries.extra.HeartsConfigEntry;
import net.mat0u5.lifeseries.gui.config.entries.extra.PercentageConfigEntry;
import net.mat0u5.lifeseries.gui.config.entries.main.*;
import net.mat0u5.lifeseries.network.NetworkHandlerClient;
import net.mat0u5.lifeseries.network.packets.ConfigPayload;
import net.mat0u5.lifeseries.utils.enums.ConfigTypes;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static net.mat0u5.lifeseries.MainClient.clientConfig;

public class ClientConfigNetwork {

    public static Map<Integer, ConfigObject> configObjects = new TreeMap<>();
    public static Map<Integer, ConfigObject> groupConfigObjects = new TreeMap<>();
    public static Map<Integer, ConfigObject> client_configObjects = new TreeMap<>();
    public static Map<Integer, ConfigObject> client_groupConfigObjects = new TreeMap<>();

    public static void load() {
        configObjects.clear();
        groupConfigObjects.clear();
        NetworkHandlerClient.sendStringPacket("request_config", "");

        int index = 0;
        for (ConfigFileEntry<?> entry : clientConfig.getAllConfigEntries()) {
            handleConfigPacket(clientConfig.getConfigPayload(entry, index), true);
            index++;
        }
    }

    public static void handleConfigPacket(ConfigPayload payload, boolean client) {
        int index = payload.index();
        ConfigObject configObject = getConfigEntry(payload);
        if (configObject == null) return;
        String argGroupInfo = payload.args().get(2);
        if (argGroupInfo.startsWith("{")) {
            if (!client) {
                groupConfigObjects.put(index, configObject);
            }
            else {
                client_groupConfigObjects.put(index, configObject);
            }
        }
        else {
            if (!client) {
                configObjects.put(index, configObject);
            }
            else {
                client_configObjects.put(index, configObject);
            }
        }
    }

    public static ConfigObject getConfigEntry(ConfigPayload payload) {
        ConfigTypes configType = ConfigTypes.getFromString(payload.configType());
        String id = payload.id();
        String name = payload.name();
        String description = payload.description();
        List<String> args = payload.args();
        if (args.size() < 3) return null;
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
            }catch(Exception e){}
        }
        if (configType.parentInteger()) {
            try {
                int value = Integer.parseInt(argValue);
                int defaultValue = Integer.parseInt(argDefaultValue);
                return new IntegerObject(payload, value, defaultValue);
            }catch(Exception e){}
        }

        return null;
    }
}
