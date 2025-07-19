package net.mat0u5.lifeseries.seasons.season.lastlife;

import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.seasons.boogeyman.Boogeyman;
import net.mat0u5.lifeseries.seasons.boogeyman.BoogeymanManager;
import net.mat0u5.lifeseries.seasons.season.Season;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.player.PermissionManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.List;

import static net.mat0u5.lifeseries.Main.seasonConfig;

public class LastLife extends Season {
    public static final String COMMANDS_ADMIN_TEXT = "/lifeseries, /session, /claimkill, /lives, /givelife, /boogeyman, /lastlife";
    public static final String COMMANDS_TEXT = "/claimkill, /lives, /givelife";
    public static int ROLL_MAX_LIVES = 6;
    public static int ROLL_MIN_LIVES = 2;

    public LastLifeLivesManager livesManager = new LastLifeLivesManager();

    @Override
    public Seasons getSeason() {
        return Seasons.LAST_LIFE;
    }

    @Override
    public ConfigManager getConfig() {
        return new LastLifeConfig();
    }

    @Override
    public boolean sessionStart() {
        if (super.sessionStart()) {
            activeActions.add(
                livesManager.actionChooseLives
            );
            return true;
        }
        return false;
    }

    @Override
    public void onPlayerFinishJoining(ServerPlayerEntity player) {
        if (PermissionManager.isAdmin(player)) {
            player.sendMessage(Text.of("§7Last Life commands: §r"+COMMANDS_ADMIN_TEXT));
        }
        else {
            player.sendMessage(Text.of("§7Last Life non-admin commands: §r"+COMMANDS_TEXT));
        }
        super.onPlayerFinishJoining(player);
    }

    @Override
    public void reload() {
        super.reload();
        if (!(seasonConfig instanceof LastLifeConfig config)) return;
        ROLL_MIN_LIVES = config.RANDOM_LIVES_MIN.get(config);
        ROLL_MAX_LIVES = config.RANDOM_LIVES_MAX.get(config);
    }
}
