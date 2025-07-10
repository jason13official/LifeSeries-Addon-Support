package net.mat0u5.lifeseries.config;

import net.mat0u5.lifeseries.Main;

import java.util.List;

public class ClientConfig extends ConfigManager {
    public ClientConfig() {
        super("./config/lifeseries/client", Main.MOD_ID+"_client.properties");
    }

    public static final ConfigFileEntry<Integer> TEST = new ConfigFileEntry<>(
            "test1", 5, "integer", "", "Test Integer 1", "Description."
    );
    public static final ConfigFileEntry<Integer> TEST2 = new ConfigFileEntry<>(
            "test2", 5, "integer", "group", "Test Integer 2", "Description."
    );
    public static final ConfigFileEntry<Boolean> TEST3 = new ConfigFileEntry<>(
            "test3", true, "boolean", "group", "Test Bool", "Description."
    );
    public static final ConfigFileEntry<Object> GROUP = new ConfigFileEntry<>(
            "test4", null, "text", "{group}", "Test group", "Description."
    );

    @Override
    protected List<ConfigFileEntry<?>> getDefaultConfigEntries() {
        return List.of(
                TEST,
                GROUP,
                TEST2,
                TEST3
        );
    }
}
