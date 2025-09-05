package com.cursee.ls_addon_support.seasons.season.thirdlife;

import com.cursee.ls_addon_support.config.ConfigManager;
import com.cursee.ls_addon_support.seasons.season.Season;
import com.cursee.ls_addon_support.seasons.season.Seasons;

public class ThirdLife extends Season {

  public static final String COMMANDS_ADMIN_TEXT = "/lifeseries, /session, /claimkill, /lives";
  public static final String COMMANDS_TEXT = "/claimkill, /lives";

  @Override
  public Seasons getSeason() {
    return Seasons.THIRD_LIFE;
  }

  @Override
  public ConfigManager createConfig() {
    return new ThirdLifeConfig();
  }

  @Override
  public String getAdminCommands() {
    return COMMANDS_ADMIN_TEXT;
  }

  @Override
  public String getNonAdminCommands() {
    return COMMANDS_TEXT;
  }
}
