package net.mat0u5.lifeseries.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.MainClient;
import net.mat0u5.lifeseries.utils.VersionControl;
import net.mat0u5.lifeseries.client.ClientHandler;
import net.mat0u5.lifeseries.client.gui.ChooseWildcardScreen;
import net.mat0u5.lifeseries.client.render.VignetteRenderer;
import net.mat0u5.lifeseries.network.packets.*;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.trivia.Trivia;
import net.mat0u5.lifeseries.series.SeriesList;
import net.mat0u5.lifeseries.series.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.Hunger;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.TimeDilation;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NetworkHandlerClient {
    public static void registerClientReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(NumberPayload.ID, (payload, context) -> {
            MinecraftClient client = context.client();
            client.execute(() -> handleNumberPacket(payload.name(),payload.number()));
        });
        ClientPlayNetworking.registerGlobalReceiver(StringPayload.ID, (payload, context) -> {
            MinecraftClient client = context.client();
            client.execute(() -> handleStringPacket(payload.name(),payload.value()));
        });
        ClientPlayNetworking.registerGlobalReceiver(HandshakePayload.ID, (payload, context) -> {
            MinecraftClient client = context.client();
            client.execute(() -> handleHandshake(payload));
        });
        ClientPlayNetworking.registerGlobalReceiver(TriviaQuestionPayload.ID, (payload, context) -> {
            MinecraftClient client = context.client();
            client.execute(() -> Trivia.receiveTrivia(payload));
        });
        ClientPlayNetworking.registerGlobalReceiver(LongPayload.ID, (payload, context) -> {
            MinecraftClient client = context.client();
            client.execute(() -> handleLongPacket(payload.name(),payload.number()));
        });
        ClientPlayNetworking.registerGlobalReceiver(PlayerDisguisePayload.ID, (payload, context) -> {
            MinecraftClient client = context.client();
            client.execute(() -> handlePlayerDisguise(payload.name(),payload.hiddenUUID(), payload.hiddenName(), payload.shownUUID(), payload.shownName()));
        });
    }
    
    public static void handleStringPacket(String name, String value) {
        if (name.equalsIgnoreCase("currentSeries")) {
            if (VersionControl.isDevVersion()) Main.LOGGER.info("[PACKET_CLIENT] Updated current series to {}", value);
            MainClient.clientCurrentSeries = SeriesList.getSeriesFromStringName(value);
            if (Main.isClient()) {
                ClientHandler.checkClientPacks();
            }
        }
        if (name.equalsIgnoreCase("activeWildcards")) {
            List<Wildcards> newList = new ArrayList<>();
            for (String wildcardStr : value.split("__")) {
                newList.add(Wildcards.getFromString(wildcardStr));
            }
            if (VersionControl.isDevVersion()) Main.LOGGER.info("[PACKET_CLIENT] Updated current wildcards to {}", newList);
            MainClient.clientActiveWildcards = newList;
        }
        if (name.equalsIgnoreCase("curse_sliding")) {
            if (value.equalsIgnoreCase("true")) {
                MainClient.CURSE_SLIDING = true;
            }
            else {
                MainClient.CURSE_SLIDING = false;
            }
        }
        if (name.equalsIgnoreCase("jump") && MinecraftClient.getInstance().player != null) {
            MinecraftClient.getInstance().player.jump();
        }
        if (name.equalsIgnoreCase("reset_trivia")) {
            Trivia.resetTrivia();
        }
        if (name.equalsIgnoreCase("select_wildcards") && Main.isClient()) {
            MinecraftClient.getInstance().setScreen(new ChooseWildcardScreen());
        }
    }

    public static void handleNumberPacket(String name, double number) {
        int intNumber = (int) number;
        if (name.equalsIgnoreCase("hunger_version")) {
            if (VersionControl.isDevVersion()) Main.LOGGER.info("[PACKET_CLIENT] Updated hunger shuffle version to {}", intNumber);
            Hunger.shuffleVersion = intNumber;
        }
        if (name.equalsIgnoreCase("player_min_mspt")) {
            if (VersionControl.isDevVersion()) Main.LOGGER.info("[PACKET_CLIENT] Updated min. player MSPT to {}", number);
            TimeDilation.MIN_PLAYER_MSPT = (float) number;
        }
        if (name.equalsIgnoreCase("snail_air")) {
            MainClient.snailAir = intNumber;
        }
        if (name.equalsIgnoreCase("fake_thunder") && MinecraftClient.getInstance().world != null) {
            MinecraftClient.getInstance().world.setLightningTicksLeft(intNumber);
        }
    }

    public static void handleLongPacket(String name, long number) {
        if (name.equalsIgnoreCase("superpower_cooldown")) {
            MainClient.SUPERPOWER_COOLDOWN_TIMESTAMP = number;
        }
        if (name.equalsIgnoreCase("show_vignette")) {
            if (VersionControl.isDevVersion()) Main.LOGGER.info("[PACKET_CLIENT] Showing vignette for {}", number);
            VignetteRenderer.showVignetteFor(0.35f, number);
        }
        if (name.equalsIgnoreCase("mimicry_cooldown")) {
            MainClient.MIMICRY_COOLDOWN_TIMESTAMP = number;
        }
        if (name.startsWith("player_invisible__")) {
            try {
                UUID uuid = UUID.fromString(name.replaceFirst("player_invisible__",""));
                if (number == 0) {
                    MainClient.invisiblePlayers.remove(uuid);
                }
                else {
                    MainClient.invisiblePlayers.put(uuid, number);
                }
            }catch(Exception ignored) {}
        }
        if (name.equalsIgnoreCase("time_dilation")) {
            MainClient.TIME_DILATION_TIMESTAMP = number;
        }
    }

    public static void handlePlayerDisguise(String name, String hiddenUUID, String hiddenName, String shownUUID, String shownName) {
        if (name.equalsIgnoreCase("player_disguise")) {
            if (shownName.isEmpty()) {
                MainClient.playerDisguiseNames.remove(hiddenName);
                try {
                    UUID hideUUID = UUID.fromString(hiddenUUID);
                    MainClient.playerDisguiseUUIDs.remove(hideUUID);
                }catch(Exception ignored) {}
            }
            else {
                MainClient.playerDisguiseNames.put(hiddenName, shownName);
                try {
                    UUID hideUUID = UUID.fromString(hiddenUUID);
                    UUID showUUID = UUID.fromString(shownUUID);
                    MainClient.playerDisguiseUUIDs.put(hideUUID, showUUID);
                }catch(Exception ignored) {}
            }

        }
    }

    public static void handleHandshake(HandshakePayload payload) {
        String clientVersionStr = Main.MOD_VERSION;
        String clientCompatibilityStr = VersionControl.clientCmpatibilityMin();

        int clientVersion = VersionControl.getModVersionInt(clientVersionStr);
        int clientCompatibility = VersionControl.getModVersionInt(clientCompatibilityStr);

        HandshakePayload sendPayload = new HandshakePayload(clientVersionStr, clientVersion, clientCompatibilityStr, clientCompatibility);
        ClientPlayNetworking.send(sendPayload);
        if (VersionControl.isDevVersion()) Main.LOGGER.info("[PACKET_CLIENT] Sent handshake");
    }

    /*
        Sending
     */

    public static void sendTriviaAnswer(int answer) {
        if (VersionControl.isDevVersion()) Main.LOGGER.info("[PACKET_CLIENT] Sending trivia answer: {}", answer);
        ClientPlayNetworking.send(new NumberPayload("trivia_answer", answer));
    }

    public static void sendHoldingJumpPacket() {
        ClientPlayNetworking.send(new StringPayload("holding_jump", "true"));
    }

    public static void pressSuperpowerKey() {
        ClientPlayNetworking.send(new StringPayload("superpower_key", "true"));
    }

    public static void sendStringPacket(String name, String value) {
        ClientPlayNetworking.send(new StringPayload(name, value));
    }
}
