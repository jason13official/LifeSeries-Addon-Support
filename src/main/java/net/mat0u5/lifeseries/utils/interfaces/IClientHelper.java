package net.mat0u5.lifeseries.utils.interfaces;

import net.mat0u5.lifeseries.series.SeriesList;
import net.mat0u5.lifeseries.series.wildlife.wildcards.Wildcards;

import java.util.List;
import java.util.UUID;

public interface IClientHelper {
    boolean isRunningIntegratedServer();
    boolean isMainClientPlayer(UUID uuid);
    SeriesList getCurrentSeries();
    List<Wildcards>  getActiveWildcards();
}
