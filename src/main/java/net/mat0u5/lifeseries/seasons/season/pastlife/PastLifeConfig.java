package net.mat0u5.lifeseries.seasons.season.pastlife;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.seasons.season.thirdlife.ThirdLifeConfig;

public class PastLifeConfig extends ThirdLifeConfig {
    public PastLifeConfig() {
        super("./config/"+ Main.MOD_ID,"pastlife.properties");
    }
}
