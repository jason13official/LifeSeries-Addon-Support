package com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard;

import static com.cursee.ls_addon_support.LSAddonSupport.currentSession;

import com.cursee.ls_addon_support.network.NetworkHandlerServer;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.Wildcard;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.WildcardManager;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.Wildcards;
import com.cursee.ls_addon_support.seasons.session.SessionTranscript;
import com.cursee.ls_addon_support.utils.other.TaskScheduler;
import com.cursee.ls_addon_support.utils.player.PlayerUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public class Callback extends Wildcard {

  private static final Random rnd = new Random();
  private static final double INITIAL_ACTIVATION_INTERVAL = 20 * 60 * 5;
  private static final double INITIAL_DEACTIVATION_INTERVAL = 20 * 30;
  public static double TURN_OFF = 0.75; // When all wildcards stop
  public static boolean allWildcardsPhaseReached = false;
  private static int activatedAt = -1;
  private static List<Wildcards> blacklistedWildcards = List.of(Wildcards.HUNGER);
  private int nextActivationTick = -1;
  private int nextDeactivationTick = -1;
  private boolean preAllWildcardsPhaseReached = false;
  private Wildcards lastActivatedWildcard;

  public static void setBlacklist(String blacklist) {
    blacklistedWildcards = new ArrayList<>();
    String[] wildcards = blacklist.replace("[", "").replace("]", "").split(",");
    for (String wildcardName : wildcards) {
      Wildcards wildcard = Wildcards.getFromString(wildcardName.trim());
        if (wildcard == null || wildcard == Wildcards.NULL) {
            continue;
        }
      blacklistedWildcards.add(wildcard);
    }
  }

  @Override
  public Wildcards getType() {
    return Wildcards.CALLBACK;
  }

  @Override
  public void tick() {
      if (currentSession.sessionLength == null) {
          return;
      }

    double sessionProgress =
        (currentSession.passedTime - activatedAt) / (currentSession.sessionLength - activatedAt);

    if (nextActivationTick == -1) {
      nextActivationTick =
          (int) currentSession.passedTime + 20 * 60 * 5; // First activation after 5 minutes
    }

    if (sessionProgress >= TURN_OFF && active) {
      deactivate();
      allWildcardsPhaseReached = true;
      return;
    }

      if (allWildcardsPhaseReached) {
          return;
      }

    double approachingEndPhase =
        TURN_OFF - (6000.0 / currentSession.sessionLength); // 5 minutes before the end
    if (sessionProgress >= approachingEndPhase) {
      activateAllWildcards();
      allWildcardsPhaseReached = true;
      return;
    }

      if (preAllWildcardsPhaseReached) {
          return;
      }
    double furtherApproachingEndPhase = TURN_OFF - (6600.0
        / currentSession.sessionLength); // 5.5 minutes before the end - no more actions
    if (sessionProgress >= furtherApproachingEndPhase) {
      if (WildcardManager.isActiveWildcard(Wildcards.TIME_DILATION)) {
        // Disable Time Dilation if it's active...
        WildcardManager.fadedWildcard();
        Wildcard wildcardInstance = WildcardManager.activeWildcards.get(Wildcards.TIME_DILATION);
        wildcardInstance.deactivate();
        WildcardManager.activeWildcards.remove(Wildcards.TIME_DILATION);
        NetworkHandlerServer.sendUpdatePackets();
      }
      preAllWildcardsPhaseReached = true;
      return;
    }

    int currentTick = (int) currentSession.passedTime;

    int targetActiveCount = getTargetActiveWildcardCount(sessionProgress);
    int currentActiveCount = Wildcards.getActiveWildcards().size() - 1;

    if ((currentActiveCount < targetActiveCount && currentTick >= nextActivationTick) ||
        (currentTick >= nextActivationTick && nextActivationTick > 0)) {

      activateRandomWildcard();

      double progressFactor = 1.0 - sessionProgress;
      int activationIntervalTicks = (int) (INITIAL_ACTIVATION_INTERVAL * Math.max(0.5,
          progressFactor));
      nextActivationTick = currentTick + activationIntervalTicks;

      double deactivationProgressFactor = 1 + (sessionProgress / TURN_OFF) * 4;
      int deactivationIntervalTicks = (int) (INITIAL_DEACTIVATION_INTERVAL * Math.clamp(
          deactivationProgressFactor, 1, 5));
      nextDeactivationTick = currentTick + deactivationIntervalTicks;
    }

    if (currentActiveCount > targetActiveCount && nextDeactivationTick > 0
        && currentTick >= nextDeactivationTick) {

      deactivateRandomWildcard();
      nextDeactivationTick = -1;
    }
  }

  private int getTargetActiveWildcardCount(double sessionProgress) {
    double approachingEndPhase = TURN_OFF - (6000.0 / currentSession.sessionLength);
    double newProgress = sessionProgress / approachingEndPhase;
      if (newProgress < 0.25) {
          return 1;
      }
      if (newProgress < 0.5) {
          return 2;
      }
      if (newProgress < 0.75) {
          return 3;
      }
    return 4;
  }

  @Override
  public void activate() {
    activatedAt = (int) currentSession.passedTime;
    nextActivationTick = -1;
    nextDeactivationTick = -1;
    allWildcardsPhaseReached = false;
    preAllWildcardsPhaseReached = false;
    if (!blacklistedWildcards.contains(Wildcards.SIZE_SHIFTING)) {
      softActivateWildcard(Wildcards.SIZE_SHIFTING);
    } else {
      softActivateWildcard(getRandomInactiveWildcard());
    }
    super.activate();
  }

  @Override
  public void deactivate() {
    deactivateAllWildcards();
    TaskScheduler.scheduleTask(50, () -> {
      if (currentSession.statusStarted()) {
        SessionTranscript.endingIsYours();
        showEndingTitles();
      }
    });
    super.deactivate();
  }

  public void showEndingTitles() {
    List<ServerPlayerEntity> players = PlayerUtils.getAllPlayers();
    PlayerUtils.sendTitleToPlayers(players, Text.of("§7The ending is §cyours§7..."), 0, 90, 0);
    TaskScheduler.scheduleTask(80, () -> {

      PlayerUtils.playSoundToPlayers(players, SoundEvents.BLOCK_NOTE_BLOCK_DIDGERIDOO.value(), 0.4f,
          1);
      PlayerUtils.sendTitleToPlayers(players, Text.of("§cMake"), 0, 40, 0);
    });
    TaskScheduler.scheduleTask(110, () -> {
      PlayerUtils.playSoundToPlayers(players, SoundEvents.BLOCK_NOTE_BLOCK_DIDGERIDOO.value(), 0.4f,
          1);
      PlayerUtils.sendTitleToPlayers(players, Text.of("§cMake §eit"), 0, 40, 0);
    });
    TaskScheduler.scheduleTask(140, () -> {
      PlayerUtils.playSoundToPlayers(players, SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, 0.2f, 1);
      PlayerUtils.sendTitleToPlayers(players, Text.of("§cMake §eit §a§lWILD"), 0, 90, 20);

    });
  }

  public void activateAllWildcards() {
    List<Wildcards> inactiveWildcards = Wildcards.getInactiveWildcards();
    for (Wildcards wildcard : inactiveWildcards) {
        if (wildcard == Wildcards.CALLBACK) {
            continue;
        }
        if (blacklistedWildcards.contains(wildcard)) {
            continue;
        }
      Wildcard wildcardInstance = wildcard.getInstance();
        if (wildcardInstance == null) {
            continue;
        }
      WildcardManager.activeWildcards.put(wildcard, wildcardInstance);
    }

    WildcardManager.showDots();
    TaskScheduler.scheduleTask(90, () -> {
      for (Wildcard wildcard : WildcardManager.activeWildcards.values()) {
          if (wildcard.active) {
              continue;
          }
        wildcard.activate();
      }
      WildcardManager.showRainbowCryptTitle("All wildcards are active!");
    });
    TaskScheduler.scheduleTask(92, NetworkHandlerServer::sendUpdatePackets);

  }

  public void deactivateAllWildcards() {
    for (Wildcard wildcard : WildcardManager.activeWildcards.values()) {
        if (wildcard.getType() == Wildcards.CALLBACK) {
            continue;
        }
      wildcard.deactivate();
      PlayerUtils.broadcastMessage(Text.of("§7A Wildcard has faded..."));
    }
    WildcardManager.activeWildcards.clear();
    PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(),
        SoundEvents.BLOCK_BEACON_DEACTIVATE);
    NetworkHandlerServer.sendUpdatePackets();
  }

  public void activateRandomWildcard() {
    Wildcards wildcard = getRandomInactiveWildcard();
      if (wildcard == null) {
          return;
      }
    Wildcard wildcardInstance = wildcard.getInstance();
      if (wildcardInstance == null) {
          return;
      }
    WildcardManager.activeWildcards.put(wildcard, wildcardInstance);
    WildcardManager.activateWildcards();
    lastActivatedWildcard = wildcard;
  }

  public void deactivateRandomWildcard() {
    Wildcards wildcard = getRandomActiveWildcard();
      if (wildcard == null) {
          return;
      }
    Wildcard wildcardInstance = WildcardManager.activeWildcards.get(wildcard);
      if (wildcardInstance == null) {
          return;
      }
    wildcardInstance.deactivate();
    WildcardManager.activeWildcards.remove(wildcard);
    WildcardManager.fadedWildcard();
    NetworkHandlerServer.sendUpdatePackets();
  }

  public Wildcards getRandomInactiveWildcard() {
    List<Wildcards> inactiveWildcards = Wildcards.getInactiveWildcards();
    inactiveWildcards.remove(Wildcards.CALLBACK);
    inactiveWildcards.removeIf(blacklistedWildcards::contains);
      if (inactiveWildcards.isEmpty()) {
          return null;
      }
    return inactiveWildcards.get(rnd.nextInt(inactiveWildcards.size()));
  }

  public Wildcards getRandomActiveWildcard() {
    List<Wildcards> activeWildcards = Wildcards.getActiveWildcards();
    activeWildcards.remove(Wildcards.CALLBACK);
    activeWildcards.removeIf(blacklistedWildcards::contains);
      if (activeWildcards.isEmpty()) {
          return null;
      }
    if (lastActivatedWildcard != null) {
      activeWildcards.remove(lastActivatedWildcard);
        if (activeWildcards.isEmpty()) {
            return lastActivatedWildcard;
        }
    }
    return activeWildcards.get(rnd.nextInt(activeWildcards.size()));
  }

  public void softActivateWildcard(Wildcards wildcard) {
      if (WildcardManager.isActiveWildcard(wildcard)) {
          return;
      }
    Wildcard wildcardInstance = wildcard.getInstance();
      if (wildcardInstance == null) {
          return;
      }
    WildcardManager.activeWildcards.put(wildcard, wildcardInstance);
    wildcardInstance.activate();
    TaskScheduler.scheduleTask(2, NetworkHandlerServer::sendUpdatePackets);
  }
}
