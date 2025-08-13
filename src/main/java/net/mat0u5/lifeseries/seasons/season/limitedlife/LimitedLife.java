package net.mat0u5.lifeseries.seasons.season.limitedlife;

import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.seasons.boogeyman.Boogeyman;
import net.mat0u5.lifeseries.seasons.boogeyman.BoogeymanManager;
import net.mat0u5.lifeseries.seasons.season.Season;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.session.SessionTranscript;
import net.mat0u5.lifeseries.utils.enums.SessionTimerStates;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.PermissionManager;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.player.ScoreboardUtils;
import net.mat0u5.lifeseries.utils.player.TeamUtils;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.ScoreboardEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;

import java.util.Collection;

import static net.mat0u5.lifeseries.Main.*;
import static net.mat0u5.lifeseries.seasons.other.WatcherManager.isWatcher;

public class LimitedLife extends Season {
    public static final String COMMANDS_ADMIN_TEXT = "/lifeseries, /session, /claimkill, /lives, /boogeyman";
    public static final String COMMANDS_TEXT = "/claimkill, /lives";
    
    public static int DEFAULT_TIME = 86400;
    public static int YELLOW_TIME = 57600;
    public static int RED_TIME = 28800;
    private boolean SHOW_DEATH_TITLE = true;
    private int DEATH_NORMAL = -3600;
    private int DEATH_BOOGEYMAN = -7200;
    private int KILL_NORMAL = 1800;
    private int KILL_BOOGEYMAN = 3600;
    public static boolean TICK_OFFLINE_PLAYERS = false;
    public static boolean BROADCAST_COLOR_CHANGES = false;

    @Override
    public Seasons getSeason() {
        return Seasons.LIMITED_LIFE;
    }

    @Override
    public ConfigManager getConfig() {
        return new LimitedLifeConfig();
    }

    @Override
    public BoogeymanManager createBoogeymanManager() {
        return new LimitedLifeBoogeymanManager();
    }

    public void displayTimers(MinecraftServer server) {
        String message = "";
        if (currentSession.statusNotStarted()) {
            message = "Session has not started";
        }
        else if (currentSession.statusStarted()) {
            message = currentSession.getRemainingTime();
        }
        else if (currentSession.statusPaused()) {
            message = "Session has been paused";
        }
        else if (currentSession.statusFinished()) {
            message = "Session has ended";
        }

        for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {

            if (NetworkHandlerServer.wasHandshakeSuccessful(player)) {
                long timestamp = SessionTimerStates.OFF.getValue();
                if (currentSession.statusNotStarted()) timestamp = SessionTimerStates.NOT_STARTED.getValue();
                else if (currentSession.statusPaused()) timestamp = SessionTimerStates.PAUSED.getValue();
                else if (currentSession.statusFinished()) timestamp = SessionTimerStates.ENDED.getValue();
                else if (currentSession.sessionLength != null) {
                    long remainingMillis = (currentSession.sessionLength - (int) currentSession.passedTime) * 50;
                    timestamp = System.currentTimeMillis() + remainingMillis;
                }
                if (timestamp != SessionTimerStates.OFF.getValue()) {
                    NetworkHandlerServer.sendLongPacket(player, "session_timer", timestamp);
                }

                if (hasAssignedLives(player) && getPlayerLives(player) != null) {
                    long playerLives;
                    if (isAlive(player)) {
                        Integer playerLivesInt = getPlayerLives(player);
                        playerLives = playerLivesInt == null ? -1 : playerLivesInt;
                    }
                    else {
                        playerLives = -1;
                    }
                    String livesColor = getColorForLives(getPlayerLives(player)).toString();
                    NetworkHandlerServer.sendLongPacket(player, "limited_life_timer__"+livesColor, playerLives);
                }
            }
            else {
                MutableText fullMessage = Text.empty();
                if (currentSession.displayTimer.contains(player.getUuid())) {
                    fullMessage.append(Text.literal(message).formatted(Formatting.GRAY));
                }
                if (hasAssignedLives(player)) {
                    if (!fullMessage.getString().isEmpty()) fullMessage.append(Text.of("  |  "));
                    fullMessage.append(getFormattedLives(getPlayerLives(player)));
                }
                player.sendMessage(fullMessage, true);
            }
        }
    }

    private int secondCounter = 0;
    @Override
    public void tickSessionOn(MinecraftServer server) {
        super.tickSessionOn(server);
        if (!currentSession.statusStarted()) return;

        secondCounter--;
        if (secondCounter <= 0) {
            secondCounter = 20;
            //TODO refactor
            for (ServerPlayerEntity player : currentSeason.getAlivePlayers()) {
                removePlayerLife(player);
            }

            if (TICK_OFFLINE_PLAYERS) {
                Collection<ScoreboardEntry> entries = ScoreboardUtils.getScores("Lives");
                for (ScoreboardEntry entry : entries) {
                    if (entry.value() <= 0) continue;
                    if (PlayerUtils.getPlayer(entry.owner()) != null) continue;
                    ScoreboardUtils.setScore(ScoreHolder.fromName(entry.owner()), "Lives", entry.value() - 1);
                }
            }
        }
    }

    @Override
    public Formatting getColorForLives(Integer lives) {
        lives = getEquivalentLives(lives);
        if (lives == null) return Formatting.GRAY;
        if (lives == 1) return Formatting.RED;
        if (lives == 2) return Formatting.YELLOW;
        if (lives == 3) return Formatting.GREEN;
        if (lives >= 4) return Formatting.DARK_GREEN;
        return Formatting.DARK_GRAY;
    }

    @Override
    public Text getFormattedLives(Integer lives) {
        if (lives == null) return Text.empty();
        Formatting color = getColorForLives(lives);
        return Text.literal(OtherUtils.formatTime(lives*20)).formatted(color);
    }

    @Override
    public String getTeamForLives(Integer lives) {
        lives = getEquivalentLives(lives);
        if (lives == null) return "lives_null";
        if (lives == 1) return "lives_1";
        if (lives == 2) return "lives_2";
        if (lives == 3) return "lives_3";
        if (lives >= 4) return "lives_4";
        return "lives_0";
    }

    @Override
    public void setPlayerLives(ServerPlayerEntity player, int lives) {
        if (isWatcher(player)) return;
        Integer livesBefore = getPlayerLives(player);
        Formatting colorBefore = null;
        if (player.getScoreboardTeam() != null) {
            colorBefore = player.getScoreboardTeam().getColor();
        }
        ScoreboardUtils.setScore(ScoreHolder.fromName(player.getNameForScoreboard()), "Lives", lives);
        if (lives <= 0) {
            playerLostAllLives(player, livesBefore);
        }
        Formatting colorNow = getColorForLives(lives);
        if (colorBefore != colorNow) {
            if (player.isSpectator() && lives > 0) {
                player.changeGameMode(GameMode.SURVIVAL);
            }
            if (lives > 0 && colorBefore != null && livesBefore != null && BROADCAST_COLOR_CHANGES) {
                Text livesText = TextUtils.format("{} name", colorNow.getName().replaceAll("_", " ").toLowerCase()).formatted(colorNow);
                PlayerUtils.broadcastMessage(TextUtils.format("{}§7 is now a {}§7.", player, livesText));
            }
            reloadPlayerTeam(player);
        }
    }

    @Override
    public Boolean isOnSpecificLives(ServerPlayerEntity player, int check) {
        if (!isAlive(player)) return null;
        Integer lives = getEquivalentLives(currentSeason.getPlayerLives(player));
        if (lives == null) return null;
        return lives == check;
    }

    public Integer getEquivalentLives(Integer limitedLifeLives) {
        if (limitedLifeLives == null) return null;
        if (limitedLifeLives <= 0) return 0;
        if (limitedLifeLives <= RED_TIME) return 1;
        if (limitedLifeLives <= YELLOW_TIME) return 2;
        if (limitedLifeLives <= DEFAULT_TIME) return 3;
        return 4;
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
        addToPlayerLives(player, DEATH_NORMAL);
        if (isAlive(player)) {
            PlayerUtils.sendTitle(player, Text.literal(OtherUtils.formatSecondsToReadable(DEATH_NORMAL)).formatted(Formatting.RED), 20, 80, 20);
        }
    }

    @Override
    public void onClaimKill(ServerPlayerEntity killer, ServerPlayerEntity victim) {
        SessionTranscript.claimKill(killer, victim);
        Boogeyman boogeyman  = boogeymanManagerNew.getBoogeyman(killer);
        if (boogeyman == null || boogeyman.cured || isOnLastLife(victim, true)) {
            addToPlayerLives(killer, KILL_NORMAL);
            PlayerUtils.sendTitle(killer, Text.literal(OtherUtils.formatSecondsToReadable(KILL_NORMAL)).formatted(Formatting.GREEN), 20, 80, 20);
            return;
        }

        boogeymanManagerNew.cure(killer);

        //Victim was killed by boogeyman - remove 2 hours from victim and add 1 hour to boogey
        boolean wasAlive = false;

        String msgVictim = OtherUtils.formatSecondsToReadable(DEATH_BOOGEYMAN-DEATH_NORMAL);
        String msgKiller = OtherUtils.formatSecondsToReadable(KILL_BOOGEYMAN);

        if (isAlive(victim)) {
            addToPlayerLives(victim, DEATH_BOOGEYMAN-DEATH_NORMAL);
            wasAlive = true;
        }
        addToPlayerLives(killer, KILL_BOOGEYMAN);
        if (isAlive(victim)) {
            PlayerUtils.sendTitle(killer, Text.literal(msgKiller).formatted(Formatting.GREEN), 20, 80, 20);
            PlayerUtils.sendTitle(victim, Text.literal(msgVictim).formatted(Formatting.RED), 20, 80, 20);
        }
        else if (wasAlive && SHOW_DEATH_TITLE) {
            PlayerUtils.sendTitleWithSubtitle(killer,
                    Text.literal(msgKiller).formatted(Formatting.GREEN),
                    getDeathMessage(victim),
                    20, 80, 20);
        }
        else {
            PlayerUtils.sendTitle(killer, Text.literal(msgKiller).formatted(Formatting.GREEN), 20, 80, 20);
        }
    }

    @Override
    public void onPlayerKilledByPlayer(ServerPlayerEntity victim, ServerPlayerEntity killer) {
        Boogeyman boogeyman  = boogeymanManagerNew.getBoogeyman(killer);
        if (boogeyman == null || boogeyman.cured || isOnLastLife(victim, true)) {
            boolean wasAllowedToAttack = isAllowedToAttack(killer, victim);
            String msgVictim = OtherUtils.formatSecondsToReadable(DEATH_NORMAL);
            String msgKiller = OtherUtils.formatSecondsToReadable(KILL_NORMAL);
            addToPlayerLives(victim, DEATH_NORMAL);
            addToPlayerLives(killer, KILL_NORMAL);
            if (isAlive(victim) || !SHOW_DEATH_TITLE) {
                PlayerUtils.sendTitle(victim, Text.literal(msgVictim).formatted(Formatting.RED), 20, 80, 20);
                PlayerUtils.sendTitle(killer, Text.literal(msgKiller).formatted(Formatting.GREEN), 20, 80, 20);
            }
            else {
                PlayerUtils.sendTitleWithSubtitle(killer,
                        Text.literal(msgKiller).formatted(Formatting.GREEN),
                        getDeathMessage(victim),
                        20, 80, 20);
            }
            if (wasAllowedToAttack) return;
            PlayerUtils.broadcastMessageToAdmins(TextUtils.format("§c [Unjustified Kill?] {}§7 was killed by {}", victim, killer));
            PlayerUtils.broadcastMessageToAdmins(Text.of("§7Remember to remove time from the killer if this was indeed an unjustified kill."));
            return;
        }

        boogeymanManagerNew.cure(killer);

        //Victim was killed by boogeyman - remove 2 hours from victim and add 1 hour to boogey
        String msgVictim = OtherUtils.formatSecondsToReadable(DEATH_BOOGEYMAN);
        String msgKiller = OtherUtils.formatSecondsToReadable(KILL_BOOGEYMAN);
        addToPlayerLives(victim, DEATH_BOOGEYMAN);
        addToPlayerLives(killer, KILL_BOOGEYMAN);

        if (isAlive(victim) || !SHOW_DEATH_TITLE) {
            PlayerUtils.sendTitle(victim, Text.literal(msgVictim).formatted(Formatting.RED), 20, 80, 20);
            PlayerUtils.sendTitleWithSubtitle(killer,Text.of("§aYou are cured!"), Text.literal(msgKiller).formatted(Formatting.GREEN), 20, 80, 20);
        }
        else {
            PlayerUtils.sendTitleWithSubtitle(killer,Text.of("§aYou are cured, "+msgKiller),
                    getDeathMessage(victim)
                    , 20, 80, 20);
        }
    }

    @Override
    public boolean isAllowedToAttack(ServerPlayerEntity attacker, ServerPlayerEntity victim) {
        if (isOnLastLife(attacker, false)) return true;
        if (attacker.getPrimeAdversary() == victim && isOnLastLife(victim, false)) return true;
        if (isOnSpecificLives(attacker, 2, false) && isOnSpecificLives(victim, 3, false)) return true;
        if (attacker.getPrimeAdversary() == victim && (isOnSpecificLives(victim, 2, false) && isOnSpecificLives(attacker, 3, false))) return true;
        Boogeyman boogeymanAttacker = boogeymanManagerNew.getBoogeyman(attacker);
        Boogeyman boogeymanVictim = boogeymanManagerNew.getBoogeyman(victim);
        if (boogeymanAttacker != null && !boogeymanAttacker.cured) return true;
        return attacker.getPrimeAdversary() == victim && (boogeymanVictim != null && !boogeymanVictim.cured);
    }

    @Override
    public void onPlayerJoin(ServerPlayerEntity player) {
        super.onPlayerJoin(player);
        if (!hasAssignedLives(player)) {
            setPlayerLives(player, DEFAULT_TIME);
        }
    }

    @Override
    public void onPlayerFinishJoining(ServerPlayerEntity player) {
        if (PermissionManager.isAdmin(player)) {
            player.sendMessage(Text.of("§7Limited Life commands: §r"+COMMANDS_ADMIN_TEXT));
        }
        else {
            player.sendMessage(Text.of("§7Limited Life non-admin commands: §r"+COMMANDS_TEXT));
        }
        super.onPlayerFinishJoining(player);
    }

    @Override
    public void reload() {
        super.reload();
        if (!(seasonConfig instanceof LimitedLifeConfig config)) return;
        DEFAULT_TIME = LimitedLifeConfig.TIME_DEFAULT.get(config);
        YELLOW_TIME = LimitedLifeConfig.TIME_YELLOW.get(config);
        RED_TIME = LimitedLifeConfig.TIME_RED.get(config);
        DEATH_NORMAL = LimitedLifeConfig.TIME_DEATH.get(config);
        DEATH_BOOGEYMAN = LimitedLifeConfig.TIME_DEATH_BOOGEYMAN.get(config);
        KILL_NORMAL = LimitedLifeConfig.TIME_KILL.get(config);
        KILL_BOOGEYMAN = LimitedLifeConfig.TIME_KILL_BOOGEYMAN.get(config);
        TICK_OFFLINE_PLAYERS = LimitedLifeConfig.TICK_OFFLINE_PLAYERS.get(config);
        BROADCAST_COLOR_CHANGES = LimitedLifeConfig.BROADCAST_COLOR_CHANGES.get(config);
    }
}
