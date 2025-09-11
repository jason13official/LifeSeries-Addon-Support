package com.cursee.ls_addon_support.utils.interfaces;

import com.cursee.ls_addon_support.seasons.season.Seasons;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.Wildcards;
import java.util.List;
import java.util.UUID;

public interface IClientHelper {

  boolean isRunningIntegratedServer();

  boolean isMainClientPlayer(UUID uuid);

  Seasons getCurrentSeason();

  List<String> getActiveWildcards();
}
