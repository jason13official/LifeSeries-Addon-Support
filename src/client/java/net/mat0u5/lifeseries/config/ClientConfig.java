package net.mat0u5.lifeseries.config;

import net.mat0u5.lifeseries.Main;

import java.util.List;

public class ClientConfig extends ConfigManager {
    public ClientConfig() {
        super("./config/lifeseries/client", Main.MOD_ID+"_client.properties");
    }

    public static final ConfigFileEntry<Boolean> MINIMAL_ARMOR = new ConfigFileEntry<>(
            "minimal_armor", true, "", "Minimal Armor Resourcepack", "Enables / Disables the minimal armor resourcepack."
    );

    @Override
    protected List<ConfigFileEntry<?>> getDefaultConfigEntries() {
        return List.of(
                MINIMAL_ARMOR
        );
    }
}
