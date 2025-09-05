package com.cursee.ls_addon_support.seasons.season.secretlife;

import static com.cursee.ls_addon_support.LSAddonSupport.livesManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.RawFilteredPair;
import net.minecraft.text.Text;

public class Task {

  public String rawTask;
  public TaskTypes type;
  public boolean anyPlayers = true;
  public boolean anyGreenPlayers = true;
  public boolean anyYellowPlayers = true;

  public Task(String task, TaskTypes type) {
    this.rawTask = task;
    this.type = type;
  }

  public void checkPlayerColors(ServerPlayerEntity owner) {
    anyGreenPlayers = livesManager.anyGreenPlayers(owner);
    anyYellowPlayers = livesManager.anyYellowPlayers(owner);
    anyPlayers = livesManager.anyAlivePlayers(owner);
  }

  public boolean isValid(ServerPlayerEntity owner) {
      if (rawTask == null) {
          return false;
      }
      if (rawTask.isEmpty()) {
          return false;
      }
    checkPlayerColors(owner);
      if (rawTask.contains("${random_player}") && !anyPlayers) {
          return false;
      }
      if (rawTask.contains("${green/yellow}") && !anyGreenPlayers && !anyYellowPlayers) {
          return false;
      }
      if (rawTask.contains("${green}") && !anyGreenPlayers) {
          return false;
      }
    return !rawTask.contains("${yellow}") || anyYellowPlayers;
  }

  /*
  "\n" - Line Break
  "\\p" - Page Break
  ${random_player} - Replaced with random player name.
  ${green/yellow} - Replaced with "green" if there are any alive, or "yellow", if greens are dead. If both are dead, tasks are unavailable.
  ${green} - Replaced with "green". Tasks are only available when a green player is alive.
  ${yellow} - Replaced with "yellow". Tasks are only available when a yellow player is alive.
  ${kill_not_permitted} - For red tasks. If its present, and the task owner kills a person, they will NOT get the 10 hearts for killing someone.
   */
  public List<RawFilteredPair<Text>> getBookLines(ServerPlayerEntity owner) {
    List<RawFilteredPair<Text>> lines = new ArrayList<>();
    for (String page : rawTask.split("\\\\p")) {
      page = formatString(owner, page);
      lines.add(RawFilteredPair.of(Text.of(page)));
    }
    return lines;
  }

  public String formatString(ServerPlayerEntity owner, String page) {
    checkPlayerColors(owner);
    if (page.contains("${random_player}")) {
      List<ServerPlayerEntity> players = livesManager.getAlivePlayers();
      players.remove(owner);
      if (!players.isEmpty()) {
        Collections.shuffle(players);
        page = page.replaceAll("\\$\\{random_player}", players.getFirst().getNameForScoreboard());
      }
    }
    if (page.contains("${green/yellow}")) {
        if (anyGreenPlayers) {
            page = page.replaceAll("\\$\\{green/yellow}", "green");
        } else if (anyYellowPlayers) {
            page = page.replaceAll("\\$\\{green/yellow}", "yellow");
        }
    }
    if (page.contains("${green}")) {
        if (anyGreenPlayers) {
            page = page.replaceAll("\\$\\{green}", "green");
        }
    }
    if (page.contains("${yellow}")) {
        if (anyYellowPlayers) {
            page = page.replaceAll("\\$\\{yellow}", "yellow");
        }
    }
    if (page.contains("${kill_not_permitted}")) {
        if (anyYellowPlayers) {
            page = page.replaceAll("\\$\\{kill_not_permitted}", "");
        }
    }
    return page;
  }

  public int getDifficulty() {
      if (type == TaskTypes.EASY) {
          return 1;
      }
      if (type == TaskTypes.HARD) {
          return 2;
      }
      if (type == TaskTypes.RED) {
          return 3;
      }
    return 0;
  }

  public boolean killPermitted() {
      if (type != TaskTypes.RED) {
          return false;
      }
    return !rawTask.contains("${kill_not_permitted}");
  }
}
