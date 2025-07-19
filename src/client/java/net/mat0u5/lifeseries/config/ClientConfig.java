package net.mat0u5.lifeseries.config;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.MainClient;
import net.mat0u5.lifeseries.seasons.season.Seasons;

import java.util.List;

public class ClientConfig extends ConfigManager {
    public ClientConfig() {
        super("./config/lifeseries/client", Main.MOD_ID+"_client.properties");
    }

    public static final ConfigFileEntry<Boolean> MINIMAL_ARMOR = new ConfigFileEntry<>(
            "minimal_armor", true, "", "Minimal Armor Resourcepack", "Enables the minimal armor resourcepack."
    );
    public static final ConfigFileEntry<Boolean> SESSION_TIMER = new ConfigFileEntry<>(
            "session_timer", true, "", "Session Timer", "Enables the session timer in the bottom right of the screen."
    );
    public static final ConfigFileEntry<Boolean> SESSION_TIMER_LIMITEDLIFE = new ConfigFileEntry<>(
            "session_timer_limitedlife", false, "", "Session Timer", "Enables the session timer in the bottom right of the screen."
    );
    public static final ConfigFileEntry<Boolean> COLORBLIND_SUPPORT = new ConfigFileEntry<>(
            "colorblind_support", false, "", "Colorblind Support", "Enables a feature that shows the team color name next to a players' usernames."
    );

    @Override
    protected List<ConfigFileEntry<?>> getDefaultConfigEntries() {
        ConfigFileEntry<?> sessionTimer = SESSION_TIMER;
        if (MainClient.clientCurrentSeason == Seasons.LIMITED_LIFE) {
            sessionTimer = SESSION_TIMER_LIMITEDLIFE;
        }

        return List.of(
                MINIMAL_ARMOR
                ,sessionTimer
                ,COLORBLIND_SUPPORT
        );
    }
}
