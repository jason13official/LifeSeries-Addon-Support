package com.cursee.ls_addon_support.seasons.season.aprilfools.reallife;

import com.cursee.ls_addon_support.config.ConfigManager;
import com.cursee.ls_addon_support.seasons.season.Seasons;
import com.cursee.ls_addon_support.seasons.season.thirdlife.ThirdLife;

public class RealLife extends ThirdLife {

  @Override
  public Seasons getSeason() {
    return Seasons.REAL_LIFE;
  }

  @Override
  public ConfigManager createConfig() {
    return new RealLifeConfig();
  }
}
