package net.mat0u5.lifeseries.config;

import net.mat0u5.lifeseries.config.entries.*;
import net.mat0u5.lifeseries.gui.config.ConfigScreen;
import net.mat0u5.lifeseries.gui.config.entries.ConfigEntry;
import net.mat0u5.lifeseries.network.NetworkHandlerClient;
import net.mat0u5.lifeseries.network.packets.ConfigPayload;

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
        String configType = payload.configType();
        String id = payload.id();
        String name = payload.name();
        String description = payload.description();
        List<String> args = payload.args();
        if (args.size() < 3) return null;
        String argValue = args.get(0);
        String argDefaultValue = args.get(1);
        String argGroupInfo = args.get(2);

        if (configType.equalsIgnoreCase("text")) {
            boolean clickable = !argDefaultValue.equalsIgnoreCase("false");
            return new TextObject(payload, name, clickable);
        }
        if (configType.equalsIgnoreCase("string")) {
            return new StringObject(payload, argValue, argDefaultValue);
        }
        if (configType.equalsIgnoreCase("boolean")) {
            boolean value = argValue.equalsIgnoreCase("true");
            boolean defaultValue = argDefaultValue.equalsIgnoreCase("true");
            return new BooleanObject(payload, value, defaultValue);
        }
        if (configType.equalsIgnoreCase("double")) {
            try {
                double value = Double.parseDouble(argValue);
                double defaultValue = Double.parseDouble(argDefaultValue);
                return new DoubleObject(payload, value, defaultValue);
            }catch(Exception e){}
        }
        if (configType.equalsIgnoreCase("integer")) {
            try {
                int value = Integer.parseInt(argValue);
                int defaultValue = Integer.parseInt(argDefaultValue);
                return new IntegerObject(payload, value, defaultValue);
            }catch(Exception e){}
        }

        return null;
    }
}
