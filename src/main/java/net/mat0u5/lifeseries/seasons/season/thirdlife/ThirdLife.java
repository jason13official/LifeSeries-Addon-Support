package net.mat0u5.lifeseries.seasons.season.thirdlife;

import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.seasons.season.Season;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.player.PermissionManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.mat0u5.lifeseries.Main.seasonConfig;

public class ThirdLife extends Season {
    public static final String COMMANDS_ADMIN_TEXT = "/lifeseries, /session, /claimkill, /lives";
    public static final String COMMANDS_TEXT = "/claimkill, /lives";
    @Override
    public Seasons getSeason() {
        return Seasons.THIRD_LIFE;
    }

    @Override
    public ConfigManager getConfig() {
        return new ThirdLifeConfig();
    }

    @Override
    public void onPlayerJoin(ServerPlayerEntity player) {
        super.onPlayerJoin(player);

        if (!livesManager.hasAssignedLives(player)) {
            int lives = seasonConfig.DEFAULT_LIVES.get(seasonConfig);
            livesManager.setPlayerLives(player, lives);
        }
    }

    @Override
    public void onPlayerFinishJoining(ServerPlayerEntity player) {
        sendPlayerJoinMessage(player);
        super.onPlayerFinishJoining(player);
    }

    public void sendPlayerJoinMessage(ServerPlayerEntity player) {
        if (PermissionManager.isAdmin(player)) {
            player.sendMessage(Text.of("§7Third Life commands: §r"+COMMANDS_ADMIN_TEXT));
        }
        else {
            player.sendMessage(Text.of("§7Third Life non-admin commands: §r"+COMMANDS_TEXT));
        }
    }
}
