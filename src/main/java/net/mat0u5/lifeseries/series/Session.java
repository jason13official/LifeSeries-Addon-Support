package net.mat0u5.lifeseries.series;

import net.mat0u5.lifeseries.events.Events;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.utils.OtherUtils;
import net.mat0u5.lifeseries.utils.PermissionManager;
import net.mat0u5.lifeseries.utils.PlayerUtils;
import net.mat0u5.lifeseries.utils.WorldUitls;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.border.WorldBorder;

import java.util.*;

import static net.mat0u5.lifeseries.Main.*;

public class Session {
    public Map<UUID, Integer> playerNaturalDeathLog = new HashMap<>();
    public List<SessionAction> activeActions = new ArrayList<>();
    public List<UUID> displayTimer = new ArrayList<>();
    public static final int NATURAL_DEATH_LOG_MAX = 2400;
    public static final int DISPLAY_TIMER_INTERVAL = 5;
    public static final int TAB_LIST_INTERVAL = 20;
    public static boolean MUTE_DEAD_PLAYERS = false;
    public long currentTimer = 0;

    public Integer sessionLength = null;
    public double passedTime = 0;
    public SessionStatus status = SessionStatus.NOT_STARTED;

    SessionAction endWarning1 = new SessionAction(OtherUtils.minutesToTicks(-5)) {
        @Override
        public void trigger() {
            OtherUtils.broadcastMessage(Text.literal("Session ends in 5 minutes!").formatted(Formatting.GOLD));
        }
    };
    SessionAction endWarning2 = new SessionAction(OtherUtils.minutesToTicks(-30)) {
        @Override
        public void trigger() {
            OtherUtils.broadcastMessage(Text.literal("Session ends in 30 minutes!").formatted(Formatting.GOLD));
        }
    };
    SessionAction actionInfoAction = new SessionAction(OtherUtils.secondsToTicks(7)) {
        @Override
        public void trigger() {
            showActionInfo();
        }
    };

    public boolean sessionStart() {
        if (!canStartSession()) return false;
        status = SessionStatus.STARTED;
        passedTime = 0;
        MutableText sessionStartedText = Text.literal("Session started!").formatted(Formatting.GOLD);
        Text firstLine = sessionStartedText.append(Text.literal(" ["+OtherUtils.formatTime(sessionLength)+"]").formatted(Formatting.GRAY));
        MutableText infoText2 = Text.literal("§f/session timer showDisplay§7 - toggles a session timer on your screen.");
        OtherUtils.broadcastMessage(firstLine);
        OtherUtils.broadcastMessage(infoText2);
        activeActions.clear();
        activeActions.add(endWarning1);
        activeActions.add(endWarning2);
        activeActions.add(actionInfoAction);
        SessionTranscript.sessionStart();
        SessionTranscript.logPlayers();
        return true;
    }

    public void sessionEnd() {
        SessionTranscript.sessionEnd();
        status = SessionStatus.FINISHED;
        OtherUtils.broadcastMessage(Text.literal("The session has ended!").formatted(Formatting.GOLD));
        SessionTranscript.sendTranscriptToAdmins();
        passedTime = 0;
    }

    public void sessionPause() {
        if (statusPaused()) {
            OtherUtils.broadcastMessage(Text.literal("Session unpaused!").formatted(Formatting.GOLD));
            status = SessionStatus.STARTED;
        }
        else {
            OtherUtils.broadcastMessage(Text.literal("Session paused!").formatted(Formatting.GOLD));
            status = SessionStatus.PAUSED;
        }
    }

    public boolean canStartSession() {
        if (!validTime()) return false;
        if (statusStarted()) return false;
        return !statusPaused();
    }

    public void setSessionLength(int lengthTicks) {
        sessionLength = lengthTicks;
    }

    public void addSessionLength(int lengthTicks) {
        if (sessionLength == null) sessionLength = 0;
        sessionLength += lengthTicks;
    }

    public void removeSessionLength(int lengthTicks) {
        if (sessionLength == null) sessionLength = 0;
        sessionLength -= lengthTicks;
    }

    public String getSessionLength() {
        if (sessionLength == null) return "";
        return OtherUtils.formatTime(sessionLength);
    }

    public String getPassedTime() {
        return OtherUtils.formatTime((int) passedTime);
    }

    public String getRemainingTime() {
        if (sessionLength == null) return "";
        return OtherUtils.formatTime(sessionLength - ((int) passedTime));
    }

    public boolean validTime() {
        return sessionLength != null;
    }

    public boolean isInDisplayTimer(ServerPlayerEntity player) {
        return displayTimer.contains(player.getUuid());
    }

    public void addToDisplayTimer(ServerPlayerEntity player) {
        displayTimer.add(player.getUuid());
        NetworkHandlerServer.sendLongPacket(player, "session_timer", 0);
    }

    public void removeFromDisplayTimer(ServerPlayerEntity player) {
        if (!displayTimer.contains(player.getUuid())) return;
        displayTimer.remove(player.getUuid());
        NetworkHandlerServer.sendLongPacket(player, "session_timer", 0);
    }

    public void tick(MinecraftServer server) {
        currentTimer++;
        if (currentTimer % DISPLAY_TIMER_INTERVAL == 0) {
            displayTimers(server);
            if (MUTE_DEAD_PLAYERS) {
                for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
                    if (currentSeries.isAlive(player)) continue;
                    if (PermissionManager.isAdmin(player)) continue;
                    NetworkHandlerServer.sendNumberPacket(player, "mute", 50);
                }
            }
            for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
                NetworkHandlerServer.sendStringPacket(player, "sessionStatus", SessionStatus.getStringName(status));
            }
            for (RegistryEntry<StatusEffect> effect : blacklist.getBannedEffects()) {
                for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
                    if (player.hasStatusEffect(effect)) {
                        StatusEffectInstance actualEffect = player.getStatusEffect(effect);
                        if (actualEffect != null) {
                            if (!actualEffect.isAmbient() && !actualEffect.shouldShowIcon() && !actualEffect.shouldShowParticles()) continue;
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
                for (UUID uuid : removeQueue) {
                    playerNaturalDeathLog.remove(uuid);
                }
            }
        }
        for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
            if (player.isSpectator()) continue;
            checkPlayerPosition(player);
        }

        if (!validTime()) return;
        if (!statusStarted()) return;
        tickSessionOn(server);
    }

    public void tickSessionOn(MinecraftServer server) {
        float tickRate = server.getTickManager().getTickRate();
        if (tickRate == 20) {
            passedTime++;
        }
        else {
            passedTime += (20/tickRate);
        }

        if (passedTime >= sessionLength) {
            sessionEnd();
        }

        //Actions
        if (activeActions == null) return;
        if (activeActions.isEmpty()) return;
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
        WorldBorder border = player.getWorld().getWorldBorder();
        double playerSize = player.getBoundingBox().getLengthX()/2;
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
            double safeY = WorldUitls.findSafeY(player.getWorld(), new Vec3d(clampedX, player.getY(), clampedZ));

            // Teleport player inside the world border
            //? if <=1.21 {
            player.teleport(PlayerUtils.getServerWorld(player),clampedX, safeY, clampedZ, player.getYaw(), player.getPitch());
            //?} else {
                /*Set<PositionFlag> flags = EnumSet.noneOf(PositionFlag.class);
                player.teleport(PlayerUtils.getServerWorld(player),clampedX, safeY, clampedZ, flags, player.getYaw(), player.getPitch(), false);
            *///?}
        }
    }
    public static final Map<UUID, Integer> skipTimer = new HashMap<>();
    public void displayTimers(MinecraftServer server) {
        String message = "";
        if (statusNotStarted()) {
            message = "Session has not started";
        }
        else if (statusStarted()) {
            message = getRemainingTime();
        }
        else if (statusPaused()) {
            message = "Session has been paused";
        }
        else if (statusFinished()) {
            message = "Session has ended";
        }

        for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
            UUID uuid = player.getUuid();
            if (displayTimer.contains(player.getUuid())) {
                if (skipTimer.containsKey(uuid)) {
                    int value = skipTimer.get(uuid);
                    value--;
                    if (value > 0) skipTimer.put(uuid, value);
                    else skipTimer.remove(uuid);
                    continue;
                }

                if (!NetworkHandlerServer.wasHandshakeSuccessful(player)) {
                    player.sendMessage(Text.literal(message).formatted(Formatting.GRAY), true);
                }
            }
            else if (NetworkHandlerServer.wasHandshakeSuccessful(player)) {
                long timestamp = 0;
                if (statusNotStarted()) timestamp = -1;
                else if (statusPaused()) timestamp = -2;
                else if (statusFinished()) timestamp = -3;
                else if (sessionLength != null) {
                    long remainingMillis = (sessionLength - (int) passedTime) * 50;
                    timestamp = System.currentTimeMillis() + remainingMillis;
                }
                if (timestamp != 0) {
                    NetworkHandlerServer.sendLongPacket(player, "session_timer", timestamp);
                }
            }
        }
    }

    public void showActionInfo() {
        if (activeActions.isEmpty()) return;
        OtherUtils.broadcastMessageToAdmins(Text.of("§7Queued session actions:"));
        for (SessionAction action : activeActions) {
            String actionMessage = action.sessionMessage;
            if (actionMessage == null) continue;
            if (actionMessage.isEmpty()) continue;
            OtherUtils.broadcastMessageToAdmins(Text.of("§7- "+actionMessage));
        }
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
