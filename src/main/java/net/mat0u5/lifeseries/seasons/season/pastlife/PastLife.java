package net.mat0u5.lifeseries.seasons.season.pastlife;

import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.seasons.season.Season;
import net.mat0u5.lifeseries.seasons.season.Seasons;

public class PastLife extends Season {
    public static final String COMMANDS_ADMIN_TEXT = "/lifeseries, /session, /claimkill, /lives, /boogeyman, /society, /pastlife";
    public static final String COMMANDS_TEXT = "/claimkill, /lives, /society, /initiate";
    @Override
    public Seasons getSeason() {
        return Seasons.PAST_LIFE;
    }

    @Override
    public ConfigManager createConfig() {
        return new PastLifeConfig();
    }

    @Override
    public String getAdminCommands() {
        return "";
    }

    @Override
    public String getNonAdminCommands() {
        return "";
    }
}
