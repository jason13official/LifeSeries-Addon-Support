package com.cursee.ls_addon_support.seasons.session;

import static com.cursee.ls_addon_support.LSAddonSupport.blacklist;
import static com.cursee.ls_addon_support.LSAddonSupport.currentSeason;

import com.cursee.ls_addon_support.events.Events;
import com.cursee.ls_addon_support.network.NetworkHandlerServer;
import com.cursee.ls_addon_support.seasons.season.limitedlife.LimitedLife;
import com.cursee.ls_addon_support.utils.enums.PacketNames;
import com.cursee.ls_addon_support.utils.enums.SessionTimerStates;
import com.cursee.ls_addon_support.utils.other.OtherUtils;
import com.cursee.ls_addon_support.utils.other.TextUtils;
import com.cursee.ls_addon_support.utils.player.PlayerUtils;
import com.cursee.ls_addon_support.utils.world.WorldUitls;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.border.WorldBorder;

public class Session {

  public static final int NATURAL_DEATH_LOG_MAX = 2400;
  public static final int DISPLAY_TIMER_INTERVAL = 5;
  public static final int TAB_LIST_INTERVAL = 20;
  public static final Map<UUID, Integer> skipTimer = new HashMap<>();
  public Map<UUID, Integer> playerNaturalDeathLog = new HashMap<>();
  public List<SessionAction> activeActions = new ArrayList<>();
  public List<UUID> displayTimer = new ArrayList<>();
  public long currentTimer = 0;
  public Integer sessionLength = null;
  public double passedTime = 0;
  public SessionStatus status = SessionStatus.NOT_STARTED;
  SessionAction endWarning1 = new SessionAction(OtherUtils.minutesToTicks(-5)) {
    @Override
    public void trigger() {
      PlayerUtils.broadcastMessage(
          Text.literal("Session ends in 5 minutes!").formatted(Formatting.GOLD));
    }
  };
  SessionAction endWarning2 = new SessionAction(OtherUtils.minutesToTicks(-30)) {
    @Override
    public void trigger() {
      PlayerUtils.broadcastMessage(
          Text.literal("Session ends in 30 minutes!").formatted(Formatting.GOLD));
    }
  };
  SessionAction actionInfoAction = new SessionAction(OtherUtils.secondsToTicks(7)) {
    @Override
    public void trigger() {
      showActionInfo();
    }
  };

  public boolean sessionStart() {
      if (!canStartSession()) {
          return false;
      }
    activeActions.clear();
      if (!currentSeason.sessionStart()) {
          return false;
      }
    status = SessionStatus.STARTED;
    passedTime = 0;
    Text line1 = TextUtils.formatLoosely("§6Session started! §7[{}]",
        OtherUtils.formatTime(sessionLength));
    Text line2 = Text.literal(
        "§f/session timer showDisplay§7 - toggles a session timer on your screen.");
    PlayerUtils.broadcastMessage(line1);
    PlayerUtils.broadcastMessage(line2);
    activeActions.add(endWarning1);
    activeActions.add(endWarning2);
    activeActions.add(actionInfoAction);
    SessionTranscript.sessionStart();
    SessionTranscript.logPlayers();
    return true;
  }

  public void sessionEnd() {
    SessionTranscript.sessionEnd();
    if (status != SessionStatus.FINISHED && status != SessionStatus.NOT_STARTED) {
      SessionTranscript.onSessionEnd();
      PlayerUtils.broadcastMessage(
          Text.literal("The session has ended!").formatted(Formatting.GOLD));
    }
    status = SessionStatus.FINISHED;
    passedTime = 0;
    currentSeason.sessionEnd();
  }

  public void sessionPause() {
    if (statusPaused()) {
      PlayerUtils.broadcastMessage(Text.literal("Session unpaused!").formatted(Formatting.GOLD));
      status = SessionStatus.STARTED;
    } else {
      PlayerUtils.broadcastMessage(Text.literal("Session paused!").formatted(Formatting.GOLD));
      status = SessionStatus.PAUSED;
    }
  }

  public boolean canStartSession() {
      if (!validTime()) {
          return false;
      }
      if (statusStarted()) {
          return false;
      }
    return !statusPaused();
  }

  public void addSessionLength(int lengthTicks) {
      if (sessionLength == null) {
          sessionLength = 0;
      }
    sessionLength += lengthTicks;
  }

  public void removeSessionLength(int lengthTicks) {
      if (sessionLength == null) {
          sessionLength = 0;
      }
    sessionLength -= lengthTicks;
  }

  public String getSessionLength() {
      if (sessionLength == null) {
          return "";
      }
    return OtherUtils.formatTime(sessionLength);
  }

  public void setSessionLength(int lengthTicks) {
    sessionLength = lengthTicks;
  }

  public String getPassedTimeStr() {
    return OtherUtils.formatTime(getPassedTime());
  }

  public String getRemainingTimeStr() {
      if (sessionLength == null) {
          return "";
      }
    return OtherUtils.formatTime(getRemainingTime());
  }

  public int getPassedTime() {
    return (int) passedTime;
  }

  public int getRemainingTime() {
    return sessionLength - getPassedTime();
  }

  public boolean validTime() {
    return sessionLength != null;
  }

  public boolean isInDisplayTimer(ServerPlayerEntity player) {
    return displayTimer.contains(player.getUuid());
  }

  public void addToDisplayTimer(ServerPlayerEntity player) {
    displayTimer.add(player.getUuid());
  }

  public void removeFromDisplayTimer(ServerPlayerEntity player) {
      if (!displayTimer.contains(player.getUuid())) {
          return;
      }
    displayTimer.remove(player.getUuid());
  }

  public void tick(MinecraftServer server) {
    currentTimer++;
    if (currentTimer % DISPLAY_TIMER_INTERVAL == 0) {
      displayTimers(server);
      for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
        NetworkHandlerServer.sendStringPacket(player, PacketNames.SESSION_STATUS, status.getName());
      }
      for (RegistryEntry<StatusEffect> effect : blacklist.getBannedEffects()) {
        for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
          if (player.hasStatusEffect(effect)) {
            StatusEffectInstance actualEffect = player.getStatusEffect(effect);
            if (actualEffect != null) {
                if (!actualEffect.isAmbient() && !actualEffect.shouldShowIcon()
                    && !actualEffect.shouldShowParticles()) {
                    continue;
                }
            }
            player.removeStatusEffect(effect);
          }
        }
      }
    }
    if (currentTimer % TAB_LIST_INTERVAL == 0) {
      Events.updatePlayerListsNextTick = true;
    }

    if (playerNaturalDeathLog != null && !playerNaturalDeathLog.isEmpty()) {
      int currentTime = server.getTicks();
      List<UUID> removeQueue = new ArrayList<>();
      for (Map.Entry<UUID, Integer> entry : playerNaturalDeathLog.entrySet()) {
        int tickDiff = currentTime - entry.getValue();
        if (tickDiff >= NATURAL_DEATH_LOG_MAX) {
          removeQueue.add(entry.getKey());
        }
      }
      if (!removeQueue.isEmpty()) {
        removeQueue.forEach(playerNaturalDeathLog::remove);
      }
    }
    for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
        if (player.isSpectator()) {
            continue;
        }
      checkPlayerPosition(player);
    }

      if (!validTime()) {
          return;
      }
      if (!statusStarted()) {
          return;
      }
    tickSessionOn(server);
    currentSeason.tickSessionOn(server);
  }

  public void tickSessionOn(MinecraftServer server) {
    float tickRate = server.getTickManager().getTickRate();
    if (tickRate == 20) {
      passedTime++;
    } else {
      passedTime += (20 / tickRate);
    }

    if (passedTime >= sessionLength) {
      sessionEnd();
    }

    //Actions
      if (activeActions == null) {
          return;
      }
      if (activeActions.isEmpty()) {
          return;
      }
    List<SessionAction> remaining = new ArrayList<>();
    for (SessionAction action : activeActions) {
      boolean triggered = action.tick((int) passedTime, sessionLength);
      if (!triggered) {
        remaining.add(action);
      }
    }
    activeActions = remaining;
  }

  public void checkPlayerPosition(ServerPlayerEntity player) {
    //TODO improve teleporting
    WorldBorder border = player.getWorld().getWorldBorder();
    double playerSize = player.getBoundingBox().getLengthX() / 2;
    double minX = Math.floor(border.getBoundWest()) + playerSize;
    double maxX = Math.ceil(border.getBoundEast()) - playerSize;
    double minZ = Math.floor(border.getBoundNorth()) + playerSize;
    double maxZ = Math.ceil(border.getBoundSouth()) - playerSize;

    double playerX = player.getX();
    double playerZ = player.getZ();

    if (playerX < minX || playerX > maxX || playerZ < minZ || playerZ > maxZ) {
      // Clamp player position inside the border

      double clampedX = Math.clamp(playerX, minX, maxX);
      double clampedZ = Math.clamp(playerZ, minZ, maxZ);
      double safeY = WorldUitls.findTopSafeY(player.getWorld(),
          new Vec3d(clampedX, player.getY(), clampedZ));

      // Teleport player inside the world border
      PlayerUtils.teleport(player, clampedX, safeY, clampedZ);
    }
  }

  public void displayTimers(MinecraftServer server) {
    if (currentSeason instanceof LimitedLife limitedLife) {
      limitedLife.displayTimers(server);
      return;
    }

    String message = "";
    if (statusNotStarted()) {
      message = "Session has not started";
    } else if (statusStarted()) {
      message = getRemainingTimeStr();
    } else if (statusPaused()) {
      message = "Session has been paused";
    } else if (statusFinished()) {
      message = "Session has ended";
    }

    for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
      UUID uuid = player.getUuid();
      if (displayTimer.contains(player.getUuid())) {
        if (skipTimer.containsKey(uuid)) {
          int value = skipTimer.get(uuid);
          value--;
            if (value > 0) {
                skipTimer.put(uuid, value);
            } else {
                skipTimer.remove(uuid);
            }
          continue;
        }

        if (!NetworkHandlerServer.wasHandshakeSuccessful(player)) {
          player.sendMessage(Text.literal(message).formatted(Formatting.GRAY), true);
        }
      }
      if (NetworkHandlerServer.wasHandshakeSuccessful(player)) {
        long timestamp = SessionTimerStates.OFF.getValue();
          if (statusNotStarted()) {
              timestamp = SessionTimerStates.NOT_STARTED.getValue();
          } else if (statusPaused()) {
              timestamp = SessionTimerStates.PAUSED.getValue();
          } else if (statusFinished()) {
              timestamp = SessionTimerStates.ENDED.getValue();
          } else if (sessionLength != null) {
              long remainingMillis = (sessionLength - (int) passedTime) * 50L;
              timestamp = System.currentTimeMillis() + remainingMillis;
          }
        if (timestamp != SessionTimerStates.OFF.getValue()) {
          NetworkHandlerServer.sendLongPacket(player, PacketNames.SESSION_TIMER, timestamp);
        }
      }
    }
  }

  public void showActionInfo() {
      if (activeActions.isEmpty()) {
          return;
      }
    List<SessionAction> actions = new ArrayList<>(activeActions);
    actions.sort(Comparator.comparingInt(SessionAction::getTriggerTime));
    List<Text> messages = new ArrayList<>();
    for (SessionAction action : actions) {
      String actionMessage = action.sessionMessage;
        if (actionMessage == null) {
            continue;
        }
        if (actionMessage.isEmpty()) {
            continue;
        }
      if (messages.isEmpty()) {
        messages.add(Text.of("§7Queued session actions:"));
      }
      messages.add(Text.of("§7- " + actionMessage));
    }

    messages.forEach(PlayerUtils::broadcastMessageToAdmins);
  }

  public boolean statusStarted() {
    return status == SessionStatus.STARTED;
  }

  public boolean statusPaused() {
    return status == SessionStatus.PAUSED;
  }

  public boolean statusFinished() {
    return status == SessionStatus.FINISHED;
  }

  public boolean statusNotStarted() {
    return status == SessionStatus.NOT_STARTED;
  }
}
