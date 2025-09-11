package com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.trivia;

import static com.cursee.ls_addon_support.LSAddonSupport.LOGGER;
import static com.cursee.ls_addon_support.LSAddonSupport.currentSession;
import static com.cursee.ls_addon_support.LSAddonSupport.livesManager;
import static com.cursee.ls_addon_support.LSAddonSupport.server;

import com.cursee.ls_addon_support.entity.snail.Snail;
import com.cursee.ls_addon_support.entity.triviabot.TriviaBot;
import com.cursee.ls_addon_support.network.NetworkHandlerServer;
import com.cursee.ls_addon_support.registries.MobRegistry;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.Wildcard;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.WildcardManager;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.Wildcards;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.SizeShifting;
import com.cursee.ls_addon_support.seasons.session.SessionTranscript;
import com.cursee.ls_addon_support.utils.enums.PacketNames;
import com.cursee.ls_addon_support.utils.player.AttributeUtils;
import com.cursee.ls_addon_support.utils.player.PlayerUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class TriviaWildcard extends Wildcard {

  public static final Map<UUID, Snail> snails = new HashMap<>();
  private static final Map<UUID, Queue<Integer>> playerSpawnQueue = new HashMap<>();
  private static final Map<UUID, Integer> spawnedBotsFor = new HashMap<>();
  private static final Random rnd = new Random();
  public static Map<UUID, TriviaBot> bots = new HashMap<>();
  public static int activatedAt = -1;
  public static int TRIVIA_BOTS_PER_PLAYER = 5;
  public static int MIN_BOT_DELAY = 8400;
  public static TriviaQuestionManager easyTrivia;
  public static TriviaQuestionManager normalTrivia;
  public static TriviaQuestionManager hardTrivia;
  public static long ticks = 0;
  private static boolean globalScheduleInitialized = false;
  private static final List<String> usedEasyQuestions = new ArrayList<>();
  private static final List<String> usedNormalQuestions = new ArrayList<>();
  private static final List<String> usedHardQuestions = new ArrayList<>();

  public static void reload() {
    resetQueue();
  }

  public static void resetQueue() {
    easyTrivia = new TriviaQuestionManager("./config/lifeseries/wildlife", "easy-trivia.json");
    normalTrivia = new TriviaQuestionManager("./config/lifeseries/wildlife", "normal-trivia.json");
    hardTrivia = new TriviaQuestionManager("./config/lifeseries/wildlife", "hard-trivia.json");
    globalScheduleInitialized = false;
    playerSpawnQueue.clear();
  }

  public static void handleAnswer(ServerPlayerEntity player, int answer) {
    if (bots.containsKey(player.getUuid())) {
      TriviaBot bot = bots.get(player.getUuid());
      if (!bot.isDead() && !bot.isRemoved()) {
        bot.handleAnswer(answer);
      }
    }
  }

  public static void spawnBotFor(ServerPlayerEntity player) {
    spawnBotFor(player,
        TriviaBot.getBlockPosNearTarget(player, player.getBlockPos().add(0, 50, 0), 10));
  }

  public static void spawnBotFor(ServerPlayerEntity player, BlockPos pos) {
    resetPlayerOnBotSpawn(player);
    TriviaBot bot = MobRegistry.TRIVIA_BOT.spawn(PlayerUtils.getServerWorld(player), pos,
        SpawnReason.COMMAND);
    if (bot != null) {
      SessionTranscript.newTriviaBot(player);
      bot.setBoundPlayer(player);
      bots.put(player.getUuid(), bot);
      player.playSoundToPlayer(SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.MASTER,
          0.5f, 1);
      NetworkHandlerServer.sendNumberPacket(player, PacketNames.FAKE_THUNDER, 7);
    }
  }

  public static void resetPlayerOnBotSpawn(ServerPlayerEntity player) {
    if (bots.containsKey(player.getUuid())) {
      TriviaBot bot = bots.get(player.getUuid());
      if (!bot.isDead() && !bot.isRemoved()) {
        bot.despawn();
      }
    }
    killTriviaSnailFor(player);

    if (TriviaBot.cursedGigantificationPlayers.contains(player.getUuid())) {
      TriviaBot.cursedGigantificationPlayers.remove(player.getUuid());
      SizeShifting.setPlayerSize(player, 1);
    }
    if (TriviaBot.cursedHeartPlayers.contains(player.getUuid())) {
      TriviaBot.cursedHeartPlayers.remove(player.getUuid());
      AttributeUtils.resetMaxPlayerHealthIfNecessary(player);
    }
    if (TriviaBot.cursedMoonJumpPlayers.contains(player.getUuid())) {
      TriviaBot.cursedMoonJumpPlayers.remove(player.getUuid());
      AttributeUtils.resetPlayerJumpHeight(player);
    }

    TriviaBot.cursedSliding.remove(player.getUuid());
    TriviaBot.cursedRoboticVoicePlayers.remove(player.getUuid());
    NetworkHandlerServer.sendLongPacket(player, PacketNames.CURSE_SLIDING, 0);

    NetworkHandlerServer.sendStringPacket(player, PacketNames.RESET_TRIVIA, "true");
  }

  public static void killAllBots() {
      if (server == null) {
          return;
      }
    List<Entity> toKill = new ArrayList<>();
    for (ServerWorld world : server.getWorlds()) {
      for (Entity entity : world.iterateEntities()) {
        if (entity instanceof TriviaBot) {
          toKill.add(entity);
        }
      }
    }
    toKill.forEach(Entity::discard);
    for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
      NetworkHandlerServer.sendStringPacket(player, PacketNames.RESET_TRIVIA, "true");
    }
  }

  public static void killAllTriviaSnails() {
      if (server == null) {
          return;
      }
    List<Entity> toKill = new ArrayList<>();
    for (ServerWorld world : server.getWorlds()) {
      for (Entity entity : world.iterateEntities()) {
        if (entity instanceof Snail snail) {
          if (snail.fromTrivia) {
            toKill.add(entity);
          }
        }
      }
    }
    toKill.forEach(Entity::discard);
  }

  public static void killTriviaSnailFor(ServerPlayerEntity player) {
      if (server == null) {
          return;
      }
    List<Entity> toKill = new ArrayList<>();
    for (ServerWorld world : server.getWorlds()) {
      for (Entity entity : world.iterateEntities()) {
        if (entity instanceof Snail snail) {
          if (snail.fromTrivia) {
            UUID boundPlayer = snail.boundPlayerUUID;
            if (boundPlayer == null || boundPlayer.equals(player.getUuid())) {
              toKill.add(entity);
            }
          }
        }
      }
    }
    toKill.forEach(Entity::discard);
  }

  public static TriviaQuestion getTriviaQuestion(int difficulty) {
    try {
      if (difficulty == 1) {
        return getEasyQuestion();
      }
      if (difficulty == 2) {
        return getNormalQuestion();
      }
      return getHardQuestion();
    } catch (Exception e) {
      LOGGER.error(e.toString());
      return TriviaQuestion.getDefault();
    }
  }

  public static TriviaQuestion getEasyQuestion() throws IOException {
    if (easyTrivia == null) {
      easyTrivia = new TriviaQuestionManager("./config/lifeseries/wildlife", "easy-trivia.json");
    }
    List<TriviaQuestion> unusedQuestions = new ArrayList<>();
    for (TriviaQuestion trivia : easyTrivia.getTriviaQuestions()) {
        if (usedEasyQuestions.contains(trivia.getQuestion())) {
            continue;
        }
      unusedQuestions.add(trivia);
    }
    if (unusedQuestions.isEmpty()) {
      usedEasyQuestions.clear();
      unusedQuestions = easyTrivia.getTriviaQuestions();
    }
      if (unusedQuestions.isEmpty()) {
          return TriviaQuestion.getDefault();
      }
    TriviaQuestion result = unusedQuestions.get(rnd.nextInt(unusedQuestions.size()));
    usedEasyQuestions.add(result.getQuestion());
    return result;
  }

  public static TriviaQuestion getNormalQuestion() throws IOException {
    if (normalTrivia == null) {
      normalTrivia = new TriviaQuestionManager("./config/lifeseries/wildlife",
          "normal-trivia.json");
    }
    List<TriviaQuestion> unusedQuestions = new ArrayList<>();
    for (TriviaQuestion trivia : normalTrivia.getTriviaQuestions()) {
        if (usedNormalQuestions.contains(trivia.getQuestion())) {
            continue;
        }
      unusedQuestions.add(trivia);
    }
    if (unusedQuestions.isEmpty()) {
      usedNormalQuestions.clear();
      unusedQuestions = normalTrivia.getTriviaQuestions();
    }
      if (unusedQuestions.isEmpty()) {
          return TriviaQuestion.getDefault();
      }
    TriviaQuestion result = unusedQuestions.get(rnd.nextInt(unusedQuestions.size()));
    usedNormalQuestions.add(result.getQuestion());
    return result;
  }

  public static TriviaQuestion getHardQuestion() throws IOException {
    if (hardTrivia == null) {
      hardTrivia = new TriviaQuestionManager("./config/lifeseries/wildlife", "hard-trivia.json");
    }
    List<TriviaQuestion> unusedQuestions = new ArrayList<>();
    for (TriviaQuestion trivia : hardTrivia.getTriviaQuestions()) {
        if (usedHardQuestions.contains(trivia.getQuestion())) {
            continue;
        }
      unusedQuestions.add(trivia);
    }
    if (unusedQuestions.isEmpty()) {
      usedHardQuestions.clear();
      unusedQuestions = hardTrivia.getTriviaQuestions();
    }
      if (unusedQuestions.isEmpty()) {
          return TriviaQuestion.getDefault();
      }
    TriviaQuestion result = unusedQuestions.get(rnd.nextInt(unusedQuestions.size()));
    usedHardQuestions.add(result.getQuestion());
    return result;
  }

  public static int getBotsPerPlayer() {
      if (isBuffed()) {
          return TRIVIA_BOTS_PER_PLAYER * 2;
      }
    return TRIVIA_BOTS_PER_PLAYER;
  }

  public static boolean isBuffed() {
    return WildcardManager.isActiveWildcard(Wildcards.CALLBACK);
  }

  @Override
  public String getId() {
    return Wildcards.TRIVIA;
  }

  @Override
  public void tickSessionOn() {
    int passedTime = (int) ((float) currentSession.passedTime - activatedAt);
      if (passedTime % 20 == 0) {
          trySpawnBots();
      }
      if (passedTime % 200 == 0) {
          updateDeadBots();
      }
  }

  @Override
  public void tick() {
    ticks++;
    if (ticks % 200 == 0) {
      for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
        UUID playerUUID = player.getUuid();
        if (snails.containsKey(playerUUID)) {
          Snail snail = snails.get(playerUUID);
          if (snail == null || snail.isDead() || snail.isRemoved()) {
            snails.remove(playerUUID);
          }
        }
      }
    }
  }

  @Override
  public void activate() {
    usedEasyQuestions.clear();
    usedNormalQuestions.clear();
    usedHardQuestions.clear();
    resetQueue();
    spawnedBotsFor.clear();
    activatedAt = (int) currentSession.passedTime;
    bots.clear();
    TriviaBot.cursedGigantificationPlayers.clear();
    TriviaBot.cursedHeartPlayers.clear();
    TriviaBot.cursedMoonJumpPlayers.clear();
    if (!currentSession.statusStarted()) {
      PlayerUtils.broadcastMessageToAdmins(
          Text.of("§7You must start a session for trivia bots to spawn!"));
    }
    PlayerUtils.broadcastMessageToAdmins(Text.of(
        "§7You can modify the trivia questions in the config files (./config/lifeseries/wildlife/*-trivia)"));
    super.activate();
  }

  @Override
  public void deactivate() {
    usedEasyQuestions.clear();
    usedNormalQuestions.clear();
    usedHardQuestions.clear();
    resetQueue();
    spawnedBotsFor.clear();
    bots.clear();
    killAllBots();
    killAllTriviaSnails();
    for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
      TriviaWildcard.resetPlayerOnBotSpawn(player);
    }
    TriviaBot.cursedGigantificationPlayers.clear();
    TriviaBot.cursedHeartPlayers.clear();
    TriviaBot.cursedMoonJumpPlayers.clear();
    super.deactivate();
  }

  public void trySpawnBots() {
    int currentTick = (int) currentSession.passedTime;
    int sessionStart = activatedAt;
    int sessionEnd =
        currentSession.sessionLength - 6000; // Don't spawn bots 5 minutes before the end
    int availableTime = sessionEnd - sessionStart;

    List<ServerPlayerEntity> players = livesManager.getAlivePlayers();
      if (players.isEmpty()) {
          return;
      }
      if (isBuffed()) {
          Collections.shuffle(players);
      }

    int numPlayers = players.size();
    int desiredTotalSpawns = numPlayers * getBotsPerPlayer();

      if (desiredTotalSpawns == 0) {
          return;
      }

    int interval = availableTime / desiredTotalSpawns;
    if (numPlayers * interval < MIN_BOT_DELAY) {
      interval = MIN_BOT_DELAY / numPlayers;
    }

    int maxSpawns = Math.min(desiredTotalSpawns, availableTime / interval);

    for (ServerPlayerEntity player : players) {
      UUID uuid = player.getUuid();
      if (!playerSpawnQueue.containsKey(uuid)) {
        playerSpawnQueue.put(uuid, new LinkedList<>());
        globalScheduleInitialized = false;
      }
    }

    if (!globalScheduleInitialized) {
      playerSpawnQueue.values().forEach(Collection::clear);
      for (int i = 0; i < maxSpawns; i++) {
        int spawnTime = sessionStart + 100 + i * interval;
        ServerPlayerEntity assignedPlayer = players.get(i % numPlayers);
        UUID uuid = assignedPlayer.getUuid();
        if (spawnTime > currentTick) {
          playerSpawnQueue.get(uuid).offer(spawnTime);
        }
      }
      globalScheduleInitialized = true;
    }

    for (ServerPlayerEntity player : players) {
      UUID uuid = player.getUuid();
      Queue<Integer> queue = playerSpawnQueue.get(uuid);
      if (queue != null && !queue.isEmpty()) {
        if (currentTick >= queue.peek()) {
          queue.poll();
          if (spawnedBotsFor.containsKey(player.getUuid())) {
            spawnedBotsFor.put(player.getUuid(), 1 + spawnedBotsFor.get(player.getUuid()));
          } else {
            spawnedBotsFor.put(player.getUuid(), 1);
          }
          if (spawnedBotsFor.get(player.getUuid()) <= getBotsPerPlayer()) {
            spawnBotFor(player);
          }
        }
      }
    }
  }

  public void updateDeadBots() {
    for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
      UUID playerUUID = player.getUuid();
      if (bots.containsKey(playerUUID)) {
        TriviaBot bot = bots.get(playerUUID);
        if (bot == null || bot.isDead() || bot.isRemoved()) {
          bots.remove(playerUUID);
        }
      }
    }
  }
}
