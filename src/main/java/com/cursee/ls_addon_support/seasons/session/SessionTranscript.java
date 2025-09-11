package com.cursee.ls_addon_support.seasons.session;

import static com.cursee.ls_addon_support.LSAddonSupport.currentSeason;
import static com.cursee.ls_addon_support.LSAddonSupport.currentSession;

import com.cursee.ls_addon_support.LSAddonSupport;
import com.cursee.ls_addon_support.seasons.season.secretlife.SecretLife;
import com.cursee.ls_addon_support.seasons.season.secretlife.Task;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import com.cursee.ls_addon_support.utils.other.OtherUtils;
import com.cursee.ls_addon_support.utils.other.TextUtils;
import com.cursee.ls_addon_support.utils.player.PlayerUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class SessionTranscript {

  public static final List<String> messages = new ArrayList<>();

  public static void societyEndSuccess(ServerPlayerEntity player) {
    addMessageWithTime(
        TextUtils.formatString("{} has marked the Secret Society as successful.", player));
  }

  public static void societyEndFail(ServerPlayerEntity player) {
    addMessageWithTime(
        TextUtils.formatString("{} has marked the Secret Society as failed.", player));
  }

  public static void societyMemberInitialized(ServerPlayerEntity player) {
    addMessageWithTime(
        TextUtils.formatString("{} has been initialized into the Secret Society.", player));
  }

  public static void societyMembersChosen(List<ServerPlayerEntity> players) {
    List<String> names = new ArrayList<>();
    for (ServerPlayerEntity player : players) {
      names.add(player.getNameForScoreboard());
    }
    addMessageWithTime(TextUtils.formatString("Secret Society members chosen: {}", names));
  }

  public static void societyStarted() {
    addMessageWithTime(TextUtils.formatString("The Secret Society has started."));
  }

  public static void societyEnded() {
    addMessageWithTime(TextUtils.formatString("The Secret Society has ended."));
  }

  public static void logHealth(ServerPlayerEntity player, double health) {
    addMessageWithTime(TextUtils.formatString("{} is now on {} health.", player, health));
  }

  public static void giftHeart(ServerPlayerEntity player, ServerPlayerEntity receiver) {
    addMessageWithTime(TextUtils.formatString("{} gifted a heart to {}.", player, receiver));
  }

  public static void newSuperpower(ServerPlayerEntity player, Superpowers superpower) {
    addMessageWithTime(TextUtils.formatString("{} has been assigned the {} superpower.", player,
        superpower.getString()));
  }

  public static void newTriviaBot(ServerPlayerEntity player) {
    addMessageWithTime(TextUtils.formatString("Spawned trivia bot for {}", player));
  }

  public static void endingIsYours() {
    addMessageWithTime("The ending is yours... Make it WILD.");
  }

  public static void newHungerRule() {
    addMessageWithTime("[Wildcard] Food has been randomized.");
  }

  public static void mobSwap() {
    addMessageWithTime("[Wildcard] Mobs have been swapped.");
  }

  public static void deactivateWildcard(String type) {
    addMessageWithTime(TextUtils.formatString("Deactivated Wildcard: {}", type));
  }

  public static void activateWildcard(String type) {
    addMessageWithTime(TextUtils.formatString("Activated Wildcard: {}", type));
  }

  public static void logPlayers() {
    List<String> names = new ArrayList<>();
    for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
      names.add(player.getNameForScoreboard());
    }
    addMessageWithTime(TextUtils.formatString("Players online: {}", names));
  }

  public static void rerollTask(ServerPlayerEntity player) {
    addMessageWithTime(TextUtils.formatString("{} has rerolled their task.", player));
  }

  public static void successTask(ServerPlayerEntity player) {
    addMessageWithTime(TextUtils.formatString("{} has passed their task.", player));
  }

  public static void failTask(ServerPlayerEntity player) {
    addMessageWithTime(TextUtils.formatString("{} has failed their task.", player));
  }

  public static void assignTask(ServerPlayerEntity player, Task task, List<String> linesStr) {
    addMessageWithTime(
        TextUtils.formatString("{} has been given a {} task: {}", player, task.type.name(),
            String.join(" ", linesStr)));
  }

  public static void claimKill(ServerPlayerEntity killer, ServerPlayerEntity victim) {
    addMessageWithTime(
        TextUtils.formatString("{}'s kill claim of {} has been accepted.", killer, victim));
  }

  public static void soulmate(ServerPlayerEntity player, ServerPlayerEntity soulmate) {
    addMessageWithTime(
        TextUtils.formatString("{}'s soulmate has been chosen to be {}", player, soulmate));
  }

  public static void assignRandomLives(ServerPlayerEntity player, int amount) {
    addMessageWithTime(
        TextUtils.formatString("{} has been randomly assigned {} lives", player, amount));
  }

  public static void givelife(Text playerName, ServerPlayerEntity target) {
    addMessageWithTime("<@", "> ",
        TextUtils.formatString("{} gave a life to {}", playerName, target));
  }

  public static void playerLeave(ServerPlayerEntity player) {
    addMessageWithTime("<@", "> ", TextUtils.formatString("{} left the game.", player));
  }

  public static void playerJoin(ServerPlayerEntity player) {
    addMessageWithTime("<@", "> ", TextUtils.formatString("{} joined the game.", player));
  }

  public static void triggerSessionAction(String message) {
      if (message == null || message.isEmpty()) {
          return;
      }
    addMessageWithTime("TRIGGERED_SESSION_ACTION: " + message);
  }

  public static void onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
    addMessageWithTime("<@", "> ", source.getDeathMessage(player).getString());
  }

  public static void onPlayerLostAllLives(ServerPlayerEntity player) {
    addMessageWithTime(TextUtils.formatString("{} lost all lives.", player));
  }

  public static void boogeymenChosen(List<ServerPlayerEntity> players) {
    List<String> names = new ArrayList<>();
    for (ServerPlayerEntity player : players) {
      names.add(player.getNameForScoreboard());
    }
    addMessageWithTime(TextUtils.formatString("Boogeymen chosen: {}", names));
  }

  public static void sessionStart() {
    addMessageWithTime("-----  Session started!  -----");
  }

  public static void sessionEnd() {
    addMessageWithTime("-----  The session has ended!  -----");
  }

  public static void addMessageWithTime(String message) {
    addMessageWithTime("[@", "] ", message);
  }

  private static void addMessageWithTime(String start, String end, String message) {
    String time = currentSession.getPassedTimeStr();
    String finalMessage = start + time + end + message;

    if (currentSession.statusNotStarted() || currentSession.statusFinished()) {
      finalMessage = message;
    }

    if (messages.isEmpty()) {
      addDefaultMessages();
    }
    messages.add(finalMessage);
  }

  public static void resetStats() {
    messages.clear();
    addDefaultMessages();
  }

  public static void addDefaultMessages() {
    messages.add(
        TextUtils.formatString("-----  Life Series Mod by Mat0u5  |  Mod version: {}  -----",
            LSAddonSupport.MOD_VERSION));
    messages.add(TextUtils.formatString("-----  {}  |  Time and date: {}  -----",
        currentSeason.getSeason().name(), OtherUtils.getTimeAndDate()));
    messages.add("-----  Session Transcript  -----");
  }

  public static String getStats() {
    return String.join("\n", messages);
  }

  public static void onSessionEnd() {
    if (currentSeason instanceof SecretLife secretLife) {
      secretLife.heartsTranscript();
    }
    sendTranscriptToAdmins();
    writeTranscriptToFile();
  }

  public static void sendTranscriptToAdmins() {
    Text sessionTranscript = getTranscriptMessage();
    PlayerUtils.broadcastMessageToAdmins(sessionTranscript);
  }

  public static void writeTranscriptToFile() {
    String content = SessionTranscript.getStats();

    LocalDateTime now = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    String filename = now.format(formatter) + ".txt";
    try {
      Path filePath = Paths.get("transcripts", filename);
      Files.createDirectories(filePath.getParent());
      Files.write(filePath, content.getBytes());
      LSAddonSupport.LOGGER.info("Session transcript file created: " + filePath);
    } catch (Exception ignored) {
    }
  }

  public static Text getTranscriptMessage() {
    return Text.literal("§7Click ").append(
        Text.literal("here")
            .styled(style -> style
                .withColor(Formatting.BLUE)
                .withClickEvent(TextUtils.copyClipboardClickEvent(SessionTranscript.getStats()))
                .withUnderline(true)
            )).append(Text.of("§7 to copy the session transcript."));
  }
}
