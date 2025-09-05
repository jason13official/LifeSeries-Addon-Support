package com.cursee.ls_addon_support.seasons.season.lastlife;

import static com.cursee.ls_addon_support.LSAddonSupport.currentSession;
import static com.cursee.ls_addon_support.LSAddonSupport.seasonConfig;

import com.cursee.ls_addon_support.config.ConfigManager;
import com.cursee.ls_addon_support.seasons.other.LivesManager;
import com.cursee.ls_addon_support.seasons.season.Season;
import com.cursee.ls_addon_support.seasons.season.Seasons;

public class LastLife extends Season {

  public static final String COMMANDS_ADMIN_TEXT = "/lifeseries, /session, /claimkill, /lives, /givelife, /boogeyman";
  public static final String COMMANDS_TEXT = "/claimkill, /lives, /givelife";
  public static int ROLL_MAX_LIVES = 6;
  public static int ROLL_MIN_LIVES = 2;

  @Override
  public Seasons getSeason() {
    return Seasons.LAST_LIFE;
  }

  @Override
  public ConfigManager createConfig() {
    return new LastLifeConfig();
  }

  @Override
  public LivesManager createLivesManager() {
    return new LastLifeLivesManager();
  }

  @Override
  public String getAdminCommands() {
    return COMMANDS_ADMIN_TEXT;
  }

  @Override
  public String getNonAdminCommands() {
    return COMMANDS_TEXT;
  }

  @Override
  public boolean sessionStart() {
    super.sessionStart();
    if (livesManager instanceof LastLifeLivesManager lastLifeLivesManager) {
      currentSession.activeActions.add(
          lastLifeLivesManager.actionChooseLives
      );
    }
    return true;
  }

  @Override
  public void reload() {
    super.reload();
      if (!(seasonConfig instanceof LastLifeConfig config)) {
          return;
      }
    int minLivesConfig = LastLifeConfig.RANDOM_LIVES_MIN.get(config);
    int maxLivesConfig = LastLifeConfig.RANDOM_LIVES_MAX.get(config);
    ROLL_MIN_LIVES = Math.min(minLivesConfig, maxLivesConfig);
    ROLL_MAX_LIVES = Math.max(minLivesConfig, maxLivesConfig);
  }

  @Override
  public Integer getDefaultLives() {
    return null;
  }
}
