package com.cursee.ls_addon_support.seasons.season.pastlife;

import com.cursee.ls_addon_support.config.ConfigManager;
import com.cursee.ls_addon_support.seasons.season.Season;
import com.cursee.ls_addon_support.seasons.season.Seasons;

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
