package com.cursee.ls_addon_support.seasons.season.aprilfools.reallife;

import com.cursee.ls_addon_support.LSAddonSupport;
import com.cursee.ls_addon_support.seasons.season.thirdlife.ThirdLifeConfig;

public class RealLifeConfig extends ThirdLifeConfig {

  public RealLifeConfig() {
    super("./config/" + LSAddonSupport.MOD_ID + "/aprilfools", "reallife.properties");
  }
}