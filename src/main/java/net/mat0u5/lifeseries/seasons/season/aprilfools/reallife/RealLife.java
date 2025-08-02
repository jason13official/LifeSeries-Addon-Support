package net.mat0u5.lifeseries.seasons.season.aprilfools.reallife;

import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.thirdlife.ThirdLife;
import net.mat0u5.lifeseries.utils.player.PermissionManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class RealLife extends ThirdLife {
    @Override
    public Seasons getSeason() {
        return Seasons.REAL_LIFE;
    }

    @Override
    public ConfigManager getConfig() {
        return new RealLifeConfig();
    }

    @Override
    public void sendPlayerJoinMessage(ServerPlayerEntity player) {
        if (PermissionManager.isAdmin(player)) {
            player.sendMessage(Text.of("§7Real Life commands: §r"+COMMANDS_ADMIN_TEXT));
        }
        else {
            player.sendMessage(Text.of("§7Real Life non-admin commands: §r"+COMMANDS_TEXT));
        }
    }
}
