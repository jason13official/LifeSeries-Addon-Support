package com.cursee.ls_addon_support.utils.player;

import static com.cursee.ls_addon_support.LSAddonSupport.server;

import com.cursee.ls_addon_support.LSAddonSupport;
import com.cursee.ls_addon_support.utils.other.TextUtils;
import java.util.Collection;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class TeamUtils {

  public static void createTeam(String teamName, Formatting color) {
    createTeam(teamName, teamName, color);
  }

  public static void createTeam(String teamName, String displayName, Formatting color) {
      if (server == null) {
          return;
      }
    Scoreboard scoreboard = server.getScoreboard();
    if (scoreboard.getTeam(teamName) != null) {
      // A team with this name already exists
      return;
    }
    Team team = scoreboard.addTeam(teamName);
    team.setDisplayName(Text.literal(displayName).formatted(color));
    team.setColor(color);
  }

  public static void addEntityToTeam(String teamName, Entity entity) {
      if (server == null) {
          return;
      }
    Scoreboard scoreboard = server.getScoreboard();
    Team team = scoreboard.getTeam(teamName);

    if (team == null) {
      // A team with this name does not exist
      return;
    }

    scoreboard.addScoreHolderToTeam(entity.getNameForScoreboard(), team);
  }

  public static boolean removePlayerFromTeam(ServerPlayerEntity player) {
      if (server == null) {
          return false;
      }
    Scoreboard scoreboard = server.getScoreboard();
    String playerName = player.getNameForScoreboard();

    Team team = scoreboard.getScoreHolderTeam(playerName);
    if (team == null) {
      LSAddonSupport.LOGGER.warn(
          TextUtils.formatString("Player {} is not part of any team!", playerName));
      return false;
    }

    scoreboard.removeScoreHolderFromTeam(playerName, team);
    return true;
  }

  public static boolean deleteTeam(String teamName) {
      if (server == null) {
          return false;
      }
    Scoreboard scoreboard = server.getScoreboard();
    Team team = scoreboard.getTeam(teamName);

    if (team == null) {
      return false;
    }

    scoreboard.removeTeam(team);
    return true;
  }

  public static Team getTeam(String teamName) {
      if (server == null) {
          return null;
      }
    Scoreboard scoreboard = server.getScoreboard();
    return scoreboard.getTeam(teamName);
  }

  public static Team getPlayerTeam(ServerPlayerEntity player) {
      if (server == null) {
          return null;
      }
    Scoreboard scoreboard = server.getScoreboard();
    return scoreboard.getScoreHolderTeam(player.getNameForScoreboard());
  }

  public static Collection<Team> getAllTeams() {
      if (server == null) {
          return null;
      }
    Scoreboard scoreboard = server.getScoreboard();
    return scoreboard.getTeams();
  }
}
