package net.mat0u5.lifeseries.series.wildlife.wildcards;

import net.mat0u5.lifeseries.dependencies.CardinalComponentsDependency;
import net.mat0u5.lifeseries.dependencies.DependencyManager;
import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.series.SessionAction;
import net.mat0u5.lifeseries.series.wildlife.WildLife;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.*;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.snails.Snails;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.superpowers.superpower.Creaking;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.superpowers.superpower.TimeControl;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.trivia.TriviaWildcard;
import net.mat0u5.lifeseries.utils.OtherUtils;
import net.mat0u5.lifeseries.utils.PermissionManager;
import net.mat0u5.lifeseries.utils.PlayerUtils;
import net.mat0u5.lifeseries.utils.TaskScheduler;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.*;

import static net.mat0u5.lifeseries.Main.*;

public class WildcardManager {
    public static final Map<Wildcards, Wildcard> activeWildcards = new HashMap<>();
    public static final Random rnd = new Random();
    public static final SessionAction wildcardNotice = new SessionAction(OtherUtils.secondsToTicks(30)) {
        @Override
        public void trigger() {
            if (activeWildcards.isEmpty()) {
                OtherUtils.broadcastMessage(Text.literal("A Wildcard will be activated in 2 minutes!").formatted(Formatting.GRAY));
            }
        }
    };
    public static final SessionAction startWildcards = new SessionAction(OtherUtils.secondsToTicks(150),"§7Activate Wildcard §f[00:02:30]", "Activate Wildcard") {
        @Override
        public void trigger() {
            if (activeWildcards.isEmpty()) {
                activateWildcards();
            }
        }
    };

    public static Wildcards chosenWildcard = null;

    public static WildLife getSeries() {
        if (currentSeries instanceof WildLife wildLife) return wildLife;
        return null;
    }

    public static void chosenWildcard(Wildcards wildcard) {
        OtherUtils.broadcastMessageToAdmins(Text.of("The " + wildcard + " wildcard has been selected for this session."));
        OtherUtils.broadcastMessageToAdmins(Text.of("§7Use the §f'/wildcard choose' §7 command if you want to change it."));
        WildcardManager.chosenWildcard = wildcard;
    }

    public static void chooseRandomWildcard() {
        if (chosenWildcard != null) {
            activeWildcards.put(chosenWildcard, Wildcards.getInstance(chosenWildcard));
            return;
        }
        int index = rnd.nextInt(7);
        if (index == 0) activeWildcards.put(Wildcards.SIZE_SHIFTING, new SizeShifting());
        if (index == 1) activeWildcards.put(Wildcards.HUNGER, new Hunger());
        if (index == 2) activeWildcards.put(Wildcards.TIME_DILATION, new TimeDilation());
        if (index == 3) activeWildcards.put(Wildcards.SNAILS, new Snails());
        if (index == 4) activeWildcards.put(Wildcards.MOB_SWAP, new MobSwap());
        if (index == 5) activeWildcards.put(Wildcards.TRIVIA, new TriviaWildcard());
        if (index == 6) activeWildcards.put(Wildcards.SUPERPOWERS, new SuperpowersWildcard());
    }

    public static void resetWildcardsOnPlayerJoin(ServerPlayerEntity player) {
        if (!isActiveWildcard(Wildcards.SIZE_SHIFTING)) {
            if (SizeShifting.getPlayerSize(player) != 1 && !TriviaBot.cursedGigantificationPlayers.contains(player.getUuid())) {
                SizeShifting.setPlayerSize(player, 1);
            }
        }
        if (!isActiveWildcard(Wildcards.HUNGER)) {
            player.removeStatusEffect(StatusEffects.HUNGER);
        }
        if (!isActiveWildcard(Wildcards.TRIVIA)) {
            TriviaWildcard.resetPlayerOnBotSpawn(player);
        }
        TaskScheduler.scheduleTask(20, () -> Hunger.updateInventory(player));

        if (DependencyManager.cardinalComponentsLoaded()) {
            CardinalComponentsDependency.resetWildcardsOnPlayerJoin(player);
        }
    }

    public static void activateWildcards() {
        showDots();
        TaskScheduler.scheduleTask(90, () -> {
            if (activeWildcards.isEmpty()) {
                chooseRandomWildcard();
            }
            for (Wildcard wildcard : activeWildcards.values()) {
                if (wildcard.active) continue;
                wildcard.activate();
            }
            showCryptTitle("A wildcard is active!");
        });
        TaskScheduler.scheduleTask(92, NetworkHandlerServer::sendUpdatePackets);
    }

    public static void fadedWildcard() {
        OtherUtils.broadcastMessage(Text.of("§7A Wildcard has faded..."));
        PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), SoundEvents.BLOCK_BEACON_DEACTIVATE);
    }

    public static void showDots() {
        List<ServerPlayerEntity> players = PlayerUtils.getAllPlayers();
        PlayerUtils.playSoundToPlayers(players, SoundEvents.BLOCK_NOTE_BLOCK_DIDGERIDOO.value(), 0.4f, 1);
        //PlayerUtils.sendTitleToPlayers(players, Text.literal("§a."),0,40,0);
        PlayerUtils.sendTitleToPlayers(players, Text.literal("§a§l,"),0,40,0);
        TaskScheduler.scheduleTask(30, () -> {
            PlayerUtils.playSoundToPlayers(players, SoundEvents.BLOCK_NOTE_BLOCK_DIDGERIDOO.value(), 0.4f, 1);
            //PlayerUtils.sendTitleToPlayers(players, Text.literal("§a. §e."),0,40,0);
            PlayerUtils.sendTitleToPlayers(players, Text.literal("§a§l, §e§l,"),0,40,0);
        });
        TaskScheduler.scheduleTask(60, () -> {
            PlayerUtils.playSoundToPlayers(players, SoundEvents.BLOCK_NOTE_BLOCK_DIDGERIDOO.value(), 0.4f, 1);
            //PlayerUtils.sendTitleToPlayers(players, Text.literal("§a. §e. §c."),0,40,0);
            PlayerUtils.sendTitleToPlayers(players, Text.literal("§a§l, §e§l, §c§l,"),0,40,0);
        });
    }

    public static void showCryptTitle(String text) {
        PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, 0.2f, 1);
        String colorCrypt = "§r§6§l§k";
        String colorNormal = "§r§6§l";
        String cryptedText = "";
        for (Character character : text.toCharArray()) {
            cryptedText += "<"+character;
        }

        float pos = 0;
        for (int i = 0; i < text.length(); i++) {
            pos += 4;
            if (!cryptedText.contains("<")) return;
            String[] split = cryptedText.split("<");
            int timesRemaining = split.length;
            int random = rnd.nextInt(1, timesRemaining);
            split[random] = ">"+split[random];
            cryptedText = String.join("<", split).replaceAll("<>", colorNormal);

            String finalCryptedText = cryptedText.replaceAll("<",colorCrypt);
            TaskScheduler.scheduleTask((int) pos, () -> PlayerUtils.sendTitleToPlayers(PlayerUtils.getAllPlayers(), Text.literal(finalCryptedText),0,30,20));
        }
    }

    private static final List<String> allColorCodes = List.of("_","9","a","b","c","d","e");
    public static void showRainbowCryptTitle(String text) {
        PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, 0.2f, 1);
        String colorCrypt = "§r§6§l§k";
        String colorNormal = "§r§6§l";
        String cryptedText = "";
        for (Character character : text.toCharArray()) {
            cryptedText += "<"+character;
        }

        float pos = 0;
        for (int i = 0; i < text.length()+24; i++) {
            pos += 2;
            String newCryptedText = cryptedText;
            if (cryptedText.contains("<")) {
                String[] split = cryptedText.split("<");
                int timesRemaining = split.length;
                int random = rnd.nextInt(1, timesRemaining);
                split[random] = ">"+split[random];
                cryptedText = String.join("<", split).replaceAll("<>", colorNormal);
                newCryptedText =  cryptedText.replaceAll("<",colorCrypt);
            }

            while (newCryptedText.contains("§6")) {
                String randomColor = "§" + allColorCodes.get(rnd.nextInt(allColorCodes.size()));
                newCryptedText = newCryptedText.replaceFirst("§6", randomColor);
            }

            newCryptedText = newCryptedText.replaceAll("§_","§6");
            String finalCryptedText = newCryptedText;
            TaskScheduler.scheduleTask((int) pos, () -> PlayerUtils.sendTitleToPlayers(PlayerUtils.getAllPlayers(), Text.literal(finalCryptedText),0,4,4));
        }
    }

    public static void tick() {
        SuperpowersWildcard.onTick();
        for (Wildcard wildcard : activeWildcards.values()) {
            wildcard.softTick();
            if (!wildcard.active) continue;
            wildcard.tick();
        }
        SizeShifting.resetSizesTick(isActiveWildcard(Wildcards.SIZE_SHIFTING));
        if (server != null && server.getTicks() % 200 == 0) {
            if (!isActiveWildcard(Wildcards.MOB_SWAP)) {
                MobSwap.killMobSwapMobs();
            }
            //? if >= 1.21.2 {
            /*Creaking.killUnassignedMobs();
            *///?}
        }

        if (TimeControl.changedSpeedFor > 0) TimeControl.changedSpeedFor--;
        if (!isActiveWildcard(Wildcards.TIME_DILATION) && TimeControl.changedSpeedFor <= 0) {
            if (TimeDilation.getWorldSpeed() != 20) {
                TimeDilation.setWorldSpeed(20);
            }
        }

        if (isActiveWildcard(Wildcards.TRIVIA)) {
            for (UUID uuid : TriviaBot.cursedSliding) {
                ServerPlayerEntity player = PlayerUtils.getPlayer(uuid);
                NetworkHandlerServer.sendLongPacket(player, "curse_sliding", System.currentTimeMillis());
            }
        }
    }

    public static void tickSessionOn() {
        for (Wildcard wildcard : activeWildcards.values()) {
            if (!wildcard.active) continue;
            wildcard.tickSessionOn();
        }
    }

    public static void onSessionStart() {
        if (chosenWildcard == null && activeWildcards.isEmpty()) {
            for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
                if (PermissionManager.isAdmin(player)) {
                    NetworkHandlerServer.sendStringPacket(player, "select_wildcards", "true");
                }
            }
        }
    }

    public static void onSessionEnd() {
        if (!activeWildcards.isEmpty()) {
            fadedWildcard();
        }
        if (isActiveWildcard(Wildcards.CALLBACK)) {
            if (activeWildcards.get(Wildcards.CALLBACK) instanceof Callback callback) {
                callback.deactivate();
                activeWildcards.remove(Wildcards.CALLBACK);
            }
        }
        for (Wildcard wildcard : activeWildcards.values()) {
            wildcard.deactivate();
        }
        activeWildcards.clear();
        SuperpowersWildcard.resetAllSuperpowers();
        NetworkHandlerServer.sendUpdatePackets();
        chosenWildcard = null;
    }

    public static boolean isActiveWildcard(Wildcards wildcard) {
        return activeWildcards.containsKey(wildcard);
    }

    public static void onUseItem(ServerPlayerEntity player) {
        Hunger.onUseItem(player);
    }
}
