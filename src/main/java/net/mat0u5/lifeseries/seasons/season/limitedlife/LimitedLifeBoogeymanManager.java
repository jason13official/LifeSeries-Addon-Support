package net.mat0u5.lifeseries.seasons.season.limitedlife;

import net.mat0u5.lifeseries.seasons.boogeyman.BoogeymanManager;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static net.mat0u5.lifeseries.Main.currentSeason;
import static net.mat0u5.lifeseries.Main.seasonConfig;

public class LimitedLifeBoogeymanManager extends BoogeymanManager {
    @Override
    public void onPlayerJoin(ServerPlayerEntity player) {
    }

    @Override
    public void boogeymenChooseRandom(List<ServerPlayerEntity> allowedPlayers, double currentChance) {
        int chooseAmount = 1;
        if (seasonConfig instanceof LimitedLifeConfig config) {
            chooseAmount = config.BOOGEYMAN_AMOUNT.get(config);
        }
        List<ServerPlayerEntity> nonRedPlayers = currentSeason.getNonRedPlayers();
        Collections.shuffle(nonRedPlayers);

        List<ServerPlayerEntity> normalPlayers = new ArrayList<>();
        List<ServerPlayerEntity> boogeyPlayers = new ArrayList<>();
        int chosen = 0;
        for (ServerPlayerEntity player : nonRedPlayers) {
            if (!allowedPlayers.contains(player)) continue;
            if (rolledPlayers.contains(player.getUuid())) continue;
            if (chosen >= chooseAmount) break;
            boogeyPlayers.add(player);
            chosen++;
        }
        for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
            if (rolledPlayers.contains(player.getUuid())) continue;
            rolledPlayers.add(player.getUuid());
            if (!allowedPlayers.contains(player)) continue;
            if (boogeyPlayers.contains(player)) continue;
            normalPlayers.add(player);
        }
        PlayerUtils.playSoundToPlayers(normalPlayers, SoundEvent.of(Identifier.of("minecraft","lastlife_boogeyman_no")));
        PlayerUtils.playSoundToPlayers(boogeyPlayers, SoundEvent.of(Identifier.of("minecraft","lastlife_boogeyman_yes")));
        PlayerUtils.sendTitleToPlayers(normalPlayers, Text.literal("NOT the Boogeyman.").formatted(Formatting.GREEN),10,50,20);
        PlayerUtils.sendTitleToPlayers(boogeyPlayers, Text.literal("The Boogeyman.").formatted(Formatting.RED),10,50,20);
        for (ServerPlayerEntity boogey : boogeyPlayers) {
            addBoogeyman(boogey);
            boogey.sendMessage(Text.of("§7You are the Boogeyman. You must by any means necessary kill a §2dark green§7, §agreen§7 or §eyellow§7 name by direct action to be cured of the curse. " +
                    "If you fail, your time will be dropped to the next color. All loyalties and friendships are removed while you are the Boogeyman."));
        }
    }

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
