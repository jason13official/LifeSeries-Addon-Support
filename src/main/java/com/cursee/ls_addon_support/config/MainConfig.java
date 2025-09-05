package com.cursee.ls_addon_support.config;

import com.cursee.ls_addon_support.LSAddonSupport;
import java.util.List;

public class MainConfig extends ConfigManager {

  public MainConfig() {
    super("./config/lifeseries/main", LSAddonSupport.MOD_ID + ".properties");
  }

  @Override
  protected List<ConfigFileEntry<?>> getDefaultConfigEntries() {
    return List.of();
  }

  @Override
  public void instantiateProperties() {
    getOrCreateProperty("currentSeries", LSAddonSupport.DEFAULT_SEASON.getId());
  }
}
