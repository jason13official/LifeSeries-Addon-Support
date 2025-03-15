package net.mat0u5.lifeseries.client.config;

import net.mat0u5.lifeseries.network.NetworkHandlerClient;
import net.mat0u5.lifeseries.network.packets.ConfigPayload;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientsideConfig {

    public static Map<Integer, ConfigObject> config = new HashMap<>();

    public static void load() {
        config.clear();
        NetworkHandlerClient.sendStringPacket("request_config", "");
    }

    public static void save() {
        for (ConfigObject object : config.values()) {
            if (!object.modified) continue;
            NetworkHandlerClient.sendConfigUpdate(
                    object.configType,
                    object.id,
                    -1,
                    object.name,
                    object.description,
                    object.getArgs()
            );
        }
    }

    public static void handleConfigPacket(ConfigPayload payload) {
        String configType = payload.configType();
        int index = payload.index();
        String id = payload.id();
        String name = payload.name();
        String description = payload.description();
        List<String> args = payload.args();

        if (configType.equalsIgnoreCase("string") && args.size() >= 2) {
            StringObject resultObject = new StringObject(payload, args.getFirst(), args.get(1));
            config.put(index, resultObject);
            return;
        }
        if (configType.equalsIgnoreCase("boolean") && args.size() >= 2) {
            boolean value = args.getFirst().equalsIgnoreCase("true");
            boolean defaultValue = args.get(1).equalsIgnoreCase("true");
            BooleanObject resultObject = new BooleanObject(payload, value, defaultValue);
            config.put(index, resultObject);
            return;
        }
        if (configType.equalsIgnoreCase("double") && args.size() >= 2) {
            try {
                double value = Double.parseDouble(args.getFirst());
                double defaultValue = Double.parseDouble(args.get(1));
                DoubleObject resultObject = new DoubleObject(payload, value, defaultValue);
                config.put(index, resultObject);
                return;
            }catch(Exception e){}
        }
        if (configType.equalsIgnoreCase("integer") && args.size() >= 2) {
            try {
                int value = Integer.parseInt(args.getFirst());
                int defaultValue = Integer.parseInt(args.get(1));
                IntegerObject resultObject = new IntegerObject(payload, value, defaultValue);
                config.put(index, resultObject);
                return;
            }catch(Exception e){}
        }

        /*
        ConfigObject resultObject = new ConfigObject(payload);
        config.put(index, resultObject);
        */
    }
}
