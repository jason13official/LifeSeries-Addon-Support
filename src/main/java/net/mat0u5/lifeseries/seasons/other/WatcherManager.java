package net.mat0u5.lifeseries.seasons.other;

import net.mat0u5.lifeseries.utils.player.ScoreboardUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.ScoreboardEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static net.mat0u5.lifeseries.Main.currentSeason;

public class WatcherManager {
    public static final String SCOREBOARD_NAME = "Watchers";
    public static final String TEAM_NAME = "watcher";
    public static final String TEAM_DISPLAY_NAME = "Watcher";
    private static List<String> watchers = new ArrayList<>();

    public static void reloadWatchers() {
        watchers.clear();
        Collection<ScoreboardEntry> entries = ScoreboardUtils.getScores(SCOREBOARD_NAME);
        if (entries == null || entries.isEmpty()) return;
        for (ScoreboardEntry entry : entries) {
            if (entry.value() <= 0) continue;
            watchers.add(entry.owner());
        }
    }

    public static void addWatcher(ServerPlayerEntity player) {
        watchers.add(player.getNameForScoreboard());
        ScoreboardUtils.setScore(player, SCOREBOARD_NAME, 1);
        currentSeason.resetPlayerLife(player);
        player.changeGameMode(GameMode.SPECTATOR);
    }

    public static void removeWatcher(ServerPlayerEntity player) {
        watchers.remove(player.getNameForScoreboard());
        ScoreboardUtils.resetScore(player, SCOREBOARD_NAME);
        currentSeason.resetPlayerLife(player);
    }

    public static boolean isWatcher(PlayerEntity player) {
        return watchers.contains(player.getNameForScoreboard());
    }

    public static List<String> getWatchers() {
        return watchers;
    }
}
