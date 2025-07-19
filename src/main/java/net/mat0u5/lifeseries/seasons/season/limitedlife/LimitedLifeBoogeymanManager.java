package net.mat0u5.lifeseries.seasons.season.limitedlife;

import net.mat0u5.lifeseries.seasons.boogeyman.BoogeymanManager;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

import static net.mat0u5.lifeseries.Main.currentSeason;

public class LimitedLifeBoogeymanManager extends BoogeymanManager {
    @Override
    public void playerFailBoogeyman(ServerPlayerEntity player) {
        if (!currentSeason.isAlive(player)) return;
        if (currentSeason.isOnLastLife(player, true)) return;
        if (currentSeason.isOnSpecificLives(player, 3, false)) {
            currentSeason.setPlayerLives(player, LimitedLife.YELLOW_TIME);
        }
        else if (currentSeason.isOnSpecificLives(player, 2, false)) {
            currentSeason.setPlayerLives(player, LimitedLife.RED_TIME);
        }
        Text setTo = currentSeason.getFormattedLives(player);

        PlayerUtils.sendTitle(player,Text.of("§cYou have failed."), 20, 30, 20);
        PlayerUtils.playSoundToPlayers(List.of(player), SoundEvent.of(Identifier.of("minecraft","lastlife_boogeyman_fail")));
        OtherUtils.broadcastMessage(player.getStyledDisplayName().copy().append(Text.literal("§7 failed to kill a player while being the §cBoogeyman§7. Their time has been dropped to ").append(setTo)));
    }
}
