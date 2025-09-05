package com.cursee.ls_addon_support.seasons.other;

import static com.cursee.ls_addon_support.LSAddonSupport.currentSeason;
import static com.cursee.ls_addon_support.LSAddonSupport.currentSession;
import static com.cursee.ls_addon_support.LSAddonSupport.seasonConfig;
import static com.cursee.ls_addon_support.seasons.other.WatcherManager.isWatcher;

import com.cursee.ls_addon_support.seasons.season.doublelife.DoubleLife;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.Necromancy;
import com.cursee.ls_addon_support.seasons.session.SessionTranscript;
import com.cursee.ls_addon_support.utils.other.TextUtils;
import com.cursee.ls_addon_support.utils.player.PlayerUtils;
import com.cursee.ls_addon_support.utils.player.ScoreboardUtils;
import com.cursee.ls_addon_support.utils.player.TeamUtils;
import com.cursee.ls_addon_support.utils.world.AnimationUtils;
import com.cursee.ls_addon_support.utils.world.WorldUitls;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.ScoreboardEntry;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;

public class LivesManager {

  public static final String SCOREBOARD_NAME = "Lives";
  public boolean FINAL_DEATH_LIGHTNING = true;
  public SoundEvent FINAL_DEATH_SOUND = SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER;
  public boolean SHOW_DEATH_TITLE = false;
  public boolean ONLY_TAKE_LIVES_IN_SESSION = false;
  public boolean SEE_FRIENDLY_INVISIBLE_PLAYERS = false;

  public void reload() {
    SHOW_DEATH_TITLE = seasonConfig.FINAL_DEATH_TITLE_SHOW.get(seasonConfig);
    FINAL_DEATH_LIGHTNING = seasonConfig.FINAL_DEATH_LIGHTNING.get(seasonConfig);
    FINAL_DEATH_SOUND = SoundEvent.of(
        Identifier.of(seasonConfig.FINAL_DEATH_SOUND.get(seasonConfig)));
    ONLY_TAKE_LIVES_IN_SESSION = seasonConfig.ONLY_TAKE_LIVES_IN_SESSION.get(seasonConfig);
    SEE_FRIENDLY_INVISIBLE_PLAYERS = seasonConfig.SEE_FRIENDLY_INVISIBLE_PLAYERS.get(seasonConfig);
    updateTeams();
  }

  public void updateTeams() {
    Collection<Team> allTeams = TeamUtils.getAllTeams();
      if (allTeams == null) {
          return;
      }
    for (Team team : allTeams) {
      String name = team.getName();
      if (name.startsWith("lives_")) {
        team.setShowFriendlyInvisibles(SEE_FRIENDLY_INVISIBLE_PLAYERS);
      }
    }
  }

  public void createTeams() {
    TeamUtils.createTeam("lives_null", "Unassigned", Formatting.GRAY);
    TeamUtils.createTeam("lives_0", "Dead", Formatting.DARK_GRAY);
    TeamUtils.createTeam("lives_1", "Red", Formatting.RED);
    TeamUtils.createTeam("lives_2", "Yellow", Formatting.YELLOW);
    TeamUtils.createTeam("lives_3", "Green", Formatting.GREEN);
    TeamUtils.createTeam("lives_4", "Dark Green", Formatting.DARK_GREEN);
  }

  public void createScoreboards() {
    ScoreboardUtils.createObjective(SCOREBOARD_NAME);
  }

  public Formatting getColorForLives(ServerPlayerEntity player) {
    return getColorForLives(getPlayerLives(player));
  }

  public Formatting getColorForLives(Integer lives) {
    Team team = TeamUtils.getTeam(getTeamForLives(lives));
    if (team != null) {
      Formatting color = team.getColor();
      if (color != null) {
        return color;
      }
    }
    return Formatting.DARK_GRAY;
  }

  public Text getFormattedLives(ServerPlayerEntity player) {
    return getFormattedLives(getPlayerLives(player));
  }

  public Text getFormattedLives(@Nullable Integer lives) {
    if (lives == null) {
      lives = 0;
    }
    Formatting color = getColorForLives(lives);
    return Text.literal(String.valueOf(lives)).formatted(color);
  }

  public String getTeamForPlayer(ServerPlayerEntity player) {
    Integer lives = getPlayerLives(player);
    return getTeamForLives(lives);
  }

  public String getTeamForLives(Integer lives) {
    String prefix = "lives_";
    String nullTeam = prefix + "null";
    if (lives == null) {
      return nullTeam;
    }
    List<Integer> livesTeams = new ArrayList<>();
    Collection<Team> allTeams = TeamUtils.getAllTeams();
    if (allTeams != null) {
      for (Team team : allTeams) {
        String name = team.getName();
        if (name.startsWith(prefix)) {
          try {
            int index = Integer.parseInt(name.replaceAll(prefix, ""));
            if (index == lives) {
              return name;
            }
            livesTeams.add(index);
          } catch (Exception ignored) {
          }
        }
      }
    }
    if (!livesTeams.isEmpty()) {
      Collections.sort(livesTeams);

      if (lives <= livesTeams.getFirst()) {
        return prefix + livesTeams.getFirst();
      }
      Collections.reverse(livesTeams);
      for (int i : livesTeams) {
        if (lives >= i) {
          return prefix + i;
        }
      }
    }
    return nullTeam;
  }

  @Nullable
  public Integer getPlayerLives(ServerPlayerEntity player) {
      if (isWatcher(player)) {
          return null;
      }
    return ScoreboardUtils.getScore(player, SCOREBOARD_NAME);
  }

  public boolean hasAssignedLives(ServerPlayerEntity player) {
    Integer lives = getPlayerLives(player);
    return lives != null;
  }

  public boolean isAlive(ServerPlayerEntity player) {
    Integer lives = getPlayerLives(player);
      if (lives == null) {
          return false;
      }
      if (!hasAssignedLives(player)) {
          return false;
      }
    return lives > 0;
  }

  public void removePlayerLife(ServerPlayerEntity player) {
    addToPlayerLives(player, -1);
  }

  public void resetPlayerLife(ServerPlayerEntity player) {
    ScoreboardUtils.resetScore(player, SCOREBOARD_NAME);
    currentSeason.reloadPlayerTeam(player);
    currentSeason.assignDefaultLives(player);
    if (currentSeason instanceof DoubleLife doubleLife) {
      doubleLife.syncSoulboundLives(player);
    }
  }

  public void resetAllPlayerLivesInner() {
    createScoreboards();
    for (ScoreboardEntry entry : ScoreboardUtils.getScores(SCOREBOARD_NAME)) {
      ScoreboardUtils.resetScore(ScoreHolder.fromName(entry.owner()), SCOREBOARD_NAME);
    }

    currentSeason.reloadAllPlayerTeams();
  }

  public void resetAllPlayerLives() {
    resetAllPlayerLivesInner();
    PlayerUtils.getAllPlayers().forEach(currentSeason::assignDefaultLives);
  }

  public void addPlayerLife(ServerPlayerEntity player) {
    addToPlayerLives(player, 1);
  }

  public void addToPlayerLives(ServerPlayerEntity player, int amount) {
    Integer currentLives = getPlayerLives(player);
      if (currentLives == null) {
          currentLives = 0;
      }
    int lives = currentLives + amount;
      if (lives < 0 && !Necromancy.isRessurectedPlayer(player)) {
          lives = 0;
      }
    setPlayerLives(player, lives);
  }

  public void addToLifeNoUpdate(ServerPlayerEntity player) {
      if (isWatcher(player)) {
          return;
      }
    Integer currentLives = getPlayerLives(player);
      if (currentLives == null) {
          currentLives = 0;
      }
    int lives = currentLives + 1;
      if (lives < 0) {
          lives = 0;
      }
    ScoreboardUtils.setScore(player, SCOREBOARD_NAME, lives);
  }

  public void receiveLifeFromOtherPlayer(Text playerName, ServerPlayerEntity target,
      boolean isRevive) {
    target.playSoundToPlayer(SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.MASTER, 10, 1);
    if (seasonConfig.GIVELIFE_BROADCAST.get(seasonConfig)) {
      PlayerUtils.broadcastMessageExcept(
          TextUtils.format("{} received a life from {}", target, playerName), target);
    }
    target.sendMessage(TextUtils.format("You received a life from {}", playerName));
    PlayerUtils.sendTitleWithSubtitle(target, Text.of("You received a life"),
        TextUtils.format("from {}", playerName), 10, 60, 10);
    AnimationUtils.createSpiral(target, 175);
    currentSeason.reloadPlayerTeam(target);
    SessionTranscript.givelife(playerName, target);
    if (currentSeason instanceof DoubleLife doubleLife) {
      doubleLife.syncSoulboundLives(target);
    }
    if (isRevive && isAlive(target)) {
      PlayerUtils.safelyPutIntoSurvival(target);
    }
  }

  public void setPlayerLives(ServerPlayerEntity player, int lives) {
      if (isWatcher(player)) {
          return;
      }
    Integer livesBefore = getPlayerLives(player);
    ScoreboardUtils.setScore(player, SCOREBOARD_NAME, lives);
    if (lives <= 0) {
      playerLostAllLives(player, livesBefore);
    } else if (player.isSpectator()) {
      PlayerUtils.safelyPutIntoSurvival(player);
    }
    currentSeason.reloadPlayerTeam(player);
  }

  @Nullable
  public Boolean isOnLastLife(ServerPlayerEntity player) {
    return isOnSpecificLives(player, 1);
  }

  public boolean isOnLastLife(ServerPlayerEntity player, boolean fallback) {
    Boolean isOnLastLife = isOnLastLife(player);
      if (isOnLastLife == null) {
          return fallback;
      }
    return isOnLastLife;
  }

  @Nullable
  public Boolean isOnSpecificLives(ServerPlayerEntity player, int check) {
      if (!isAlive(player)) {
          return null;
      }
    Integer lives = getPlayerLives(player);
      if (lives == null) {
          return null;
      }
    return lives == check;
  }

  public boolean isOnSpecificLives(ServerPlayerEntity player, int check, boolean fallback) {
    Boolean isOnLife = isOnSpecificLives(player, check);
      if (isOnLife == null) {
          return fallback;
      }
    return isOnLife;
  }

  @Nullable
  public Boolean isOnAtLeastLives(ServerPlayerEntity player, int check) {
      if (!isAlive(player)) {
          return null;
      }
    Integer lives = getPlayerLives(player);
      if (lives == null) {
          return null;
      }
    return lives >= check;
  }

  public boolean isOnAtLeastLives(ServerPlayerEntity player, int check, boolean fallback) {
    Boolean isOnAtLeast = isOnAtLeastLives(player, check);
      if (isOnAtLeast == null) {
          return fallback;
      }
    return isOnAtLeast;
  }


  public void playerLostAllLives(ServerPlayerEntity player, Integer livesBefore) {
    player.changeGameMode(GameMode.SPECTATOR);
    Vec3d pos = player.getPos();
    HashMap<Vec3d, List<Float>> info = new HashMap<>();
    info.put(pos, List.of(player.getYaw(), player.getPitch()));
    currentSeason.respawnPositions.put(player.getUuid(), info);
    currentSeason.dropItemsOnLastDeath(player);
    if (livesBefore != null && livesBefore > 0) {
      if (FINAL_DEATH_SOUND != null) {
        PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), FINAL_DEATH_SOUND);
      }
      if (FINAL_DEATH_LIGHTNING) {
        WorldUitls.summonHarmlessLightning(player);
      }
      showDeathTitle(player);
    }
    SessionTranscript.onPlayerLostAllLives(player);
    currentSeason.boogeymanManager.playerLostAllLives(player);
  }

  public void showDeathTitle(ServerPlayerEntity player) {
    if (SHOW_DEATH_TITLE) {
      String subtitle = seasonConfig.FINAL_DEATH_TITLE_SUBTITLE.get(seasonConfig);
      PlayerUtils.sendTitleWithSubtitleToPlayers(PlayerUtils.getAllPlayers(),
          player.getStyledDisplayName(), Text.literal(subtitle), 20, 80, 20);
    }
    Text deathMessage = getDeathMessage(player);
    if (!deathMessage.getString().isEmpty()) {
      PlayerUtils.broadcastMessage(deathMessage);
    }
  }

  public Text getDeathMessage(ServerPlayerEntity player) {
    String message = seasonConfig.FINAL_DEATH_MESSAGE.get(seasonConfig);
    if (message.contains("${player}")) {
      return TextUtils.format(message.replace("${player}", "{}"), player);
    }
    return Text.literal(message);
  }

  public List<ServerPlayerEntity> getNonRedPlayers() {
    List<ServerPlayerEntity> players = PlayerUtils.getAllFunctioningPlayers();
    players.removeIf(player -> isOnLastLife(player, true));
    return players;
  }

  public List<ServerPlayerEntity> getRedPlayers() {
    List<ServerPlayerEntity> players = PlayerUtils.getAllFunctioningPlayers();
    players.removeIf(player -> !isOnLastLife(player, false));
    return players;
  }

  public List<ServerPlayerEntity> getAlivePlayers() {
    List<ServerPlayerEntity> players = PlayerUtils.getAllFunctioningPlayers();
    players.removeIf(player -> !isAlive(player));
    return players;
  }

  public List<ServerPlayerEntity> getDeadPlayers() {
    List<ServerPlayerEntity> players = PlayerUtils.getAllFunctioningPlayers();
    players.removeIf(this::isAlive);
    return players;
  }

  public boolean anyGreenPlayers(ServerPlayerEntity exception) {
    for (ServerPlayerEntity player : getAlivePlayers()) {
        if (player == exception) {
            continue;
        }
        if (isOnSpecificLives(player, 3, false)) {
            return true;
        }
    }
    return false;
  }

  public boolean anyYellowPlayers(ServerPlayerEntity exception) {
    for (ServerPlayerEntity player : getAlivePlayers()) {
        if (player == exception) {
            continue;
        }
        if (isOnSpecificLives(player, 2, false)) {
            return true;
        }
    }
    return false;
  }

  public boolean anyAlivePlayers(ServerPlayerEntity exception) {
    for (ServerPlayerEntity player : getAlivePlayers()) {
        if (player == exception) {
            continue;
        }
      return true;
    }
    return false;
  }

  public boolean canChangeLivesNaturally() {
    if (ONLY_TAKE_LIVES_IN_SESSION && currentSession != null) {
      return currentSession.statusStarted();
    }
    return true;
  }
}
