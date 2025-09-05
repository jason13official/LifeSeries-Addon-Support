package com.cursee.ls_addon_support.seasons.other;

import static com.cursee.ls_addon_support.LSAddonSupport.currentSeason;
import static com.cursee.ls_addon_support.LSAddonSupport.livesManager;

import com.cursee.ls_addon_support.seasons.season.doublelife.DoubleLife;
import com.cursee.ls_addon_support.utils.player.PlayerUtils;
import com.cursee.ls_addon_support.utils.player.ScoreboardUtils;
import com.cursee.ls_addon_support.utils.player.TeamUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.ScoreboardEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;

public class WatcherManager {

  public static final String SCOREBOARD_NAME = "Watchers";
  public static final String TEAM_NAME = "watcher";
  public static final String TEAM_DISPLAY_NAME = "Watcher";
  private static final List<String> watchers = new ArrayList<>();

  public static void createTeams() {
    TeamUtils.createTeam(WatcherManager.TEAM_NAME, WatcherManager.TEAM_DISPLAY_NAME,
        Formatting.DARK_GRAY);
  }

  public static void createScoreboards() {
    ScoreboardUtils.createObjective(WatcherManager.SCOREBOARD_NAME);
  }

  public static void reloadWatchers() {
    watchers.clear();
    Collection<ScoreboardEntry> entries = ScoreboardUtils.getScores(SCOREBOARD_NAME);
      if (entries == null || entries.isEmpty()) {
          return;
      }
    for (ScoreboardEntry entry : entries) {
        if (entry.value() <= 0) {
            continue;
        }
      watchers.add(entry.owner());
    }
  }

  public static void addWatcher(ServerPlayerEntity player) {
    watchers.add(player.getNameForScoreboard());
    ScoreboardUtils.setScore(player, SCOREBOARD_NAME, 1);
    livesManager.resetPlayerLife(player);
    player.changeGameMode(GameMode.SPECTATOR);
    if (currentSeason instanceof DoubleLife doubleLife) {
      doubleLife.resetSoulmate(player);
    }
    player.sendMessage(Text.of("§7§nYou are now a Watcher.\n"));
    player.sendMessage(Text.of(
        "§7Watchers are players that are online, but are not affected by most season mechanics. They can only observe - this is very useful for spectators and for admins."));
    player.sendMessage(
        Text.of("§8§oNOTE: This is an experimental feature, report any bugs you find!"));
  }

  public static void removeWatcher(ServerPlayerEntity player) {
    watchers.remove(player.getNameForScoreboard());
    ScoreboardUtils.resetScore(player, SCOREBOARD_NAME);
    livesManager.resetPlayerLife(player);
    player.sendMessage(Text.of("§7You are no longer a Watcher."));
  }

  public static boolean isWatcher(PlayerEntity player) {
    return watchers.contains(player.getNameForScoreboard());
  }

  public static boolean isWatcher(String playerName) {
    return watchers.contains(playerName);
  }

  private static boolean isNotWatcher(PlayerEntity player) {
    return !isWatcher(player);
  }

  public static List<String> getWatchers() {
    return watchers;
  }

  public static List<ServerPlayerEntity> getWatcherPlayers() {
    List<ServerPlayerEntity> watcherPlayers = PlayerUtils.getAllPlayers();
    watcherPlayers.removeIf(WatcherManager::isNotWatcher);
    return watcherPlayers;
  }
}
