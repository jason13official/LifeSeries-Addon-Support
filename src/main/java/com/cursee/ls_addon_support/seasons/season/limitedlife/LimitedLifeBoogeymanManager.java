package com.cursee.ls_addon_support.seasons.season.limitedlife;

import static com.cursee.ls_addon_support.LSAddonSupport.livesManager;
import static com.cursee.ls_addon_support.LSAddonSupport.server;

import com.cursee.ls_addon_support.seasons.boogeyman.Boogeyman;
import com.cursee.ls_addon_support.seasons.boogeyman.BoogeymanManager;
import com.cursee.ls_addon_support.seasons.other.LivesManager;
import com.cursee.ls_addon_support.utils.other.TextUtils;
import com.cursee.ls_addon_support.utils.player.PlayerUtils;
import com.cursee.ls_addon_support.utils.player.ScoreboardUtils;
import java.util.List;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class LimitedLifeBoogeymanManager extends BoogeymanManager {

  @Override
  public void sessionEnd() {
      if (!BOOGEYMAN_ENABLED) {
          return;
      }
      if (server == null) {
          return;
      }
    for (Boogeyman boogeyman : boogeymen) {
        if (boogeyman.died) {
            continue;
        }

      if (!boogeyman.cured) {
        ServerPlayerEntity player = PlayerUtils.getPlayer(boogeyman.uuid);
        if (player == null) {
          Integer currentLives = ScoreboardUtils.getScore(ScoreHolder.fromName(boogeyman.name),
              LivesManager.SCOREBOARD_NAME);
            if (currentLives == null) {
                continue;
            }
            if (currentLives <= LimitedLifeLivesManager.RED_TIME) {
                continue;
            }

          if (BOOGEYMAN_ANNOUNCE_OUTCOME) {
            PlayerUtils.broadcastMessage(TextUtils.format(
                "{}§7 failed to kill a player while being the §cBoogeyman§7. They have been dropped to their §cLast Life§7.",
                boogeyman.name));
          }
          if (currentLives > LimitedLifeLivesManager.RED_TIME
              && currentLives <= LimitedLifeLivesManager.YELLOW_TIME) {
            ScoreboardUtils.setScore(ScoreHolder.fromName(boogeyman.name),
                LivesManager.SCOREBOARD_NAME, LimitedLifeLivesManager.RED_TIME);
          }
          if (currentLives > LimitedLifeLivesManager.YELLOW_TIME) {
            ScoreboardUtils.setScore(ScoreHolder.fromName(boogeyman.name),
                LivesManager.SCOREBOARD_NAME, LimitedLifeLivesManager.YELLOW_TIME);
          }
          continue;
        }
        playerFailBoogeyman(player);
      }
    }
  }

  @Override
  public void playerFailBoogeyman(ServerPlayerEntity player) {
      if (!livesManager.isAlive(player)) {
          return;
      }
      if (livesManager.isOnLastLife(player, true)) {
          return;
      }
    if (livesManager.isOnSpecificLives(player, 3, false)) {
      livesManager.setPlayerLives(player, LimitedLifeLivesManager.YELLOW_TIME);
    } else if (livesManager.isOnSpecificLives(player, 2, false)) {
      livesManager.setPlayerLives(player, LimitedLifeLivesManager.RED_TIME);
    }
    Text setTo = livesManager.getFormattedLives(player);

    PlayerUtils.sendTitle(player, Text.of("§cYou have failed."), 20, 30, 20);
    PlayerUtils.playSoundToPlayer(player,
        SoundEvent.of(Identifier.of("minecraft", "lastlife_boogeyman_fail")));
    if (BOOGEYMAN_ANNOUNCE_OUTCOME) {
      PlayerUtils.broadcastMessage(TextUtils.format(
          "{}§7 failed to kill a player while being the §cBoogeyman§7. Their time has been dropped to {}",
          player, setTo));
    }
  }

  @Override
  public List<ServerPlayerEntity> getRandomBoogeyPlayers(List<ServerPlayerEntity> allowedPlayers,
      BoogeymanRollType rollType) {
    List<ServerPlayerEntity> boogeyPlayers = super.getRandomBoogeyPlayers(allowedPlayers, rollType);
    int chooseBoogeymen = getBoogeymanAmount(rollType) - boogeyPlayers.size();
    if (chooseBoogeymen > 0) {
      for (ServerPlayerEntity player : livesManager.getRedPlayers()) {
        // Third loop for red boogeymen if necessary
          if (chooseBoogeymen <= 0) {
              break;
          }
          if (!allowedPlayers.contains(player)) {
              continue;
          }
          if (rolledPlayers.contains(player.getUuid())) {
              continue;
          }
          if (BOOGEYMAN_IGNORE.contains(player.getNameForScoreboard().toLowerCase())) {
              continue;
          }
          if (BOOGEYMAN_FORCE.contains(player.getNameForScoreboard().toLowerCase())) {
              continue;
          }
          if (boogeyPlayers.contains(player)) {
              continue;
          }

        boogeyPlayers.add(player);
        chooseBoogeymen--;
      }
    }

    return boogeyPlayers;
  }

  @Override
  public List<ServerPlayerEntity> getAllowedBoogeyPlayers() {
    return livesManager.getAlivePlayers();
  }
}
