package net.mat0u5.lifeseries.seasons.season.pastlife;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.seasons.season.thirdlife.ThirdLifeConfig;
import net.mat0u5.lifeseries.utils.other.TextUtils;

public class PastLifeConfig extends ConfigManager {
    public PastLifeConfig() {
        super("./config/"+ Main.MOD_ID,"pastlife.properties");
    }

    @Override
    public void instantiateProperties() {
        BLACKLIST_ITEMS.defaultValue = TextUtils.formatString("[{}]", ThirdLifeConfig.BLACKLISTED_ITEMS);
        BLACKLIST_BLOCKS.defaultValue = TextUtils.formatString("[{}]", ThirdLifeConfig.BLACKLISTED_BLOCKS);
        BLACKLIST_CLAMPED_ENCHANTS.defaultValue = TextUtils.formatString("[{}]", ThirdLifeConfig.CLAMPED_ENCHANTMENTS);
        DEFAULT_LIVES.defaultValue = 6;
        BOOGEYMAN.defaultValue = true;
        SECRET_SOCIETY.defaultValue = true;
        super.instantiateProperties();
    }
}
