package net.mat0u5.lifeseries.seasons.boogeyman;

import net.mat0u5.lifeseries.seasons.season.lastlife.LastLifeConfig;
import net.mat0u5.lifeseries.seasons.session.SessionAction;
import net.mat0u5.lifeseries.seasons.session.SessionTranscript;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static net.mat0u5.lifeseries.Main.*;

public class BoogeymanManager {

    public SessionAction actionBoogeymanWarn1 = new SessionAction(OtherUtils.minutesToTicks(5)) {
        @Override
        public void trigger() {
            if (boogeymanChosen) return;
            OtherUtils.broadcastMessage(Text.literal("The Boogeyman is being chosen in 5 minutes.").formatted(Formatting.RED));
            PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER);
        }
    };
    public SessionAction actionBoogeymanWarn2 = new SessionAction(OtherUtils.minutesToTicks(9)) {
        @Override
        public void trigger() {
            if (boogeymanChosen) return;
            OtherUtils.broadcastMessage(Text.literal("The Boogeyman is being chosen in 1 minute.").formatted(Formatting.RED));
            PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER);
        }
    };
    public SessionAction actionBoogeymanChoose = new SessionAction(
            OtherUtils.minutesToTicks(10),"§7Choose Boogeymen §f[00:10:00]", "Choose Boogeymen"
    ) {
        @Override
        public void trigger() {
            if (boogeymanChosen) return;
            prepareToChooseBoogeymen();
        }
    };
    public List<Boogeyman> boogeymen = new ArrayList<>();
    public List<UUID> rolledPlayers = new ArrayList<>();
    public boolean boogeymanChosen = false;


    public boolean isBoogeyman(ServerPlayerEntity player) {
        if (player == null) return false;
        for (Boogeyman boogeyman : boogeymen) {
            if (boogeyman.uuid.equals(player.getUuid())) {
                return true;
            }
        }
        return false;
    }

    public Boogeyman getBoogeyman(ServerPlayerEntity player) {
        if (player == null) return null;
        for (Boogeyman boogeyman : boogeymen) {
            if (boogeyman.uuid.equals(player.getUuid())) {
                return boogeyman;
            }
        }
        return null;
    }

    public void addBoogeyman(ServerPlayerEntity player) {
        if (!rolledPlayers.contains(player.getUuid())) {
            rolledPlayers.add(player.getUuid());
        }
        Boogeyman newBoogeyman = new Boogeyman(player);
        boogeymen.add(newBoogeyman);
        boogeymanChosen = true;
    }

    public void addBoogeymanManually(ServerPlayerEntity player) {
        addBoogeyman(player);
        player.sendMessage(Text.of("§c [NOTICE] You are now a Boogeyman!"));
    }

    public void removeBoogeymanManually(ServerPlayerEntity player) {
        Boogeyman boogeyman = getBoogeyman(player);
        if (boogeyman == null) return;
        boogeymen.remove(boogeyman);
        if (boogeymen.isEmpty()) boogeymanChosen = false;
        player.sendMessage(Text.of("§c [NOTICE] You are no longer a Boogeyman!"));
    }

    public void resetBoogeymen() {
        if (server == null) return;
        for (Boogeyman boogeyman : boogeymen) {
            ServerPlayerEntity player = PlayerUtils.getPlayer(boogeyman.uuid);
            if (player == null) continue;
            player.sendMessage(Text.of("§c [NOTICE] You are no longer a Boogeyman!"));
        }
        boogeymen = new ArrayList<>();
        boogeymanChosen = false;
        rolledPlayers = new ArrayList<>();
    }

    public void cure(ServerPlayerEntity player) {
        Boogeyman boogeyman = getBoogeyman(player);
        if (boogeymen == null) return;
        boogeyman.cured = true;
        PlayerUtils.sendTitle(player,Text.of("§aYou are cured!"), 20, 30, 20);
        PlayerUtils.playSoundToPlayers(List.of(player), SoundEvent.of(Identifier.of("minecraft","lastlife_boogeyman_cure")));
    }

    public void prepareToChooseBoogeymen() {
        OtherUtils.broadcastMessage(Text.literal("The Boogeyman is about to be chosen.").formatted(Formatting.RED));
        PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER);
        TaskScheduler.scheduleTask(100, () -> {
            resetBoogeymen();
            double chanceMultiplier = 1;
            if (seasonConfig instanceof LastLifeConfig config) {
                chanceMultiplier = config.BOOGEYMAN_MIN_AMOUNT.get(config);
            }
            chooseBoogeymen(currentSeason.getAlivePlayers(), 100 * chanceMultiplier);
        });
    }

    public void chooseBoogeymen(List<ServerPlayerEntity> allowedPlayers, double currentChance) {
        PlayerUtils.playSoundToPlayers(allowedPlayers, SoundEvents.UI_BUTTON_CLICK.value());
        PlayerUtils.sendTitleToPlayers(allowedPlayers, Text.literal("3").formatted(Formatting.GREEN),0,35,0);

        TaskScheduler.scheduleTask(30, () -> {
            PlayerUtils.playSoundToPlayers(allowedPlayers, SoundEvents.UI_BUTTON_CLICK.value());
            PlayerUtils.sendTitleToPlayers(allowedPlayers, Text.literal("2").formatted(Formatting.YELLOW),0,35,0);
        });
        TaskScheduler.scheduleTask(60, () -> {
            PlayerUtils.playSoundToPlayers(allowedPlayers, SoundEvents.UI_BUTTON_CLICK.value());
            PlayerUtils.sendTitleToPlayers(allowedPlayers, Text.literal("1").formatted(Formatting.RED),0,35,0);
        });
        TaskScheduler.scheduleTask(90, () -> {
            PlayerUtils.playSoundToPlayers(allowedPlayers, SoundEvent.of(Identifier.ofVanilla("lastlife_boogeyman_wait")));
            PlayerUtils.sendTitleToPlayers(allowedPlayers, Text.literal("You are...").formatted(Formatting.YELLOW),10,50,20);
        });
        TaskScheduler.scheduleTask(180, () -> boogeymenChooseRandom(allowedPlayers, currentChance));
    }

    public void boogeymenChooseRandom(List<ServerPlayerEntity> allowedPlayers, double currentChance) {
        int maxAmount = 2000000000;
        if (seasonConfig instanceof LastLifeConfig config) {
            maxAmount = config.BOOGEYMAN_MAX_AMOUNT.get(config);
        }
        List<ServerPlayerEntity> nonRedPlayers = currentSeason.getNonRedPlayers();
        Collections.shuffle(nonRedPlayers);

        List<ServerPlayerEntity> normalPlayers = new ArrayList<>();
        List<ServerPlayerEntity> boogeyPlayers = new ArrayList<>();
        int chosen = 0;
        for (ServerPlayerEntity player : nonRedPlayers) {
            if (!allowedPlayers.contains(player)) continue;
            if (rolledPlayers.contains(player.getUuid())) continue;
            double currentRoll = Math.random()*100;
            if (currentChance >= currentRoll && currentChance != 0) {
                if (chosen >= maxAmount) break;
                boogeyPlayers.add(player);
                chosen++;
            }
            else {
                currentChance = 0;
            }

            if (currentChance != 0) {
                currentChance/=2;
            }
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
                    "If you fail, you will become a §cred name§7. All loyalties and friendships are removed while you are the Boogeyman."));
        }
        SessionTranscript.boogeymenChosen(boogeyPlayers);
    }

    public void sessionEnd() {
        if (server == null) return;
        for (Boogeyman boogeyman : boogeymen) {
            if (boogeyman.died) continue;

            if (!boogeyman.cured) {
                ServerPlayerEntity player = PlayerUtils.getPlayer(boogeyman.uuid);
                if (player == null) {
                    OtherUtils.broadcastMessageToAdmins(Text.of("§c[BoogeymanManager] The Boogeyman ("+boogeyman.name+") has failed to kill a person, and is offline at session end. " +
                            "That means their lives have not been set to 1. You must do this manually once they are online again."));
                    continue;
                }
                playerFailBoogeyman(player);
            }
        }
    }

    public void playerFailBoogeyman(ServerPlayerEntity player) {
        if (!currentSeason.isAlive(player)) return;
        if (currentSeason.isOnLastLife(player, true)) return;
        PlayerUtils.sendTitle(player,Text.of("§cYou have failed."), 20, 30, 20);
        PlayerUtils.playSoundToPlayers(List.of(player), SoundEvent.of(Identifier.of("minecraft","lastlife_boogeyman_fail")));
        OtherUtils.broadcastMessage(player.getStyledDisplayName().copy().append(Text.of("§7 failed to kill a player while being the §cBoogeyman§7. They have been dropped to their §cLast Life§7")));
        currentSeason.setPlayerLives(player, 1);
    }

    public void playerLostAllLives(ServerPlayerEntity player) {
        Boogeyman boogeyman = getBoogeyman(player);
        if (boogeyman == null) return;
        boogeyman.died = true;
    }

    public void onPlayerJoin(ServerPlayerEntity player) {
        if (!boogeymanChosen) return;
        if (rolledPlayers.contains(player.getUuid())) return;
        if (!currentSeason.isAlive(player)) return;
        TaskScheduler.scheduleTask(200, () -> {
            player.sendMessage(Text.of("§cSince you were not present when the Boogeyman was being chosen, your chance to become the Boogeyman is now. Good luck!"));
            double chanceForBoogey = 100.0 /PlayerUtils.getAllPlayers().size();
            chooseBoogeymen(List.of(player), chanceForBoogey);
        });
    }
}
