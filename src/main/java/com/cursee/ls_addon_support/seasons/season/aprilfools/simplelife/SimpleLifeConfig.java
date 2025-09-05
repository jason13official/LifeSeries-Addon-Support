package com.cursee.ls_addon_support.seasons.season.aprilfools.simplelife;

import com.cursee.ls_addon_support.LSAddonSupport;
import com.cursee.ls_addon_support.seasons.season.thirdlife.ThirdLifeConfig;

public class SimpleLifeConfig extends ThirdLifeConfig {

  public SimpleLifeConfig() {
    super("./config/" + LSAddonSupport.MOD_ID + "/aprilfools", "simplelife.properties");
  }
}
