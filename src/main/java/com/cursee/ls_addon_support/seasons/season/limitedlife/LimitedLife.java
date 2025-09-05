package com.cursee.ls_addon_support.seasons.season.limitedlife;

import static com.cursee.ls_addon_support.LSAddonSupport.currentSession;
import static com.cursee.ls_addon_support.LSAddonSupport.seasonConfig;

import com.cursee.ls_addon_support.config.ConfigManager;
import com.cursee.ls_addon_support.network.NetworkHandlerServer;
import com.cursee.ls_addon_support.seasons.boogeyman.BoogeymanManager;
import com.cursee.ls_addon_support.seasons.other.LivesManager;
import com.cursee.ls_addon_support.seasons.season.Season;
import com.cursee.ls_addon_support.seasons.season.Seasons;
import com.cursee.ls_addon_support.utils.enums.PacketNames;
import com.cursee.ls_addon_support.utils.enums.SessionTimerStates;
import com.cursee.ls_addon_support.utils.other.OtherUtils;
import com.cursee.ls_addon_support.utils.player.PlayerUtils;
import com.cursee.ls_addon_support.utils.player.ScoreboardUtils;
import java.util.Collection;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.ScoreboardEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class LimitedLife extends Season {

  public static final String COMMANDS_ADMIN_TEXT = "/lifeseries, /session, /claimkill, /lives, /boogeyman";
  public static final String COMMANDS_TEXT = "/claimkill, /lives";
  public static boolean TICK_OFFLINE_PLAYERS = false;
  private final boolean SHOW_DEATH_TITLE = true;
  private int DEATH_NORMAL = -3600;
  private int DEATH_BOOGEYMAN = -7200;
  private int KILL_NORMAL = 1800;
  private int KILL_BOOGEYMAN = 3600;
  private int secondCounter = 0;

  @Override
  public Seasons getSeason() {
    return Seasons.LIMITED_LIFE;
  }

  @Override
  public ConfigManager createConfig() {
    return new LimitedLifeConfig();
  }

  @Override
  public BoogeymanManager createBoogeymanManager() {
    return new LimitedLifeBoogeymanManager();
  }

  @Override
  public LivesManager createLivesManager() {
    return new LimitedLifeLivesManager();
  }

  @Override
  public String getAdminCommands() {
    return COMMANDS_ADMIN_TEXT;
  }

  @Override
  public String getNonAdminCommands() {
    return COMMANDS_TEXT;
  }

  public void displayTimers(MinecraftServer server) {
    String message = "";
    if (currentSession.statusNotStarted()) {
      message = "Session has not started";
    } else if (currentSession.statusStarted()) {
      message = currentSession.getRemainingTimeStr();
    } else if (currentSession.statusPaused()) {
      message = "Session has been paused";
    } else if (currentSession.statusFinished()) {
      message = "Session has ended";
    }

    for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {

      if (NetworkHandlerServer.wasHandshakeSuccessful(player)) {
        long timestamp = SessionTimerStates.OFF.getValue();
          if (currentSession.statusNotStarted()) {
              timestamp = SessionTimerStates.NOT_STARTED.getValue();
          } else if (currentSession.statusPaused()) {
              timestamp = SessionTimerStates.PAUSED.getValue();
          } else if (currentSession.statusFinished()) {
              timestamp = SessionTimerStates.ENDED.getValue();
          } else if (currentSession.sessionLength != null) {
              long remainingMillis =
                  (currentSession.sessionLength - (int) currentSession.passedTime) * 50L;
              timestamp = System.currentTimeMillis() + remainingMillis;
          }
        if (timestamp != SessionTimerStates.OFF.getValue()) {
          NetworkHandlerServer.sendLongPacket(player, PacketNames.SESSION_TIMER, timestamp);
        }

        if (livesManager.hasAssignedLives(player) && livesManager.getPlayerLives(player) != null) {
          long playerLives;
          if (livesManager.isAlive(player)) {
            Integer playerLivesInt = livesManager.getPlayerLives(player);
            playerLives = playerLivesInt == null ? -1 : playerLivesInt;
          } else {
            playerLives = -1;
          }
          String livesColor = livesManager.getColorForLives(player).toString();
          NetworkHandlerServer.sendLongPacket(player,
              PacketNames.fromName(PacketNames.LIMITED_LIFE_TIMER.getName() + livesColor),
              playerLives);
        }
      } else {
        MutableText fullMessage = Text.empty();
        if (currentSession.displayTimer.contains(player.getUuid())) {
          fullMessage.append(Text.literal(message).formatted(Formatting.GRAY));
        }
        if (livesManager.hasAssignedLives(player)) {
            if (!fullMessage.getString().isEmpty()) {
                fullMessage.append(Text.of("  |  "));
            }
          fullMessage.append(livesManager.getFormattedLives(player));
        }
        player.sendMessage(fullMessage, true);
      }
    }
  }

  @Override
  public void tickSessionOn(MinecraftServer server) {
    super.tickSessionOn(server);
      if (!currentSession.statusStarted()) {
          return;
      }

    secondCounter--;
    if (secondCounter <= 0) {
      secondCounter = 20;
      livesManager.getAlivePlayers().forEach(livesManager::removePlayerLife);

      if (TICK_OFFLINE_PLAYERS) {
        Collection<ScoreboardEntry> entries = ScoreboardUtils.getScores(
            LivesManager.SCOREBOARD_NAME);
        for (ScoreboardEntry entry : entries) {
            if (entry.value() <= 0) {
                continue;
            }
            if (PlayerUtils.getPlayer(entry.owner()) != null) {
                continue;
            }
          ScoreboardUtils.setScore(ScoreHolder.fromName(entry.owner()),
              LivesManager.SCOREBOARD_NAME, entry.value() - 1);
        }
      }
    }
  }

  @Override
  public void onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
    if (source != null) {
      if (source.getAttacker() instanceof ServerPlayerEntity serverAttacker) {
        if (player != source.getAttacker()) {
          onPlayerKilledByPlayer(player, serverAttacker);
          return;
        }
      }
    }
    if (player.getPrimeAdversary() != null) {
      if (player.getPrimeAdversary() instanceof ServerPlayerEntity serverAdversary) {
        if (player != player.getPrimeAdversary()) {
          onPlayerKilledByPlayer(player, serverAdversary);
          return;
        }
      }
    }
    onPlayerDiedNaturally(player);
    livesManager.addToPlayerLives(player, DEATH_NORMAL);
    if (livesManager.isAlive(player)) {
      PlayerUtils.sendTitle(player,
          Text.literal(OtherUtils.formatSecondsToReadable(DEATH_NORMAL)).formatted(Formatting.RED),
          20, 80, 20);
    }
  }

  @Override
  public void onClaimKill(ServerPlayerEntity killer, ServerPlayerEntity victim) {
    boolean wasAllowedToAttack = isAllowedToAttack(killer, victim, false);
    boolean wasBoogeyCure = boogeymanManager.isBoogeymanThatCanBeCured(killer, victim);
    super.onClaimKill(killer, victim);

    if (!wasBoogeyCure) {
      if (wasAllowedToAttack) {
        livesManager.addToPlayerLives(killer, KILL_NORMAL);
        PlayerUtils.sendTitle(killer, Text.literal(OtherUtils.formatSecondsToReadable(KILL_NORMAL))
            .formatted(Formatting.GREEN), 20, 80, 20);
      }
    } else {
      //Victim was killed by boogeyman - remove 2 hours from victim and add 1 hour to boogey

      boolean wasAlive = false;

      String msgVictim = OtherUtils.formatSecondsToReadable(DEATH_BOOGEYMAN - DEATH_NORMAL);
      String msgKiller = OtherUtils.formatSecondsToReadable(KILL_BOOGEYMAN);

      if (livesManager.isAlive(victim)) {
        livesManager.addToPlayerLives(victim, DEATH_BOOGEYMAN - DEATH_NORMAL);
        wasAlive = true;
      }
      livesManager.addToPlayerLives(killer, KILL_BOOGEYMAN);
      if (livesManager.isAlive(victim)) {
        PlayerUtils.sendTitle(killer, Text.literal(msgKiller).formatted(Formatting.GREEN), 20, 80,
            20);
        PlayerUtils.sendTitle(victim, Text.literal(msgVictim).formatted(Formatting.RED), 20, 80,
            20);
      } else if (wasAlive && SHOW_DEATH_TITLE) {
        PlayerUtils.sendTitleWithSubtitle(killer,
            Text.literal(msgKiller).formatted(Formatting.GREEN),
            livesManager.getDeathMessage(victim),
            20, 80, 20);
      } else {
        PlayerUtils.sendTitle(killer, Text.literal(msgKiller).formatted(Formatting.GREEN), 20, 80,
            20);
      }
    }
  }

  @Override
  public void onPlayerKilledByPlayer(ServerPlayerEntity victim, ServerPlayerEntity killer) {
    boolean wasAllowedToAttack = isAllowedToAttack(killer, victim, false);
    boolean wasBoogeyCure = boogeymanManager.isBoogeymanThatCanBeCured(killer, victim);
    super.onPlayerKilledByPlayer(victim, killer);

    if (!wasBoogeyCure) {
      boolean wasFinalKill = livesManager.isAlive(victim) || !SHOW_DEATH_TITLE;
      if (wasAllowedToAttack) {
        String msgKiller = OtherUtils.formatSecondsToReadable(KILL_NORMAL);
        livesManager.addToPlayerLives(killer, KILL_NORMAL);
        if (wasFinalKill) {
          PlayerUtils.sendTitle(killer, Text.literal(msgKiller).formatted(Formatting.GREEN), 20, 80,
              20);
        } else {
          PlayerUtils.sendTitleWithSubtitle(killer,
              Text.literal(msgKiller).formatted(Formatting.GREEN),
              livesManager.getDeathMessage(victim),
              20, 80, 20);
        }
      }
      String msgVictim = OtherUtils.formatSecondsToReadable(DEATH_NORMAL);
      livesManager.addToPlayerLives(victim, DEATH_NORMAL);
      if (wasFinalKill) {
        PlayerUtils.sendTitle(victim, Text.literal(msgVictim).formatted(Formatting.RED), 20, 80,
            20);
      }
    } else {

      //Victim was killed by boogeyman - remove 2 hours from victim and add 1 hour to boogey
      String msgVictim = OtherUtils.formatSecondsToReadable(DEATH_BOOGEYMAN);
      String msgKiller = OtherUtils.formatSecondsToReadable(KILL_BOOGEYMAN);
      livesManager.addToPlayerLives(victim, DEATH_BOOGEYMAN);
      livesManager.addToPlayerLives(killer, KILL_BOOGEYMAN);

      if (livesManager.isAlive(victim) || !SHOW_DEATH_TITLE) {
        PlayerUtils.sendTitle(victim, Text.literal(msgVictim).formatted(Formatting.RED), 20, 80,
            20);
        PlayerUtils.sendTitleWithSubtitle(killer, Text.of("§aYou are cured!"),
            Text.literal(msgKiller).formatted(Formatting.GREEN), 20, 80, 20);
      } else {
        PlayerUtils.sendTitleWithSubtitle(killer, Text.of("§aYou are cured, " + msgKiller),
            livesManager.getDeathMessage(victim)
            , 20, 80, 20);
      }
    }
  }

  @Override
  public boolean isAllowedToAttack(ServerPlayerEntity attacker, ServerPlayerEntity victim,
      boolean allowSelfDefense) {
      if (livesManager.isOnSpecificLives(attacker, 2, false) && livesManager.isOnAtLeastLives(
          victim,
          3, false)) {
          return true;
      }
    return super.isAllowedToAttack(attacker, victim, allowSelfDefense);
  }

  @Override
  public void reload() {
    super.reload();
      if (!(seasonConfig instanceof LimitedLifeConfig config)) {
          return;
      }
    LimitedLifeLivesManager.DEFAULT_TIME = LimitedLifeConfig.TIME_DEFAULT.get(config);
    LimitedLifeLivesManager.YELLOW_TIME = LimitedLifeConfig.TIME_YELLOW.get(config);
    LimitedLifeLivesManager.RED_TIME = LimitedLifeConfig.TIME_RED.get(config);
    DEATH_NORMAL = LimitedLifeConfig.TIME_DEATH.get(config);
    DEATH_BOOGEYMAN = LimitedLifeConfig.TIME_DEATH_BOOGEYMAN.get(config);
    KILL_NORMAL = LimitedLifeConfig.TIME_KILL.get(config);
    KILL_BOOGEYMAN = LimitedLifeConfig.TIME_KILL_BOOGEYMAN.get(config);
    TICK_OFFLINE_PLAYERS = LimitedLifeConfig.TICK_OFFLINE_PLAYERS.get(config);
    LimitedLifeLivesManager.BROADCAST_COLOR_CHANGES = LimitedLifeConfig.BROADCAST_COLOR_CHANGES.get(
        config);
  }

  @Override
  public Integer getDefaultLives() {
    return LimitedLifeLivesManager.DEFAULT_TIME;
  }
}
