package net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard;

import net.mat0u5.lifeseries.series.wildlife.wildcards.Wildcard;
import net.mat0u5.lifeseries.series.wildlife.wildcards.Wildcards;

public class Callback extends Wildcard {
    @Override
    public Wildcards getType() {
        return Wildcards.CALLBACK;
    }
}
