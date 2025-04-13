package net.mat0u5.lifeseries.series.aprilfools.simplelife;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.series.thirdlife.ThirdLifeConfig;

public class SimpleLifeConfig extends ThirdLifeConfig {
    public SimpleLifeConfig() {
        super("./config/"+ Main.MOD_ID+"/aprilfools","simplelife.properties");
    }
}
