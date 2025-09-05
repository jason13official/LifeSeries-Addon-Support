package com.cursee.ls_addon_support.seasons.season.pastlife;

import com.cursee.ls_addon_support.LSAddonSupport;
import com.cursee.ls_addon_support.config.ConfigManager;
import com.cursee.ls_addon_support.seasons.season.thirdlife.ThirdLifeConfig;
import com.cursee.ls_addon_support.utils.other.TextUtils;

public class PastLifeConfig extends ConfigManager {

  public PastLifeConfig() {
    super("./config/" + LSAddonSupport.MOD_ID, "pastlife.properties");
  }

  @Override
  public void instantiateProperties() {
    BLACKLIST_ITEMS.defaultValue = TextUtils.formatString("[{}]",
        ThirdLifeConfig.BLACKLISTED_ITEMS);
    BLACKLIST_BLOCKS.defaultValue = TextUtils.formatString("[{}]",
        ThirdLifeConfig.BLACKLISTED_BLOCKS);
    BLACKLIST_CLAMPED_ENCHANTS.defaultValue = TextUtils.formatString("[{}]",
        ThirdLifeConfig.CLAMPED_ENCHANTMENTS);
    DEFAULT_LIVES.defaultValue = 6;
    BOOGEYMAN.defaultValue = true;
    SECRET_SOCIETY.defaultValue = true;
    super.instantiateProperties();
  }
}
